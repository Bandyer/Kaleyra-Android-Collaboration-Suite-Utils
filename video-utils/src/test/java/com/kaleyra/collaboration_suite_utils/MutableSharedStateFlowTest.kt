package com.kaleyra.collaboration_suite_utils

import com.kaleyra.video_utils.MutableSharedStateFlow
import dev.olog.flow.test.observer.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class MutableSharedStateFlowTest : BaseTest() {

    @Test
    fun create() = runBlockingTest {
        val stateFlow: MutableSharedStateFlow<String> = MutableSharedStateFlow("ciao")
        assertEquals("ciao", stateFlow.value)
    }

    @Test
    fun collectAllChanges() = runBlockingTest {
        val stateFlow = MutableSharedStateFlow("ciao0")
        stateFlow.test(this) {
            assertValues(
                "ciao0",
                "ciao1",
                "ciao2",
            )
        }
        stateFlow.value = "ciao1"
        stateFlow.value = "ciao2"
        stateFlow.value = "ciao2"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testFlowValuesEmittedNearEachOther() = runTest {
        val stateFlow = MutableSharedStateFlow("ciao0")

        val result = mutableListOf<String>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            stateFlow.toList(result)
        }

        val emits = 10000
        val deferred = (1..<emits).map {
            async(Dispatchers.IO) {
                stateFlow.value = "ciao$it"
            }
        }
        deferred.awaitAll()

        println(result)

        assertEquals(emits, result.size)
    }
}
