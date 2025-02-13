package com.earldev.app_update.api

import com.earldev.app_update.di.AppUpdaterComponentHolder

/**
 * Stores instances of objects provided by [UpdaterApi].
 */
object UpdaterApiRegistry {

    private val updaterApi: UpdaterApi = AppUpdaterComponentHolder.updaterApi()

    /**
     * Instance of [UpdateAvailabilityUseCase]
     */
    val updateAvailabilityUseCase: UpdateAvailabilityUseCase
        get() = updaterApi.updateAvailabilityUseCase()

    /**
     * Instance of [UpdateManager]
     */
    val updateManager: UpdateManager
        get() = updaterApi.appUpdate()
}