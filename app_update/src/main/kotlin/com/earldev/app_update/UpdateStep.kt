package com.earldev.app_update

interface StepStatus {
    val started: Boolean
    val success: Boolean
    val failure: Exception?
}

sealed interface UpdateStep : StepStatus {

    val key: Int

    data class UpdateAvailabilityCheck(
        override val started: Boolean = false,
        override val success: Boolean = false,
        override val failure: Exception? = null,
    ) : UpdateStep {
        override val key: Int = UPDATE_AVAILABLE_STEP_KEY
    }

    data class DownloadApk(
        override val started: Boolean = false,
        override val success: Boolean = false,
        override val failure: Exception? = null,
    ) : UpdateStep {
        override val key: Int = DOWNLOAD_APK_STEP_KEY
    }

    data class SecurityCheck(
        override val started: Boolean = false,
        override val success: Boolean = false,
        override val failure: Exception? = null,
    ) : UpdateStep {
        override val key: Int = SECURITY_CHECK_STEP_KEY
    }

    data class InstallApk(
        override val started: Boolean = false,
        override val success: Boolean = false,
        override val failure: Exception? = null,
    ) : UpdateStep {
        override val key: Int = INSTALL_APK_STEP_KEY
    }

    data class Finish(
        override val started: Boolean = false,
        override val success: Boolean = false,
        override val failure: Exception? = null,
    ) : UpdateStep {
        override val key: Int = FINISH_KEY
    }

    companion object {
        internal const val UPDATE_AVAILABLE_STEP_KEY = 0
        internal const val DOWNLOAD_APK_STEP_KEY = 1
        internal const val SECURITY_CHECK_STEP_KEY = 2
        internal const val INSTALL_APK_STEP_KEY = 3
        internal const val FINISH_KEY = 4
    }
}