package com.earldev.app_update.api

interface UpdateAvailabilityUseCase {

    suspend fun updateAvailable(): Result<Boolean>
}
