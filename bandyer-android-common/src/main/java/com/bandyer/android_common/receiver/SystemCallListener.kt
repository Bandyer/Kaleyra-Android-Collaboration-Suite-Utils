/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.android_common.receiver

import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import java.lang.ref.WeakReference

/**
 * Detector for system telephony calls.
 * @param systemCallStateListener optional SystemCallStateListener that will be notified of system telephony call started or ended.
 * @constructor
 */
class SystemCallListener(systemCallStateListener: SystemCallStateListener? = null) : PhoneStateListener() {

    companion object {
        val TAG = "SystemCallListener"
    }

    private var hasOngoingSystemCall: Boolean? = null
    private var listener: WeakReference<SystemCallStateListener>? = null

    init {
        if (systemCallStateListener != null)
            listener = WeakReference(systemCallStateListener)
    }

    override fun onCallStateChanged(state: Int, incomingNumber: String?) {
        val isIdle = when (state) {
            TelephonyManager.CALL_STATE_RINGING, TelephonyManager.CALL_STATE_OFFHOOK -> false
            else -> true
        }
        if (hasOngoingSystemCall == null && !isIdle) return

        hasOngoingSystemCall = !isIdle
        if (hasOngoingSystemCall!!) listener?.get()?.onSystemCallStarted()
        else listener?.get()?.onSystemCallEnded()
    }

    fun hasOngoingSystemCall(): Boolean = hasOngoingSystemCall == true

    /**
     * Represent a listener that will be notified when a system telephony call starts or ends.
     */
    interface SystemCallStateListener {
        fun onSystemCallStarted()
        fun onSystemCallEnded()
    }
}