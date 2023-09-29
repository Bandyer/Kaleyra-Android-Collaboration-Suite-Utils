/*
 *  Copyright (C) 2022 Kaleyra S.p.a. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.kaleyra.video_utils

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