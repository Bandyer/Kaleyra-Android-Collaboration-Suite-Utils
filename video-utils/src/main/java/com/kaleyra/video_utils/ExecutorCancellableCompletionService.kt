package com.kaleyra.video_utils

import java.util.concurrent.BlockingQueue
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.Future
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit.MILLISECONDS


/**
 * Executor cancellable completion service
 *
 * @param V type of futures handled
 * @param executor where to execute the tasks
 * @property submitQueue submit queue for execution
 * @property completionQueue completion queue after execution
 *
 * @constructor
 */
open class ExecutorCancellableCompletionService<V, E : Executor>(
    val executor: E,
    private val submitQueue: LinkedBlockingQueue<Future<V>> = LinkedBlockingQueue(),
    private val completionQueue: LinkedBlockingQueue<Future<V>> = LinkedBlockingQueue()
) : ExecutorCompletionService<V>(executor, completionQueue) {

    /**
     * @suppress
     */
    override fun submit(task: Callable<V>): Future<V> = super.submit(task).apply { submitQueue.add(this) }

    /**
     * @suppress
     */
    override fun submit(task: Runnable?, result: V): Future<V> = super.submit(task, result).apply { submitQueue.add(this) }

    /**
     * Cancel all the tasks in the submit queue and the completion queue.
     */
    fun cancelAll() {
        cancel(submitQueue)
        cancel(completionQueue)
    }

    /**
     * Tries to remove from the work queue all {@link Future}
     * tasks that have been cancelled or completed. This method can be useful as a
     * storage reclamation operation, that has no other impact on
     * functionality. Cancelled tasks are never executed, but may
     * accumulate in work queues until worker threads can actively
     * remove them. Invoking this method instead tries to remove them now.
     * However, this method may fail to remove tasks in
     * the presence of interference by other threads.
     */
    fun purge() {
        purge(submitQueue) { it.isCancelled || it.isDone }
        purge(completionQueue)
    }

    private fun cancel(queue: BlockingQueue<Future<V>>) {
        val q: BlockingQueue<Future<V>> = queue
        do {
            val submit = kotlin.runCatching {
                q.poll(100, MILLISECONDS)?.apply { cancel(true) }
            }.getOrNull()
        } while (submit != null)
    }

    private fun purge(queue: BlockingQueue<Future<V>>, condition: (Future<V>) -> Boolean = { it.isCancelled }) {
        val q: BlockingQueue<Future<V>> = queue
        try {
            val it = q.iterator()
            while (it.hasNext()) {
                val r = it.next()
                if (condition(r)) it.remove()
            }
        } catch (fallThrough: ConcurrentModificationException) {
            // Take slow path if we encounter interference during traversal.
            // Make copy for traversal and call remove for cancelled entries.
            // The slow path is more likely to be O(N*N).
            for (r in q.toTypedArray()) if (condition(r)) q.remove(r)
        }
    }
}