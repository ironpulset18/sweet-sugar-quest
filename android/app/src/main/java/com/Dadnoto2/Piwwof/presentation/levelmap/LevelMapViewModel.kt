package com.Dadnoto2.Piwwof.presentation.levelmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Dadnoto2.Piwwof.core.config.GameConfig
import com.Dadnoto2.Piwwof.data.sample.SampleData
import com.Dadnoto2.Piwwof.domain.model.GardenBand
import com.Dadnoto2.Piwwof.domain.model.StageGoal
import com.Dadnoto2.Piwwof.domain.repository.ProgressRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StageNode(
    val index: Int,
    val nameRes: Int,
    val band: GardenBand,
    val unlocked: Boolean,
    val stars: Int,
    val best: Int,
    val moveBudget: Int,
    val goals: List<StageGoal>,
    val crustGoal: Int
)

data class LevelMapUiState(
    val nodes: List<StageNode> = emptyList(),
    val totalStars: Int = 0,
    val maxStars: Int = GameConfig.STAGE_COUNT * 3,
    val selected: Int = 1,
    val lockedNotice: Int? = null
)

class LevelMapViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val uiState = MutableStateFlow(LevelMapUiState())
    val state: StateFlow<LevelMapUiState> = uiState.asStateFlow()

    private var noticeJob: Job? = null

    init {
        viewModelScope.launch {
            progressRepository.progress.collect { progress ->
                val tier = progress.gardenTier
                val nodes = SampleData.stages.map { stage ->
                    StageNode(
                        index = stage.index,
                        nameRes = stage.nameRes,
                        band = stage.band,
                        unlocked = stage.index <= progress.unlockedStage,
                        stars = progress.starsByStage[stage.index] ?: 0,
                        best = progress.bestScoreByStage[stage.index] ?: 0,
                        moveBudget = stage.moveBudget + tier,
                        goals = stage.goals,
                        crustGoal = stage.crustGoal
                    )
                }
                uiState.value = uiState.value.copy(
                    nodes = nodes,
                    totalStars = progress.totalStars,
                    selected = uiState.value.selected.coerceIn(1, GameConfig.STAGE_COUNT)
                )
            }
        }
    }

    fun select(index: Int) {
        val node = uiState.value.nodes.firstOrNull { it.index == index } ?: return
        if (node.unlocked) {
            noticeJob?.cancel()
            uiState.value = uiState.value.copy(selected = index, lockedNotice = null)
        } else {
            showLockedNotice(index)
        }
    }

    private fun showLockedNotice(index: Int) {
        noticeJob?.cancel()
        uiState.value = uiState.value.copy(lockedNotice = index)
        noticeJob = viewModelScope.launch {
            delay(2500L)
            uiState.value = uiState.value.copy(lockedNotice = null)
        }
    }
}
