/*
 *  Copyright (C) 2022 Kaleyra S.p.a. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.kaleyra.collaboration_suite_utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.fragment.app.FragmentActivity

/**
 * Abstract class for lifecycle events used also for single instances
 *
 * @author kristiyan
 */
object LifecyleBinder {

    /**
     * Bind the activity to the listener for lifecycle events
     *
     * @param activity activity to bind
     * @param lifecycleEvents listener for the events
     */
    fun bind(activity: FragmentActivity, lifecycleEvents: LifecycleEvents) {

        activity.lifecycle.addObserver(object : LifecycleObserver, LifecycleEvents {

            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            override fun create() {
                lifecycleEvents.create()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            override fun stop() {
                lifecycleEvents.stop()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            override fun start() {
                lifecycleEvents.start()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            override fun resume() {
                lifecycleEvents.resume()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            override fun pause() {
                lifecycleEvents.pause()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            override fun destroy() {
                lifecycleEvents.destroy()
                activity.lifecycle.removeObserver(this)
            }
        })
    }
}

/**
 * Interface defining the lifecycle events
 */
interface LifecycleEvents {
    /**
     * Create callback
     */
    fun create()

    /**
     * Start callback
     */
    fun start()

    /**
     * Resume callback
     */
    fun resume()

    /**
     * Pause callback
     */
    fun pause()

    /**
     * Stop callback
     */
    fun stop()

    /**
     * Destroy callback
     */
    fun destroy()
}