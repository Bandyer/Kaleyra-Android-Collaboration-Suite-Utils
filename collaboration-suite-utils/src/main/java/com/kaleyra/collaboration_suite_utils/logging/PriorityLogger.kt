/*
 *  Copyright (C) 2022 Kaleyra S.p.a. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.kaleyra.collaboration_suite_utils.logging

import android.os.Build
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.kaleyra.collaboration_suite_utils.BuildConfig
import com.kaleyra.collaboration_suite_utils.logging.BaseLogger.Priority.DEBUG
import com.kaleyra.collaboration_suite_utils.logging.BaseLogger.Priority.ERROR
import com.kaleyra.collaboration_suite_utils.logging.BaseLogger.Priority.INFO
import com.kaleyra.collaboration_suite_utils.logging.BaseLogger.Priority.LogPriority
import com.kaleyra.collaboration_suite_utils.logging.BaseLogger.Priority.VERBOSE
import com.kaleyra.collaboration_suite_utils.logging.BaseLogger.Priority.WARN
import java.util.regex.Pattern

/**
 * Priority based logger abstraction
 *
 * @property priority The priority target ex: VERBOSE, DEBUG, INFO etc.
 * @constructor
 *
 * @author kristiyan
 */
abstract class PriorityLogger(@LogPriority val priority: Int = VERBOSE) : BaseLogger {

    override val target: Int = 0

    /**
     * PriorityLogger
     */
    companion object {
        private const val MAX_LOG_LENGTH = 4000
        private const val MAX_TAG_LENGTH = 23
        private const val CALL_STACK_INDEX = 2
        private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")
    }

    /**
     * @suppress
     * Method to call when you want to log a verbose message
     * @param logTarget Logging target
     * @param tag Optional tag, by default is set to the calling class
     * @param message Message to log
     */
    fun verbose(logTarget: Int, tag: String = getTag(), message: String) {
        if (priority <= VERBOSE && target and logTarget != 0)
            log(tag, message, ::verbose)
    }

    /**
     * @suppress
     * Method to call when you want to log a debug message
     * @param logTarget Logging target
     * @param tag Optional tag, by default is set to the calling class
     * @param message Message to log
     */
    fun debug(logTarget: Int, tag: String = getTag(), message: String) {
        if (priority <= DEBUG && target and logTarget != 0)
            log(tag, message, ::debug)
    }

    /**
     * Method to call when you want to log a information message
     * @param logTarget Logging target
     * @param tag Optional tag, by default is set to the calling class
     * @param message Message to log
     */
    fun info(logTarget: Int, tag: String = getTag(), message: String) {
        if (priority <= INFO && target and logTarget != 0)
            log(tag, message, ::info)
    }

    /**
     * Method to call when you want to log a warning message
     * @param logTarget Logging target
     * @param tag Optional tag, by default is set to the calling class
     * @param message Message to log
     */
    fun warn(logTarget: Int, tag: String = getTag(), message: String) {
        if (priority <= WARN && target and logTarget != 0)
            log(tag, message, ::warn)
    }

    /**
     * Method to call when you want to log a error message
     * @param logTarget Logging target
     * @param tag Optional tag, by default is set to the calling class
     * @param message Message to log
     */
    fun error(logTarget: Int, tag: String = getTag(), message: String) {
        if (priority <= ERROR && target and logTarget != 0)
            log(tag, message, ::error)
    }


    /**
     * Split if log length is too big
     */
    private fun log(tag: String, message: String, logFunction: (tag: String, message: String) -> Unit) {
        if (message.length < MAX_LOG_LENGTH) {
            logFunction(tag, message)
            return
        }

        var i = 0
        val length = message.length
        while (i < length) {
            var newline = message.indexOf('\n', i)
            newline = if (newline != -1) newline else length
            do {
                val end = Math.min(newline, i + MAX_LOG_LENGTH)
                val part = message.substring(i, end)
                logFunction(tag, part)
                i = end
            } while (i < newline)
            i++
        }
    }

    /**
     * Extract the tag which should be used for the message from the `element`. By default
     * this will use the class name without any anonymous class suffixes (e.g., `Foo$1`
     * becomes `Foo`).
     *
     *
     * Note: This will not be called if a [manual tag][.tag] was specified.
     */
    @Nullable
    private fun createStackElementTag(@NonNull element: StackTraceElement): String {
        var tag = element.className
        val m = ANONYMOUS_CLASS.matcher(tag)
        if (m.find()) {
            tag = m.replaceAll("")
        }
        tag = tag.substring(tag.lastIndexOf('.') + 1)
        // Tag length limit was removed in API 24.
        return if (tag.length <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tag
        } else tag.substring(0, MAX_TAG_LENGTH)
    }

    private fun getTag(): String {
        // DO NOT switch this to Thread.getCurrentThread().getStackTrace(). The test will pass
        // because Robolectric runs them on the JVM but on Android the elements are different.
        val stackTrace = Throwable().stackTrace
        if (stackTrace.size <= CALL_STACK_INDEX)
            return BuildConfig.LIBRARY_PACKAGE_NAME
        return createStackElementTag(stackTrace[CALL_STACK_INDEX])
    }
}

