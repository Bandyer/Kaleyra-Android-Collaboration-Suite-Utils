/*
 *   ~ Copyright (C) 2018 Bandyer S.r.l. All Rights Reserved.
 *   ~ See LICENSE.txt for licensing information
 */

package com.bandyer.android_common

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk

open class BaseTest {

    val context: AppCompatActivity = mockk(relaxed = true)
    val applicationContext: Context = mockk(relaxed = true)

    init {
        MockKAnnotations.init(this)
        every { context.applicationContext } returns applicationContext
    }
}