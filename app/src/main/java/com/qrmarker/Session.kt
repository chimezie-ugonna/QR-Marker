package com.qrmarker

import android.content.Context
import android.content.SharedPreferences

class Session(context: Context) {
    private val sp: SharedPreferences =
        context.getSharedPreferences(
            context.resources.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
    private val spe: SharedPreferences.Editor = sp.edit()

    fun password(value: String) {
        spe.putString("password", value)
        spe.commit()
    }

    fun password(): String? {
        return sp.getString("password", "")
    }
}