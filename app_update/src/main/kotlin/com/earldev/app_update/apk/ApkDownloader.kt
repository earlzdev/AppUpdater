package com.earldev.app_update.apk

import android.content.Context
import android.content.Intent
import com.earldev.app_update.api.UpdateStateHolder
import com.earldev.app_update.UpdateStep
import com.earldev.app_update.models.UpdateJob
import com.earldev.app_update.service.ApkDownloadService
import javax.inject.Inject

internal class ApkDownloader @Inject constructor(
    nextHandler: ApkHandler,
    private val context: Context,
) : ApkHandler(nextHandler) {

    override fun canHandle(job: UpdateJob): Boolean = job.needUpdate

    override suspend fun handle(job: UpdateJob) {
        startLoadingService()

        UpdateStateHolder.currentState().collect { step: UpdateStep? ->
            when {
                step is UpdateStep.DownloadApk && step.success -> {
                    super.handle(job.copy(downloaded = true))
                }
                step is UpdateStep.DownloadApk && step.failure != null -> {
                    UpdateStateHolder.emit(
                        UpdateStep.Finish(
                            success = false,
                            failure = step.failure
                        )
                    )
                }
            }
        }
    }

    private fun startLoadingService() {
        val intent = Intent(context, ApkDownloadService::class.java).apply {
            action = ApkDownloadService.ACTION_START_DOWNLOAD
        }
        context.startForegroundService(intent)
    }
}