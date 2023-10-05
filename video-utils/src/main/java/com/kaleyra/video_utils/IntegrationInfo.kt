/*
 *  Copyright (C) 2022 Kaleyra S.p.a. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.kaleyra.video_utils

import android.content.Context
import android.os.Build
import android.os.Build.VERSION
import androidx.startup.Initializer

/**
 * IntegrationInfo representing the library, app and device details
 */
class IntegrationInfo : Initializer<Unit> {

    /**
     * IntegrationInfo instance
     */
    companion object {

        private lateinit var appPackageName: String

        /**
         * Lib info
         */
        var libInfo by cached { LibInfo(appPackageName) }
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

        /**
         * @suppress
         */
        override fun toString() = libInfo?.let { "$libInfo $hostAppInfo $deviceInfo" } ?: ""
    }

    /**
     * @suppress
     */
    @OptIn(ExperimentalStdlibApi::class)
    override fun create(context: Context) {
        appPackageName = with(context.packageName) {
            // skips fourth level package declaration
            "^[^.]*(?:.[^.]*){2}".toRegex().matchAt(this, 0)?.value ?: this
        }
    }

    /**
     * @suppress
     */
    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(HostAppInfo::class.java, DeviceInfo::class.java)

    /**
     * @suppress
     */
    override fun toString() = Companion.toString()
}

/**
 * DeviceInfo represents info about the runnning device
 */
class DeviceInfo : Initializer<String> {

    /**
     * DeviceInfo instance
     */
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

        private var wrapperInfo: List<String>? = null

        /**
         * Wrapper name
         */
        val wrapperName by lazy { wrapperInfo?.get(0)?.takeIf { it.isNotBlank() && it.length <= 100 } }

        /**
         * Wrapper version
         */
        val wrapperVersion by lazy {
            wrapperInfo?.get(1)?.takeIf {
                it.isNotBlank() && it.matches(
                    Regex(
                        "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$"
                    )
                )
            }
        }

        /**
         * @suppress
         */
        override fun toString() = "OS/Android/$platformOS Device/$name/$model ${
            "Wrapper/$wrapperName/$wrapperVersion ".takeIf { wrapperName != null && wrapperVersion != null } ?: ""
        }ABIs/$arch API/$sdkVersion Fingerprint/${Build.FINGERPRINT}"
    }

    /**
     * @suppress
     */
    override fun create(context: Context): String {
        wrapperInfo = kotlin.runCatching {
            context.assets.open("kaleyra_video_wrapper_info.txt").bufferedReader().use { it.readText() }
        }.getOrNull()?.split("/", limit = 2)?.map { it.trim() }?.takeIf { it.size == 2 }
        return toString()
    }

    /**
     * @suppress
     */
    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

    /**
     * @suppress
     */
    override fun toString() = Companion.toString()
}

/**
 * HostAppInfo represents info about the application that is hosting the library
 */
class HostAppInfo : Initializer<String> {

    /**
     * HostAppInfo instance
     */
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

        /**
         * @suppress
         */
        override fun toString() = "Host/$name/$version"
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
    override fun toString() = Companion.toString()
}

/**
 * LibInfo representing the library info
 */
class LibInfo internal constructor(appPackageName: String) {

    /**
     * LibInfo instance
     */
    companion object {
        private const val legacyCompanyPrefix = "com.bandyer"
        private const val companyPrefix = "com.kaleyra"
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
        val stackTraces = Thread.currentThread().stackTrace.filter { (it.className.startsWith(companyPrefix) || it.className.startsWith(legacyCompanyPrefix)) && !it.className.startsWith(appPackageName) }
        getFirstValidLibInfo(stackTraces)
    }

    private fun getFirstValidLibInfo(stackTraces: List<StackTraceElement>) {
        if (stackTraces.isEmpty()) return
        val callerTrace = stackTraces.last()
        kotlin.runCatching {
            val callerClassName = callerTrace.className
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
            name = buildConfig.fields.first { it.name == "LIBRARY_PACKAGE_NAME" }?.get(null).toString()
            version = buildConfig.fields.first { it.name == "LIBRARY_VERSION_NAME" }?.get(null).toString()
            require(name != BuildConfig.LIBRARY_PACKAGE_NAME)
        }.onFailure { getFirstValidLibInfo(stackTraces.minus(callerTrace)) }
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