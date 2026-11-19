package com.Dadnoto2.Piwwof.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Dadnoto2.Piwwof.R
import com.Dadnoto2.Piwwof.data.sample.SampleData
import com.Dadnoto2.Piwwof.domain.model.StageOutcome
import com.Dadnoto2.Piwwof.domain.model.StageResult
import com.Dadnoto2.Piwwof.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GameOverUiState(
    val ready: Boolean = false,
    val stageIndex: Int = 1,
    val isWin: Boolean = false,
    val headlineRes: Int = R.string.result_win,
    val score: Int = 0,
    val bloomsBurst: Int = 0,
    val crustCracked: Int = 0,
    val stars: Int = 0,
    val coinsEarned: Int = 0,
    val totalCoins: Int = 0,
    val heroSpriteSlot: Int = 1,
    val bloomNameRes: Int = R.string.bloom_1,
    val missionTitleRes: Int? = null,
    val nextStageUnlocked: Boolean = false
)

class GameOverViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val uiState = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = uiState.asStateFlow()

    init {
        viewModelScope.launch {
            progressRepository.progress.collect { progress ->
                uiState.value = uiState.value.copy(totalCoins = progress.coins)
            }
        }
    }

    fun bind(result: StageResult) {
        val bloom = SampleData.bloomAt(result.rewardBloomIndex)
        uiState.value = GameOverUiState(
            ready = true,
            stageIndex = result.stageIndex,
            isWin = result.isWin,
            headlineRes = when (result.outcome) {
                StageOutcome.Win -> R.string.result_win
                StageOutcome.OutOfMoves -> R.string.result_out_of_moves
                StageOutcome.Crushed -> R.string.result_crushed
            },
            score = result.score,
            bloomsBurst = result.bloomsBurst,
            crustCracked = result.crustCracked,
            stars = result.stars,
            coinsEarned = result.coins,
            totalCoins = progressRepository.progress.value.coins,
            heroSpriteSlot = bloom.bloom.spriteSlot,
            bloomNameRes = bloom.nameRes,
            missionTitleRes = result.missionTitleRes,
            nextStageUnlocked = result.isWin
        )
    }
}
