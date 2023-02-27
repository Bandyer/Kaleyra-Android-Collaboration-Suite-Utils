/*
 *  Copyright (C) 2022 Kaleyra S.p.a. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */

package com.kaleyra.collaboration_suite_utils.observer

import android.os.Looper
import com.kaleyra.collaboration_suite_utils.ExecutorsServiceFactory.mainExecutorService
import com.kaleyra.collaboration_suite_utils.WeakHandler
import java.lang.reflect.Proxy
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * A factory to create observers given a generic type
 * @author kristiyan
 */
object ObserverFactory {

    /**
     * Method to create observer collection of type T
     * @param executor completion service executor
     * @return T the observer collection created
     */
    inline fun <reified T> createObserverCollection(executor: ExecutorCompletionService<Any?> = mainExecutorService): T where T : ObserverCollection<*> {
        return Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java), BaseObserverCollection<T>(executor)) as T
    }

    /**
     * Method to create observer collection of type T
     * @param handler WeakHandler? handler where the callbacks will be posted
     * @param executor completion service executor
     * @return T the observer collection created
     */
    @Deprecated("Deprecated since v2.1.0")
    @JvmOverloads
    inline fun <reified T> createObserverCollection(handler: WeakHandler? = WeakHandler(Looper.getMainLooper()), executor: ExecutorService? = Executors.newSingleThreadExecutor()): T where T : ObserverCollection<*> {
        executor?.let { return Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java), BaseObserverCollection<T>(handler, it)) as T }
        return Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java), BaseObserverCollection<T>(handler)) as T
    }
}