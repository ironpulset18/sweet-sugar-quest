package com.Dadnoto2.Piwwof.domain.repository

import com.Dadnoto2.Piwwof.domain.model.GameSettings
import com.Dadnoto2.Piwwof.domain.model.MissionProgress
import com.Dadnoto2.Piwwof.domain.model.PlayerProgress
import com.Dadnoto2.Piwwof.domain.model.RoundStats
import com.Dadnoto2.Piwwof.domain.model.StageResult
import kotlinx.coroutines.flow.StateFlow

interface ProgressRepository {
    val progress: StateFlow<PlayerProgress>
    fun applyResult(result: StageResult)
    fun spendCoins(amount: Int): Boolean
    fun grantCoins(amount: Int)
    fun plantBloom(index: Int)
    fun markTutorialSeen()
    fun acknowledgeReset()
    fun resetGarden()
}

interface SettingsRepository {
    val settings: StateFlow<GameSettings>
    fun setColourBlindMarkers(enabled: Boolean)
    fun setReducedMotion(enabled: Boolean)
    fun setHintGlow(enabled: Boolean)
    fun resetSettings()
}

interface MissionRepository {
    val entries: StateFlow<List<MissionProgress>>
    val setIndex: StateFlow<Int>
    fun recordRound(stats: RoundStats)
    fun claim(missionId: Int): Int
    fun drawNewSet()
    fun resetMissions()
}
