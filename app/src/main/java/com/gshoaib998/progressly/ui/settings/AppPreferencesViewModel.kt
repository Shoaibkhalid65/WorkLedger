package com.gshoaib998.progressly.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gshoaib998.progressly.data.local.datastore.AppPreferencesDataStore
import com.gshoaib998.progressly.ui.theme.AppColorScheme
import com.gshoaib998.progressly.ui.theme.AppThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AppPreferencesState {
    data object Loading : AppPreferencesState
    data class Ready(
        val colorScheme: AppColorScheme,
        val themeMode: AppThemeMode,
        val useDynamicColor: Boolean,
        val onboardingComplete: Boolean
    ) : AppPreferencesState
}

@HiltViewModel
class AppPreferencesViewModel @Inject constructor(
    private val dataStore: AppPreferencesDataStore
) : ViewModel() {

    val uiState: StateFlow<AppPreferencesState> = combine(
        dataStore.colorScheme,
        dataStore.themeMode,
        dataStore.useDynamicColor,
        dataStore.onboardingComplete
    ) { scheme, mode, dynamicColor,onboarding ->
        AppPreferencesState.Ready(
            colorScheme = scheme,
            themeMode = mode,
            useDynamicColor = dynamicColor,
            onboardingComplete = onboarding
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AppPreferencesState.Loading
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

