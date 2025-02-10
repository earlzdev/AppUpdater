package com.earldev.app_update.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import javax.inject.Inject

internal class UpdateCoroutineScopeHolder @Inject constructor(
    private val coroutineDispatchers: CoroutineDispatchers
) {

    private var coroutineScope: CoroutineScope? = null

    fun coroutineScope(): CoroutineScope {
        return coroutineScope ?: run {
            val scope = CoroutineScope(coroutineDispatchers.io)
            coroutineScope = scope
            scope
        }
    }

    fun clear() {
        coroutineScope?.coroutineContext?.cancelChildren()
        coroutineScope?.cancel()
        coroutineScope = null
    }
}