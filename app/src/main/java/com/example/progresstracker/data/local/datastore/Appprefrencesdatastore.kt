package com.example.progresstracker.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.progresstracker.ui.theme.AppColorScheme
import com.example.progresstracker.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppPreferencesDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private object PreferencesKey{
        val COLOR_SCHEME_KEY = intPreferencesKey("color_scheme")
        val THEME_MODE_KEY = intPreferencesKey("theme_mode")
        val USE_DYNAMIC_COLOR   = booleanPreferencesKey("use_dynamic_color")
    }


    val colorScheme: Flow<AppColorScheme> = dataStore.data.map { prefs ->
        val ordinal = prefs[PreferencesKey.COLOR_SCHEME_KEY] ?: 0
        AppColorScheme.entries.getOrElse(ordinal) { AppColorScheme.TERRACOTTA }
    }

    val themeMode: Flow<AppThemeMode> = dataStore.data.map { prefs ->
        val ordinal = prefs[PreferencesKey.THEME_MODE_KEY] ?: 0
        AppThemeMode.entries.getOrElse(ordinal) { AppThemeMode.SYSTEM }
    }

    val useDynamicColor: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[PreferencesKey.USE_DYNAMIC_COLOR] ?: false
    }

    suspend fun setColorScheme(scheme: AppColorScheme) {
        dataStore.edit { prefs ->
            prefs[PreferencesKey.COLOR_SCHEME_KEY] = scheme.ordinal
        }
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        dataStore.edit { prefs ->
            prefs[PreferencesKey.THEME_MODE_KEY] = mode.ordinal
        }
    }

    suspend fun setUseDynamicColor(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[PreferencesKey.USE_DYNAMIC_COLOR] = enabled
        }
    }
}