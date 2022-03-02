package com.bandyer.android_common

import android.annotation.SuppressLint
import android.content.Context
import androidx.startup.Initializer

/**
 * Context retainer
 */
@SuppressLint("StaticFieldLeak")
class ContextRetainer : Initializer<Unit> {

    companion object {
        private lateinit var mContext: Context
        /**
c         */
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