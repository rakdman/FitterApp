package com.example.fittr.broadcastreciever
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import com.example.fittr.fragments.BikeFragment
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import java.lang.Exception


private const val DETECTED_PENDING_INTENT_REQUEST_CODE = 100
private const val RELIABLE_CONFIDENCE = 0

private const val DETECTED_ACTIVITY_CHANNEL_ID = "detected_activity_channel_id"
const val DETECTED_ACTIVITY_NOTIFICATION_ID = 10

class DetectionsReciever :  BroadcastReceiver() {

    companion object {

        fun getPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, DetectionsReciever::class.java)
            return PendingIntent.getBroadcast(context, DETECTED_PENDING_INTENT_REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }


    override fun onReceive(context: Context, intent: Intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val selectedActivity = preferences.getInt("Activity",-10)
            result?.let { handleDetectedActivities(it.probableActivities, context,selectedActivity) }
        }
    }

    fun toActivityString(activity: Int): String {
        return when (activity) {
            DetectedActivity.STILL -> "STILL"
            DetectedActivity.WALKING -> "WALKING"
            DetectedActivity.IN_VEHICLE -> "IN VEHICLE"
            DetectedActivity.RUNNING -> "RUNNING"
            DetectedActivity.TILTING -> "TILTING"
            DetectedActivity.ON_FOOT -> "ON_FOOT"
            DetectedActivity.UNKNOWN -> "UNKNOWN"
            DetectedActivity.ON_BICYCLE -> "ON_BICYCLE"
            else -> "UNKNOWN"
        }
    }



    private fun handleDetectedActivities(detectedActivities: List<DetectedActivity>,
                                         context: Context, activitySelected:Int) {

        detectedActivities.filter {
                    it.type == activitySelected
            }
            .filter { it.confidence > RELIABLE_CONFIDENCE }
            .run {
                if (isNotEmpty()) {
                    try
                    {
                        BikeFragment.getInstance()?.
                        changeState(
                            toActivityString(this[0].type)
                            , this[0].confidence.toString())
                    }
                    catch (e: Exception)
                    {

                    }
                }
            }

    }

}