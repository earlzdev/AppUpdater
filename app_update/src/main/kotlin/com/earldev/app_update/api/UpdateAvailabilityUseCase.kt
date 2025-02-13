package com.earldev.app_update.api

/**
 * Interface for checking the availability of an update.
 */
interface UpdateAvailabilityUseCase {

    /**
     * Checks for an available update.
     *
     * @return [Result] with true if an update is available.
     */
    suspend fun updateAvailable(): Result<Boolean>
}
