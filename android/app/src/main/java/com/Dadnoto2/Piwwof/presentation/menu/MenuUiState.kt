package com.Dadnoto2.Piwwof.presentation.menu

import com.Dadnoto2.Piwwof.core.config.GameConfig
import com.Dadnoto2.Piwwof.R

data class MenuUiState(
    val coins: Int = 0,
    val currentStage: Int = 1,
    val stageNameRes: Int = R.string.stage_1,
    val unlockedStages: Int = 1,
    val totalStages: Int = GameConfig.STAGE_COUNT,
    val missionsDone: Int = 0,
    val missionsTotal: Int = GameConfig.MISSION_SLOTS,
    val bloomsOwned: Int = 0,
    val bloomSlots: Int = GameConfig.BLOOM_SLOTS,
    val claimableChests: Int = 0,
    val bestScore: Int = 0,
    val stars: Int = 0,
    val dataWasReset: Boolean = false
) {
    val firstRun: Boolean get() = bestScore == 0 && stars == 0
}
