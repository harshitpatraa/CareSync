package com.caresync.app.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.caresync.app.database.HealthRecordEntity
import com.caresync.app.database.PatientEntity
import com.caresync.app.database.VitalsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object PdfReferralGenerator {

    fun createCameraPhotoUri(context: Context): Uri {
        val dir = File(context.cacheDir, "captures").apply { mkdirs() }
        val file = File(dir, "capture_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    suspend fun saveBitmapToCacheUri(context: Context, bitmap: Bitmap, fileName: String): Uri =
        withContext(Dispatchers.IO) {
            val dir = File(context.cacheDir, "shared_cards").apply { mkdirs() }
            val file = File(dir, fileName)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        }

    suspend fun generateReferralPdf(
        context: Context,
        patient: PatientEntity,
        latestVitals: VitalsEntity?,
        records: List<HealthRecordEntity>,
        clinicianName: String,
        clinicName: String
    ): Uri = withContext(Dispatchers.IO) {
        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = doc.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val headerPaint = Paint().apply { color = Color.rgb(46, 125, 255) }
        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val subtitlePaint = Paint().apply {
            color = Color.rgb(225, 238, 255)
            textSize = 11f
        }
        val sectionPaint = Paint().apply {
            color = Color.rgb(46, 125, 255)
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val bodyPaint = Paint().apply {
            color = Color.rgb(30, 41, 59)
            textSize = 11.5f
        }
        val boldBodyPaint = Paint().apply {
            color = Color.rgb(15, 23, 42)
            textSize = 11.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val criticalPaint = Paint().apply {
            color = Color.rgb(220, 38, 38)
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val linePaint = Paint().apply {
            color = Color.rgb(203, 213, 225)
            strokeWidth = 1f
        }

        // Top Banner
        canvas.drawRect(0f, 0f, 595f, 88f, headerPaint)
        canvas.drawText("CareSync Clinical Referral Slip", 32f, 40f, titlePaint)
        canvas.drawText("$clinicName  |  Prepared by $clinicianName", 32f, 64f, subtitlePaint)

        var y = 118f
        if (patient.isCritical || patient.riskLevel == "CRITICAL" || patient.riskLevel == "HIGH") {
            canvas.drawText("PRIORITY TRIAGE: ${patient.riskLevel} ${if (patient.isCritical) "(CRITICAL FLAG)" else ""}", 32f, y, criticalPaint)
            y += 22f
        }

        // Patient Demographics
        canvas.drawText("1. PATIENT IDENTIFICATION", 32f, y, sectionPaint)
        y += 18f
        canvas.drawText("Name: ${patient.fullName}    |    ID: ${patient.id}", 32f, y, boldBodyPaint)
        y += 16f
        canvas.drawText("Age / Gender: ${patient.age} yrs / ${patient.gender}    |    Blood Group: ${patient.bloodGroup}", 32f, y, bodyPaint)
        y += 16f
        canvas.drawText("Ward / Village: ${patient.villageOrWard}    |    Phone: ${patient.phone.ifBlank { "Not recorded" }}", 32f, y, bodyPaint)
        y += 16f
        canvas.drawText("Program: ${patient.category}    |    Next Follow-up: ${patient.nextFollowUpDate}", 32f, y, bodyPaint)
        if (patient.pregnancyWeeks > 0) {
            y += 16f
            canvas.drawText("ANC Details: ${patient.pregnancyWeeks} weeks gestation    |    EDD: ${patient.expectedDueDate}", 32f, y, boldBodyPaint)
        }

        y += 16f
        canvas.drawLine(32f, y, 563f, y, linePaint)
        y += 22f

        // Allergies & Chronic Conditions
        canvas.drawText("2. ALLERGIES & CLINICAL HISTORY", 32f, y, sectionPaint)
        y += 18f
        canvas.drawText("Known Allergies: ${patient.allergies.ifBlank { "None reported" }}", 32f, y, boldBodyPaint)
        y += 16f
        canvas.drawText("Chronic Conditions: ${patient.chronicConditions.ifBlank { "None" }}", 32f, y, bodyPaint)
        y += 16f
        val notesLines = wrapText("Clinical Notes: ${patient.notes.ifBlank { "No notes recorded." }}", 86)
        notesLines.forEach { line ->
            canvas.drawText(line, 32f, y, bodyPaint)
            y += 15f
        }

        y += 10f
        canvas.drawLine(32f, y, 563f, y, linePaint)
        y += 22f

        // Latest Vitals
        canvas.drawText("3. LATEST RECORDED VITALS", 32f, y, sectionPaint)
        y += 18f
        if (latestVitals != null) {
            canvas.drawText(
                "Recorded: ${latestVitals.recordedAtLabel}    |    BP: ${latestVitals.systolicBp}/${latestVitals.diastolicBp} mmHg    |    SpO2: ${latestVitals.spo2Percent}%",
                32f,
                y,
                boldBodyPaint
            )
            y += 16f
            canvas.drawText(
                "Heart Rate: ${latestVitals.heartRate} bpm    |    Temp: ${latestVitals.temperatureC}°C    |    Blood Sugar: ${latestVitals.bloodSugarMgDl} mg/dL",
                32f,
                y,
                bodyPaint
            )
        } else {
            canvas.drawText("No baseline vitals recorded yet.", 32f, y, bodyPaint)
        }

        y += 20f
        canvas.drawLine(32f, y, 563f, y, linePaint)
        y += 22f

        // Recent Medicines & Records
        canvas.drawText("4. RECENT MEDICATIONS, VACCINES & RECORDS", 32f, y, sectionPaint)
        y += 18f
        val recent = records.take(6)
        if (recent.isEmpty()) {
            canvas.drawText("No medication or timeline records attached.", 32f, y, bodyPaint)
        } else {
            recent.forEach { rec ->
                if (y < 760f) {
                    canvas.drawText("• [${rec.recordType}] ${rec.title} (${rec.dateLabel})", 32f, y, boldBodyPaint)
                    y += 14f
                    val detailLine = listOf(rec.subtitle, rec.detail).filter { it.isNotBlank() }.joinToString(" — ")
                    wrapText("  $detailLine", 84).take(2).forEach { line ->
                        canvas.drawText(line, 32f, y, bodyPaint)
                        y += 14f
                    }
                    y += 4f
                }
            }
        }

        // Embed QR Code in bottom-right corner
        val qrBitmap = QrUtils.generateQrBitmap(QrUtils.buildPatientQrPayload(patient), 96)
        canvas.drawBitmap(qrBitmap, 468f, 718f, null)
        canvas.drawText("CareSync Offline-First Referral · Scan QR for Patient ID", 32f, 808f, bodyPaint)

        doc.finishPage(page)

        val dir = File(context.cacheDir, "referrals").apply { mkdirs() }
        val safeId = patient.id.replace(Regex("[^A-Za-z0-9_-]"), "_")
        val file = File(dir, "CareSync_Referral_${safeId}.pdf")
        FileOutputStream(file).use { out ->
            doc.writeTo(out)
        }
        doc.close()

        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    fun shareUri(context: Context, uri: Uri, mimeType: String, subject: String, text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        runCatching {
            context.startActivity(Intent.createChooser(intent, subject).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    private fun wrapText(text: String, maxChars: Int): List<String> {
        if (text.length <= maxChars) return listOf(text)
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var current = StringBuilder()
        for (word in words) {
            if (current.length + word.length + 1 > maxChars) {
                lines += current.toString().trim()
                current = StringBuilder()
            }
            current.append(word).append(' ')
        }
        if (current.isNotBlank()) lines += current.toString().trim()
        return lines
    }
}
