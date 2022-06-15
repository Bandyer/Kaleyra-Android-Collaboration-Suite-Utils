/*
 *  Copyright (C) 2022 Kaleyra S.p.a. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.kaleyra.collaboration_suite_utils.network_observer

import android.Manifest.permission.ACCESS_WIFI_STATE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import androidx.annotation.RequiresPermission
import com.kaleyra.collaboration_suite_utils.ContextRetainer
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Utility class which allows to observe the WiFi info events
 */
class WiFiObserver @RequiresPermission(ACCESS_WIFI_STATE) constructor() {

    private val wifiManager = ContextRetainer.context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val intentFilter = IntentFilter().apply {
        addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        addAction(WifiManager.RSSI_CHANGED_ACTION)
    }
    private val broadcastReceiver: BroadcastReceiver = WiFiReceiver()

    private val _wifiInfo: MutableSharedFlow<WiFiInfo> =
        MutableSharedFlow(onBufferOverflow = BufferOverflow.DROP_OLDEST, replay = 1)

    /**
     * The wifi info flow
     */
    val wifiInfo = _wifiInfo.asSharedFlow()

    private var isRegistered = false

    init {
        start()
    }

    /**
     * Start the observer
     */
    fun start() = if(!isRegistered) {ContextRetainer.context.registerReceiver(broadcastReceiver, intentFilter); isRegistered = true } else Unit

    /**
     * Stop the observer
     */
    fun stop() = if(isRegistered) { ContextRetainer.context.unregisterReceiver(broadcastReceiver); isRegistered = false } else Unit

    /**
     * A broadcast receiver which handle the WiFi events
     */
    inner class WiFiReceiver : BroadcastReceiver() {
        private var state = WifiManager.WIFI_STATE_UNKNOWN

        /**
         * @suppress
         */
        override fun onReceive(context: Context?, intent: Intent?) {
            intent ?: return
            if(intent.action == WifiManager.WIFI_STATE_CHANGED_ACTION)
                state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)

            val rssi = wifiManager.connectionInfo.rssi
            _wifiInfo.tryEmit(WiFiInfo(mapState(state), WiFiInfo.Level.getValue(rssi)))
        }

        private fun mapState(state: Int): WiFiInfo.State = when (state) {
            WifiManager.WIFI_STATE_ENABLING -> WiFiInfo.State.ENABLING
            WifiManager.WIFI_STATE_ENABLED -> WiFiInfo.State.ENABLED
            WifiManager.WIFI_STATE_DISABLING -> WiFiInfo.State.DISABLING
            WifiManager.WIFI_STATE_DISABLED -> WiFiInfo.State.DISABLED
            else -> WiFiInfo.State.UNKNOWN
        }
    }
}