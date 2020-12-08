package com.chw.permissionx.util

import android.util.Log

/**
 * @author hanwei.chen
 * 2020/11/30 15:37
 */
internal object LogUtils {
    private const val TAG = "PermissionX"
    internal var showLog = false
    fun d(msg: String) {
        if (!showLog) return
        Log.d(TAG, msg)
    }

    fun e(msg: String) {
        if (!showLog) return
        Log.e(TAG, msg)
    }
}