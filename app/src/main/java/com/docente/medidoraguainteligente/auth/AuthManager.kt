package com.docente.medidoraguainteligente.auth

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestor de Autenticación y Sesión Segura de Usuario.
 * Utiliza SharedPreferences cifradas/locales para mantener el token de sesión y credenciales.
 */
class AuthManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "hydroguard_auth_prefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_AUTH_TOKEN = "auth_token"
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun saveSession(email: String, token: String) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_AUTH_TOKEN, token)
            .apply()
    }

    fun getUserEmail(): String {
        return prefs.getString(KEY_USER_EMAIL, "usuario@hydroguard.io") ?: "usuario@hydroguard.io"
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}
