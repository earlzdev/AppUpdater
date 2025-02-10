package com.earldev.app_update.models

internal data class UpdateJob(
    val needUpdate: Boolean = false,
    val downloaded: Boolean = false,
    val secured: Boolean = false
)