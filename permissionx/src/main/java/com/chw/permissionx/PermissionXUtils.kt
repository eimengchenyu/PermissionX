package com.chw.permissionx

import com.chw.permissionx.core.PermissionChecker
import com.chw.permissionx.core.PermissionEngine
import com.chw.permissionx.util.LogUtils

/**
 * @author hanwei.chen
 * 2020/11/25 14:53
 */
object PermissionXUtils {
    /**
     *
     * @param permissions 权限
     * @param useRecommendPermission 默认true，使用推荐的权限，如在高版本使用电话、电话号码、存储、后台定位等权限(若AndroidManifest.xml中没配置对应权限，则会出现崩溃)
     * @param onPermissionGranded 权限申请通过
     * @param onPermissionDined 权限申请拒绝
     */
    fun requestPermissions(
        vararg permissions: String,
        onPermissionGranded: (() -> Unit)? = null,
        onPermissionDined: ((dinedList: Array<out String>, noShowRationableList: Array<out String>) -> Unit)? = null,
        showLog: Boolean = false
    ) {
        LogUtils.showLog = showLog
        PermissionEngine.requestPermissions(
            permissions,
            onPermissionGranded,
            onPermissionDined
        )
    }

    fun hasPermissions(vararg permissions: String) = PermissionChecker.hasPermissions(permissions)

    fun logD(msg: String) = LogUtils.d(msg)
    fun logE(msg: String) = LogUtils.e(msg)
}