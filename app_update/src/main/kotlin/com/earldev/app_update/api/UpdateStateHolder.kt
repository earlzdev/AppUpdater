package com.earldev.app_update.api

import com.earldev.app_update.UpdateStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object UpdateStateHolder {

    private val currentState: MutableStateFlow<UpdateStep?> = MutableStateFlow(null)

    fun currentState(): StateFlow<UpdateStep?> = currentState.asStateFlow()

    internal fun emit(step: UpdateStep?) {
        currentState.tryEmit(step)
    }
}