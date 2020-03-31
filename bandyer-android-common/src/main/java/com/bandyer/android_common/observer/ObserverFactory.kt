package com.bandyer.android_common.observer

import android.os.Looper
import com.badoo.mobile.util.WeakHandler
import java.lang.reflect.Proxy
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * A factory to create observers given a generic type
 * @author kristiyan
 */
object ObserverFactory {

    /**
     * Method to create observer collection of type T
     * @param handler WeakHandler? handler where the callbacks will be posted
     * @return T the observer collection created
     */
    @JvmOverloads
    inline fun <reified T> createObserverCollection(handler: WeakHandler? = WeakHandler(Looper.getMainLooper()), executor: ExecutorService? = Executors.newSingleThreadExecutor()): T where T : ObserverCollection<*> {
        executor?.let { return Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java), BaseObserverCollection<T>(handler,it)) as T }
        return Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java), BaseObserverCollection<T>(handler)) as T
    }
}