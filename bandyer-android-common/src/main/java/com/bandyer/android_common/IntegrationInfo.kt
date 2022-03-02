/*
 * Copyright (C) 2021 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.android_common

import android.content.Context
import android.os.Build
import android.os.Build.VERSION
import androidx.startup.Initializer

/**
 * Integration info representing the library, app and device details
 */
class IntegrationInfo : Initializer<Unit> {

    companion object {

        private lateinit var appPackageName: String

        /**
         * Lib info
         */
        var libInfo by cached { LibInfo(appPackageName).takeIf { it.name != BuildConfig.LIBRARY_PACKAGE_NAME } }
            private set

        /**
         * Device info
         */
        var deviceInfo by cached { libInfo?.let { DeviceInfo } }
            private set

        /**
         * Host app info
         */
        var hostAppInfo by cached { libInfo?.let { HostAppInfo } }
            private set

        override fun toString() = libInfo?.let { "$libInfo $hostAppInfo $deviceInfo" } ?: ""
    }

    /**
     * @suppress
     */
    override fun create(context: Context) {
        appPackageName = context.packageName
    }

    /**
     * @suppress
     */
    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(HostAppInfo::class.java, DeviceInfo::class.java)

    /**
     * @suppress
     */
    override fun toString() = IntegrationInfo.toString()
}

/**
 * Device info
 */
class DeviceInfo : Initializer<String> {

    companion object {
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

        override fun toString() = "OS/Android/$platformOS Device/$name/$model ABIs/$arch API/$sdkVersion Fingerprint/${Build.FINGERPRINT}"
    }

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
    override fun toString() = DeviceInfo.toString()
}

/**
 * Host app info
 */
class HostAppInfo : Initializer<String> {

    companion object {
        private lateinit var mName: String

        private lateinit var mVersion: String

        /**
         * Name
         */
        val name: String
            get() = mName

        /**
         * Version
         */
        val version: String
            get() = mVersion

        override fun toString() = "Host/${name}/${version}"
    }

    /**
     * @suppress
     */
    override fun create(context: Context): String {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        mName = context.packageName
        mVersion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) "${packageInfo?.longVersionCode}" else "${packageInfo?.versionCode?.toLong()}"
        return toString()
    }

    /**
     * @suppress
     */
    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

    /**
     * @suppress
     */
    override fun toString() = HostAppInfo.toString()
}

/**
 * Bandyer's lib info
 */
class LibInfo internal constructor(appPackageName: String) {

    companion object {
        private const val companyPrefix = "com.bandyer"
    }

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
            val callerClassName = Thread.currentThread().stackTrace.last { it.className.startsWith(companyPrefix) && !it.className.startsWith(appPackageName) }.className

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