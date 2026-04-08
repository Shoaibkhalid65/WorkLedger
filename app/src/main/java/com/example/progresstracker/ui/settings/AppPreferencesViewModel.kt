package com.example.progresstracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progresstracker.data.local.datastore.AppPreferencesDataStore
import com.example.progresstracker.ui.theme.AppColorScheme
import com.example.progresstracker.ui.theme.AppThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppPreferencesUiState(
    val colorScheme: AppColorScheme = AppColorScheme.TERRACOTTA,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val useDynamicColor: Boolean = false
)

@HiltViewModel
class AppPreferencesViewModel @Inject constructor(
    private val dataStore: AppPreferencesDataStore
) : ViewModel() {

    val uiState: StateFlow<AppPreferencesUiState> = combine(
        dataStore.colorScheme, dataStore.themeMode, dataStore.useDynamicColor
    ) { scheme, mode, dynamicColor ->
        AppPreferencesUiState(
            colorScheme = scheme, themeMode = mode, useDynamicColor = dynamicColor
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly, // Eagerly so theme applies before first frame
        initialValue = AppPreferencesUiState()
    )

    fun setColorScheme(scheme: AppColorScheme) = viewModelScope.launch {
        dataStore.setColorScheme(scheme)
    }

    fun setThemeMode(mode: AppThemeMode) = viewModelScope.launch {
        dataStore.setThemeMode(mode)
    }

    fun setUseDynamicColor(enabled: Boolean) = viewModelScope.launch {
        dataStore.setUseDynamicColor(enabled)
    }
}

