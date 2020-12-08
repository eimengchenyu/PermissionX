package com.chw.permissionx.util

import android.os.Build

/**
 * @author hanwei.chen
 * 2020/11/27 14:27
 */
internal object AndroidUtils {
    fun isAndroid6() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    fun isAndroid7() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

    fun isAndroid8() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    fun isAndroid10() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    fun isAndroid11() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
}