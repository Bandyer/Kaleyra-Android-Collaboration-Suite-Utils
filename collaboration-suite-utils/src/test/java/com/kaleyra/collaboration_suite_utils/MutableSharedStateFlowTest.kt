package com.kaleyra.collaboration_suite_utils

import dev.olog.flow.test.observer.test
import kotlinx.coroutines.test.runBlockingTest
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
}
