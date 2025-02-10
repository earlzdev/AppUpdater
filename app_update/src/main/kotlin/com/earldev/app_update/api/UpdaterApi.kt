package com.earldev.app_update.api

interface UpdaterApi {

    fun updateAvailabilityUseCase(): UpdateAvailabilityUseCase

    fun appUpdate(): UpdateManager
}
