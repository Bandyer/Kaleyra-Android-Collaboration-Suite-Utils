package com.bandyer.android_common

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.support.v7.app.AppCompatActivity

/**
 * Abstract class for lifecycle events used also for single instances
 *
 * @author kristiyan
 */
object LifecyleBinder {

    /**
     * Bind the activity to the listener for lifecycle events
     *
     * @param appCompatActivity activity to bind
     * @param lifecycleEvents listener for the events
     */
    fun bind(appCompatActivity: AppCompatActivity, lifecycleEvents: LifecycleEvents) {

        appCompatActivity.lifecycle.addObserver(object : LifecycleObserver, LifecycleEvents {

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
                appCompatActivity.lifecycle.removeObserver(this)
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