package com.earldev.app_update.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.earldev.app_update.UpdateStep
import com.earldev.app_update.api.UpdateStateHolder
import com.earldev.app_update.api.models.UnauthorizedException
import com.earldev.app_update.di.AppUpdaterComponentHolder
import com.earldev.app_update.usecase.DownloadApkUseCase
import com.earldev.app_update.utils.CoroutineDispatchers
import com.earldev.app_update.utils.SelfUpdateLog
import com.earldev.app_update.utils.UpdateCoroutineScopeHolder
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

/**
 * A service for downloading APK files in the background.
 * Uses [startForeground] to display a notification during the download process.
 *
 * @constructor Creates an instance of the service.
 */
internal class ApkDownloadService : Service() {

    @Inject
    lateinit var coroutineDispatchers: CoroutineDispatchers

    @Inject
    lateinit var downloadUseCase: DownloadApkUseCase

    @Inject
    lateinit var updateCoroutineScopeHolder: UpdateCoroutineScopeHolder

    private val downloadNotification = ApkDownloadNotification(contextWrapper = this)

    /**
     * Called when the service is created. Initializes dependencies and creates the notification channel.
     */
    override fun onCreate() {
        AppUpdaterComponentHolder.component().inject(this)
        super.onCreate()

        downloadNotification.createNotificationChannel()
        SelfUpdateLog.logInfo("ApkDownloadService created")
    }

    /**
     * Starts the service and handles incoming actions.
     *
     * @param intent The intent containing the action for the service.
     * @param flags Additional flags.
     * @param startId Unique start identifier.
     * @return The service running mode after completion (`START_NOT_STICKY`).
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        SelfUpdateLog.logInfo("ApkDownloadService started")
        val action = intent?.action

        if (action == ACTION_START_DOWNLOAD) {
            val notification = downloadNotification.notification(
                context = applicationContext,
                contentText = "Downloading APK",
            )
            startForeground(NOTIFICATION_ID, notification)
            startDownload()
        }

        return START_NOT_STICKY
    }

    /**
     * Binding to this service is not supported, so it always returns `null`.
     *
     * @param intent The intent requesting binding.
     * @return `null`.
     */
    override fun onBind(intent: Intent?): IBinder? = null

    /**
     * Starts the APK download process in a coroutine.
     * Handles possible errors and updates the download status in [UpdateStateHolder].
     */
    private fun startDownload() {
        updateCoroutineScopeHolder.coroutineScope().launch {
            try {
                UpdateStateHolder.emit(UpdateStep.DownloadApk(started = true))
                val downloadSuccessful = downloadUseCase.download()
                UpdateStateHolder.emit(UpdateStep.DownloadApk(success = downloadSuccessful))
            } catch (e: UnauthorizedException) {
                e.printStackTrace()
                UpdateStateHolder.emit(UpdateStep.DownloadApk(failure = e))
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                UpdateStateHolder.emit(UpdateStep.DownloadApk(failure = e))
            } catch (e: IllegalStateException) {
                e.printStackTrace()
                UpdateStateHolder.emit(UpdateStep.DownloadApk(failure = e))
            } catch (e: IOException) {
                e.printStackTrace()
                UpdateStateHolder.emit(UpdateStep.DownloadApk(failure = e))
            } finally {
                SelfUpdateLog.logInfo("Download service stopped")
                stopSelf()
            }
        }
    }

    companion object {
        /** Action to start the APK download process. */
        const val ACTION_START_DOWNLOAD = "ACTION_START_DOWNLOAD"

        /** Notification ID for the Foreground service. */
        private const val NOTIFICATION_ID = 1001
    }
}
