package com.Dadnoto2.Piwwof.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Dadnoto2.Piwwof.domain.model.GameSettings
import com.Dadnoto2.Piwwof.domain.repository.MissionRepository
import com.Dadnoto2.Piwwof.domain.repository.ProgressRepository
import com.Dadnoto2.Piwwof.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val options: GameSettings = GameSettings(),
    val confirmingReset: Boolean = false
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository,
    private val missionRepository: MissionRepository
) : ViewModel() {

    private val uiState = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { options ->
                uiState.value = uiState.value.copy(options = options)
            }
        }
    }

    fun setColourBlindMarkers(enabled: Boolean) {
        settingsRepository.setColourBlindMarkers(enabled)
    }

    fun setReducedMotion(enabled: Boolean) {
        settingsRepository.setReducedMotion(enabled)
    }

    fun setHintGlow(enabled: Boolean) {
        settingsRepository.setHintGlow(enabled)
    }

    fun requestReset() {
        uiState.value = uiState.value.copy(confirmingReset = true)
    }

    fun cancelReset() {
        uiState.value = uiState.value.copy(confirmingReset = false)
    }

    fun confirmReset() {
        progressRepository.resetGarden()
        missionRepository.resetMissions()
        settingsRepository.resetSettings()
        uiState.value = uiState.value.copy(confirmingReset = false)
    }
}
