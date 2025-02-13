package com.earldev.app_update

/**
 * Status of an update step.
 */
interface StepStatus {

    /**
     * Indicates whether the step has started.
     */
    val started: Boolean

    /**
     * Indicates whether the step has completed successfully.
     */
    val success: Boolean

    /**
     * Indicates whether there was a failure during the step.
     */
    val failure: Exception?
}

/**
 * Parent interface for all update steps.
 */
sealed interface UpdateStep : StepStatus {

    /**
     * Step identifier.
     */
    val key: Int

    /**
     * Checking for update availability.
     */
    data class UpdateAvailabilityCheck(
        override val started: Boolean = false,
        override val success: Boolean = false,
        override val failure: Exception? = null,
    ) : UpdateStep {
        override val key: Int = UPDATE_AVAILABLE_STEP_KEY
    }

    /**
     * Downloading the APK file.
     */
    data class DownloadApk(
        override val started: Boolean = false,
        override val success: Boolean = false,
        override val failure: Exception? = null,
    ) : UpdateStep {
        override val key: Int = DOWNLOAD_APK_STEP_KEY
    }

    /**
     * Checking the security of the downloaded APK file.
     */
    data class SecurityCheck(
        override val started: Boolean = false,
        override val success: Boolean = false,
        override val failure: Exception? = null,
    ) : UpdateStep {
        override val key: Int = SECURITY_CHECK_STEP_KEY
    }

    /**
     * Installing the APK file.
     */
    data class InstallApk(
        override val started: Boolean = false,
        override val success: Boolean = false,
        override val failure: Exception? = null,
    ) : UpdateStep {
        override val key: Int = INSTALL_APK_STEP_KEY
    }

    /**
     * Completing the update process.
     */
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