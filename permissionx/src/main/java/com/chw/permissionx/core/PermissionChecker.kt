package com.chw.permissionx.core

import android.Manifest
import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.provider.Settings
import com.chw.permissionx.util.*
import java.lang.reflect.InvocationTargetException

/**
 * @author hanwei.chen
 * 2020/11/27 14:11
 */
@SuppressLint("NewApi")
internal object PermissionChecker {
    /**
     * 特殊权限：通知栏权限，Android 6.0及以上需要
     */
    const val SPECIAL_PERMISSION_NOTIFICATION = Manifest.permission.ACCESS_NOTIFICATION_POLICY

    /**
     * 特殊权限：悬浮窗权限，Android 6.0及以上需要
     */
    const val SPECIAL_PERMISSION_SYSTEM_ALERT_WINDOW = Manifest.permission.SYSTEM_ALERT_WINDOW

    /**
     * 特殊权限：系统设置权限，Android 6.0及以上需要
     */
    const val SPECIAL_PERMISSION_WRITE_SETTINGS = Manifest.permission.WRITE_SETTINGS

    /**
     * 特殊权限：安装应用权限，Android 8.0及以上需要
     */
    const val SPECIAL_PERMISSION_INSTALL_PACKAGES = Manifest.permission.REQUEST_INSTALL_PACKAGES

    /**
     * 特殊权限：外部存储权限，Android 11.0及以上需要
     * 用于替代:"android.permission.WRITE_EXTERNAL_STORAGE"和"android.permission.READ_EXTERNAL_STORAGE"
     */
    const val SPECIAL_PERMISSION_MANAGE_EXTERNAL_STORAGE =
        Manifest.permission.MANAGE_EXTERNAL_STORAGE

    /**
     * 危险权限：后台获取位置的权限，Android 10.0上支持
     */
    private const val DANGER_PERMISSION_ACCESS_BACKGROUND_LOCATION =
        Manifest.permission.ACCESS_BACKGROUND_LOCATION

    /**
     * 危险权限：获取照片中的位置信息，Android 10.0上支持
     */
    private const val DANGER_PERMISSION_ACCESS_MEDIA_LOCATION =
        Manifest.permission.ACCESS_MEDIA_LOCATION

    /**
     * 危险权限：获取活动步数，Android 10.0上支持
     */
    private const val DANGER_PERMISSION_ACTIVITY_RECOGNITION =
        Manifest.permission.ACTIVITY_RECOGNITION

    /**
     * 危险权限：拨出电话的权限，Android 10.0上已废弃，使用android.permission.ANSWER_PHONE_CALLS替代
     */
    private const val DANGER_PERMISSION_PROCESS_OUTGOING_CALLS =
        Manifest.permission.PROCESS_OUTGOING_CALLS

    /**
     * 危险权限：接听电话的权限，Android 8.0上支持
     */
    private const val DANGER_PERMISSION_ANSWER_PHONE_CALLS = Manifest.permission.ANSWER_PHONE_CALLS

    /**
     * 读危险权限：读取手机号码的权限，Android 8.0上支持
     */
    private const val DANGER_PERMISSION_READ_PHONE_NUMBERS = Manifest.permission.READ_PHONE_NUMBERS

    /**
     * 特殊权限的集合
     */
    private val specialPermissions =
        arrayOf(
            SPECIAL_PERMISSION_NOTIFICATION,
            SPECIAL_PERMISSION_SYSTEM_ALERT_WINDOW,
            SPECIAL_PERMISSION_WRITE_SETTINGS,
            SPECIAL_PERMISSION_INSTALL_PACKAGES,
            SPECIAL_PERMISSION_MANAGE_EXTERNAL_STORAGE
        )

    /**
     * 判断是否有权限
     */
    fun hasPermissions(permissions: Array<out String>): Boolean {
        if (!AndroidUtils.isAndroid6()) return true
        for (permission in permissions) {
            when {
                isSpecialPermission(
                    permission
                ) -> {
                    if (!isSpecialPermissionHasPermission(
                            permission
                        )
                    ) {
                        return false
                    }
                }
                isPermissionDenied(permission) -> return false
            }
        }
        return true
    }

    /**
     * 判断权限是否合法
     */
    fun checkPermissionValid(permissions: Array<out String>): Array<out String>? {
        val noPermissionList = mutableListOf<String>()
        for (permission in permissions) {
            if (isSpecialPermission(permission)) {
                // 处理特殊权限
                if (!isSpecialPermissionHasPermission(permission) && !checkSpecialPermissionToDangerPermission(permission, noPermissionList)) {
                    noPermissionList.add(permission)
                }
            } else {
                // 处理未同意的危险权限
                if (isPermissionDenied(permission) && !checkDangerPermissionToSpecialPermission(permission, noPermissionList)) {
                    checkDangerPermissionToDangerPermission(permission, permissions, noPermissionList)
                }
            }
        }
        return noPermissionList.toTypedArray()
    }

    /**
     * 是否是特殊权限
     */
    fun isSpecialPermission(permission: String) = specialPermissions.contains(permission)

    /**
     * 检测对应的特殊权限是否有权限
     */
    fun isSpecialPermissionHasPermission(permission: String): Boolean {
        return when (permission) {
            SPECIAL_PERMISSION_NOTIFICATION -> hasNotificationPermission()
            SPECIAL_PERMISSION_SYSTEM_ALERT_WINDOW -> hasSystemAlertWindowPermission()
            SPECIAL_PERMISSION_WRITE_SETTINGS -> hasWriteSettingsPermission()
            SPECIAL_PERMISSION_INSTALL_PACKAGES -> hasInstallPackagesPermission()
            SPECIAL_PERMISSION_MANAGE_EXTERNAL_STORAGE -> hasManageExternalStoragePermission()
            else -> false
        }
    }

    /**
     * 判断特殊权限是否需要转换为危险权限
     */
    private fun checkSpecialPermissionToDangerPermission(
        permission: String,
        noPermissionList: MutableList<String>
    ): Boolean {
        if (!AndroidUtils.isAndroid11() && permission == SPECIAL_PERMISSION_MANAGE_EXTERNAL_STORAGE) {
            // 非android11的系统，且又申请Android11的存储权限，需要做转换
            val list =
                getAndroidManifestPermissionList()
            val writeExternalStoragePermission =
                checkManifestPermissionAndAddToNoPermissionList(
                    list,
                    noPermissionList,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            val readExternalStoragePermission =
                checkManifestPermissionAndAddToNoPermissionList(
                    list,
                    noPermissionList,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            if (!writeExternalStoragePermission && !readExternalStoragePermission) {
                throw NoAndroid11StoragePermissionException()
            } else {
                return true
            }
        }
        return false
    }

    /**
     * 判断危险权限是否需要转换为特殊权限
     */
    private fun checkDangerPermissionToSpecialPermission(
        permission: String,
        noPermissionList: MutableList<String>
    ): Boolean {
        when {
            AndroidUtils.isAndroid11() -> {
                if (permission == Manifest.permission.WRITE_EXTERNAL_STORAGE || permission == Manifest.permission.READ_EXTERNAL_STORAGE) {
                    // android11系统，且申请的又是旧版的存储权限，需要转换为android11的存储权限
                    if (!isSpecialPermissionHasPermission(SPECIAL_PERMISSION_MANAGE_EXTERNAL_STORAGE)
                        && !noPermissionList.contains(SPECIAL_PERMISSION_MANAGE_EXTERNAL_STORAGE)
                    ) {
                        if (!getAndroidManifestPermissionList()
                                .contains(
                                    SPECIAL_PERMISSION_MANAGE_EXTERNAL_STORAGE
                                )
                        ) {
                            throw Android11StoragePermissionException()
                        }
                        noPermissionList.add(SPECIAL_PERMISSION_MANAGE_EXTERNAL_STORAGE)
                    }
                    return true
                }
            }
        }
        return false
    }

    /**
     * 判断危险权限是否需要转换到其他的危险权限
     * 如低版本系统请求高版本系统的危险权限
     */
    private fun checkDangerPermissionToDangerPermission(
        permission: String,
        permissions: Array<out String>,
        noPermissionList: MutableList<String>
    ) {
        var addPermission = true
        when (permission) {
            DANGER_PERMISSION_ACTIVITY_RECOGNITION -> {
                if (!AndroidUtils.isAndroid10()) {
                    addPermission = false
                }
            }
            DANGER_PERMISSION_PROCESS_OUTGOING_CALLS -> {
                if (AndroidUtils.isAndroid8()
                    && !permissions.contains(DANGER_PERMISSION_ANSWER_PHONE_CALLS)
                ) {
                    addPermission = false
                    val list =
                        getAndroidManifestPermissionList()
                    if (!checkManifestPermissionAndAddToNoPermissionList(
                            list,
                            noPermissionList,
                            DANGER_PERMISSION_ANSWER_PHONE_CALLS
                        )
                    ) {
                        throw Android8ProcessOutgoingCallsPermissionException()
                    }
                }
            }
            DANGER_PERMISSION_ANSWER_PHONE_CALLS -> {
                if (!AndroidUtils.isAndroid8()
                    && !permissions.contains(DANGER_PERMISSION_PROCESS_OUTGOING_CALLS)
                ) {
                    addPermission = false
                    val list =
                        getAndroidManifestPermissionList()
                    if (!checkManifestPermissionAndAddToNoPermissionList(
                            list,
                            noPermissionList,
                            DANGER_PERMISSION_PROCESS_OUTGOING_CALLS
                        )
                    ) {
                        throw NoAndroid8AnswerPhoneCallsPermissionException()
                    }
                }
            }
            DANGER_PERMISSION_READ_PHONE_NUMBERS -> {
                if (!AndroidUtils.isAndroid8() && !permissions.contains(Manifest.permission.READ_PHONE_STATE)) {
                    addPermission = false
                    val list =
                        getAndroidManifestPermissionList()
                    if (!checkManifestPermissionAndAddToNoPermissionList(
                            list,
                            noPermissionList,
                            Manifest.permission.READ_PHONE_STATE
                        )
                    ) {
                        throw NoAndroid8ReadPhoneNumbersPermissionException()
                    }
                }
            }
            Manifest.permission.READ_PHONE_STATE -> {
                if (AndroidUtils.isAndroid8()
                    && !permissions.contains(DANGER_PERMISSION_READ_PHONE_NUMBERS)
                ) {
                    addPermission = false
                    val list =
                        getAndroidManifestPermissionList()
                    if (!checkManifestPermissionAndAddToNoPermissionList(
                            list,
                            noPermissionList,
                            DANGER_PERMISSION_READ_PHONE_NUMBERS
                        )
                    ) {
                        throw Android8ReadPhoneStatePermissionException()
                    }
                }
            }
            DANGER_PERMISSION_ACCESS_MEDIA_LOCATION -> {
                if (!AndroidUtils.isAndroid10()) {
                    addPermission = false
                }
            }
            DANGER_PERMISSION_ACCESS_BACKGROUND_LOCATION -> {
                if (AndroidUtils.isAndroid10()) {
                    if (!hasPermissions(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                        )
                        && !hasPermissions(
                            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
                        )
                    ) {
                        handleDangerPermissionAccessBackgroundLocation(
                            permissions,
                            noPermissionList,
                            true
                        )
                    }
                } else {
                    addPermission = false
                    handleDangerPermissionAccessBackgroundLocation(
                        permissions,
                        noPermissionList
                    )
                }
            }
        }
        if (addPermission && !noPermissionList.contains(permission)) {
            noPermissionList.add(permission)
        }
    }

    private fun handleDangerPermissionAccessBackgroundLocation(
        permissions: Array<out String>,
        noPermissionList: MutableList<String>,
        android10RequestBackgroundLocation: Boolean = false
    ) {
        LogUtils.e("android.permission.ACCESS_BACKGROUND_LOCATION is android10.0's api , you need use android.permission.ACCESS_FINE_LOCATION or android.permission.ACCESS_COARSE_LOCATION!")
        if (!permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION)
            && !permissions.contains(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            val list =
                getAndroidManifestPermissionList()
            val accessFineLocation =
                checkManifestPermissionAndAddToNoPermissionList(
                    list,
                    noPermissionList,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            val accessCoarseLocation =
                checkManifestPermissionAndAddToNoPermissionList(
                    list,
                    noPermissionList,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            if (!accessFineLocation && !accessCoarseLocation) {
                if (android10RequestBackgroundLocation) {
                    throw Android10AccessBackgroundLocationPermissionException()
                } else {
                    throw NoAndroid10AccessBackgroundLocationPermissionException()
                }
            }
        }
    }

    private fun checkManifestPermissionAndAddToNoPermissionList(
        manifestPermissionList: Array<out String>,
        noPermissionList: MutableList<String>,
        permission: String
    ) = manifestPermissionList.contains(permission).also {
        if (it && !noPermissionList.contains(permission) && isPermissionDenied(
                permission
            )
        ) {
            noPermissionList.add(permission)
        }
    }

    /**
     * 检测是否有特殊权限
     */
    fun hasSpecialPermission(permissions: Array<out String>): Boolean {
        if (permissions.isNullOrEmpty()) return false
        for (permission in permissions) {
            if (specialPermissions.contains(permission)) {
                return true
            }
        }
        return false
    }

    /**
     * 是否有外部存储的权限
     */
    private fun hasManageExternalStoragePermission() =
        if (AndroidUtils.isAndroid11()) Environment.isExternalStorageManager() else hasPermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )

    /**
     * 是否有安装应用的权限
     */
    private fun hasWriteSettingsPermission() =
        if (AndroidUtils.isAndroid6()) Settings.System.canWrite(ContextUtils.context) else true

    /**
     * 是否有安装应用的权限
     */
    private fun hasInstallPackagesPermission() =
        if (AndroidUtils.isAndroid8()) ContextUtils.context.packageManager.canRequestPackageInstalls() else true

    /**
     * 是否有悬浮窗权限
     */
    private fun hasSystemAlertWindowPermission() =
        if (AndroidUtils.isAndroid6()) Settings.canDrawOverlays(ContextUtils.context) else true

    /**
     * 是否有通知栏权限
     */
    private fun hasNotificationPermission(): Boolean {
        return when {
            AndroidUtils.isAndroid7() -> ContextUtils.context.getSystemService(
                NotificationManager::class.java
            ).areNotificationsEnabled()
            AndroidUtils.isAndroid6() -> {
                val appOps =
                    ContextUtils.context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                try {
                    val method = appOps.javaClass.getMethod(
                        "checkOpNoThrow", Integer.TYPE, Integer.TYPE,
                        String::class.java
                    )
                    val field =
                        appOps.javaClass.getDeclaredField("OP_POST_NOTIFICATION")
                    val value = field[Int::class.java] as Int
                    method.invoke(
                        appOps,
                        value,
                        ContextUtils.context.applicationInfo.uid,
                        ContextUtils.context.packageName
                    ) as Int == AppOpsManager.MODE_ALLOWED
                } catch (ignored: NoSuchMethodException) {
                    true
                } catch (ignored: NoSuchFieldException) {
                    true
                } catch (ignored: InvocationTargetException) {
                    true
                } catch (ignored: IllegalAccessException) {
                    true
                } catch (ignored: RuntimeException) {
                    true
                }
            }
            else -> true
        }
    }

    /**
     * 获取AndroidManifest.xml下的权限列表
     */
    fun getAndroidManifestPermissionList(): Array<out String> {
        return try {
            ContextUtils.context.packageManager.getPackageInfo(
                ContextUtils.context.packageName,
                PackageManager.GET_PERMISSIONS
            ).requestedPermissions
        } catch (e: PackageManager.NameNotFoundException) {
            emptyArray()
        }
    }

    /**
     * 判断是否有权限需要延迟申请
     * 如：Android10.0后台定位权限，需要先申请定位权限，才能申请后台定位权限
     */
    fun checkNeedRequestLater(permissions: Array<out String>): Array<out String> {
        if (permissions.isNullOrEmpty()) return permissions
        if (permissions.contains(DANGER_PERMISSION_ACCESS_BACKGROUND_LOCATION)) {
            return arrayOf(DANGER_PERMISSION_ACCESS_BACKGROUND_LOCATION)
        }
        return emptyArray()
    }

    private fun isPermissionDenied(permission: String) = ContextUtils.context.run {
        PackageManager.PERMISSION_DENIED == packageManager.checkPermission(permission, packageName)
    }
}