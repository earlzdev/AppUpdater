package com.earldev.self_update

import androidx.compose.runtime.Immutable

@Immutable
data class ScreenState(
    val loading: Boolean = false,
    val noPermissionAlert: Boolean = false,
    val updateAvailable: Boolean? = null,
)
