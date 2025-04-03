/*
 * Copyright (C) 2023 Kaleyra S.p.A. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.kaleyra.collaboration_suite_utils.dispatcher

import com.kaleyra.video_utils.dispatcher.StandardDispatchers
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Test

class StandardDispatchersTest {

    @Test
    fun testMainDispatcher() {
        assertEquals(Dispatchers.Main, StandardDispatchers.main)
    }

    @Test
    fun testMainImmediateDispatcher() {
        assertEquals(Dispatchers.Main.immediate, StandardDispatchers.mainImmediate)
    }

    @Test
    fun testIODispatcher() {
        assertEquals(Dispatchers.IO, StandardDispatchers.io)
    }

    @Test
    fun testDefaultDispatcher() {
        assertEquals(Dispatchers.Default, StandardDispatchers.default)
    }
}
