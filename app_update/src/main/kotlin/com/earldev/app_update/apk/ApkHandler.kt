package com.earldev.app_update.apk

import com.earldev.app_update.api.UpdateStateHolder
import com.earldev.app_update.api.models.CancelledInstallationException
import com.earldev.app_update.models.UpdateJob

internal abstract class ApkHandler(
    private val nextHandler: ApkHandler? = null
) {

    protected open fun canHandle(job: UpdateJob): Boolean =
        UpdateStateHolder.currentState()?.failure !is CancelledInstallationException

    protected open suspend fun handle(job: UpdateJob) {
        nextHandler ?: return

        if (nextHandler.canHandle(job)) {
            nextHandler.handle(job)
        }
    }

    open suspend fun retry() {}
}