package com.earldev.app_update.di

import android.content.Context
import com.earldev.app_update.ApkFileNameProvider
import com.earldev.app_update.UpdateManagerImpl
import com.earldev.app_update.AppVersionsComparator
import com.earldev.app_update.AppVersionsComparatorImpl
import com.earldev.app_update.api.UpdateManager
import com.earldev.app_update.api.UpdateAvailabilityUseCase
import com.earldev.app_update.datastore.PreferencesDataStore
import com.earldev.app_update.permission.InstallPermissionLauncher
import com.earldev.app_update.usecase.DownloadApkUseCase
import com.earldev.app_update.usecase.DownloadApkUseCaseImpl
import com.earldev.app_update.usecase.FirstRunAfterUpdateHandler
import com.earldev.app_update.usecase.SaveAndDeleteApkUseCase
import com.earldev.app_update.usecase.SaveAndDeleteApkUseCaseImpl
import com.earldev.app_update.usecase.UpdateAvailabilityUseCaseImpl
import com.earldev.app_update.utils.CoroutineDispatchers
import com.earldev.app_update.utils.CoroutineDispatchersImpl
import com.earldev.app_update.utils.HttpClientProvider
import com.earldev.app_update.utils.UpdateCoroutineScopeHolder
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
internal interface AppUpdaterModule {

    @Binds
    fun bindUpdateAvailabilityUseCase(impl: UpdateAvailabilityUseCaseImpl): UpdateAvailabilityUseCase

    @Binds
    @Singleton
    fun bindCoroutineDispatchers(impl: CoroutineDispatchersImpl): CoroutineDispatchers

    @Binds
    fun bindAppVersionsComparator(impl: AppVersionsComparatorImpl): AppVersionsComparator

    @Binds
    fun bindDownloadApkUseCase(impl: DownloadApkUseCaseImpl): DownloadApkUseCase

    @Binds
    fun bindAppUpdater(impl: UpdateManagerImpl): UpdateManager

    companion object {

        @Provides
        @Singleton
        fun providePreferencesDataStore(context: Context): PreferencesDataStore = PreferencesDataStore(context)

        @Provides
        @Singleton
        fun provideHttpClientProvider(): HttpClientProvider = HttpClientProvider()

        @Provides
        fun provideSaveAndDeleteUseCase(
            context: Context,
            apkFileNameProvider: ApkFileNameProvider
        ): SaveAndDeleteApkUseCase =
            SaveAndDeleteApkUseCaseImpl(
                context = context,
                apkFileNameProvider = apkFileNameProvider
            )

        @Singleton
        @Provides
        fun provideUpdaterCoroutineScope(
            dispatchers: CoroutineDispatchers
        ): CoroutineScope = CoroutineScope(dispatchers.io)

        @Provides
        @Singleton
        fun provideInstallPermissionLauncher(): InstallPermissionLauncher = InstallPermissionLauncher()

        @Provides
        fun provideFirstStartChecker(
            dataStore: PreferencesDataStore,
            saveAndDeleteApkUseCase: SaveAndDeleteApkUseCase
        ): FirstRunAfterUpdateHandler = FirstRunAfterUpdateHandler(
            dataStore = dataStore,
            saveAndDeleteApkUseCase = saveAndDeleteApkUseCase
        )

        @Provides
        @Singleton
        fun provideUpdateCoroutineScopeHandler(
            dispatchers: CoroutineDispatchers
        ): UpdateCoroutineScopeHolder = UpdateCoroutineScopeHolder(
            coroutineDispatchers = dispatchers
        )
    }
}