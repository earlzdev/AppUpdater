package com.earldev.app_update

import com.earldev.app_update.models.AppVersionInfo
import com.earldev.app_update.utils.SelfUpdateLog
import javax.inject.Inject

/**
 * Interface for comparing app versions.
 *
 */
internal interface AppVersionsComparator {

    /**
     * Determines if the app needs to be updated.
     *
     * @param currentVersion the current version of the app
     * @param remoteVersion the version of the app available on the server
     * @return a flag indicating whether the app needs to be updated
     */
    fun needUpdate(currentVersion: AppVersionInfo, remoteVersion: AppVersionInfo): Boolean
}

/**
 * Implementation of [AppVersionsComparator].
 *
 */
internal class AppVersionsComparatorImpl @Inject constructor(): AppVersionsComparator {

    override fun needUpdate(currentVersion: AppVersionInfo, remoteVersion: AppVersionInfo): Boolean {

        SelfUpdateLog.logInfo("Compare versions:\nActual version -> $currentVersion\nRemote version -> $remoteVersion")

        return remoteVersion.versionCode > currentVersion.versionCode ||
                (remoteVersion.versionName.toFloatOrNull() ?: 0f) > (currentVersion.versionName.toFloatOrNull() ?: 0f)
    }
}
