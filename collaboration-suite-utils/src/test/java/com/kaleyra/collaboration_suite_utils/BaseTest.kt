/*
 *  Copyright (C) 2022 Kaleyra S.p.a. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.kaleyra.collaboration_suite_utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.badoo.mobile.util.WeakHandler
import io.mockk.MockK
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import java.util.concurrent.*

open class BaseTest {

    val context: AppCompatActivity = mockk(relaxed = true)
    val applicationContext: Context = mockk(relaxed = true)

    val handler: WeakHandler = mockk(relaxed = true)
    val executor: ExecutorService = currentThreadExecutorService()

    init {
        MockKAnnotations.init(this)
        every { context.applicationContext } returns applicationContext

        mockkStatic(Executors::class)
        every { Executors.newSingleThreadExecutor() } returns executor

        every { handler.post(any()) } answers {
            executor.execute(firstArg())
            true
        }
    }

    /**
     * create executor service which runs on main thread
     * @return ExecutorService
     */
    private fun currentThreadExecutorService(): ExecutorService {
        val callerRunsPolicy = ThreadPoolExecutor.CallerRunsPolicy()
        return object : ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS, SynchronousQueue(), callerRunsPolicy) {
            override fun execute(command: Runnable?) = callerRunsPolicy.rejectedExecution(command, this)
            override fun submit(task: Runnable?): Future<*> {
                callerRunsPolicy.rejectedExecution(task, this)
                return newTaskFor {}
            }
        }
    }
}