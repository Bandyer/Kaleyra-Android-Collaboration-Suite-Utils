package com.kaleyra.collaboration_suite_utils.thermal

import android.content.Context
import android.os.PowerManager
import com.kaleyra.video_utils.dispatcher.DispatcherProvider
import com.kaleyra.video_utils.SdkUtils
import com.kaleyra.video_utils.thermal.DeviceThermalManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeviceThermalManagerTest {

    private lateinit var deviceThermalManager: DeviceThermalManager
    private lateinit var context: Context
    private lateinit var powerManager: PowerManager
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: TestScope

    @Before
    fun setup() {
        mockkObject(SdkUtils)
        context = mockk()
        powerManager = mockk()
        every { context.getSystemService(Context.POWER_SERVICE) } returns powerManager
        testDispatcher = StandardTestDispatcher()
        testScope = TestScope(testDispatcher)
        deviceThermalManager =
            DeviceThermalManager(context, StandardTestDispatchers(testDispatcher))
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `isThermalApiSupported returns true when API is supported and thermal headroom is valid`() {
        every { SdkUtils.isAtLeastR() } returns true
        every { powerManager.getThermalHeadroom(15) } returns 1.0f

        assertTrue(deviceThermalManager.isThermalApiSupported())
    }

    @Test
    fun `isThermalApiSupported returns false when API is not supported`() {
        every { SdkUtils.isAtLeastR() } returns false

        assertFalse(deviceThermalManager.isThermalApiSupported())
    }

    @Test
    fun `isThermalApiSupported returns false when thermal headroom is NaN`() {
        every { SdkUtils.isAtLeastR() } returns true
        every { powerManager.getThermalHeadroom(15) } returns Float.NaN

        assertFalse(deviceThermalManager.isThermalApiSupported())
    }

    @Test
    fun `isThermalApiSupported returns false when thermal headroom is 0`() {
        every { SdkUtils.isAtLeastR() } returns true
        every { powerManager.getThermalHeadroom(15) } returns 0f

        assertFalse(deviceThermalManager.isThermalApiSupported())
    }

    @Test
    fun `updateThermalStatus updates throttlingStatus with correct status from PowerManager when status is LIGHT`() =
        runTest(testDispatcher) {
            every { SdkUtils.isAtLeastR() } returns true
            every { powerManager.getThermalHeadroom(15) } returns 1.0f
            every { powerManager.currentThermalStatus } returns PowerManager.THERMAL_STATUS_LIGHT

            deviceThermalManager.startThermalMonitoring()
            advanceTimeBy(DeviceThermalManager.POLLING_INTERVAL_LIGHT_MS)
            Assert.assertEquals(
                DeviceThermalManager.ThrottlingStatus.LIGHT,
                deviceThermalManager.throttlingStatus.first()
            )
            deviceThermalManager.stopThermalMonitoring()
        }

    @Test
    fun `updateThermalStatus updates throttlingStatus with correct status from thermalHeadroom when status is LIGHT`() =
        runTest(testDispatcher) {
            every { SdkUtils.isAtLeastR() } returns true
            every { powerManager.getThermalHeadroom(15) } returns 0.86f
            every { powerManager.currentThermalStatus } returns PowerManager.THERMAL_STATUS_NONE

            deviceThermalManager.startThermalMonitoring()
            advanceTimeBy(DeviceThermalManager.POLLING_INTERVAL_LIGHT_MS)
            Assert.assertEquals(
                DeviceThermalManager.ThrottlingStatus.LIGHT,
                deviceThermalManager.throttlingStatus.first()
            )
            deviceThermalManager.stopThermalMonitoring()
        }

    @Test
    fun `updateThermalStatus updates throttlingStatus with correct status from PowerManager when status is MODERATE`() =
        runTest(testDispatcher) {
            every { SdkUtils.isAtLeastR() } returns true
            every { powerManager.getThermalHeadroom(15) } returns 1.0f
            every { powerManager.currentThermalStatus } returns PowerManager.THERMAL_STATUS_MODERATE

            deviceThermalManager.startThermalMonitoring()
            advanceTimeBy(DeviceThermalManager.POLLING_INTERVAL_MODERATE_MS)
            Assert.assertEquals(
                DeviceThermalManager.ThrottlingStatus.MODERATE,
                deviceThermalManager.throttlingStatus.first()
            )
            deviceThermalManager.stopThermalMonitoring()
        }

    @Test
    fun `updateThermalStatus updates throttlingStatus with correct status from thermalHeadroom when status is MODERATE`() =
        runTest(testDispatcher) {
            every { SdkUtils.isAtLeastR() } returns true
            every { powerManager.getThermalHeadroom(15) } returns 0.96f
            every { powerManager.currentThermalStatus } returns PowerManager.THERMAL_STATUS_NONE

            deviceThermalManager.startThermalMonitoring()
            advanceTimeBy(DeviceThermalManager.POLLING_INTERVAL_MODERATE_MS)
            Assert.assertEquals(
                DeviceThermalManager.ThrottlingStatus.MODERATE,
                deviceThermalManager.throttlingStatus.first()
            )
            deviceThermalManager.stopThermalMonitoring()
        }

    @Test
    fun `updateThermalStatus updates throttlingStatus with correct status from PowerManager when status is SEVERE`() =
        runTest(testDispatcher) {
            every { SdkUtils.isAtLeastR() } returns true
            every { powerManager.getThermalHeadroom(15) } returns .85f
            every { powerManager.currentThermalStatus } returns PowerManager.THERMAL_STATUS_SEVERE

            deviceThermalManager.startThermalMonitoring()
            advanceTimeBy(DeviceThermalManager.POLLING_INTERVAL_SEVERE_MS)
            Assert.assertEquals(
                DeviceThermalManager.ThrottlingStatus.SEVERE,
                deviceThermalManager.throttlingStatus.first()
            )
            deviceThermalManager.stopThermalMonitoring()
        }

    @Test
    fun `updateThermalStatus updates throttlingStatus with correct status from thermalHeadroom when status is SEVERE`() =
        runTest(testDispatcher) {
            every { SdkUtils.isAtLeastR() } returns true
            every { powerManager.getThermalHeadroom(15) } returns 1.01f
            every { powerManager.currentThermalStatus } returns PowerManager.THERMAL_STATUS_NONE

            deviceThermalManager.startThermalMonitoring()
            advanceTimeBy(DeviceThermalManager.POLLING_INTERVAL_SEVERE_MS)
            Assert.assertEquals(
                DeviceThermalManager.ThrottlingStatus.SEVERE,
                deviceThermalManager.throttlingStatus.first()
            )
            deviceThermalManager.stopThermalMonitoring()
        }

    @Test
    fun `updateThermalStatus doesn't update throttlingStatus when isThermalApiSupported is false`() =
        runTest(testDispatcher) {
            every { SdkUtils.isAtLeastR() } returns false
            every { powerManager.getThermalHeadroom(15) } returns 0.96f
            every { powerManager.currentThermalStatus } returns PowerManager.THERMAL_STATUS_NONE

            deviceThermalManager.startThermalMonitoring()
            advanceTimeBy(DeviceThermalManager.POLLING_INTERVAL_LIGHT_MS * 2)
            Assert.assertEquals(
                DeviceThermalManager.ThrottlingStatus.UNKNOWN,
                deviceThermalManager.throttlingStatus.first()
            )
            deviceThermalManager.stopThermalMonitoring()
        }

    @Test
    fun `startThermalMonitoring starts polling and updates throttlingStatus`() =
        runTest(testDispatcher) {
            every { SdkUtils.isAtLeastR() } returns true
            every { powerManager.getThermalHeadroom(15) } returns 1.0f
            every { powerManager.currentThermalStatus } returns PowerManager.THERMAL_STATUS_MODERATE

            deviceThermalManager.startThermalMonitoring()
            advanceTimeBy(DeviceThermalManager.POLLING_INTERVAL_LIGHT_MS * 2)
            Assert.assertEquals(
                DeviceThermalManager.ThrottlingStatus.MODERATE,
                deviceThermalManager.throttlingStatus.first()
            )
            deviceThermalManager.stopThermalMonitoring()
        }

    @Test
    fun `stopThermalMonitoring cancels polling job`() = runTest(testDispatcher) {
        every { SdkUtils.isAtLeastR() } returns true
        every { powerManager.getThermalHeadroom(15) } returns 1.0f
        every { powerManager.currentThermalStatus } returns PowerManager.THERMAL_STATUS_MODERATE

        deviceThermalManager.startThermalMonitoring()
        deviceThermalManager.stopThermalMonitoring()
        advanceTimeBy(DeviceThermalManager.POLLING_INTERVAL_LIGHT_MS * 2)
        Assert.assertEquals(
            DeviceThermalManager.ThrottlingStatus.UNKNOWN,
            deviceThermalManager.throttlingStatus.first()
        )
    }
}

class StandardTestDispatchers(testDispatcher: TestDispatcher) : DispatcherProvider {
    override val main: CoroutineDispatcher = testDispatcher
    override val mainImmediate: CoroutineDispatcher = testDispatcher
    override val io: CoroutineDispatcher = testDispatcher
    override val default: CoroutineDispatcher = testDispatcher
}


