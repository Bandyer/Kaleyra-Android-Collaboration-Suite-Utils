/*
 *  Copyright (C) 2022 Kaleyra S.p.a. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.kaleyra.collaboration_suite_utils.observer

import com.kaleyra.collaboration_suite_utils.ExecutorCancellableCompletionService
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Implementation of a Generic Observer Collection
 * @param T type of observers
 * @property executor ExecutorService? where all requests will be submitted
 * @constructor
 * @author kristiyan
 */
open class BaseObserverCollection<T>(
    private var executor: ExecutorCancellableCompletionService<Any?>
) : InvocationHandler, ObserverCollection<T> {

    @Volatile
    private var observersList = ConcurrentLinkedQueue<T>()

    override fun forEach(item: (T) -> Unit) {
        executor.submit {
            observersList.forEach {
                item(it)
            }
        }
    }

    override fun isEmpty(result: (Boolean) -> Unit) {
        executor.submit {
            result(observersList.isEmpty())
        }
    }

    override fun size(result: (Int) -> Unit) {
        executor.submit {
            result(observersList.size)
        }
    }

    override fun contains(observer: T, result: (Boolean) -> Unit) {
        executor.submit {
            result(observersList.any { it == observer })
        }
    }

    override fun getObservers(result: (List<T>) -> Unit) {
        executor.submit {
            val list = mutableListOf<T>()
            observersList.forEach { list.add(it) }
            result(list)
        }
    }

    override fun set(obs: MutableList<T>) {
        executor.submit {
            observersList.clear()
            observersList.addAll(obs)
        }
    }

    override fun add(observer: T) {
        executor.submit {
            observersList.add(observer)
        }
    }

    override fun remove(observer: T) {
        executor.submit {
            val removeObj = observersList.firstOrNull { it == observer } ?: return@submit Unit
            observersList.remove(removeObj)
        }
    }

    override fun clear() {
        executor.submit {
            observersList.clear()
        }
    }

    private val methods: Array<Method> = ObserverCollection::class.java.methods

    /**
     * @suppress
     */
    override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any? {
        if (method in methods)
            return if (args != null) method.invoke(this, *args) else method.invoke(this)
        forEach {
            it ?: return@forEach
            if (args != null) method.invoke(it, *args)
            else method.invoke(it)
        }
        return null
    }
}

