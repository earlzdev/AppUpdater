package com.earldev.app_update.init

/**
 * Configuration class for initializing the app update process.
 *
 * @param downloadApkUrl URL for downloading the APK file
 * @param updateAvailabilityCheckUrl URL for checking update availability
 * @param actualVersionCode the current versionCode of the app
 * @param actualVersionName the current versionName of the app
 * @param bearerToken optional authentication token
 * @param logEnabled flag to enable logging
 */
data class AppUpdaterInitConfig(
    val downloadApkUrl: String,
    val updateAvailabilityCheckUrl: String,
    val actualVersionCode: Int,
    val actualVersionName: String,
    val bearerToken: String? = null,
    val logEnabled: Boolean = false,
)
