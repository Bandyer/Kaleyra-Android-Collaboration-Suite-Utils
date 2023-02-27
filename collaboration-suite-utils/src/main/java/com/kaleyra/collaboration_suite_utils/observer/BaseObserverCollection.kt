/*
 *  Copyright (C) 2022 Kaleyra S.p.a. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.kaleyra.collaboration_suite_utils.observer

import com.kaleyra.collaboration_suite_utils.ExecutorsServiceFactory
import com.kaleyra.collaboration_suite_utils.WeakHandler
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.util.concurrent.CancellationException
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Implementation of a Generic Observer Collection
 * @param T type of observers
 * @property executor ExecutorService? where all requests will be submitted
 * @constructor
 * @author kristiyan
 */

open class BaseObserverCollection<T> constructor(
    private val executor: ExecutorCompletionService<Any?>
) : InvocationHandler, ObserverCollection<T> {

    private var service: ExecutorService? = null

    private val runExecutor = Executors.newSingleThreadExecutor()

    @Deprecated("Deprecated since v2.1.0")
    constructor(weakHandler: WeakHandler? = null, executorService: ExecutorService? = null) : this(weakHandler?.let { ExecutorsServiceFactory.create(it) } ?: ExecutorsServiceFactory.create(executorService ?: Executors.newSingleThreadExecutor())) {
        service = executorService
    }

    @Volatile
    private var observersList = ConcurrentLinkedQueue<T>()

    override fun forEach(item: (T) -> Unit) = execute { observersList.forEach { item(it) } }

    override fun isEmpty(result: (Boolean) -> Unit) = execute { result(observersList.isEmpty()) }

    override fun size(result: (Int) -> Unit) = execute { result(observersList.size) }

    override fun contains(observer: T, result: (Boolean) -> Unit) = execute { result(observersList.any { it == observer }) }

    override fun getObservers(result: (List<T>) -> Unit) = execute {
        val list = mutableListOf<T>()
        observersList.forEach { list.add(it) }
        result(list)
    }

    override fun set(obs: MutableList<T>) = execute {
        clear()
        observersList.addAll(obs)
    }

    override fun add(observer: T) = execute { observersList.add(observer) }

    override fun remove(observer: T) = execute { observersList.remove(observer) }

    override fun clear() = execute { observersList.clear() }

    private val methods: Array<Method> = ObserverCollection::class.java.methods

    private fun execute(block: () -> Unit) = runExecutor.execute {
        try {
            executor.submit(block).get()
        } catch (_: CancellationException) {
        } catch (_: InterruptedException) {
        }
    }

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

