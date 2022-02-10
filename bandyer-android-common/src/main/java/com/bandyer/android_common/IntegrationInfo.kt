/*
 * Copyright (C) 2021 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.android_common

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Build.VERSION
import androidx.startup.Initializer

/**
 * Integration info representing the library, app and device details
 */
object IntegrationInfo : Initializer<Unit> {

    /**
     * Lib info
     */
    val libInfo by lazy { LibInfo() }

    /**
     * Device info
     */
    val deviceInfo by lazy { DeviceInfo }

    /**
     * Host app info
     */
    val hostAppInfo by lazy { HostAppInfo }

    /**
     * @suppress
     */
    override fun create(context: Context): Unit = Unit

    /**
     * @suppress
     */
    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(HostAppInfo::class.java, DeviceInfo::class.java)

    /**
     * @suppress
     */
    override fun toString(): String = "$libInfo $HostAppInfo $DeviceInfo"
}

/**
 * Device info
 */
object DeviceInfo : Initializer<String> {

    /**
     * Platform Operating System
     */
    val platformOS: String = VERSION.RELEASE ?: "Unknown"

    /**
     * Android Sdk Version
     */
    val sdkVersion: String = "${VERSION.SDK_INT}"

    /**
     * Manufacturer Name
     */
    val name: String = Build.MANUFACTURER ?: "Unknown"

    /**
     * Supported device Arch (arm64-v8a etc)
     */
    val arch: String = kotlin.runCatching { Build.SUPPORTED_ABIS.joinToString(",") }.getOrDefault("Unknown")

    /**
     * Phone model
     */
    val model: String = Build.DEVICE ?: "Unknown"

    /**
     * Finger print
     */
    val fingerPrint: String = Build.FINGERPRINT

    /**
     * @suppress
     */
    override fun create(context: Context) = toString()

    /**
     * @suppress
     */
    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

    /**
     * @suppress
     */
    override fun toString() = "OS/Android/$platformOS Device/$name/$model ABIs/$arch API/$sdkVersion Fingerprint/${Build.FINGERPRINT}"
}

/**
 * Host app info
 */
object HostAppInfo : Initializer<String> {

    /**
     * Name
     */
    lateinit var name: String
        private set

    /**
     * Version
     */
    lateinit var version: String
        private set

    /**
     * @suppress
     */
    override fun create(context: Context): String {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        name = context.packageName
        version = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) "${packageInfo?.longVersionCode}" else "${packageInfo?.versionCode?.toLong()}"
        return toString()
    }

    /**
     * @suppress
     */
    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

    /**
     * @suppress
     */
    override fun toString() = "Host/${name}/${version}"
}

/**
 * Bandyer's lib info
 */
class LibInfo {
    /**
     * Name
     */
    var name: String = BuildConfig.LIBRARY_PACKAGE_NAME
        private set

    /**
     * Version
     */
    var version: String = BuildConfig.LIBRARY_VERSION_NAME
        private set

    init {
        kotlin.runCatching {
            val filteredStack = Thread.currentThread().stackTrace.reversed().filter { it.className.startsWith("com.bandyer") }
            // get the last bandyer class extending the application if any
            val demoAppIndex = filteredStack.indexOfLast { kotlin.runCatching { Class.forName(it.className).asSubclass(Application::class.java) }.isSuccess }
            // the next class will be the sdk
            val callerClassName = filteredStack[demoAppIndex + 1].className

            val callerPackageName = Class.forName(callerClassName).`package`!!.name
                .split(".")
                .reduceUntil(
                    { acc ->
                        kotlin.runCatching {
                            val buildConfig = "$acc.BuildConfig"
                            Class.forName(buildConfig)
                        }.isFailure
                    },
                    { acc, k -> "$acc.$k" }
                )

            val buildConfig = Class.forName("$callerPackageName.BuildConfig")
            name = buildConfig.fields.firstOrNull { it.name == "APPLICATION_ID" || it.name == "LIBRARY_PACKAGE_NAME" }?.get(null).toString()
            version = buildConfig.fields.firstOrNull { it.name == "VERSION_NAME" || it.name == "LIBRARY_VERSION_NAME" }?.get(null).toString()
        }
    }

    private inline fun <S, T : S> Iterable<T>.reduceUntil(condition: (acc: S) -> Boolean, operation: (acc: S, T) -> S): S {
        val iterator = this.iterator()
        if (!iterator.hasNext()) throw UnsupportedOperationException("Empty collection can't be reduced.")
        var accumulator: S = iterator.next()
        while (iterator.hasNext() && condition.invoke(accumulator)) {
            accumulator = operation(accumulator, iterator.next())
        }
        return accumulator
    }

    /**
     * @suppress
     */
    override fun toString(): String = "Bandyer/${name}/${version}"
}