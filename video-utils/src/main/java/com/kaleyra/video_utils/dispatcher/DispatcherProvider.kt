/*
 * Copyright (C) 2023 Kaleyra S.p.A. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.kaleyra.video_utils.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Provides access to common CoroutineDispatcher instances.
 */
interface DispatcherProvider {
    /**
     * The main CoroutineDispatcher, typically used for UI updates.
     */
    val main: CoroutineDispatcher

    /**
     * The immediate main CoroutineDispatcher, typically used for UI updates that need to run immediately.
     */
    val mainImmediate: CoroutineDispatcher

    /**
     * The IO CoroutineDispatcher, typically used for network and disk operations.
     */
    val io: CoroutineDispatcher

    /**
     * The default CoroutineDispatcher, typically used for CPU-intensive tasks.
     */
    val default: CoroutineDispatcher
}
