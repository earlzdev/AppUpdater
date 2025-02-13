package com.earldev.app_update.apk

import android.content.Context
import android.content.Intent
import com.earldev.app_update.UpdateStep
import com.earldev.app_update.api.UpdateStateHolder
import com.earldev.app_update.models.UpdateJob
import com.earldev.app_update.service.ApkDownloadService
import com.earldev.app_update.utils.SelfUpdateLog
import javax.inject.Inject

/**
 * Responsible for downloading the APK file from the server.
 *
 * @param nextHandler the next handler in the chain
 * @property context the [Context] of the application
 */
internal class ApkDownloader @Inject constructor(
    nextHandler: ApkHandler,
    private val context: Context,
) : ApkHandler(nextHandler) {

    private var job: UpdateJob? = null

    override fun canHandle(job: UpdateJob): Boolean = job.needUpdate && super.canHandle(job)

    override suspend fun handle(job: UpdateJob) {
        this.job = job
        startLoadingService()

        UpdateStateHolder.currentStateFlow().collect {
            if (it is UpdateStep.DownloadApk && it.success) {
                SelfUpdateLog.logInfo("Successfully loaded APK file")
                super.handle(job.copy(downloaded = true))
            }
        }
    }

    override suspend fun retry() {
        SelfUpdateLog.logInfo("Retrying from download APK step")
        job?.let {
            handle(it)
        } ?: throw IllegalStateException("Cannot retry download APK step, because job is null")
    }

    private fun startLoadingService() {
        val intent = Intent(context, ApkDownloadService::class.java).apply {
            action = ApkDownloadService.ACTION_START_DOWNLOAD
        }
        context.startForegroundService(intent)
    }
}
