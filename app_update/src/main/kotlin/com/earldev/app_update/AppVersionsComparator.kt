package com.earldev.app_update

import com.earldev.app_update.models.AppVersionInfo
import com.earldev.app_update.utils.SelfUpdateLog
import javax.inject.Inject

internal interface AppVersionsComparator {

    fun needUpdate(currentVersion: AppVersionInfo, remoteVersion: AppVersionInfo): Boolean
}

internal class AppVersionsComparatorImpl @Inject constructor(): AppVersionsComparator {

    override fun needUpdate(currentVersion: AppVersionInfo, remoteVersion: AppVersionInfo): Boolean {

        SelfUpdateLog.logInfo("Compare versions:\nActual version -> $currentVersion\nRemote version -> $remoteVersion")

        return remoteVersion.versionCode > currentVersion.versionCode ||
                (remoteVersion.versionName.toFloatOrNull() ?: 0f) > (currentVersion.versionName.toFloatOrNull() ?: 0f)
    }
}

