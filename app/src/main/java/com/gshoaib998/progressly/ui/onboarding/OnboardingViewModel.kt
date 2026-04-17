package com.gshoaib998.progressly.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gshoaib998.progressly.data.local.datastore.AppPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val dataStore: AppPreferencesDataStore
) : ViewModel() {

    fun completeOnboarding() {
        viewModelScope.launch {
            dataStore.setOnboardingComplete()
        }
    }
}

