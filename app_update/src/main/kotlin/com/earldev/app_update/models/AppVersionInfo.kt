package com.earldev.app_update.models

import kotlinx.serialization.Serializable

/**
 * Contains information about the application version. Used for network requests.
 *
 * @property versionCode the version code
 * @property versionName the version name
 * @property checksum the checksum (optional)
 */
@Serializable
internal data class AppVersionInfo(
    val versionCode: Int,
    val versionName: String,
    val checksum: String? = null
)
