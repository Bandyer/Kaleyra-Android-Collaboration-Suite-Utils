/*
 *  Copyright (C) 2022 Kaleyra S.p.a. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.kaleyra.collaboration_suite_utils.audio

import android.content.Context
import android.media.AudioManager.FLAG_SHOW_UI
import android.os.Build
import com.kaleyra.collaboration_suite_utils.ContextRetainer

/**
 *  Manager for the call's audio
 *
 * @constructor
 */
object CallAudioManager {

    private val manager = ContextRetainer.context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager

    /**
     * The current call volume. This volume goes from 0 to 5.
     */
    val currentVolume: Int
        get() = manager.getStreamVolume(if (manager.isBluetoothScoOn) 6 else android.media.AudioManager.STREAM_VOICE_CALL)

    /**
     * The max call volume
     */
    val maxVolume: Int
        get() = manager.getStreamMaxVolume(if (manager.isBluetoothScoOn) 6 else android.media.AudioManager.STREAM_VOICE_CALL)

    /**
     * The min call volume
     */
    val minVolume = when {
        manager.isBluetoothScoOn -> 0
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> manager.getStreamMinVolume(android.media.AudioManager.STREAM_VOICE_CALL)
        else -> 1
    }

    /**
     * Set the call volume. The accepted values are from 0 to 5.
     *
     * @param value Int
     */
    fun setVolume(value: Int) {
        if(value < minVolume || value > maxVolume) return
        manager.setStreamVolume(if (manager.isBluetoothScoOn) 6 else android.media.AudioManager.STREAM_VOICE_CALL, value, FLAG_SHOW_UI)
    }
}