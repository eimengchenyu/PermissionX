package com.chw.permissionx.demo

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.chw.permissionx.PermissionXUtils

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun requestPermission(functionName: String, vararg permission: String) {
        PermissionXUtils.logD(" $functionName ")
        PermissionXUtils.requestPermissions(permissions = *permission,
            showLog = true,
            onPermissionGranded = {
                PermissionXUtils.logD(" $functionName 权限申请通过 ")
            },
            onPermissionDined = { dinedList, noShowRationableList ->
                dinedList.forEach {
                    PermissionXUtils.logD(" $functionName 权限被拒绝 = $it ")
                }
                noShowRationableList.forEach {
                    PermissionXUtils.logD(" $functionName 权限被拒绝且不显示弹窗 = $it ")
                }
            })
    }

    fun clickAppDetailSetting(view: View) {
        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${packageName}")
        })
    }

    fun clickNotificationPermission(view: View) {
        requestPermission(
            "clickNotificationPermission",
            Manifest.permission.ACCESS_NOTIFICATION_POLICY
        )
    }

    fun clickAlertWindowPermission(view: View) {
        requestPermission(
            "clickAlertWindowPermission",
            Manifest.permission.SYSTEM_ALERT_WINDOW
        )
    }

    fun clickWriteSettingsPermission(view: View) {
        requestPermission(
            "clickWriteSettingsPermission",
            Manifest.permission.WRITE_SETTINGS
        )
    }

    fun clickInstallPermission(view: View) {
        requestPermission(
            "clickWriteSettingsPermission",
            Manifest.permission.REQUEST_INSTALL_PACKAGES
        )
    }

    fun clickOldStoragePermission(view: View) {
        requestPermission(
            "clickOldStoragePermission",
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    fun clickNewStoragePermission(view: View) {
        requestPermission(
            "clickNewStoragePermission",
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
        )
    }

    fun clickAccessBackgroundLocationPermission(view: View) {
        requestPermission(
            "clickAccessBackgroundLocationPermission",
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    }

    fun clickLocationPermission(view: View) {
        requestPermission(
            "clickLocationPermission",
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    fun clickAccessMediaLocationPermission(view: View) {
        requestPermission(
            "clickAccessMediaLocationPermission",
            Manifest.permission.ACCESS_MEDIA_LOCATION
        )
    }

    fun clickActivityRecognitionPermission(view: View) {
        requestPermission(
            "clickActivityRecognitionPermission",
            Manifest.permission.ACTIVITY_RECOGNITION
        )
    }

    fun clickAnswerPhoneCallsPermission(view: View) {
        requestPermission(
            "clickAnswerPhoneCallsPermission",
            Manifest.permission.ANSWER_PHONE_CALLS
        )
    }

    fun clickReadPhoneNumbersPermission(view: View) {
        requestPermission(
            "clickReadPhoneNumbersPermission",
            Manifest.permission.READ_PHONE_NUMBERS
        )
    }

    fun clickProcessOutgoingCallsPermission(view: View) {
        requestPermission(
            "clickProcessOutgoingCallsPermission",
            Manifest.permission.PROCESS_OUTGOING_CALLS
        )
    }

    fun clickReadPhoneStatePermission(view: View) {
        requestPermission(
            "clickReadPhoneStatePermission",
            Manifest.permission.READ_PHONE_STATE
        )
    }
}