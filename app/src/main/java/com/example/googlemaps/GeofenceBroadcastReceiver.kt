package com.example.googlemaps

//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.util.Log
//import com.google.android.gms.location.Geofence
//import com.google.android.gms.location.GeofencingEvent
//
//class GeofenceBroadcastReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context?, intent: Intent?) {
//        if (intent != null) {
//            val geofencingEvent = GeofencingEvent.fromIntent(intent)
//            if (geofencingEvent?.hasError() == true) {
//                Log.e(TAG, "GeofencingEvent error: ${geofencingEvent.errorCode}")
//                return
//            }
//
//            val geofenceTransition = geofencingEvent?.geofenceTransition
//            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
//                // Handle geofence transition event here
//                Log.d(TAG, "Geofence transition: $geofenceTransition")
//            } else {
//                Log.e(TAG, "Invalid geofence transition type: $geofenceTransition")
//            }
//        }
//    }
//
//    companion object {
//        private const val TAG = "GeofenceBroadcastReceiver"
//    }
//}

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.androidbrowserhelper.trusted.NotificationUtils

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            val geofenceTransition = GeofencingEvent.fromIntent(intent)?.geofenceTransition
            val geofenceId = intent.getStringExtra("geofence_id")

            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                NotificationUtils.createNotificationChannel(context, "Entered geofence")
            } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                NotificationUtils.createNotificationChannel(context, "Exited geofence")
            }
        }
    }
}




