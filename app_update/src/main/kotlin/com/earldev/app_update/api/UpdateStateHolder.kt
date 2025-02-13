package com.earldev.app_update.api

import com.earldev.app_update.UpdateStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Stores the current state of the update process.
 */
object UpdateStateHolder {

    private val currentState: MutableStateFlow<UpdateStep?> = MutableStateFlow(null)

    /**
     * Returns a flow with the current update state [UpdateStep].
     */
    fun currentStateFlow(): StateFlow<UpdateStep?> = currentState.asStateFlow()

    /**
     * Returns the current update state.
     */
    fun currentState(): UpdateStep? = currentStateFlow().value

    /**
     * Emits a state to [currentState].
     *
     * @param step the new state
     */
    internal fun emit(step: UpdateStep?) {
        currentState.tryEmit(step)
    }
}