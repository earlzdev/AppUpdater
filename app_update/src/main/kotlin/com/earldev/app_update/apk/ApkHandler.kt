package com.earldev.app_update.apk

import com.earldev.app_update.models.UpdateJob

internal abstract class ApkHandler(
    private val nextHandler: ApkHandler? = null
) {

    protected abstract fun canHandle(job: UpdateJob): Boolean

    protected open suspend fun handle(job: UpdateJob) {
        nextHandler ?: return

        if (nextHandler.canHandle(job)) {
            nextHandler.handle(job)
        }
    }
}