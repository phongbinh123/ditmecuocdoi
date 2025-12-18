package com.example.ffridge.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.ffridge.data.model.AppTheme
import com.example.ffridge.data.model.User
import com.example.ffridge.data.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserRepository(private val context: Context) {

    private val dataStore = context.dataStore

    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_DISPLAY_NAME = stringPreferencesKey("user_display_name")
        private val USER_AVATAR_URL = stringPreferencesKey("user_avatar_url")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")

        private val EXPIRY_NOTIFICATIONS = booleanPreferencesKey("expiry_notifications")
        private val THEME = stringPreferencesKey("theme")
        private val UI_SCALE = floatPreferencesKey("ui_scale")
        private val NOTIFICATION_TIME = stringPreferencesKey("notification_time")
    }

    /**
     * Save user data
     */
    suspend fun saveUser(user: User) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = user.id
            preferences[USER_EMAIL] = user.email
            preferences[USER_DISPLAY_NAME] = user.displayName
            preferences[USER_AVATAR_URL] = user.avatarUrl ?: ""
            preferences[IS_LOGGED_IN] = true
        }
    }

    /**
     * Get current user
     */
    fun getUser(): Flow<User?> {
        return dataStore.data.map { preferences ->
            val isLoggedIn = preferences[IS_LOGGED_IN] ?: false
            if (isLoggedIn) {
                User(
                    id = preferences[USER_ID] ?: "",
                    email = preferences[USER_EMAIL] ?: "",
                    displayName = preferences[USER_DISPLAY_NAME] ?: "",
                    avatarUrl = preferences[USER_AVATAR_URL]?.takeIf { it.isNotEmpty() }
                )
            } else {
                null
            }
        }
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }
    }

    /**
     * Logout user
     */
    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = false
            preferences.remove(USER_ID)
            preferences.remove(USER_EMAIL)
            preferences.remove(USER_DISPLAY_NAME)
            preferences.remove(USER_AVATAR_URL)
        }
    }

    /**
     * Save user settings
     */
    suspend fun saveSettings(settings: UserSettings) {
        dataStore.edit { preferences ->
            preferences[EXPIRY_NOTIFICATIONS] = settings.expiryNotifications
            preferences[THEME] = settings.theme.name
            preferences[UI_SCALE] = settings.uiScale
            preferences[NOTIFICATION_TIME] = settings.notificationTime
        }
    }

    /**
     * Get user settings
     */
    fun getSettings(): Flow<UserSettings> {
        return dataStore.data.map { preferences ->
            UserSettings(
                expiryNotifications = preferences[EXPIRY_NOTIFICATIONS] ?: true,
                theme = try {
                    AppTheme.valueOf(preferences[THEME] ?: AppTheme.FROST.name)
                } catch (e: Exception) {
                    AppTheme.FROST
                },
                uiScale = preferences[UI_SCALE] ?: 1.0f,
                notificationTime = preferences[NOTIFICATION_TIME] ?: "09:00"
            )
        }
    }

    /**
     * Update individual setting
     */
    suspend fun updateExpiryNotifications(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[EXPIRY_NOTIFICATIONS] = enabled
        }
    }

    suspend fun updateTheme(theme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[THEME] = theme.name
        }
    }

    suspend fun updateUiScale(scale: Float) {
        dataStore.edit { preferences ->
            preferences[UI_SCALE] = scale
        }
    }

    suspend fun updateNotificationTime(time: String) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_TIME] = time
        }
    }

    /**
     * Clear all data
     */
    suspend fun clearAllData() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
