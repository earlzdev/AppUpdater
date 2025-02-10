package com.earldev.app_update.utils

import android.util.Log

internal object SelfUpdateLog {

    private const val TAG = "SelfUpdate"
    private var logsEnabled: Boolean = false

    fun init(logsEnabled: Boolean) {
        SelfUpdateLog.logsEnabled = logsEnabled
    }

    fun logInfo(msg: String) {
        if (logsEnabled) {
            Log.i(TAG, msg)
        }
    }

    fun logError(msg: String) {
        if (logsEnabled) {
            Log.e(TAG, msg)
        }
    }
}