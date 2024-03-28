package com.example.googlemaps

import com.google.android.gms.maps.model.LatLng
import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class GeofencingManager(private val context: Context) {
    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)

    fun addGeofence(latLng: LatLng) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val requestId = "geofence_${latLng.latitude}_${latLng.longitude}"
            val geofence = Geofence.Builder()
                .setRequestId(requestId)
                .setCircularRegion(latLng.latitude, latLng.longitude, GEOFENCE_RADIUS)
                .setExpirationDuration(GEOFENCE_EXPIRATION_DURATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()

            val geofencingRequest = GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build()

            val geofencePendingIntent = getGeofencePendingIntent(context)

            geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
                addOnSuccessListener {
                    // Geofence added successfully
                }
                addOnFailureListener {
                    // Failed to add geofence
                }
            }
        }else {
            Log.e("Geofence", "Location permission not granted")
        }
    }

    private fun getGeofencePendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        intent.action = "HuntMainActivity.treasureHunt.action.ACTION_GEOFENCE_EVENT"
        return PendingIntent.getBroadcast(
            context,
            GEOFENCE_PENDING_INTENT_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    companion object {
        private const val GEOFENCE_RADIUS = 50f // in meters
        private const val GEOFENCE_EXPIRATION_DURATION: Long = 24 * 60 * 60 * 1000 // 24 hours in milliseconds
        private const val GEOFENCE_PENDING_INTENT_ID = 0
    }
}


