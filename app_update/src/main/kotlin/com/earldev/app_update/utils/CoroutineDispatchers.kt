package com.earldev.app_update.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * [CoroutineDispatcher] for the library's operations.
 */
internal interface CoroutineDispatchers {
    /**
     * IO dispatcher.
     */
    val io: CoroutineDispatcher
}

/**
 * Implementation of [CoroutineDispatchers].
 */
internal class CoroutineDispatchersImpl @Inject constructor() : CoroutineDispatchers {

    override val io: CoroutineDispatcher = Dispatchers.IO
}