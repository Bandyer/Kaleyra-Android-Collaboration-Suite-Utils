package com.bandyer.android_common.extensions

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Process

/**
 * Internally checks if permission has been granted
 * @param permission the permission to be checked
 * @return check for the granted permission
 */
fun Context.hasPermission(permission: String): Boolean {
    return checkPermission(permission, Process.myPid(), Process.myUid()) == PackageManager.PERMISSION_GRANTED
}