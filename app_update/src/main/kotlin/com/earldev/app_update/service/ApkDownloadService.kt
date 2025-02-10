package com.earldev.app_update.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.earldev.app_update.api.UpdateStateHolder
import com.earldev.app_update.UpdateStep
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
            updateCoroutineScopeHolder.coroutineScope().launch {
                try {
                    val notification = downloadNotification.notification(
                        context = applicationContext,
                        contentText = "Downloading apk",
                    )
                    startForeground(NOTIFICATION_ID, notification)

                    UpdateStateHolder.emit(UpdateStep.DownloadApk(started = true))
                    downloadUseCase.download()
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
                    UpdateStateHolder.emit(UpdateStep.DownloadApk(success = true))
                    stopSelf()
                }
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_START_DOWNLOAD = "ACTION_START_DOWNLOAD"

        private const val NOTIFICATION_ID = 1001
    }
}
