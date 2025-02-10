package com.earldev.app_update.models

import kotlinx.serialization.Serializable

@Serializable
internal data class AppVersionInfo(
    val versionCode: Int,
    val versionName: String,
    val checksum: String? = null
)
