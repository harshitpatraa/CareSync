package com.caresync.app.ai

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.caresync.app.repository.CareSyncRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.DecimalFormat
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "LocalModelManager"
private const val GGUF_MAGIC = 0x46554747 // "GGUF" in Little Endian: 'G'(0x47), 'G'(0x47), 'U'(0x55), 'F'(0x46)
private const val STORAGE_SAFETY_MARGIN_BYTES = 50L * 1024L * 1024L // 50 MB buffer for system operations

data class LocalModelInfo(
    val fileName: String,
    val absolutePath: String,
    val sizeBytes: Long,
    val formattedSize: String,
    val isReady: Boolean,
    val version: Int = 3
)

sealed class ModelImportProgress {
    data class Checking(val message: String) : ModelImportProgress()
    data class Copying(
        val bytesCopied: Long,
        val totalBytes: Long,
        val progress: Float,
        val formattedProgress: String
    ) : ModelImportProgress()
    data class Validating(val message: String) : ModelImportProgress()
    data class Success(val modelInfo: LocalModelInfo, val message: String) : ModelImportProgress()
    data class Error(val reason: String) : ModelImportProgress()
}

/**
 * Manages on-device import, validation, storage pre-checks, and lifecycle
 * of GGUF quantized models (e.g. Qwen 2.5 0.5B Instruct) for future llama.cpp inference.
 *
 * Copies models from external storage (e.g. phone Downloads via SAF) into the app's
 * private internal storage: files/models/
 */
@Singleton
class LocalModelManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val repository: CareSyncRepository?
) {

    constructor(context: Context) : this(context, null)

    val modelsDir: File by lazy {
        val dir = File(context.filesDir, "models")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        dir
    }

    /**
     * Checks if a validated local GGUF model is currently present and registered in the internal directory.
     */
    fun getInstalledModel(): LocalModelInfo? {
        val prefs = repository?.getPreferencesSnapshot()
        val path = prefs?.localModelPath ?: ""
        if (path.isNotBlank()) {
            val file = File(path)
            if (file.exists() && file.isFile && file.length() > 0) {
                val validation = validateGgufHeader(file)
                if (validation.isValid) {
                    return LocalModelInfo(
                        fileName = file.name,
                        absolutePath = file.absolutePath,
                        sizeBytes = file.length(),
                        formattedSize = formatBytes(file.length()),
                        isReady = true,
                        version = validation.version
                    )
                }
            }
        }

        // Fallback: Check if any .gguf exists in files/models/
        val existingGguf = modelsDir.listFiles { f -> f.isFile && f.name.endsWith(".gguf", ignoreCase = true) }?.firstOrNull()
        if (existingGguf != null && existingGguf.length() > 0) {
            val validation = validateGgufHeader(existingGguf)
            if (validation.isValid) {
                return LocalModelInfo(
                    fileName = existingGguf.name,
                    absolutePath = existingGguf.absolutePath,
                    sizeBytes = existingGguf.length(),
                    formattedSize = formatBytes(existingGguf.length()),
                    isReady = true,
                    version = validation.version
                )
            }
        }
        return null
    }

    /**
     * Imports a GGUF model via Storage Access Framework (SAF) Uri.
     * Emits progress states: Checking -> Copying -> Validating -> Success/Error.
     */
    fun importGgufFromUri(sourceUri: Uri): Flow<ModelImportProgress> = flow {
        emit(ModelImportProgress.Checking("Reading file metadata from Downloads..."))

        // 1. Resolve source file name and size via ContentResolver
        val (fileName, sourceSize) = resolveSourceMetadata(sourceUri)
        val cleanFileName = if (fileName.isNotBlank()) fileName else "imported_model.gguf"

        if (!cleanFileName.contains(".gguf", ignoreCase = true)) {
            emit(ModelImportProgress.Error("Selected file '$cleanFileName' does not have a .gguf extension. Please select a valid GGUF model."))
            return@flow
        }

        emit(ModelImportProgress.Checking("Checking available device internal storage..."))

        // 2. Validate internal storage capacity
        val usableSpace = modelsDir.usableSpace
        val requiredSpace = if (sourceSize > 0) sourceSize + STORAGE_SAFETY_MARGIN_BYTES else 550L * 1024L * 1024L

        if (usableSpace < requiredSpace) {
            val neededMb = formatBytes(requiredSpace)
            val availableMb = formatBytes(usableSpace)
            emit(
                ModelImportProgress.Error(
                    "Insufficient internal storage: Model requires approximately $neededMb, but only $availableMb is available in app storage."
                )
            )
            return@flow
        }

        // 3. Pre-validate GGUF magic header directly from the input stream before full copy
        emit(ModelImportProgress.Checking("Verifying GGUF binary magic header..."))
        val precheckStream = runCatching { context.contentResolver.openInputStream(sourceUri) }.getOrNull()
        if (precheckStream == null) {
            emit(ModelImportProgress.Error("Unable to open source model file. Please ensure storage permissions are granted."))
            return@flow
        }

        val precheckValidation = validateStreamGgufHeader(precheckStream)
        if (!precheckValidation.isValid) {
            emit(ModelImportProgress.Error("Validation failed: ${precheckValidation.errorReason}"))
            return@flow
        }

        // 4. Stream copy from SAF content resolver into files/models/
        val targetFile = File(modelsDir, cleanFileName)
        val tempFile = File(modelsDir, "${cleanFileName}.tmp")

        try {
            if (tempFile.exists()) tempFile.delete()

            val inputStream: InputStream? = context.contentResolver.openInputStream(sourceUri)
            if (inputStream == null) {
                emit(ModelImportProgress.Error("Could not open input stream for selected model."))
                return@flow
            }

            var bytesCopied = 0L
            val totalBytes = if (sourceSize > 0) sourceSize else 0L
            val buffer = ByteArray(64 * 1024) // 64 KB buffer
            var lastEmittedBytes = 0L

            inputStream.use { input ->
                FileOutputStream(tempFile).use { output ->
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                        bytesCopied += read

                        // Emit progress update every ~1.5 MB copied or when near completion
                        if (bytesCopied - lastEmittedBytes > 1_500_000L || (totalBytes > 0 && bytesCopied == totalBytes)) {
                            lastEmittedBytes = bytesCopied
                            val progress = if (totalBytes > 0) (bytesCopied.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f) else 0f
                            val formatted = if (totalBytes > 0) {
                                "${formatBytes(bytesCopied)} / ${formatBytes(totalBytes)} (${(progress * 100).toInt()}%)"
                            } else {
                                formatBytes(bytesCopied)
                            }
                            emit(
                                ModelImportProgress.Copying(
                                    bytesCopied = bytesCopied,
                                    totalBytes = totalBytes,
                                    progress = progress,
                                    formattedProgress = formatted
                                )
                            )
                        }
                    }
                    output.flush()
                }
            }

            // 5. Final validation of copied file
            emit(ModelImportProgress.Validating("Verifying integrity of imported GGUF file..."))
            val finalValidation = validateGgufHeader(tempFile)
            if (!finalValidation.isValid) {
                tempFile.delete()
                emit(ModelImportProgress.Error("Integrity check failed: ${finalValidation.errorReason}"))
                return@flow
            }

            // Atomically move tempFile to targetFile
            if (targetFile.exists()) targetFile.delete()
            val renamed = tempFile.renameTo(targetFile)
            val finalFile = if (renamed) targetFile else tempFile

            // 6. Save model path and metadata in DataStore for future llama.cpp integration
            val modelInfo = LocalModelInfo(
                fileName = finalFile.name,
                absolutePath = finalFile.absolutePath,
                sizeBytes = finalFile.length(),
                formattedSize = formatBytes(finalFile.length()),
                isReady = true,
                version = finalValidation.version
            )

            repository?.saveLocalModelInfo(
                path = finalFile.absolutePath,
                name = finalFile.name,
                sizeBytes = finalFile.length()
            )

            emit(
                ModelImportProgress.Success(
                    modelInfo = modelInfo,
                    message = "Successfully imported ${finalFile.name} (${formatBytes(finalFile.length())}) to internal storage. Ready for llama.cpp!"
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed during model import", e)
            if (tempFile.exists()) tempFile.delete()
            emit(ModelImportProgress.Error("Model import failed: ${e.localizedMessage ?: e.message}"))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Deletes the currently installed local model and clears preferences.
     */
    suspend fun deleteLocalModel(): Boolean = withContext(Dispatchers.IO) {
        try {
            val files = modelsDir.listFiles()
            files?.forEach { file ->
                if (file.isFile && (file.name.endsWith(".gguf", ignoreCase = true) || file.name.contains(".tmp"))) {
                    file.delete()
                }
            }
            repository?.clearLocalModelInfo()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete local model", e)
            false
        }
    }

    /**
     * Validates whether a file is a valid GGUF binary by checking the first 8 bytes.
     */
    fun validateGgufHeader(file: File): GgufValidationResult {
        if (!file.exists() || file.length() < 8) {
            return GgufValidationResult(isValid = false, errorReason = "File does not exist or is too small (<8 bytes).")
        }
        return try {
            FileInputStream(file).use { input ->
                validateStreamGgufHeader(input)
            }
        } catch (e: Exception) {
            GgufValidationResult(isValid = false, errorReason = "IO error reading header: ${e.message}")
        }
    }

    /**
     * Validates GGUF magic bytes from an input stream.
     * Byte 0..3: 'G', 'G', 'U', 'F'
     * Byte 4..7: Version (uint32 little endian, typically 2 or 3)
     */
    fun validateStreamGgufHeader(inputStream: InputStream): GgufValidationResult {
        return try {
            val headerBytes = ByteArray(8)
            var bytesRead = 0
            while (bytesRead < 8) {
                val r = inputStream.read(headerBytes, bytesRead, 8 - bytesRead)
                if (r == -1) break
                bytesRead += r
            }

            if (bytesRead < 8) {
                return GgufValidationResult(isValid = false, errorReason = "Unexpected end of stream while reading GGUF header.")
            }

            val buffer = ByteBuffer.wrap(headerBytes).order(ByteOrder.LITTLE_ENDIAN)
            val magic = buffer.int
            val version = buffer.int

            if (magic != GGUF_MAGIC) {
                val ascii = String(headerBytes, 0, 4, Charsets.US_ASCII)
                return GgufValidationResult(
                    isValid = false,
                    errorReason = "Invalid magic bytes: Found '$ascii' (0x${Integer.toHexString(magic)}), expected 'GGUF' (0x46554747)."
                )
            }

            if (version !in 1..10) {
                return GgufValidationResult(
                    isValid = false,
                    errorReason = "Unsupported GGUF version $version. Expected version 2 or 3."
                )
            }

            GgufValidationResult(isValid = true, version = version)
        } catch (e: Exception) {
            GgufValidationResult(isValid = false, errorReason = "Exception validating header: ${e.message}")
        }
    }

    private fun resolveSourceMetadata(uri: Uri): Pair<String, Long> {
        var name = ""
        var size = 0L
        try {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIdx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIdx >= 0) {
                        name = it.getString(nameIdx) ?: ""
                    }
                    val sizeIdx = it.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIdx >= 0 && !it.isNull(sizeIdx)) {
                        size = it.getLong(sizeIdx)
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to query cursor metadata for URI: $uri", e)
        }

        if (name.isBlank()) {
            name = uri.lastPathSegment?.substringAfterLast('/') ?: "model.gguf"
        }
        return Pair(name, size)
    }

    companion object {
        fun formatBytes(bytes: Long): String {
            if (bytes <= 0) return "0 B"
            val units = arrayOf("B", "KB", "MB", "GB", "TB")
            val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
            val df = DecimalFormat("#,##0.#")
            return "${df.format(bytes / Math.pow(1024.0, digitGroups.toDouble()))} ${units[digitGroups]}"
        }
    }
}

data class GgufValidationResult(
    val isValid: Boolean,
    val version: Int = 0,
    val errorReason: String? = null
)