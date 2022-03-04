/*
 *  Copyright (C) 2022 Kaleyra S.p.a. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.kaleyra.collaboration_suite_utils.proximity_listener

import androidx.appcompat.app.AppCompatActivity

/**
 * ProximitySensor is implemented as a boolean-sensor and works based on the lifecycle of the binded activity.
 * It will be automatically unbinded on the activity destroy.
 *
 * It returns just two values "NEAR" or "FAR". Threshold is done on the LUX
 * value i.e. the LUX value of the light sensor is compared with a threshold.
 * A LUX-value more than the threshold means the proximity sensor returns "FAR".
 * Anything less than the threshold value and the sensor  returns "NEAR".
 *
 * @author kristiyan
 */

interface ProximitySensor {

    /**
     * Maximum debounce factor in millis for event reporting
     */
    val debounceMillis: Long

    /**
     * ProximitySensor Instance
     */
    companion object Instance {

        /**
         * Method to bind the ProximitySensor to the current Activity lifecycle
         *
         * @param context Activity
         * @param listener Listener where state changes are forwarded
         * @param debounceMillis Maximum debounce factor in millis for event reporting
         * @return ProximitySensor interface
         */
        @kotlin.jvm.JvmOverloads
        fun bind(context: AppCompatActivity, listener: ProximitySensorListener, debounceMillis: Long = 500): ProximitySensor {
            return ProximityReceiver(context, listener, debounceMillis)
        }

        /**
         * Method to bind the ProximitySensor to the current Activity lifecycle
         *
         * @param context Activity
         * @param debounceMillis Maximum debounce factor in millis for event reporting
         * @return ProximitySensor interface
         */
        @kotlin.jvm.JvmOverloads
        fun <T> bind(context: T, debounceMillis: Long = 500): ProximitySensor where T : AppCompatActivity, T : ProximitySensorListener {
            return bind(context, context, debounceMillis)
        }
    }

    /**
     * Getter for last reported state. Set to true if "near" is reported.
     * @return true for current near state, false otherwise
     */
    fun isNear(): Boolean

    /**
     * Method to call when you are not interested in the ProximitySensor anymore.
     * To re-enable the ProximitySensor after a destroy, the bind method must be called once again.
     */
    fun destroy()
}