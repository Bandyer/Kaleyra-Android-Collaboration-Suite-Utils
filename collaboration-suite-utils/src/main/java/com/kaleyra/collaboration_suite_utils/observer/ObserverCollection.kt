/*
 *  Copyright (C) 2022 Kaleyra S.p.a. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.kaleyra.collaboration_suite_utils.observer

/**
 * A Generic Observer Collection
 * @param T the type of observer
 * @author kristiyan
 */
interface ObserverCollection<T> {

    /**
     * Add observer to collection
     * @param observer T
     */
    fun add(observer: T)

    /**
     * Remove observer to collection
     * @param observer T
     */
    fun remove(observer: T)

    /**
     * Set a new collection
     * @param obs MutableList<T>
     */
    fun set(obs: MutableList<T>)

    /**
     * Get the observers in the collection
     * @param result Function1<List<T>, Unit> returns the list of observers in the collection
     */
    fun getObservers(result: (List<T>) -> Unit)

    /**
     * Loop the observers in the collection
     * @param item Function1<T, Unit> returns each observer in the collection
     */
    fun forEach(item: (T) -> Unit)

    /**
     * Get the collection size
     * @param result Function1<Int, Unit> returns the size of the collection
     */
    fun size(result: (Int) -> Unit)

    /**
     * Removes all observers in the collection
     */
    fun clear()

    /**
     * Check if the observer provided is in the collection
     * @param observer T observer to look for
     * @param result Function1<Boolean, Unit> returns true if the observer is in the list, false otherwise
     */
    fun contains(observer: T, result: (Boolean) -> Unit)

    /**
     * Check if the collection is empty
     * @param result Function1<Boolean, Unit> returns true if is empty, false otherwise
     */
    fun isEmpty(result: (Boolean) -> Unit)

}