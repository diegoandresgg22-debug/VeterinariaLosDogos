package com.example.myapplication.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

    fun saveLogin(email: String, remember: Boolean) {
        prefs.edit().apply {
            putBoolean("remember", remember)
            if (remember) putString("email", email) else remove("email")
            apply()
        }
    }

    fun shouldRemember(): Boolean = prefs.getBoolean("remember", false) [cite: 324]
    fun getEmail(): String? = prefs.getString("email", null) [cite: 325]
    fun clear() = prefs.edit().clear().apply()
}