package com.chw.permissionx.core

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.chw.permissionx.util.ContextUtils

/**
 * @author hanwei.chen
 * 2020/11/25 11:10
 */
internal class ApplicationContextProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        context?.applicationContext?.apply {
            ContextUtils.context = this
            LifecycleManager.init(this)
        }
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun query(
        uri: Uri, projection: Array<out String>?, selection: String?,
        selectionArgs: Array<out String>?, sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }
}