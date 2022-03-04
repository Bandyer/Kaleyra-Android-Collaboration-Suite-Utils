/*
 *  Copyright (C) 2022 Kaleyra S.p.a. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.kaleyra.collaboration_suite_utils

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import java.util.*

/**
 * @suppress
 * Implements a combination of WeakHashMap and IdentityHashMap.
 * Useful for caches that need to key off of a == comparison
 * instead of a .equals.
 *
 * This class is not a general-purpose Map implementation! While
 * this class implements the Map interface, it intentionally violates
 * Map's general contract, which mandates the use of the equals method
 * when comparing objects. This class is designed for use only in the
 * rare cases wherein reference-equality semantics are required.
 *
 * Note that this implementation is not synchronized.
 *
 * @param K key
 * @param V value
 * @property queue ReferenceQueue<K> queue of keys
 * @property backingStore HashMap<IdentityWeakReference<K, V>, V>
 * @property entries MutableSet<MutableEntry<K, V>>
 * @property keys MutableSet<K>
 * @property size Int
 * @property values MutableCollection<V>
 */
class WeakIdentityHashMap<K, V> : MutableMap<K, V> {

    private val queue = ReferenceQueue<K>()
    private val backingStore = HashMap<IdentityWeakReference, V>()

    override fun clear() {
        backingStore.clear()
        reap()
    }

    override fun containsKey(key: K): Boolean {
        reap()
        return backingStore.containsKey(IdentityWeakReference(key))
    }

    override fun containsValue(value: V): Boolean {
        reap()
        return backingStore.containsValue(value)
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() {
            reap()
            val ret = HashSet<MutableMap.MutableEntry<K, V>>()
            for ((key1, value) in backingStore) {
                val key = key1.get()
                val entry = object : MutableMap.MutableEntry<K, V> {
                    override val key: K = key!!
                    override val value: V = value
                    override fun setValue(newValue: V): V {
                        throw UnsupportedOperationException()
                    }
                }
                ret.add(entry)
            }
            return Collections.unmodifiableSet<MutableMap.MutableEntry<K, V>>(ret)
        }
    override val keys: MutableSet<K>
        get() {
            reap()
            val ret = HashSet<K>()
            for (ref in backingStore.keys)
                ret.add(ref.get()!!)
            return Collections.unmodifiableSet(ret)
        }

    override fun equals(other: Any?): Boolean {
        return if (other !is WeakIdentityHashMap<*, *>) {
            false
        } else backingStore == other.backingStore
    }

    override operator fun get(key: K): V? {
        reap()
        return backingStore[IdentityWeakReference(key)]
    }

    override fun put(key: K, value: V): V? {
        reap()
        return backingStore.put(IdentityWeakReference(key), value)
    }

    override fun hashCode(): Int {
        reap()
        return backingStore.hashCode()
    }

    override fun isEmpty(): Boolean {
        reap()
        return backingStore.isEmpty()
    }

    override fun putAll(from: Map<out K, V>) {
        throw UnsupportedOperationException()
    }

    override fun remove(key: K): V? {
        reap()
        return backingStore.remove(IdentityWeakReference(key!!))
    }

    override val size: Int
        get() {
            reap()
            return backingStore.size
        }


    override val values: MutableCollection<V>
        get() {
            reap()
            return backingStore.values
        }

    @Synchronized
    private fun reap() {

        var zombie: Any? = queue.poll()

        while (zombie != null) {
            val victim = zombie as WeakIdentityHashMap<*, *>.IdentityWeakReference
            backingStore.remove(victim)
            zombie = queue.poll()
        }
    }

    internal inner class IdentityWeakReference(obj: K) : WeakReference<K>(obj, queue) {

        private var hash: Int = System.identityHashCode(obj)

        override fun hashCode() = hash

        override fun equals(other: Any?): Boolean {
            if (this === other)
                return true

            if (other !is WeakIdentityHashMap<*, *>.IdentityWeakReference)
                return false

            return this.get() === other.get()
        }
    }
}
