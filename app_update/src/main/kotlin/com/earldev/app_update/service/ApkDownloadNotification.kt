package com.earldev.app_update.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import androidx.core.app.NotificationCompat

/**
 * Manages notifications during the APK file download process.
 *
 * @param contextWrapper A context wrapper used to obtain the [NotificationManager].
 */
internal class ApkDownloadNotification(
    private val contextWrapper: ContextWrapper,
) {

    /**
     * Creates a notification for the APK download process.
     *
     * @param context The application context.
     * @param contentText The text displayed in the notification.
     * @return An instance of [Notification].
     */
    fun notification(context: Context, contentText: String): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Downloading new version")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    /**
     * Creates a notification channel for APK downloads if it does not already exist.
     */
    fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "APK Download",
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = contextWrapper.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        /** Notification channel ID. */
        private const val CHANNEL_ID = "apk_download_channel"
    }
}
