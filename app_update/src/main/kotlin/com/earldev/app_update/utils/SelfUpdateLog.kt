package com.earldev.app_update.utils

import android.util.Log

/**
 * Object for logging the update process.
 */
internal object SelfUpdateLog {

    private const val TAG = "SelfUpdate"
    private var logsEnabled: Boolean = false

    /**
     * Initializes the logger.
     *
     * @param logsEnabled flag to indicate whether events should be logged
     */
    fun init(logsEnabled: Boolean) {
        SelfUpdateLog.logsEnabled = logsEnabled
    }

    /**
     * Log an info message with the "i" tag.
     *
     * @param msg the message to log
     */
    fun logInfo(msg: String) {
        if (logsEnabled) {
            Log.i(TAG, msg)
        }
    }

    /**
     * Log an error message with the "e" tag.
     *
     * @param msg the message to log
     */
    fun logError(msg: String) {
        if (logsEnabled) {
            Log.e(TAG, msg)
        }
    }
}
