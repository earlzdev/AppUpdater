package com.earldev.app_update.apk

import com.earldev.app_update.UpdateStep
import com.earldev.app_update.api.UpdateAvailabilityUseCase
import com.earldev.app_update.api.UpdateStateHolder
import com.earldev.app_update.api.models.NoNeedToUpdateException
import com.earldev.app_update.datastore.SelfUpdateStore
import com.earldev.app_update.models.UpdateJob
import com.earldev.app_update.utils.SelfUpdateLog
import javax.inject.Inject

internal class AvailableApkChecker @Inject constructor(
    nextHandler: ApkHandler,
    private val updateAvailabilityUseCase: UpdateAvailabilityUseCase,
) : ApkHandler(nextHandler) {

    private var job: UpdateJob? = null

    override suspend fun handle(job: UpdateJob) {
        this.job = job
        SelfUpdateLog.logInfo("Check available apk started")
        UpdateStateHolder.emit(UpdateStep.UpdateAvailabilityCheck(started = true))
        if (SelfUpdateStore.updateAvailable() == true) {
            super.handle(job.copy(needUpdate = true))
        } else {
            updateAvailabilityUseCase.updateAvailable().onSuccess { newApkAvailable ->
                if (newApkAvailable) {
                    SelfUpdateLog.logInfo("Found available apk")
                    UpdateStateHolder.emit(UpdateStep.UpdateAvailabilityCheck(success = true))
                    super.handle(job.copy(needUpdate = true))
                } else {
                    SelfUpdateLog.logInfo("No available apk")
                    UpdateStateHolder.emit(
                        UpdateStep.UpdateAvailabilityCheck(
                            success = false,
                            failure = NoNeedToUpdateException()
                        )
                    )
                }
            }.onFailure {
                SelfUpdateLog.logInfo("Failed to fetch available apk info")
                UpdateStateHolder.emit(
                    UpdateStep.UpdateAvailabilityCheck(
                        success = false,
                        failure = it as Exception
                    )
                )
            }
        }
    }

    override suspend fun retry() {
        job?.let {
            handle(it)
        } ?: throw IllegalStateException("Cannot retry check availability step, because job is null")
    }

    suspend fun start() {
        handle(UpdateJob())
    }
}