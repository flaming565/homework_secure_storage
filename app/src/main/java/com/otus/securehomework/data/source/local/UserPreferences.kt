package com.otus.securehomework.data.source.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.otus.securehomework.security.AesEncryptionService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val dataStoreFile: String = "securePref"

class UserPreferences
@Inject constructor(
    private val context: Context,
    private val encryptionService: AesEncryptionService
) {

    val accessToken: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN]?.let { encryptionService.decrypt(it) }
        }

    val refreshToken: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN]?.let { encryptionService.decrypt(it) }
        }

    suspend fun saveAccessTokens(accessToken: String?, refreshToken: String?) {
        context.dataStore.edit { preferences ->
            val encAccessToken = accessToken?.let { encryptionService.encrypt(it) }
            val encRefreshToken = refreshToken?.let { encryptionService.encrypt(it) }
            encAccessToken?.let { preferences[ACCESS_TOKEN] = it }
            encRefreshToken?.let { preferences[REFRESH_TOKEN] = it }
        }
    }

    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val Context.dataStore by preferencesDataStore(name = dataStoreFile)
        private val ACCESS_TOKEN = stringPreferencesKey("key_access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("key_refresh_token")
    }
}