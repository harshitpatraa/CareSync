package com.caresync.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import com.caresync.app.database.PatientEntity
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max

object QrUtils {

    fun buildPatientQrPayload(patient: PatientEntity): String = buildString {
        append("CARESYNC|")
        append(patient.id)
        append("|")
        append(patient.fullName)
        append("|BG:")
        append(patient.bloodGroup)
        append("|ALLERGY:")
        append(patient.allergies.ifBlank { "None" })
        append("|RISK:")
        append(patient.riskLevel)
    }

    fun extractPatientIdFromQr(raw: String): String? {
        val trimmed = raw.trim()
        if (trimmed.startsWith("CARESYNC|", ignoreCase = true)) {
            return trimmed.split("|").getOrNull(1)?.trim()?.takeIf { it.isNotBlank() }
        }
        val match = Regex("""PT-[A-Za-z0-9\-]+""").find(trimmed)
        return match?.value ?: trimmed.takeIf { it.startsWith("PT-", ignoreCase = true) }
    }

    fun generateQrBitmap(content: String, sizePx: Int = 512): Bitmap {
        val matrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx)
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        for (x in 0 until sizePx) {
            for (y in 0 until sizePx) {
                bitmap.setPixel(x, y, if (matrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    suspend fun decodeQrFromUri(context: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
        runCatching {
            val rawBitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream)
            } ?: return@runCatching null

            val maxDim = 900
            val largest = max(rawBitmap.width, rawBitmap.height)
            val bitmap = if (largest > maxDim) {
                val scale = maxDim.toFloat() / largest.toFloat()
                Bitmap.createScaledBitmap(
                    rawBitmap,
                    (rawBitmap.width * scale).toInt().coerceAtLeast(1),
                    (rawBitmap.height * scale).toInt().coerceAtLeast(1),
                    true
                )
            } else {
                rawBitmap
            }

            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            val source = RGBLuminanceSource(width, height, pixels)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            MultiFormatReader().decode(binaryBitmap).text
        }.getOrNull()
    }
}
