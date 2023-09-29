/*
 *  Copyright (C) 2022 Kaleyra S.p.a. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.kaleyra.video_utils.logging

import android.util.Log

/**
 * Android logger
 *
 * @property target Target to log
 * @param level logLevel (verbose, debug, info, warn, error)
 */
private class AndroidPriorityLogger(
    level: Int = BaseLogger.VERBOSE,
    override val target: Int
) : PriorityLogger(level) {

    override fun verbose(tag: String, message: String) {
        Log.v(tag, message)
    }

    override fun debug(tag: String, message: String) {
        Log.d(tag, message)
    }

    override fun info(tag: String, message: String) {
        Log.i(tag, message)
    }

    override fun warn(tag: String, message: String) {
        Log.w(tag, message)
    }

    override fun error(tag: String, message: String) {
        Log.e(tag, message)
    }
}

/**
 * Android priory logger commodity function
 *
 * @param level logLevel (verbose, debug, info, warn, error)
 * @param target Target to log
 */
fun androidPrioryLogger(level: Int = BaseLogger.VERBOSE, target: Int) : PriorityLogger = AndroidPriorityLogger(level, target)