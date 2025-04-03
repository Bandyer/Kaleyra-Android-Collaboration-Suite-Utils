package com.kaleyra.video_utils

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

/**
 * Utility functions related to the Android SDK.
 */
object SdkUtils {

    /**
     * Checks if the current device's SDK version is at least Android 11 (API level 30 or R).
     *
     * This function utilizes the [ChecksSdkIntAtLeast] annotation to indicate that
     * the return value is based on the SDK_INT and guarantees the specified API level
     * if the function returns `true`.
     *
     * @return `true` if the device's SDK version is at least Android 11 (API 30), `false` otherwise.
     */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
    fun isAtLeastR(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }
}