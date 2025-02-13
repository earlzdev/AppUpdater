package com.earldev.app_update.models

/**
 * Represents the state of the update process. Used in [ApkHandler].
 *
 * @property needUpdate indicates whether an update is available
 * @property downloaded indicates whether the APK file has been successfully downloaded
 * @property secured indicates whether the APK file is verified as secure
 */
internal data class UpdateJob(
    val needUpdate: Boolean = false,
    val downloaded: Boolean = false,
    val secured: Boolean = false
)