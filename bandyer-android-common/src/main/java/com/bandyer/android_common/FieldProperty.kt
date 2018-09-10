package com.bandyer.android_common

import kotlin.reflect.KProperty

internal class FieldProperty<R, T>(private val initializer: (R) -> T = { throw Throwable("Not initialized.") }) {

    private val map = WeakIdentityHashMap<R, T>()

    operator fun getValue(thisRef: R, property: KProperty<*>): T =
            map[thisRef] ?: setValue(thisRef, property, initializer(thisRef))

    operator fun setValue(thisRef: R, property: KProperty<*>, value: T): T {
        map[thisRef] = value
        return value
    }

}