package com.chw.permissionx.core

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import java.lang.ref.WeakReference

/**
 * @author hanwei.chen
 * 2020/11/25 11:27
 */
internal object LifecycleManager {
    private var topActivity: WeakReference<Activity>? = null

    fun init(context: Context?) {
        // 只需要注册，不需要释放，除非application被回收了
        context?.applicationContext?.apply {
            if (this is Application) {
                registerActivityLifecycleCallbacks(lifecycle)
            }
        }
    }

    fun getTopActivity(): Activity? {
        return topActivity?.get()
    }

    private val lifecycle = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        }

        override fun onActivityDestroyed(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
            topActivity = WeakReference(activity)
        }

        override fun onActivityPaused(activity: Activity) {
            topActivity?.clear()
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }
    }
}