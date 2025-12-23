package com.example.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Maneja la opción "recordar email" en el login.
 */
public class SharedPrefManager {
    private static final String PREF_NAME = "login_prefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_REMEMBER = "remember";

    private final SharedPreferences prefs;

    public SharedPrefManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Guarda si se debe recordar el email del usuario.
     * Si remember = false, limpia el email.
     */
    public void saveLogin(String email, boolean remember) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_REMEMBER, remember);
        if (remember) {
            editor.putString(KEY_EMAIL, email);
        } else {
            editor.remove(KEY_EMAIL);
        }
        editor.apply();
    }

    public boolean shouldRemember() {
        return prefs.getBoolean(KEY_REMEMBER, false);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
