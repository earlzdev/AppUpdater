package com.earldev.app_update

interface StepStatus {
    val started: Boolean
    val success: Boolean
    val failure: Exception?
}

sealed interface UpdateStep : StepStatus {

    data class UpdateAvailabilityCheck(
        override val started: Boolean = false,
        override val success: Boolean = false,
        override val failure: Exception? = null,
    ) : UpdateStep

    data class DownloadApk(
        override val started: Boolean = false,
        override val success: Boolean = false,
        override val failure: Exception? = null,
    ) : UpdateStep

    data class SecurityCheck(
        override val started: Boolean = false,
        override val success: Boolean = false,
        override val failure: Exception? = null,
    ) : UpdateStep

    data class InstallApk(
        override val started: Boolean = false,
        override val success: Boolean = false,
        override val failure: Exception? = null,
    ) : UpdateStep

    data class Finish(
        override val started: Boolean = false,
        override val success: Boolean = false,
        override val failure: Exception? = null,
    ) : UpdateStep
}