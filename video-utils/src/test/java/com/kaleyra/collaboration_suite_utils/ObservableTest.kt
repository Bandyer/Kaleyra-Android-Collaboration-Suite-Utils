/*
 *  Copyright (C) 2022 Kaleyra S.p.a. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.kaleyra.collaboration_suite_utils

import com.kaleyra.video_utils.observer.BaseObserverCollection
import com.kaleyra.video_utils.observer.ObserverCollection
import com.kaleyra.video_utils.observer.ObserverFactory
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 *
 * @author kristiyan
 */
class ObservableTest : BaseTest() {

    internal interface TestObserver {
        fun test()
    }

    internal interface TestObserverCollection : ObserverCollection<TestObserver>, TestObserver

    @Test(expected = RuntimeException::class)
    fun testHandlerPost() {
        handler.post { throw RuntimeException("executed") }
    }

    @Test(expected = RuntimeException::class)
    fun testExecutorSubmit() {
        executor.submit { throw RuntimeException("executed") }.get()
    }

    @Test
    fun testRemoveAt() {
        val observers = spyk(BaseObserverCollection<TestObserver>(handler, executor))

        val obs = mockk<TestObserver>()
        val obs2 = mockk<TestObserver>()

        observers.add(obs)
        observers.add(obs2)

        observers.remove(obs2)

        verify(exactly = 1) {
            observers.remove(obs2)
        }

        observers.size { assertEquals(1, it) }

        observers.contains(obs) { assertTrue(it) }
    }

    @Test
    fun testRemoveAll() {
        val observers = spyk(BaseObserverCollection<Any>(handler, executor))

        observers.add(mockk(relaxed = true))
        observers.add(mockk(relaxed = true))
        observers.add(mockk(relaxed = true))

        observers.clear()

        observers.isEmpty {
            assertTrue(it)
        }
    }

    @Test
    fun testNotifyAndRemoveAt() {
        val observers = spyk(ObserverFactory.createObserverCollection<TestObserverCollection>(handler, executor))

        val obs = mockk<TestObserver>(relaxed = true)
        val obs2 = mockk<TestObserver>(relaxed = true)

        observers.add(obs)
        observers.add(obs2)

        observers.test()

        observers.getObservers {
            assertEquals(2, it.size)
        }

        verify(exactly = 1) {
            obs.test()
        }

        verify(exactly = 1) {
            obs2.test()
        }

        observers.remove(obs2)

        observers.getObservers {
            assertEquals(1, it.size)
        }

        observers.contains(obs) {
            assertTrue(it)
        }

        verify(exactly = 1) {
            observers.remove(obs2)
        }
    }

    @Test
    fun testConcurrentModificationException() {

        val observers = ObserverFactory.createObserverCollection<TestObserverCollection>(handler, executor)
        val obs = mockk<TestObserver>(relaxed = true)
        val obs1 = mockk<TestObserver>(relaxed = true)
        val obs2 = mockk<TestObserver>(relaxed = true)

        observers.add(obs)
        observers.add(obs1)
        observers.add(obs2)

        observers.forEach {
            observers.remove(obs2)
        }

        observers.getObservers {
            assertEquals(2, it.size)
        }

        observers.forEach {
            observers.clear()
        }

        observers.getObservers {
            assertEquals(0, it.size)
        }
    }

    @Test
    fun testProxy() {
        val observers = ObserverFactory.createObserverCollection<TestObserverCollection>(handler, executor)
        val obs = mockk<TestObserver>(relaxed = true)
        val obs1 = mockk<TestObserver>(relaxed = true)
        val obs2 = mockk<TestObserver>(relaxed = true)

        observers.add(obs)
        observers.add(obs1)
        observers.add(obs2)

        observers.test()


        verify(exactly = 1) {
            obs.test()
        }

        verify(exactly = 1) {
            obs1.test()
        }

        verify(exactly = 1) {
            obs2.test()
        }

    }

    @Test
    fun testNotifyAllAndThenRemoveAll() {
        val observers = ObserverFactory.createObserverCollection<TestObserverCollection>(handler, executor)

        val obs = mockk<TestObserver>(relaxed = true)
        val obs1 = mockk<TestObserver>(relaxed = true)
        val obs2 = mockk<TestObserver>(relaxed = true)
        observers.add(obs)
        observers.add(obs1)
        observers.add(obs2)

        observers.size { assertTrue(it == 3) }

        observers.test()

        verify(exactly = 1) {
            obs.test()
        }

        verify(exactly = 1) {
            obs1.test()
        }

        verify(exactly = 1) {
            obs2.test()
        }

        observers.clear()

        observers.isEmpty { assertTrue(it) }
    }
}