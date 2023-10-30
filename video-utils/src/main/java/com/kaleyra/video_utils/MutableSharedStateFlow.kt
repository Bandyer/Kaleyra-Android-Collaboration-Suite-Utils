package com.kaleyra.video_utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 *  It's a MutableStateFlow that emits all values,
 *  N.B. use it only on background threads since it will may lock the caller's thread on set and get value
 *
 *  @constructor
 *  @param T type of value to emit
 *  @param initialValue initial value
 **/
class MutableSharedStateFlow<T>(initialValue: T) : StateFlow<T>, MutableSharedFlow<T> {

    private val flow: MutableSharedFlow<T> = MutableSharedFlow(replay = 1, extraBufferCapacity = 100) // give latest value to subscribers, and set a buffer of 1 value if subscribers are slow to collect

    /**
     * A snapshot of the replay cache.
     **/
    override val replayCache = flow.replayCache

    private var lastValue: T = initialValue

    private val mutex = Mutex()

    /**
     * The current value of this state flow.
     **/
    override var value: T
        get() {
            return runBlocking {
                mutex.withLock {
                    replayCache.last()
                }
            }
        }
        set(value) {
            runBlocking {
                mutex.withLock {
                    if (value == lastValue) return@withLock
                    lastValue = value
                    flow.emit(value)
                }
            }
        }

    /**
     * @suppress
     */
    override suspend fun collect(collector: FlowCollector<T>) = flow.collect(collector)

    init {
        flow.tryEmit(initialValue)
    }

    /**
     * The number of subscribers (active collectors) to this shared flow.
     *
     * The integer in the resulting [StateFlow] is not negative and starts with zero for a freshly created
     * shared flow.
     *
     * This state can be used to react to changes in the number of subscriptions to this shared flow.
     * For example, if you need to call `onActive` when the first subscriber appears and `onInactive`
     * when the last one disappears, you can set it up like this:
     *
     * ```
     * sharedFlow.subscriptionCount
     *     .map { count -> count > 0 } // map count into active/inactive flag
     *     .distinctUntilChanged() // only react to true<->false changes
     *     .onEach { isActive -> // configure an action
     *         if (isActive) onActive() else onInactive()
     *     }
     *     .launchIn(scope) // launch it
     * ```
     */
    override val subscriptionCount: StateFlow<Int> = flow.subscriptionCount

    /**
     * Emits a [value] to this shared flow, suspending on buffer overflow if the shared flow was created
     * with the default [BufferOverflow.SUSPEND] strategy.
     *
     * See [tryEmit] for a non-suspending variant of this function.
     *
     * This method is **thread-safe** and can be safely invoked from concurrent coroutines without
     * external synchronization.
     */
    override suspend fun emit(value: T) = flow.emit(value)

    /**
     * Resets the [replayCache] of this shared flow to an empty state.
     * New subscribers will be receiving only the values that were emitted after this call,
     * while old subscribers will still be receiving previously buffered values.
     * To reset a shared flow to an initial value, emit the value after this call.
     *
     * On a [MutableStateFlow], which always contains a single value, this function is not
     * supported, and throws an [UnsupportedOperationException]. To reset a [MutableStateFlow]
     * to an initial value, just update its [value][MutableStateFlow.value].
     *
     * This method is **thread-safe** and can be safely invoked from concurrent coroutines without
     * external synchronization.
     *
     * **Note: This is an experimental api.** This function may be removed or renamed in the future.
     */
    @ExperimentalCoroutinesApi
    override fun resetReplayCache() = flow.resetReplayCache()

    /**
     * Tries to emit a [value] to this shared flow without suspending. It returns `true` if the value was
     * emitted successfully. When this function returns `false`, it means that the call to a plain [emit]
     * function will suspend until there is a buffer space available.
     *
     * A shared flow configured with a [BufferOverflow] strategy other than [SUSPEND][BufferOverflow.SUSPEND]
     * (either [DROP_OLDEST][BufferOverflow.DROP_OLDEST] or [DROP_LATEST][BufferOverflow.DROP_LATEST]) never
     * suspends on [emit], and thus `tryEmit` to such a shared flow always returns `true`.
     *
     * This method is **thread-safe** and can be safely invoked from concurrent coroutines without
     * external synchronization.
     */
    override fun tryEmit(value: T): Boolean = flow.tryEmit(value)
}
