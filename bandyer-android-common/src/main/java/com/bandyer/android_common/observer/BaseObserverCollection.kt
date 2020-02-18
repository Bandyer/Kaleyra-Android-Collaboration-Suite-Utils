package com.bandyer.android_common.observer

import com.badoo.mobile.util.WeakHandler
import java.lang.ref.WeakReference
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Implementation of a Generic Observer Collection
 * @param T type of observers
 * @property callbackHandler WeakHandler? where all results will be posted
 * @property executor ExecutorService? where all requests will be submitted
 * @constructor
 * @author kristiyan
 */
open class BaseObserverCollection<T>(private val callbackHandler: WeakHandler? = null,
                                     private var executor: ExecutorService = Executors.newSingleThreadExecutor()) : InvocationHandler, ObserverCollection<T> {

    @Volatile
    private var observersList = mutableListOf<WeakReference<T>>()

    override fun forEach(item: (T) -> Unit) {
        executor.submit {
            observersList.forEach { item ->
                notifyUI {
                    val obs = item.get() ?: return@notifyUI
                    item(obs)
                }
            }
        }
    }

    override fun isEmpty(result: (Boolean) -> Unit) {
        executor.submit { notifyUI { result(observersList.isEmpty()) } }
    }

    override fun size(result: (Int) -> Unit) {
        executor.submit { notifyUI { result(observersList.size) } }
    }

    override fun contains(observer: T, result: (Boolean) -> Unit) {
        executor.submit {
            notifyUI { notifyUI { result(observersList.any { it.get() == observer }) } }
        }
    }

    override fun getObservers(result: (List<T>) -> Unit) {
        executor.submit {
            val list = mutableListOf<T>()
            observersList.map { it.get() }.forEach {
                val item = it ?: return@forEach
                list.add(item)
            }
            notifyUI { result(list) }
        }
    }

    override fun set(obs: MutableList<T>) {
        executor.submit {
            observersList.clear()
            observersList.addAll(obs.map { WeakReference(it) })
        }
    }

    override fun add(observer: T) {
        executor.submit {
            observersList.add(WeakReference(observer))
        }
    }

    override fun remove(observer: T) {
        executor.submit {
            val idx = observersList.indexOfFirst { it.get() == observer }
            if (idx == -1) return@submit
            observersList.removeAt(idx)
        }
    }

    override fun clear() {
        executor.submit {
            observersList.clear()
        }
    }

    private fun notifyUI(block: () -> Unit) {
        if (callbackHandler == null) block.invoke()
        else callbackHandler.post { block.invoke() }
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

