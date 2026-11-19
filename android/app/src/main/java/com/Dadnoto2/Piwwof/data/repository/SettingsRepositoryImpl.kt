package com.Dadnoto2.Piwwof.data.repository

import com.Dadnoto2.Piwwof.data.local.SugarPreferences
import com.Dadnoto2.Piwwof.domain.model.GameSettings
import com.Dadnoto2.Piwwof.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(
    private val prefs: SugarPreferences
) : SettingsRepository {

    private val state = MutableStateFlow(
        GameSettings(
            colourBlindMarkers = prefs.bool(SugarPreferences.KEY_COLOURBLIND, false),
            reducedMotion = prefs.bool(SugarPreferences.KEY_MOTION, false),
            hintGlow = prefs.bool(SugarPreferences.KEY_HINT, true)
        )
    )

    override val settings: StateFlow<GameSettings> = state.asStateFlow()

    override fun setColourBlindMarkers(enabled: Boolean) {
        prefs.putBool(SugarPreferences.KEY_COLOURBLIND, enabled)
        state.value = state.value.copy(colourBlindMarkers = enabled)
    }

    override fun setReducedMotion(enabled: Boolean) {
        prefs.putBool(SugarPreferences.KEY_MOTION, enabled)
        state.value = state.value.copy(reducedMotion = enabled)
    }

    override fun setHintGlow(enabled: Boolean) {
        prefs.putBool(SugarPreferences.KEY_HINT, enabled)
        state.value = state.value.copy(hintGlow = enabled)
    }

    override fun resetSettings() {
        state.value = GameSettings()
        prefs.putBool(SugarPreferences.KEY_COLOURBLIND, false)
        prefs.putBool(SugarPreferences.KEY_MOTION, false)
        prefs.putBool(SugarPreferences.KEY_HINT, true)
    }
}
