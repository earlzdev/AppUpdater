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

internal class ApkDownloadService : Service() {

    @Inject
    lateinit var coroutineDispatchers: CoroutineDispatchers

    @Inject
    lateinit var downloadUseCase: DownloadApkUseCase

    @Inject
    lateinit var updateCoroutineScopeHolder: UpdateCoroutineScopeHolder

    private val downloadNotification = ApkDownloadNotification(contextWrapper = this)

    override fun onCreate() {
        AppUpdaterComponentHolder.component().inject(this)
        super.onCreate()

        downloadNotification.createNotificationChannel()
        SelfUpdateLog.logInfo("ApkDownloadService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        SelfUpdateLog.logInfo("ApkDownloadService started")
        val action = intent?.action

        if (action == ACTION_START_DOWNLOAD) {
            val notification = downloadNotification.notification(
                context = applicationContext,
                contentText = "Downloading apk",
            )
            startForeground(NOTIFICATION_ID, notification)
            startDownload()
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

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
                SelfUpdateLog.logInfo("Download service stop")
                stopSelf()
            }
        }
    }

    companion object {
        const val ACTION_START_DOWNLOAD = "ACTION_START_DOWNLOAD"

        private const val NOTIFICATION_ID = 1001
    }
}
