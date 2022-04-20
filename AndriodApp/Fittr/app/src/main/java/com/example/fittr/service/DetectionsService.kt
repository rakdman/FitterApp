package com.example.fittr.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.example.fittr.broadcastreciever.DetectionsReciever
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient

const val ACTIVITY_UPDATES_INTERVAL = 5000L

class DetectionsService : Service() {

    inner class LocalBinder : Binder() {

        val serverInstance: DetectionsService
            get() = this@DetectionsService
    }

    override fun onBind(p0: Intent?): IBinder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        requestActivityUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeActivityUpdates()
    }

    private fun requestActivityUpdates() {
        val task = ActivityRecognition.getClient(this).requestActivityUpdates(
            ACTIVITY_UPDATES_INTERVAL,
            DetectionsReciever.getPendingIntent(this)
        )

        task.run {
            addOnSuccessListener {
                Log.d("Activity Start", "Success")
            }
            addOnFailureListener {
                Log.d("Activity", "Failure")
            }
        }
    }

    private fun removeActivityUpdates() {
        val task = ActivityRecognitionClient(this).removeActivityUpdates(
            DetectionsReciever.getPendingIntent(this)
        )

        task.run {
            addOnSuccessListener {
                Log.d("Activity Destroy","Success")
            }
            addOnFailureListener {
                Log.d("Activity Destroy", "Failure")
            }
        }
    }
}


