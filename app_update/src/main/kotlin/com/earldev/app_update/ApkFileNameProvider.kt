package com.earldev.app_update

import android.content.Context
import javax.inject.Inject

/**
 * Provides the name for the APK file.
 *
 * @property context [Context] of the application.
 */
internal class ApkFileNameProvider @Inject constructor(
    private val context: Context
) {

    /**
     * Returns the name for the APK file.
     */
    fun provide(): String = context.packageName + "_new_version"
}
