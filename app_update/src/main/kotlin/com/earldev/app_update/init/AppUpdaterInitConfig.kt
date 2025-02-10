package com.earldev.app_update.init

data class AppUpdaterInitConfig(
    val downloadApkUrl: String,
    val updateAvailabilityCheckUrl: String,
    val actualVersionCode: Int,
    val actualVersionName: String,
    val logEnabled: Boolean = false
)
