package com.earldev.app_update

import android.content.Context
import javax.inject.Inject

internal class ApkFileNameProvider @Inject constructor(
    private val context: Context
) {

    fun provide(): String = context.packageName + "_new_version"
}