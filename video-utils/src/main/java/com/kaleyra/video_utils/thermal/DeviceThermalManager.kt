package com.kaleyra.video_utils.thermal

import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.annotation.ChecksSdkIntAtLeast
import com.kaleyra.video_utils.dispatcher.DispatcherProvider
import com.kaleyra.video_utils.SdkUtils
import com.kaleyra.video_utils.dispatcher.StandardDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Manages device thermal status monitoring and provides information about the current throttling state.
 *
 * This class uses the Android PowerManager's thermal APIs (available from API level R and above) to
 * determine the device's thermal state. It polls the system periodically to check for changes in
 * thermal status and updates a `StateFlow` with the current throttling state.
 *
 * @property context The application context.
 * @property dispatcherProvider The dispatcher provider used for coroutines. Defaults to [StandardDispatchers].
 */
class DeviceThermalManager(
    context: Context,
    private val dispatcherProvider: DispatcherProvider = StandardDispatchers
) : ThermalMonitor {

    /**
     * Represents the current throttling status of the device.
     *
     * This enum defines various levels of throttling, from no throttling to complete shutdown.
     * Each status has an associated integer value, providing a numerical representation of the throttling severity.
     *
     * @property value The integer value representing the throttling severity.
     */
    enum class ThrottlingStatus(val value: Int) {
        NONE(0),
        LIGHT(1),
        MODERATE(2),
        SEVERE(3),
        CRITICAL(4),
        EMERGENCY(5),
        SHUTDOWN(6),
        UNKNOWN(-1)
    }

    private val powerManager: PowerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    private var pollingJob: Job? = null

    private val _throttlingStatusFlow = MutableStateFlow(ThrottlingStatus.UNKNOWN)
    override val throttlingStatus: StateFlow<ThrottlingStatus> = _throttlingStatusFlow
    
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
    override fun isThermalApiSupported(): Boolean {
        return if (SdkUtils.isAtLeastR()) {
            val thermalHeadroom = powerManager.getThermalHeadroom(THERMAL_FORECAST_SECONDS)
            // Checked the thermal Headroom API's initial return value
            // it is NaN or 0，so, return false (not supported)
            thermalHeadroom != 0f && !thermalHeadroom.isNaN()
        } else false
    }

 
    /**
     * Starts monitoring the device's thermal status and updates the throttling status periodically.
     *
     * This function continuously polls the device's thermal status at intervals determined by the
     * current throttling level. If the thermal monitoring is already active, this function will
     * do nothing.
     *
     * The monitoring continues indefinitely until it is manually stopped.
     */
    override fun startThermalMonitoring() {
        if (pollingJob?.isActive == true) return
        pollingJob = CoroutineScope(dispatcherProvider.default).launch {
            while (isActive) {
                val throttlingStatus = updateThrottlingStatus()
                val pollingIntervalMs = when (throttlingStatus) {
                    ThrottlingStatus.NONE, ThrottlingStatus.LIGHT -> POLLING_INTERVAL_LIGHT_MS
                    ThrottlingStatus.MODERATE -> POLLING_INTERVAL_MODERATE_MS
                    else -> POLLING_INTERVAL_SEVERE_MS
                }
                delay(pollingIntervalMs)
            }
        }
    }

    /**
     * Stops the thermal monitoring process.
     */
    override fun stopThermalMonitoring() {
        pollingJob?.cancel()
        pollingJob = null
    }

    private fun updateThrottlingStatus(): ThrottlingStatus {
        if (!isThermalApiSupported()) return ThrottlingStatus.UNKNOWN

        // Use thermalStatus API to check if it returns valid values.
        val currentThermalStatus = powerManager.currentThermalStatus
        return if (currentThermalStatus > PowerManager.THERMAL_STATUS_NONE) {
            // The device is being thermally throttled
            when (currentThermalStatus) {
                PowerManager.THERMAL_STATUS_LIGHT -> ThrottlingStatus.LIGHT
                PowerManager.THERMAL_STATUS_MODERATE -> ThrottlingStatus.MODERATE
                PowerManager.THERMAL_STATUS_SEVERE -> ThrottlingStatus.SEVERE
                PowerManager.THERMAL_STATUS_CRITICAL -> ThrottlingStatus.CRITICAL
                PowerManager.THERMAL_STATUS_EMERGENCY -> ThrottlingStatus.EMERGENCY
                PowerManager.THERMAL_STATUS_SHUTDOWN -> ThrottlingStatus.SHUTDOWN
                else -> ThrottlingStatus.NONE
            }
        } else {
            // The device is not being thermally throttled currently. However, it
            // could also be an indicator that the ThermalStatus API may not be
            // supported in the device.
            // Currently this API uses predefined threshold values for thermal status
            // mapping. In the future you may be able to query this directly.
            val thermalHeadroom = powerManager.getThermalHeadroom(THERMAL_FORECAST_SECONDS)
            when {
                thermalHeadroom > 1.0f -> ThrottlingStatus.SEVERE
                thermalHeadroom > 0.95f -> ThrottlingStatus.MODERATE
                thermalHeadroom > 0.85f -> ThrottlingStatus.LIGHT
                else -> ThrottlingStatus.NONE
            }
        }.also { throttlingStatus ->
            _throttlingStatusFlow.value = throttlingStatus
        }
    }

    companion object {
        internal const val THERMAL_FORECAST_SECONDS = 15

        // N.B. This value cannot be less of 10 seconds, otherwise the API will returns NaN.
        internal const val POLLING_INTERVAL_LIGHT_MS = 60_000L
        internal const val POLLING_INTERVAL_MODERATE_MS = 30_000L
        internal const val POLLING_INTERVAL_SEVERE_MS = 10_000L
    }
}

