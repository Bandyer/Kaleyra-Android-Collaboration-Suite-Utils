package com.kaleyra.video_utils.thermal

import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for monitoring the thermal state of the device.
 *
 * This interface provides access to the current throttling status of the device
 * and allows starting and stopping thermal monitoring.
 */
interface ThermalMonitor {
    /**
     * Represents the status of throttling for a specific operation or resource.
     */
    val throttlingStatus: StateFlow<DeviceThermalManager.ThrottlingStatus>

    /**
     * Checks if the Thermal API is supported on the current device.
     *
     * The Thermal API provides information about the thermal status of the device,
     * such as CPU and GPU throttling. This information can be used to adapt the
     * application's behavior to avoid overheating.
     *
     * This function checks for the presence of the necessary system features and API levels
     * required to use the Thermal API.
     *
     * @return `true` if the Thermal API is supported, `false` otherwise.
     *
     * @see android.os.HardwarePropertiesManager
     * @see android.os.PowerManager
     * @see android.os.PowerManager.ThermalStatus
     * @see android.content.Context.getSystemService
     */
    fun isThermalApiSupported(): Boolean

    /**
     * Starts monitoring the device's thermal state.
     *
     * This function initiates the thermal monitoring process. It typically involves
     * registering listeners or callbacks to receive updates on the device's temperature
     * and thermal throttling status. The specific implementation details of how
     * thermal state is monitored are platform-dependent.
     *
     * This function should be called when the application needs to be aware of the
     * device's thermal condition, for example, before starting a resource-intensive
     * operation like rendering high-quality graphics or processing large files.
     *
     * Note:
     *  - The application is responsible for stopping thermal monitoring when it's no
     *    longer needed to avoid unnecessary battery consumption and system overhead.
     *  - The exact behavior and the frequency of thermal updates depend on the
     *    underlying platform and its thermal management policies.
     *  - It is also possible that the system could restrict or deny the request to monitor
     *    the thermal state in certain circumstances.
     *
     * @see stopThermalMonitoring
     * @see OnThermalStatusChangedCallback
     */
    fun startThermalMonitoring()

    /**
     * Stops the thermal monitoring process.
     *
     * This function is responsible for halting the monitoring of the device's
     * thermal state. It should be called when thermal monitoring is no longer
     * needed, such as when the application is paused or terminated, or when
     * the feature requiring thermal monitoring is deactivated.
     *
     * After calling this function, no further thermal state updates will be
     * received. Any resources used for monitoring, such as background threads
     * or listeners, should be released or unregistered within this function.
     *
     * Note: Calling this function when thermal monitoring is not active has
     * no effect.
     *
     * @see startThermalMonitoring
     */
    fun stopThermalMonitoring()
}