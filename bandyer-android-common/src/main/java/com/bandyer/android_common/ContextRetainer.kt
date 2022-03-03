package com.bandyer.android_common

import android.annotation.SuppressLint
import android.content.Context
import androidx.startup.Initializer

/**
 * Context Retainer holder for application context
 */
@SuppressLint("StaticFieldLeak")
class ContextRetainer : Initializer<Unit> {

    /**
     * Context Retainer instance
     */
    companion object {
        private lateinit var mContext: Context

        /**
         * Application context
         */
        val context: Context
            get() = mContext
    }

    /**
     * @suppress
     */
    override fun create(ctx: Context) {
        mContext = ctx.applicationContext
    }

    /**
     * @suppress
     */
    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}