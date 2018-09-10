

package com.bandyer.android_common.logging

import android.support.annotation.IntDef

/**
 * Base logger abstraction
 *
 * @author kristiyan
 */
interface BaseLogger {

    /**
     * BaseLogger
     */
    companion object Priority {
        /**
         * Verbose priority
         */
        const val VERBOSE = 0

        /**
         * Debug priority
         */
        const val DEBUG = 1

        /**
         * Info priority
         */
        const val INFO = 2

        /**
         * Warn priority
         */
        const val WARN = 3

        /**
         * Error priority
         */
        const val ERROR = 4

        /**
         * Logging priority line
         */
        @IntDef(VERBOSE, DEBUG, INFO, WARN, ERROR)
        @Retention(AnnotationRetention.SOURCE)
        @MustBeDocumented
        annotation class LogPriority
    }

    /**
     * User defined target, must be defined as numbers power of 2, to enable the concatenation of multiple targets of interest.
     */
    val target: Int

    /**
     * Method called for verbose logging
     * @param tag Logging tag
     * @param message Logging message
     */
    fun verbose(tag: String, message: String)

    /**
     * Method called for debug logging
     * @param tag Logging tag
     * @param message Logging message
     */
    fun debug(tag: String, message: String)

    /**
     * Method called for info logging
     * @param tag Logging tag
     * @param message Logging message
     */
    fun info(tag: String, message: String)

    /**
     * Method called for warning logging
     * @param tag Logging tag
     * @param message Logging message
     */
    fun warn(tag: String, message: String)

    /**
     * Method called for error logging
     * @param tag Logging tag
     * @param message Logging message
     */
    fun error(tag: String, message: String)
}