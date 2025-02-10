package com.earldev.app_update.di

import android.content.Context
import android.util.Log
import com.earldev.app_update.api.UpdaterApi

internal object AppUpdaterComponentHolder {

    private var component: AppUpdaterComponent? = null

    fun init(context: Context)  {
        Log.d("TAg1", "init: ")
        component = null
        component = DaggerAppUpdaterComponent.builder()
            .context(context)
            .build()
    }

    fun component(): AppUpdaterComponent = requireNotNull(component) {
        "AppUpdater was not initialized!"
    }

    fun updaterApi(): UpdaterApi = requireNotNull(component) {
        "UpdaterApi was not initialized!"
    }

    fun clear() {
        component = null
    }
}