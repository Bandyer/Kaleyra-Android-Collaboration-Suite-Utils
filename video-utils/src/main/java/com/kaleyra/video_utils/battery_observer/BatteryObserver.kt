/*
 *  Copyright (C) 2022 Kaleyra S.p.a. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.kaleyra.video_utils.battery_observer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import java.lang.ref.WeakReference
import kotlin.math.roundToInt

/**
 * Utility class which allows to observe the battery info events
 */
class BatteryObserver(context: Context) {

    private val weakContext: WeakReference<Context> = WeakReference(context)
    private val batteryInfo: MutableSharedFlow<BatteryInfo> =
        MutableSharedFlow(onBufferOverflow = BufferOverflow.DROP_OLDEST, replay = 1)
    private val broadcastReceiver: BroadcastReceiver = BatteryReceiver()

    private var isRegistered = false

    init {
        start()
    }

    /**
     * Start the observer
     */
    fun start() = if (!isRegistered) { weakContext.get()?.registerReceiver(broadcastReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED)); isRegistered = true } else Unit

    /**
     * Call to observe the battery info events
     *
     * @return SharedFlow<BatteryInfo>
     */
    fun observe(): SharedFlow<BatteryInfo> = batteryInfo.asSharedFlow()

    /**
     * Stop the observer
     */
    fun stop() = if(isRegistered) { weakContext.get()?.unregisterReceiver(broadcastReceiver); isRegistered = false } else Unit

    /**
     * A broadcast receiver which handle the battery events
     */
    inner class BatteryReceiver : BroadcastReceiver() {
        /**
         * @suppress
         */
        override fun onReceive(context: Context?, intent: Intent?) {
            intent ?: return

            val defaultValue = -1
            val status: Int = intent.getIntExtra(BatteryManager.EXTRA_STATUS, defaultValue)
            val plugged: Int = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, defaultValue)
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, defaultValue)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, defaultValue)

            batteryInfo.tryEmit(
                BatteryInfo(
                    mapStatus(status),
                    mapPlugged(plugged),
                    computePercentage(level, scale)
                )
            )
        }

        private fun mapStatus(status: Int): BatteryInfo.State = when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> BatteryInfo.State.CHARGING
            BatteryManager.BATTERY_STATUS_DISCHARGING -> BatteryInfo.State.DISCHARGING
            BatteryManager.BATTERY_STATUS_FULL -> BatteryInfo.State.FULL
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> BatteryInfo.State.NOT_CHARGING
            else -> BatteryInfo.State.UNKNOWN
        }

        private fun mapPlugged(plugged: Int): BatteryInfo.Plugged = when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> BatteryInfo.Plugged.AC
            BatteryManager.BATTERY_PLUGGED_USB -> BatteryInfo.Plugged.USB
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> BatteryInfo.Plugged.WIRELESS
            else -> BatteryInfo.Plugged.UNKNOWN
        }

        private fun computePercentage(level: Int, scale: Int): Int =
            if (level > 0 && scale > 0) (level * 100 / scale.toFloat()).roundToInt()
            else 0
    }
}

