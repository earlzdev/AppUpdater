package com.earldev.app_update.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import javax.inject.Inject

/**
 * Holds the [CoroutineScope] for the update process.
 *
 * @property coroutineDispatchers Coroutine dispatchers to be used.
 */
internal class UpdateCoroutineScopeHolder @Inject constructor(
    private val coroutineDispatchers: CoroutineDispatchers
) {

    private var coroutineScope: CoroutineScope? = null

    /**
     * Returns an instance of [CoroutineScope].
     */
    fun coroutineScope(): CoroutineScope {
        return coroutineScope ?: run {
            val scope = CoroutineScope(coroutineDispatchers.io + SupervisorJob())
            coroutineScope = scope
            scope
        }
    }

    /**
     * Clears the current [CoroutineScope] instance.
     */
    fun clear() {
        coroutineScope?.coroutineContext?.cancelChildren()
        coroutineScope?.cancel()
        coroutineScope = null
    }
}
