package com.earldev.app_update.apk

import android.content.Context
import android.content.Intent
import com.earldev.app_update.UpdateStep
import com.earldev.app_update.api.UpdateStateHolder
import com.earldev.app_update.models.UpdateJob
import com.earldev.app_update.service.ApkDownloadService
import com.earldev.app_update.utils.SelfUpdateLog
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

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
                SelfUpdateLog.logInfo("Successfully loaded apk file")
                super.handle(job.copy(downloaded = true))
            }
        }
    }

    private fun startLoadingService() {
        val intent = Intent(context, ApkDownloadService::class.java).apply {
            action = ApkDownloadService.ACTION_START_DOWNLOAD
        }
        context.startForegroundService(intent)
    }

    override suspend fun retry() {
        SelfUpdateLog.logInfo("Retrying from download apk step")
        job?.let {
            handle(it)
        } ?: throw IllegalStateException("Cannot retry download apk step, because job is null")
    }
}