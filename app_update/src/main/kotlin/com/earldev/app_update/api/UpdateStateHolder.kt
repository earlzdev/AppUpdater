package com.earldev.app_update.api

import com.earldev.app_update.UpdateStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object UpdateStateHolder {

    private val currentState: MutableStateFlow<UpdateStep?> = MutableStateFlow(null)

    fun currentStateFlow(): StateFlow<UpdateStep?> = currentState.asStateFlow()

    fun currentState(): UpdateStep? = currentStateFlow().value

    internal fun emit(step: UpdateStep?) {
        currentState.tryEmit(step)
    }
}