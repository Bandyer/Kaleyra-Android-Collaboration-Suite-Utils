package com.bandyer.android_common

import kotlin.reflect.KProperty

/**
 * Created by Brian Parma on 2/10/16.
 *
 * This is basically just the lazy delegate copy and pasted, then modified so that it can be
 * reset by setting it equal to null
 *
 * Represents a value with lazy initialization.
 *
 * To create an instance use the [cached] function.
 */
interface Cached<T> {
    /**
     * Gets the lazily initialized value of the current Cached instance.
     * Once the value was initialized it must not change during the rest of lifetime of this Cached instance.
     */
    var value: T?

    /**
     * Returns `true` if a value for this Cached instance has a value, and `false` otherwise.
     */
    fun isCached(): Boolean
}

/**
 * Creates a lazy instance for variable
 *
 * @param initializer () -> T initializer block
 * @return Cached<T> lazy instance
 */
fun <T> cached(initializer: () -> T): Cached<T> = SynchronizedCachedImpl(initializer)

/**
 * Gets the value for the cached instance
 * @receiver Cached<T>
 * @param thisRef Any?
 * @param property KProperty<*>
 * @return T?
 * @suppress
 */
operator fun <T> Cached<T>.getValue(thisRef: Any?, property: KProperty<*>): T? = value

/**
 * Sets the new value for the cached instance
 * @receiver Cached<T>
 * @param thisRef Any?
 * @param property KProperty<*>
 * @param new_value T?
 * @suppress
 */
operator fun <T> Cached<T>.setValue(thisRef: Any?, property: KProperty<*>, new_value: T?) {
    value = new_value
}


private class SynchronizedCachedImpl<T>(initializer: () -> T, lock: Any? = null) : Cached<T>, java.io.Serializable {
    private var initializer: (() -> T)? = initializer
    @Volatile
    private var _value: Any? = null
    // final field is required to enable safe publication of constructed instance
    private val lock = lock ?: this

    @Suppress("UNCHECKED_CAST")
    override var value: T?
        set(new_value) {
            synchronized(lock) {
                _value = new_value
            }
        }
        get() {
            val _v1 = _value
            if (_v1 !== null)
                return _v1 as T

            return synchronized(lock) {
                val _v2 = _value
                if (_v2 !== null)
                    _v2 as T
                else {
                    val typedValue = initializer!!()
                    _value = typedValue
//                    initializer = null
                    typedValue
                }
            }
        }

    override fun isCached(): Boolean = _value !== null

    override fun toString(): String = if (isCached()) value.toString() else "Cached value is not set yet."

}