package com.caresync.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import com.caresync.app.database.PatientEntity
import com.caresync.app.database.VitalsEntity
import com.caresync.app.model.NearbyHealthCenter
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class FieldCoordinates(
    val latitude: Double,
    val longitude: Double,
    val label: String
)

object LocationHelper {

    @SuppressLint("MissingPermission")
    suspend fun getBestLastLocation(context: Context): FieldCoordinates? {
        runCatching {
            val client = LocationServices.getFusedLocationProviderClient(context)
            val loc = client.lastLocation.await()
                ?: client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null).await()
            if (loc != null) {
                return loc.toFieldCoordinates()
            }
        }
        return runCatching {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val fallback = lm?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                ?: lm?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            fallback?.toFieldCoordinates()
        }.getOrNull()
    }

    private fun Location.toFieldCoordinates(): FieldCoordinates = FieldCoordinates(
        latitude = latitude,
        longitude = longitude,
        label = String.format(Locale.US, "%.4f, %.4f", latitude, longitude)
    )

    fun distanceLabelKm(from: FieldCoordinates?, toLat: Double, toLng: Double, fallback: String): String {
        if (from == null) return fallback
        val d = haversineKm(from.latitude, from.longitude, toLat, toLng)
        return String.format(Locale.US, "%.1f km", d)
    }

    fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    fun openMapsDirections(context: Context, center: NearbyHealthCenter) {
        val query = Uri.encode("${center.name} (${center.latitude}, ${center.longitude})")
        val uri = Uri.parse("geo:${center.latitude},${center.longitude}?q=$query")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(intent) }
    }

    fun buildSosDispatchMessage(
        clinicianName: String,
        patient: PatientEntity?,
        latestVitals: VitalsEntity?,
        coordinates: FieldCoordinates?
    ): String = buildString {
        append("[CareSync SOS ALERT] ")
        if (patient != null) {
            append("Patient: ${patient.fullName} (${patient.age}y ${patient.gender}, BG ${patient.bloodGroup}). ")
            append("Allergies: ${patient.allergies.ifBlank { "None" }}. ")
            append("Ward: ${patient.villageOrWard}. ")
            if (latestVitals != null) {
                append("Vitals: BP ${latestVitals.systolicBp}/${latestVitals.diastolicBp}, SpO2 ${latestVitals.spo2Percent}%, HR ${latestVitals.heartRate} bpm, Temp ${latestVitals.temperatureC}C. ")
            }
        } else {
            append("Field emergency dispatch requested by $clinicianName. ")
        }
        if (coordinates != null) {
            append("GPS: https://maps.google.com/?q=${coordinates.latitude},${coordinates.longitude}")
        } else {
            append("Location: District Field Sector (GPS unavailable offline)")
        }
    }

    fun sendSmsFallback(context: Context, phone: String, message: String) {
        val cleanPhone = phone.filter { it.isDigit() || it == '+' }.ifBlank { "108" }
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$cleanPhone")
            putExtra("sms_body", message)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(intent) }
    }

    fun shareSosText(context: Context, message: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "CareSync Emergency SOS Alert")
            putExtra(Intent.EXTRA_TEXT, message)
        }
        runCatching {
            context.startActivity(Intent.createChooser(intent, "Share SOS Dispatch").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
}
