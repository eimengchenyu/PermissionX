package com.chw.permissionx.core

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.chw.permissionx.util.AndroidUtils
import com.chw.permissionx.util.ContextUtils

/**
 * @author hanwei.chen
 * 2020/11/27 14:19
 */
internal object PermissionIntent {
    fun getPermissionIntent(permission: String): Intent {
        return when (permission) {
            PermissionChecker.SPECIAL_PERMISSION_NOTIFICATION -> getNotificationIntent()
            PermissionChecker.SPECIAL_PERMISSION_SYSTEM_ALERT_WINDOW -> getSystemAlertWindowIntent()
            PermissionChecker.SPECIAL_PERMISSION_WRITE_SETTINGS -> getWriteSettingsIntent()
            PermissionChecker.SPECIAL_PERMISSION_INSTALL_PACKAGES -> getInstallPackagesIntent()
            PermissionChecker.SPECIAL_PERMISSION_MANAGE_EXTERNAL_STORAGE -> getManageExternalStorageIntent()
            else -> getApplicationDetailsSettings()
        }
    }

    private fun getPackageUriString() = "package:${ContextUtils.context.packageName}"

    /**
     * 外部存储权限
     */
    private fun getManageExternalStorageIntent(): Intent {
        return checkAndGetValidIntent(
            if (AndroidUtils.isAndroid11())
                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data =
                        Uri.parse(getPackageUriString())
                }
            else null)
    }

    /**
     * 安装应用权限
     */
    private fun getInstallPackagesIntent(): Intent {
        return checkAndGetValidIntent(
            if (AndroidUtils.isAndroid8())
                Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data =
                        Uri.parse(getPackageUriString())
                }
            else null)
    }

    /**
     * 系统设置权限
     */
    private fun getWriteSettingsIntent(): Intent {
        return checkAndGetValidIntent(
            if (AndroidUtils.isAndroid6())
                Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                    data =
                        Uri.parse(getPackageUriString())
                }
            else null)
    }

    /**
     * 悬浮窗权限
     */
    private fun getSystemAlertWindowIntent(): Intent {
        return checkAndGetValidIntent(
            if (AndroidUtils.isAndroid6())
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                    data =
                        Uri.parse(getPackageUriString())
                }
            else null)
    }

    /**
     * 通知栏权限
     */
    private fun getNotificationIntent(): Intent {
        return checkAndGetValidIntent(
            if (AndroidUtils.isAndroid8())
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(
                        Settings.EXTRA_APP_PACKAGE,
                        ContextUtils.context.packageName
                    )
                }
            else null)
    }

    private fun checkAndGetValidIntent(intent: Intent?) =
        if (ContextUtils.isIntentValid(intent)) intent!! else getApplicationDetailsSettings()

    /**
     * 应用详情
     */
    private fun getApplicationDetailsSettings() =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse(getPackageUriString())
        }
}