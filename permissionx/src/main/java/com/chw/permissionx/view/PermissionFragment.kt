package com.chw.permissionx.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import com.chw.permissionx.core.PermissionChecker
import com.chw.permissionx.core.PermissionIntent
import com.chw.permissionx.util.ContextUtils
import com.chw.permissionx.util.LogUtils

/**
 * @author hanwei.chen
 * 2020/11/25 13:57
 */
@SuppressLint("NewApi")
internal class PermissionFragment : Fragment() {
    companion object {
        private const val TAG = "InnerPermissionFragment"
        private const val REQUEST_CODE = 0x333
        fun instance(
            activity: Activity?,
            permissions: Array<out String>?,
            onPermissionGranded: (() -> Unit)?,
            onPermissionDined: ((dinedList: Array<out String>, noShowRationableList: Array<out String>) -> Unit)? = null
        ) {
            if (!ContextUtils.isContextValid(activity)) return
            if (permissions.isNullOrEmpty()) return
            var fragment =
                activity!!.fragmentManager.findFragmentByTag(TAG) as? PermissionFragment
            val add = if (fragment == null) {
                fragment = PermissionFragment()
                true
            } else {
                false
            }
            fragment.permissions = permissions
            fragment.onPermissionGranded = onPermissionGranded
            fragment.onPermissionDined = onPermissionDined
            if (add) {
                activity.fragmentManager.beginTransaction().add(
                    fragment,
                    TAG
                ).commitAllowingStateLoss()
            } else {
                fragment.doRequestPermissions()
            }
        }
    }

    /**
     * 申请的权限
     */
    private var permissions: Array<out String>? = null

    /**
     * 权限申请通过
     */
    private var onPermissionGranded: (() -> Unit)? = null

    /**
     * 权限拒绝
     */
    private var onPermissionDined: ((dinedList: Array<out String>, noShowRationableList: Array<out String>) -> Unit)? =
        null

    /**
     * 是否包含特殊权限
     */
    private var hasSpecialPermission = false

    /**
     * 危险权限
     */
    private val dangerPermissionList = mutableListOf<String>()

    /**
     * 需要延迟申请的危险权限
     */
    private val dangerPermissionLaterList = mutableListOf<String>()

    /**
     * 特殊权限的集合
     */
    private val specialPermissionList = mutableListOf<String>()

    /**
     * 被拒绝的权限
     */
    private val denidPermissions = mutableListOf<String>()

    /**
     * 被拒绝且不再显示的权限
     */
    private val rationalePermissions = mutableListOf<String>()

    fun doRequestPermissions() {
        LogUtils.d(" doRequestPermissions ")
        if (!ContextUtils.isContextValid(activity)) return
        val permissions = this.permissions
        if (permissions.isNullOrEmpty()) {
            activity?.fragmentManager?.beginTransaction()?.remove(this)?.commitAllowingStateLoss()
            return
        }
        dangerPermissionList.clear()
        dangerPermissionLaterList.clear()
        specialPermissionList.clear()
        denidPermissions.clear()
        rationalePermissions.clear()
        hasSpecialPermission =
            PermissionChecker.hasSpecialPermission(
                permissions
            )
        PermissionChecker.checkNeedRequestLater(permissions).apply {
            if (!isNullOrEmpty()) {
                dangerPermissionLaterList.addAll(this)
            }
        }
        if (hasSpecialPermission) {
            for (permission in permissions) {
                if (PermissionChecker.isSpecialPermission(permission)) {
                    specialPermissionList.add(permission)
                } else if (!dangerPermissionLaterList.contains(permission)) {
                    dangerPermissionList.add(permission)
                }
            }
        } else {
            for (permission in permissions) {
                if (!dangerPermissionLaterList.contains(permission)) {
                    dangerPermissionList.add(permission)
                }
            }
        }
        checkRequestPermission()
    }

    private fun checkRequestPermission() {
        when {
            dangerPermissionList.isNotEmpty() -> {
                val permissions = dangerPermissionList.toTypedArray()
                dangerPermissionList.clear()
                requestPermissions(permissions, REQUEST_CODE)
            }
            dangerPermissionLaterList.isNotEmpty() -> {
                val permissions = dangerPermissionLaterList.toTypedArray()
                dangerPermissionLaterList.clear()
                requestPermissions(permissions, REQUEST_CODE)
            }
            else -> doRequestSpecialPermission()
        }
    }

    private fun doRequestSpecialPermission() {
        if (!ContextUtils.isContextValid(activity)) return
        if (specialPermissionList.isNullOrEmpty()) {
            finish()
            return
        }
        startActivityForResult(
            PermissionIntent.getPermissionIntent(
                specialPermissionList[0]
            ),
            REQUEST_CODE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtils.d(" $this onCreate ")
        doRequestPermissions()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val startTime = System.currentTimeMillis()
        if (requestCode != REQUEST_CODE) return
        if (!ContextUtils.isContextValid(activity)) return
        for (i in grantResults.indices) {
            if (grantResults[i] != 0) {
                val permission = permissions[i]
                denidPermissions.add(permission)
                if (!shouldShowRequestPermissionRationale(permission)) {
                    rationalePermissions.add(permission)
                }
            }
        }
        LogUtils.d(" 耗时 = ${System.currentTimeMillis() - startTime} , 拒绝的权限 = ${denidPermissions.size} , 拒绝且不再显示的权限 = ${rationalePermissions.size} ")
        checkRequestPermission()
    }

    private fun finish() {
        if (!ContextUtils.isContextValid(activity)) return
        when {
            denidPermissions.isNotEmpty() -> onPermissionDined?.invoke(
                denidPermissions.toTypedArray(),
                rationalePermissions.toTypedArray()
            )
            else -> onPermissionGranded?.invoke()
        }
        activity?.fragmentManager?.beginTransaction()?.remove(this)?.commitAllowingStateLoss()
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d(" $this onDestroy ")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQUEST_CODE) return
        if (!ContextUtils.isContextValid(activity)) return
        specialPermissionList.removeAt(0).apply {
            if (!PermissionChecker.isSpecialPermissionHasPermission(
                    this
                )
            ) {
                denidPermissions.add(this)
            }
        }
        checkRequestPermission()
    }
}