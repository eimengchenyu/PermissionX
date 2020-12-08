package com.chw.permissionx.core

import android.content.Intent
import com.chw.permissionx.util.*
import com.chw.permissionx.view.PermissionActivity
import com.chw.permissionx.view.PermissionFragment

/**
 * @author hanwei.chen
 * 2020/11/25 16:24
 */
internal object PermissionEngine {
    var innerPermissions: Array<out String>? = null
    var innerOnPermissionGranded: (() -> Unit)? = null
    var innerOnPermissionDined: ((dinedList: Array<out String>, noShowRationableList: Array<out String>) -> Unit)? =
        null

    fun release() {
        innerPermissions = null
        innerOnPermissionGranded = null
        innerOnPermissionDined = null
    }

    fun requestPermissions(
        permissions: Array<out String>,
        onPermissionGranded: (() -> Unit)? = null,
        onPermissionDined: ((dinedList: Array<out String>, noShowRationableList: Array<out String>) -> Unit)? = null
    ) {
        if (permissions.isNullOrEmpty()) throw RequestPermissionEmptyException()
        val manifestPermissions = PermissionChecker.getAndroidManifestPermissionList()
        if (manifestPermissions.isNullOrEmpty()) throw AndroidManifestPermissionEmptyException()
        for (permission in permissions) {
            if (!manifestPermissions.contains(permission)) {
                throw AndroidManifestPermissionException(
                    permission
                )
            }
        }
        if (!AndroidUtils.isAndroid6()) {
            onPermissionGranded?.invoke()
            return
        }
        val noPermissionList = PermissionChecker.checkPermissionValid(permissions)
        if (noPermissionList.isNullOrEmpty()) {
            onPermissionGranded?.invoke()
            return
        }
        noPermissionList.forEach {
            LogUtils.d(" 需要申请的权限 -> $it ")
        }
        if (ContextUtils.isContextValid(LifecycleManager.getTopActivity())) {
            PermissionFragment.instance(
                LifecycleManager.getTopActivity(),
                noPermissionList,
                onPermissionGranded,
                onPermissionDined
            )
        } else {
            innerPermissions = noPermissionList
            innerOnPermissionGranded = onPermissionGranded
            innerOnPermissionDined = onPermissionDined
            ContextUtils.context.apply {
                startActivity(Intent(this, PermissionActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
        }
    }
}