package com.earldev.app_update.apk

import com.earldev.app_update.api.UpdateStateHolder
import com.earldev.app_update.api.models.CancelledInstallationException
import com.earldev.app_update.models.UpdateJob

/**
 * Abstract handler for a specific step in the app update process. Subclasses implement the
 * "Chain of Responsibility" pattern.
 *
 * @property nextHandler reference to the next handler
 */
internal abstract class ApkHandler(
    private val nextHandler: ApkHandler? = null
) {

    /**
     * Returns a flag indicating whether the current [UpdateJob] can be processed.
     *
     * @param job contains information about the update steps
     */
    protected open fun canHandle(job: UpdateJob): Boolean =
        UpdateStateHolder.currentState()?.failure !is CancelledInstallationException

    /**
     * Handles a specific update step.
     *
     * @param job contains information about the update steps
     */
    protected open suspend fun handle(job: UpdateJob) {
        nextHandler ?: return

        if (nextHandler.canHandle(job)) {
            nextHandler.handle(job)
        }
    }

    /**
     * Retries the current step.
     *
     */
    open suspend fun retry() = Unit
}
