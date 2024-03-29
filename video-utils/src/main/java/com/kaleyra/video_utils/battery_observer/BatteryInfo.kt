/*
 *  Copyright (C) 2022 Kaleyra S.p.a. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.kaleyra.video_utils.battery_observer

/**
 * BatteryInfo
 *
 * @property state The battery state
 * @property plugged To what the device is plugged
 * @property percentage The battery percentage
 * @constructor
 */
data class BatteryInfo(
    val state: State = State.UNKNOWN,
    val plugged: Plugged = Plugged.UNKNOWN,
    val percentage: Int = -1
) {

    /**
     * Battery state
     */
    enum class State {
        /**
         * c h a r g i n g
         */
        CHARGING,

        /**
         * d i s c h a r g i n g
         */
        DISCHARGING,

        /**
         * f u l l
         */
        FULL,

        /**
         * n o t_c h a r g i n g
         */
        NOT_CHARGING,

        /**
         * u n k n o w n
         */
        UNKNOWN
    }

    /**
     * Battery charger types
     */
    enum class Plugged {
        /**
         * a c
         */
        AC,

        /**
         * u s b
         */
        USB,

        /**
         * w i r e l e s s
         */
        WIRELESS,

        /**
         * u n k n o w n
         */
        UNKNOWN
    }
}

