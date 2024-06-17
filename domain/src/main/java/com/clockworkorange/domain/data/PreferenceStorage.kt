package com.clockworkorange.domain.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.clockworkorange.domain.data.DataStorePreferenceStorage.PreferencesKeys.PREF_KEEP_LOGIN_STATUS
import com.clockworkorange.domain.data.DataStorePreferenceStorage.PreferencesKeys.PREF_ONBOARDING
import com.clockworkorange.domain.data.DataStorePreferenceStorage.PreferencesKeys.PREF_REFRESH_TOKEN
import com.clockworkorange.domain.data.DataStorePreferenceStorage.PreferencesKeys.PREF_TOKEN
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface PreferenceStorage {

    suspend fun completeOnBoarding(complete: Boolean)
    suspend fun isOnBoardingCompleted(): Boolean

    suspend fun setKeepLoginStatus(keep: Boolean)
    suspend fun isKeepLoginStatus(): Boolean

    suspend fun saveToken(token: String)
    suspend fun saveRefreshToken(refreshToken: String)

    suspend fun getToken(): String?
    suspend fun getRefreshToken(): String?
}

@Singleton
class DataStorePreferenceStorage @Inject constructor(
    private val dataStore: DataStore<Preferences>
): PreferenceStorage {

    companion object {
        const val PREFS_NAME = "cwo_haohsing"
    }

    object PreferencesKeys{
        val PREF_ONBOARDING = booleanPreferencesKey("pref_onboarding")
        val PREF_KEEP_LOGIN_STATUS = booleanPreferencesKey("pref_keep_login_status")
        val PREF_TOKEN = stringPreferencesKey("pref_token")
        val PREF_REFRESH_TOKEN = stringPreferencesKey("pref_refresh_token")
    }

    override suspend fun completeOnBoarding(complete: Boolean) {
        dataStore.edit {
            it[PREF_ONBOARDING] = complete
        }
    }

    override suspend fun isOnBoardingCompleted(): Boolean {
        return dataStore.data.map { it[PREF_ONBOARDING] ?: false }.first()
    }

    override suspend fun setKeepLoginStatus(keep: Boolean) {
        dataStore.edit {
            it[PREF_KEEP_LOGIN_STATUS] = keep
        }
    }

    override suspend fun isKeepLoginStatus(): Boolean {
        return dataStore.data.map { it[PREF_KEEP_LOGIN_STATUS] ?: false }.first()
    }

    override suspend fun saveToken(token: String) {
        dataStore.edit {
            it[PREF_TOKEN] = token
        }
    }

    override suspend fun saveRefreshToken(refreshToken: String) {
        dataStore.edit {
            it[PREF_REFRESH_TOKEN] = refreshToken
        }
    }

    override suspend fun getToken(): String? {
        return dataStore.data.map { it[PREF_TOKEN] }.first()
    }

    override suspend fun getRefreshToken(): String? {
        return dataStore.data.map { it[PREF_REFRESH_TOKEN] }.first()
    }
}