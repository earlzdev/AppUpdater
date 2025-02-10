package com.earldev.app_update.apk

import com.earldev.app_update.api.UpdateStateHolder
import com.earldev.app_update.UpdateStep
import com.earldev.app_update.api.UpdateAvailabilityUseCase
import com.earldev.app_update.api.models.NoNeedToUpdateException
import com.earldev.app_update.datastore.SelfUpdateStore
import com.earldev.app_update.models.UpdateJob
import javax.inject.Inject

internal class AvailableApkChecker @Inject constructor(
    nextHandler: ApkHandler,
    private val updateAvailabilityUseCase: UpdateAvailabilityUseCase,
) : ApkHandler(nextHandler) {

    override fun canHandle(job: UpdateJob): Boolean = true

    override suspend fun handle(job: UpdateJob) {
        UpdateStateHolder.emit(UpdateStep.UpdateAvailabilityCheck(started = true))
        if (SelfUpdateStore.updateAvailable() == true) {
            super.handle(job.copy(needUpdate = true))
        } else {
            updateAvailabilityUseCase.updateAvailable().onSuccess { newApkAvailable ->
                if (newApkAvailable) {
                    UpdateStateHolder.emit(UpdateStep.UpdateAvailabilityCheck(success = true))
                    super.handle(job.copy(needUpdate = true))
                } else {
                    UpdateStateHolder.emit(
                        UpdateStep.UpdateAvailabilityCheck(
                            success = false,
                            failure = NoNeedToUpdateException()
                        )
                    )
                }
            }.onFailure {
                UpdateStateHolder.emit(
                    UpdateStep.UpdateAvailabilityCheck(
                        success = false,
                        failure = Exception(it)
                    )
                )
            }
        }
    }

    suspend fun start() {
        handle(UpdateJob())
    }
}