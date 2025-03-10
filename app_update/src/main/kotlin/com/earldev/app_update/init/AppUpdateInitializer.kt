package com.earldev.app_update.init

import androidx.activity.ComponentActivity
import com.earldev.app_update.datastore.SelfUpdateStore
import com.earldev.app_update.di.AppUpdaterComponentHolder
import com.earldev.app_update.utils.SelfUpdateLog

/**
 * Object for initializing the library.
 */
object AppUpdateInitializer {

    /**
     * Initializes the library. Should be called during the creation of the Activity.
     *
     * @param activity the reference to the activity
     * @param config a configuration class containing the information required for the library to function
     */
    fun initialize(activity: ComponentActivity, config: AppUpdaterInitConfig) {
        AppUpdaterComponentHolder.init(activity)

        val datastore = AppUpdaterComponentHolder.component().preferencesDataStore()
        datastore.saveApkDownloadUrl(config.downloadApkUrl)
        datastore.saveUpdateAvailabilityCheckUrl(config.updateAvailabilityCheckUrl)
        datastore.saveActualVersionCode(config.actualVersionCode)
        datastore.saveActualVersionName(config.actualVersionName)

        AppUpdaterComponentHolder.component().installPermissionLauncher().init(activity)
        AppUpdaterComponentHolder.component().firstRunAfterUpdateChecker().handleFirstStartIfNeeded()

        SelfUpdateStore.updateAvailable()

        SelfUpdateLog.init(config.logEnabled)
    }
}
