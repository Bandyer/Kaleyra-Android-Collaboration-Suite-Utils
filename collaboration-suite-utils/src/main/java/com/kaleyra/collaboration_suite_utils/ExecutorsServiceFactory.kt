package com.kaleyra.collaboration_suite_utils

import android.os.Handler
import android.os.Looper
import androidx.core.os.HandlerCompat
import com.badoo.mobile.util.WeakHandler
import java.util.concurrent.Executor

/**
 * Executors service factory
 */
object ExecutorsServiceFactory {

    /**
     * Main executor
     */
    val mainExecutor by lazy { HandlerExecutor(HandlerCompat.createAsync(Looper.getMainLooper())) }

    /**
     * Main executor service
     */
    val mainExecutorService: ExecutorCancellableCompletionService<Any?, HandlerExecutor> by lazy { create(mainExecutor) }

    /**
     * Handler executor
     *
     * @property handler handler async of the main looper
     * @constructor Create Handler executor
     */
    class HandlerExecutor(val handler: Handler) : Executor {
        /**
         * @suppress
         */
        override fun execute(r: Runnable) = Unit.apply { handler.post(r) }
    }

    /**
     * Handler executor
     *
     * @property handler handler async of the main looper
     * @constructor Create Handler executor
     */
    class WeakHandlerExecutor(val handler: WeakHandler) : Executor {
        /**
         * @suppress
         */
        override fun execute(r: Runnable) = Unit.apply { handler.post(r) }
    }

    /**
     * "createService" creates a "ExecutorCancellableCompletionService" that uses a "HandlerExecutor" that uses the "handler" parameter
     *
     * @param handler The handler that will be used to execute the tasks.
     */
    fun <V> create(handler: Handler) = ExecutorCancellableCompletionService<V, HandlerExecutor>(HandlerExecutor(handler))

    /**
     * "createService" creates a "ExecutorCancellableCompletionService" that uses a "HandlerExecutor" that uses the "handler" parameter
     *
     * @param handler The handler that will be used to execute the tasks.
     */
    fun <V> create(handler: WeakHandler) = ExecutorCancellableCompletionService<V, WeakHandlerExecutor>(WeakHandlerExecutor(handler))

    /**
     * Create a new ExecutorCancellableCompletionService with the given executor.
     *
     * @param executor The executor to use for the service.
     */
    fun <V, E : Executor> create(executor: E) = ExecutorCancellableCompletionService<V, E>(executor)
}