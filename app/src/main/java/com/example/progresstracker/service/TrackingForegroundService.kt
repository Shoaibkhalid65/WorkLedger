package com.example.progresstracker.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.progresstracker.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrackingForegroundService : Service() {

    @Inject
    lateinit var trackingServiceManager: TrackingServiceManager

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var timerJob: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val startTime = intent.getLongExtra(EXTRA_START_TIME, System.currentTimeMillis())
                startTracking(startTime)
            }
            ACTION_STOP -> stopTracking()
        }
        return START_STICKY

    }

    private fun startTracking(startTime: Long) {
        val notificationManager = getSystemService(NotificationManager::class.java)
        createNotificationChannel(notificationManager)

        val pendingIntent= PendingIntent.getActivity(
            this,0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = Intent(this, TrackingForegroundService::class.java).apply {
            action=ACTION_STOP
        }

        val stopPendingIntent = PendingIntent.getService(
            this,1,stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        startForeground(
            NOTIFICATION_ID,
            buildNotification(
                context=this,
                elapsed = "00:00:00",
                contentIntent = pendingIntent,
                stopIntent = stopPendingIntent
            ).build()
        )

        // tick every second and update the notification
        timerJob=serviceScope.launch {
             while (true){
                 val elapsed = System.currentTimeMillis() - startTime
                 val formated = formatElapsed(elapsed)

                 notificationManager.notify(
                     NOTIFICATION_ID,
                     buildNotification(
                         this@TrackingForegroundService,
                         elapsed=formated,
                         contentIntent = pendingIntent,
                         stopIntent = stopPendingIntent
                     ).build()
                 )

                 trackingServiceManager.updateElapsed(elapsed)

                 delay(1000)
             }
        }

    }

    private fun stopTracking(){
        timerJob?.cancel()
        trackingServiceManager.updateElapsed(0L)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(p0: Intent?): IBinder? = null

    // helpers
    private fun buildNotification(
        context: Context,
        elapsed: String,
        contentIntent: PendingIntent,
        stopIntent: PendingIntent
    ) = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_menu_recent_history)
        .setContentTitle("Tracking in progress")
        .setContentText("Elapsed: $elapsed")
        .setOngoing(true)
        .setOnlyAlertOnce(true)           // silent updates — no repeated sound
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setContentIntent(contentIntent)
        .addAction(
            android.R.drawable.ic_media_pause,
            "Stop",
            stopIntent
        )

    private fun createNotificationChannel(manager: NotificationManager) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Work tracking",
            NotificationManager.IMPORTANCE_LOW    // LOW = no sound, still visible
        ).apply {
            description = "Shows elapsed time while tracking a work session"
            setShowBadge(false)
        }
        manager.createNotificationChannel(channel)
    }

    private fun formatElapsed(millis: Long): String {
        val totalSeconds = millis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d:%02d".format(hours, minutes, seconds)
    }

    companion object {
        const val ACTION_START = "ACTION_START_TRACKING"
        const val ACTION_STOP = "ACTION_STOP_TRACKING"
        const val EXTRA_START_TIME = "EXTRA_START_TIME"
        private const val NOTIFICATION_ID = 11
        private const val CHANNEL_ID = "tracking_channel"

        fun startIntent(context: Context, startTime: Long): Intent =
            Intent(context, TrackingForegroundService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_START_TIME, startTime)
            }

        fun stopIntent(context: Context): Intent =
            Intent(context, TrackingForegroundService::class.java).apply {
                action = ACTION_STOP
            }

    }
}