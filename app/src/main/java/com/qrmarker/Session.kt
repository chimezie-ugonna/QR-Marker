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

    fun token(value: String) {
        spe.putString("token", value)
        spe.commit()
    }

    fun token(): String? {
        return sp.getString("token", "")
    }

    fun fullName(value: String) {
        spe.putString("fullName", value)
        spe.commit()
    }

    fun fullName(): String? {
        return sp.getString("fullName", "")
    }

    fun email(value: String) {
        spe.putString("email", value)
        spe.commit()
    }

    fun email(): String? {
        return sp.getString("email", "")
    }

    fun userType(value: String) {
        spe.putString("userType", value)
        spe.commit()
    }

    fun userType(): String? {
        return sp.getString("userType", "")
    }

    fun phoneNumber(value: String) {
        spe.putString("phoneNumber", value)
        spe.commit()
    }

    fun phoneNumber(): String? {
        return sp.getString("phoneNumber", "")
    }
}