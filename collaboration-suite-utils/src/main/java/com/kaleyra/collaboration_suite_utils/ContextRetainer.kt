/*
 *  Copyright (C) 2022 Kaleyra S.p.a. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.kaleyra.collaboration_suite_utils

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