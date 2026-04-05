package com.fredy.gamevault.core.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "gamevault_session")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_FIRST_NAME_KEY = stringPreferencesKey("user_first_name")
        private val USER_LAST_NAME_KEY = stringPreferencesKey("user_last_name")
        private val USE_BIOMETRIC_KEY = booleanPreferencesKey("use_biometric")
    }

    val authToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[TOKEN_KEY]
        }

    val useBiometricEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[USE_BIOMETRIC_KEY] ?: false
        }

    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun saveUserSession(userId: String, email: String, firstName: String, lastName: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_EMAIL_KEY] = email
            preferences[USER_FIRST_NAME_KEY] = firstName
            preferences[USER_LAST_NAME_KEY] = lastName
        }
    }

    suspend fun getToken(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[TOKEN_KEY] }
            .first()
    }

    suspend fun getUserId(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[USER_ID_KEY] }
            .first()
    }

    suspend fun setUseBiometric(use: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[USE_BIOMETRIC_KEY] = use
        }
    }

    suspend fun shouldUseBiometric(): Boolean {
        return context.dataStore.data
            .map { preferences -> preferences[USE_BIOMETRIC_KEY] ?: false }
            .first()
    }

    fun isLoggedIn(): Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[TOKEN_KEY] != null
        }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}