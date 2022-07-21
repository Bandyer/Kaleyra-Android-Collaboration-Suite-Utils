package com.kaleyra.collaboration_suite_utils

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 *  It's a MutableStateFlow that emits all values
 *
 *  @constructor
 *  @param T type of value to emit
 *  @param initialValue initial value
 **/
class MutableSharedStateFlow<T>(initialValue: T) : StateFlow<T> {

    private val flow: MutableSharedFlow<T> = MutableSharedFlow(replay = 1, extraBufferCapacity = 1) // give latest value to subscribers, and set a buffer of 1 value if subscribers are slow to collect

    override val replayCache = flow.replayCache

    private var lastValue: T = initialValue

    override var value: T
        get() = flow.replayCache.last()
        set(value) {
            if (value == lastValue) return
            lastValue = value
            flow.tryEmit(value)
        }

    /**
     * @suppress
     */
    @OptIn(InternalCoroutinesApi::class)
    override suspend fun collect(collector: FlowCollector<T>) = flow.collect(collector)

    init {
        flow.tryEmit(initialValue)
    }
}
