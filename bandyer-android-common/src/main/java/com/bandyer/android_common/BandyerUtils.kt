package com.bandyer.android_common

import android.os.Looper

/**
 * Helper method which throws an exception  when an assertion has failed.
 */
fun assertIsTrue(condition: Boolean) {
    if (!condition) {
        throw AssertionError("Expected condition to be true")
    }
}

/**
 * Helper function to check that all operations on audio routing are requested on the main tread.
 */
fun checkMainThread() {
    if (Looper.getMainLooper() != null) {
        assertIsTrue(Thread.currentThread() === Looper.getMainLooper().thread)
    }
}