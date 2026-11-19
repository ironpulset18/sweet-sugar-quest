package com.Dadnoto2.Piwwof.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Dadnoto2.Piwwof.core.config.GameConfig
import com.Dadnoto2.Piwwof.data.sample.SampleData
import com.Dadnoto2.Piwwof.domain.repository.MissionRepository
import com.Dadnoto2.Piwwof.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MenuViewModel(
    private val progressRepository: ProgressRepository,
    missionRepository: MissionRepository
) : ViewModel() {

    val state: StateFlow<MenuUiState> = combine(
        progressRepository.progress,
        missionRepository.entries,
        missionRepository.setIndex
    ) { progress, entries, setIndex ->
        val active = SampleData.missionsFor(setIndex)
        val done = active.count { mission ->
            val entry = entries.firstOrNull { it.missionId == mission.id }
            entry != null && (entry.claimed || entry.progress >= mission.target)
        }
        val claimable = SampleData.chests.count {
            progress.coins >= it.cost && it.bloomIndex !in progress.ownedBlooms
        }
        val stage = progress.unlockedStage.coerceIn(1, GameConfig.STAGE_COUNT)
        MenuUiState(
            coins = progress.coins,
            currentStage = stage,
            stageNameRes = SampleData.stageAt(stage).nameRes,
            unlockedStages = progress.unlockedStage,
            missionsDone = done,
            bloomsOwned = progress.ownedBlooms.size,
            claimableChests = claimable,
            bestScore = progress.bestScore,
            stars = progress.totalStars,
            dataWasReset = progress.dataWasReset
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MenuUiState())

    fun acknowledgeReset() {
        progressRepository.acknowledgeReset()
    }
}
