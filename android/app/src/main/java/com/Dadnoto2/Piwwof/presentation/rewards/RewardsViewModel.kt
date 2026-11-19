package com.Dadnoto2.Piwwof.presentation.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Dadnoto2.Piwwof.data.sample.SampleData
import com.Dadnoto2.Piwwof.domain.model.RewardChest
import com.Dadnoto2.Piwwof.domain.repository.ProgressRepository
import com.Dadnoto2.Piwwof.domain.usecase.ClaimRewardUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChestUi(
    val chest: RewardChest,
    val affordable: Boolean,
    val owned: Boolean
)

data class RewardsUiState(
    val coins: Int = 0,
    val chests: List<ChestUi> = emptyList(),
    val openedBloomNameRes: Int? = null,
    val openedSpriteSlot: Int = 1,
    val openingChestId: Int = -1
) {
    val neverEarned: Boolean get() = coins == 0 && chests.none { it.owned }
}

class RewardsViewModel(
    private val progressRepository: ProgressRepository,
    private val claimReward: ClaimRewardUseCase
) : ViewModel() {

    private val uiState = MutableStateFlow(RewardsUiState())
    val state: StateFlow<RewardsUiState> = uiState.asStateFlow()

    init {
        viewModelScope.launch {
            progressRepository.progress.collect { progress ->
                val offers = claimReward.offers(SampleData.chests, progress).map {
                    ChestUi(it.chest, it.affordable, it.alreadyOwned)
                }
                uiState.value = uiState.value.copy(coins = progress.coins, chests = offers)
            }
        }
    }

    fun openChest(chestId: Int) {
        val chest = SampleData.chests.firstOrNull { it.id == chestId } ?: return
        val progress = progressRepository.progress.value
        if (!claimReward.canOpen(chest, progress)) return
        if (!progressRepository.spendCoins(chest.cost)) return
        progressRepository.plantBloom(chest.bloomIndex)
        progressRepository.grantCoins(claimReward.rebateFor(chest))
        val bloom = SampleData.bloomAt(chest.bloomIndex)
        uiState.value = uiState.value.copy(
            openedBloomNameRes = bloom.nameRes,
            openedSpriteSlot = bloom.bloom.spriteSlot,
            openingChestId = chest.id
        )
    }

    fun dismissOpened() {
        uiState.value = uiState.value.copy(openedBloomNameRes = null, openingChestId = -1)
    }
}
