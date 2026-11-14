package com.caresync.app.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.caresync.app.model.PatientCategory
import com.caresync.app.model.RiskLevel
import com.caresync.app.model.SyncStatus

object FormatUtils {
    fun initials(fullName: String): String {
        val parts = fullName.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        return when {
            parts.isEmpty() -> "CS"
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> "${parts.first().first()}${parts.last().first()}".uppercase()
        }
    }

    fun parseSyncStatus(raw: String): SyncStatus =
        SyncStatus.entries.find { it.name == raw } ?: SyncStatus.PENDING_UPLOAD

    fun parseRiskLevel(raw: String): RiskLevel =
        RiskLevel.entries.find { it.name == raw } ?: RiskLevel.LOW

    fun parseCategory(raw: String): PatientCategory =
        PatientCategory.entries.find { it.name == raw } ?: PatientCategory.GENERAL

    fun dialPhone(context: Context, phone: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${phone.filter { it.isDigit() || it == '+' }}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(intent) }
    }
}
