/*
 *  Copyright (C) 2022 Kaleyra S.p.a. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.kaleyra.collaboration_suite_utils.observer

import com.kaleyra.collaboration_suite_utils.ExecutorCancellableCompletionService
import java.lang.reflect.Proxy

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
    inline fun <reified T> createObserverCollection(executor: ExecutorCancellableCompletionService<Any?>): T where T : ObserverCollection<*> {
        return Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java), BaseObserverCollection<T>(executor)) as T
    }
}