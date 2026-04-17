package com.gshoaib998.progressly.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.gshoaib998.progressly.MainActivity
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

            ACTION_CANCEL -> {
                // Discard — no save needed
                trackingServiceManager.cancelAndStop {
                    shutdownService()
                }
            }

            ACTION_STOP_AND_SAVE -> {
                // Save happens inside saveAndStop(), THEN shutdownService() is called
                // Works whether app is alive or dead
                trackingServiceManager.saveAndStop {
                    shutdownService()
                }
            }
        }
        return START_STICKY
    }

    private fun startTracking(startTime: Long) {
        val notificationManager = getSystemService(NotificationManager::class.java)
        createNotificationChannel(notificationManager)

        val openAppIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val cancelPendingIntent = PendingIntent.getService(
            this, 1,
            Intent(this, TrackingForegroundService::class.java).apply {
                action = ACTION_CANCEL
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopSavePendingIntent = PendingIntent.getService(
            this, 2,
            Intent(this, TrackingForegroundService::class.java).apply {
                action = ACTION_STOP_AND_SAVE
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        startForeground(
            NOTIFICATION_ID,
            buildNotification(
                elapsed = "00:00:00",
                openAppIntent = openAppIntent,
                cancelIntent = cancelPendingIntent,
                stopSaveIntent = stopSavePendingIntent
            ).build()
        )

        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (true) {
                val elapsed = System.currentTimeMillis() - startTime
                trackingServiceManager.updateElapsed(elapsed)
                notificationManager.notify(
                    NOTIFICATION_ID,
                    buildNotification(
                        elapsed = formatElapsed(elapsed),
                        openAppIntent = openAppIntent,
                        cancelIntent = cancelPendingIntent,
                        stopSaveIntent = stopSavePendingIntent
                    ).build()
                )
                delay(1000L)
            }
        }
    }

    private fun shutdownService() {
        timerJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(
        elapsed: String,
        openAppIntent: PendingIntent,
        cancelIntent: PendingIntent,
        stopSaveIntent: PendingIntent
    ) = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_menu_recent_history)
        .setContentTitle("Tracking in progress")
        .setContentText("Elapsed: $elapsed")
        .setOngoing(true)
        .setOnlyAlertOnce(true)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setContentIntent(openAppIntent)
        .addAction(android.R.drawable.ic_delete, "Discard", cancelIntent)
        .addAction(android.R.drawable.ic_media_pause, "Save & Stop", stopSaveIntent)

    private fun createNotificationChannel(manager: NotificationManager) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Work tracking",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows elapsed time while tracking a work session"
            setShowBadge(false)
        }
        manager.createNotificationChannel(channel)
    }

    private fun formatElapsed(millis: Long): String {
        val totalSeconds = millis / 1000
        val h = totalSeconds / 3600
        val m = (totalSeconds % 3600) / 60
        val s = totalSeconds % 60
        return "%02d:%02d:%02d".format(h, m, s)
    }

    companion object {
        const val ACTION_START = "ACTION_START_TRACKING"
        const val ACTION_CANCEL = "ACTION_CANCEL_TRACKING"
        const val ACTION_STOP_AND_SAVE = "ACTION_STOP_AND_SAVE_TRACKING"
        const val EXTRA_START_TIME = "EXTRA_START_TIME"
        private const val NOTIFICATION_ID = 11
        private const val CHANNEL_ID = "tracking_channel"

        fun startIntent(context: Context, startTime: Long) =
            Intent(context, TrackingForegroundService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_START_TIME, startTime)
            }
    }
}