package com.baaki.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.authDataStore by preferencesDataStore(name = "auth_prefs")

data class UserSession(
    val email: String,
    val fullName: String,
    val businessName: String,
    val phone: String,
    val isLoggedIn: Boolean
)

class AuthRepository(private val context: Context) {
    companion object {
        private val KEY_EMAIL = stringPreferencesKey("user_email")
        private val KEY_NAME = stringPreferencesKey("user_name")
        private val KEY_BUSINESS = stringPreferencesKey("user_business")
        private val KEY_PHONE = stringPreferencesKey("user_phone")
        private val KEY_LOGGED_IN = stringPreferencesKey("is_logged_in")
    }

    val userSessionFlow: Flow<UserSession> = context.authDataStore.data.map { prefs ->
        val email = prefs[KEY_EMAIL] ?: "rajesh@sharmapackaging.in"
        val name = prefs[KEY_NAME] ?: "Rajesh Sharma"
        val business = prefs[KEY_BUSINESS] ?: "Sharma Packaging"
        val phone = prefs[KEY_PHONE] ?: "9876543210"
        val isLoggedIn = prefs[KEY_LOGGED_IN]?.toBoolean() ?: true

        UserSession(email, name, business, phone, isLoggedIn)
    }

    suspend fun login(emailInput: String, passInput: String): Result<Unit> {
        val email = emailInput.trim().lowercase()
        if (email.isBlank() || passInput.isBlank()) {
            return Result.failure(IllegalArgumentException("Email and password cannot be empty."))
        }
        context.authDataStore.edit { prefs ->
            prefs[KEY_EMAIL] = email
            if (prefs[KEY_NAME] == null) prefs[KEY_NAME] = "Rajesh Sharma"
            if (prefs[KEY_BUSINESS] == null) prefs[KEY_BUSINESS] = "Sharma Packaging"
            if (prefs[KEY_PHONE] == null) prefs[KEY_PHONE] = "9876543210"
            prefs[KEY_LOGGED_IN] = "true"
        }
        return Result.success(Unit)
    }

    suspend fun register(fullName: String, emailInput: String, phone: String, passInput: String): Result<Unit> {
        val email = emailInput.trim().lowercase()
        if (fullName.isBlank() || email.isBlank() || passInput.isBlank()) {
            return Result.failure(IllegalArgumentException("Full name, email, and password are required."))
        }
        context.authDataStore.edit { prefs ->
            prefs[KEY_NAME] = fullName.trim()
            prefs[KEY_EMAIL] = email
            prefs[KEY_PHONE] = phone.trim()
            prefs[KEY_BUSINESS] = "Sharma Packaging"
            prefs[KEY_LOGGED_IN] = "true"
        }
        return Result.success(Unit)
    }

    suspend fun updateProfile(fullName: String, businessName: String, phone: String): Result<Unit> {
        context.authDataStore.edit { prefs ->
            prefs[KEY_NAME] = fullName.trim()
            prefs[KEY_BUSINESS] = businessName.trim()
            prefs[KEY_PHONE] = phone.trim()
        }
        return Result.success(Unit)
    }

    suspend fun logout() {
        context.authDataStore.edit { prefs ->
            prefs[KEY_LOGGED_IN] = "false"
        }
    }
}
