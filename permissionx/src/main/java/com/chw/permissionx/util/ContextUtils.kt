package com.chw.permissionx.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

/**
 * @author hanwei.chen
 * 2020/11/25 10:57
 */
internal object ContextUtils {
    lateinit var context: Context

    /**
     * 判断context是否合法
     */
    fun isContextValid(context: Context?): Boolean {
        if (context == null) return false
        if (context is Activity) {
            return !context.isFinishing && if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) !context.isDestroyed else true
        }
        return true
    }

    /**
     * 判断intent是否合法
     */
    fun isIntentValid(intent: Intent?) =
        if (intent == null) false else context.packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        ).isNotEmpty()
}