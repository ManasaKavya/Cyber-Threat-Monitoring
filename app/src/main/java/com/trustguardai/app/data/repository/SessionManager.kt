package com.trustguardai.app.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.trustguardai.app.data.model.User
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SessionManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "trustguard_session",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveSession(token: String, user: User) {
        val editor = sharedPreferences.edit()
        editor.putString("jwt_token", token)
        editor.putString("user_data", Json.encodeToString(user))
        editor.apply()
    }

    fun getToken(): String? = sharedPreferences.getString("jwt_token", null)

    fun getUser(): User? {
        val jsonString = sharedPreferences.getString("user_data", null) ?: return null
        return try {
            Json.decodeFromString<User>(jsonString)
        } catch (e: Exception) {
            null
        }
    }

    fun logout() {
        sharedPreferences.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = getToken() != null
}
