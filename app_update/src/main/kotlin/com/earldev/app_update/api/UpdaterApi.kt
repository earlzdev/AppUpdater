package com.earldev.app_update.api

/**
 * Interface for providing objects that manage the update process.
 */
interface UpdaterApi {

    /**
     * Returns an instance of [UpdateAvailabilityUseCase].
     */
    fun updateAvailabilityUseCase(): UpdateAvailabilityUseCase

    /**
     * Returns an [UpdateManager].
     */
    fun appUpdate(): UpdateManager
}
