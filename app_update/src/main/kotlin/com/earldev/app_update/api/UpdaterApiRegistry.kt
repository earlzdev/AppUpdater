package com.earldev.app_update.api

import com.earldev.app_update.di.AppUpdaterComponentHolder

object UpdaterApiRegistry {

    private val updaterApi: UpdaterApi = AppUpdaterComponentHolder.updaterApi()

    val updateAvailabilityUseCase: UpdateAvailabilityUseCase
        get() = updaterApi.updateAvailabilityUseCase()

    val updateManager: UpdateManager
        get() = updaterApi.appUpdate()
}