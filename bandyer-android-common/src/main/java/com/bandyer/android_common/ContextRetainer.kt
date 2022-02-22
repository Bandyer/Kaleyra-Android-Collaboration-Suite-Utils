package com.bandyer.android_common

import android.annotation.SuppressLint
import android.content.Context
import androidx.startup.Initializer

/**
 * Context retainer
 */
@SuppressLint("StaticFieldLeak")
object ContextRetainer : Initializer<Unit> {

    /**
     * Context
     */
    lateinit var context: Context
        private set

    /**
     * @suppress
     */
    override fun create(context: Context) {
        this.context = context.applicationContext
    }

    /**
     * @suppress
     */
    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}