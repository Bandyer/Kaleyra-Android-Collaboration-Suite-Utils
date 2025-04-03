package com.kaleyra.video_utils.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Provides standard CoroutineDispatcher implementations.
 */
object StandardDispatchers : DispatcherProvider {
    override val main: CoroutineDispatcher
        get() = Dispatchers.Main
    override val mainImmediate: CoroutineDispatcher
        get() = Dispatchers.Main.immediate
    override val io: CoroutineDispatcher
        get() = Dispatchers.IO
    override val default: CoroutineDispatcher
        get() = Dispatchers.Default
}