/*
 *  Copyright (C) 2022 Kaleyra S.p.a. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.kaleyra.video_utils

import kotlin.reflect.KProperty

/**
 * Class to add a property to any existing class
 *
 * Example
 * private var View.cornerRadius: Float by FieldProperty { 0f }
 *
 * with this line we will have the cornerRadius property available on all Views
 * @param R property reference
 * @param T property value
 * @property initializer Function1<R, T>
 * @property map WeakIdentityHashMap<R, T>
 * @constructor
 */
class FieldProperty<R, T>(private val initializer: (R) -> T = { throw Throwable("Not initialized.") }) {

    private val map = WeakIdentityHashMap<R, T>()

    /**
     * Get Method
     * @param thisRef R property reference
     * @param property KProperty<*> property
     * @return T the current value
     */
    operator fun getValue(thisRef: R, property: KProperty<*>): T =
            map[thisRef] ?: setValue(thisRef, property, initializer(thisRef))

    /**
     * Set Method
     * @param thisRef R property reference
     * @param property KProperty<*> property
     * @param value T the new value
     * @return T the new value
     */
    operator fun setValue(thisRef: R, property: KProperty<*>, value: T): T {
        map[thisRef] = value
        return value
    }

}