package com.chw.permissionx.view

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.chw.permissionx.core.PermissionEngine


/**
 * Android M及以上才会显示的界面
 * @author hanwei.chen
 * 2020/11/25 16:29
 */
@SuppressLint("NewApi")
internal class PermissionActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.apply {
            clearFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            )
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
            navigationBarColor = Color.TRANSPARENT
        }
        PermissionFragment.instance(
            this,
            PermissionEngine.innerPermissions,
            onPermissionGranded = {
                finish()
                PermissionEngine.innerOnPermissionGranded?.invoke()
                PermissionEngine.release()
            },
            onPermissionDined = { dinedList, noShowRationableList ->
                finish()
                PermissionEngine.innerOnPermissionDined?.invoke(dinedList, noShowRationableList)
                PermissionEngine.release()
            }
        )
    }
}