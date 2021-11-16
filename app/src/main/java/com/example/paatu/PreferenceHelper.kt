package com.example.paatu

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper {

    private val SHARED_PREFERENCE_KEY = "com.example.paatu"

    fun getSharedPreference(context: Context): SharedPreferences? {
        return context.getSharedPreferences(SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE)
    }

    fun writeAppPermissionStatusToPreference(context: Context?, key: String?, value: Boolean) {
        val editor = getSharedPreference(context!!)!!.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getAppPermissionStatusFromPreference(context: Context?, key: String?): Boolean {
        return getSharedPreference(context!!)!!.getBoolean(key, false)
    }

}