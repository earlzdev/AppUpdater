package com.earldev.app_update.di

import android.content.Context
import com.earldev.app_update.api.UpdateManager
import com.earldev.app_update.api.UpdateAvailabilityUseCase
import com.earldev.app_update.api.UpdaterApi
import com.earldev.app_update.datastore.PreferencesDataStore
import com.earldev.app_update.permission.InstallPermissionLauncher
import com.earldev.app_update.service.ApkDownloadService
import com.earldev.app_update.usecase.FirstRunAfterUpdateHandler
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppUpdaterModule::class])
internal interface AppUpdaterComponent : UpdaterApi {

    override fun updateAvailabilityUseCase(): UpdateAvailabilityUseCase

    override fun appUpdate(): UpdateManager

    fun inject(downloadService: ApkDownloadService)

    fun firstRunAfterUpdateChecker(): FirstRunAfterUpdateHandler

    fun preferencesDataStore(): PreferencesDataStore

    fun installPermissionLauncher(): InstallPermissionLauncher

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppUpdaterComponent
    }
}