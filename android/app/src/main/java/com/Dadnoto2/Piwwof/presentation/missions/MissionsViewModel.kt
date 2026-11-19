package com.Dadnoto2.Piwwof.presentation.missions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Dadnoto2.Piwwof.data.sample.SampleData
import com.Dadnoto2.Piwwof.domain.model.MissionCard
import com.Dadnoto2.Piwwof.domain.repository.MissionRepository
import com.Dadnoto2.Piwwof.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class MissionsUiState(
    val cards: List<MissionCard> = emptyList(),
    val coins: Int = 0
) {
    val allClaimed: Boolean get() = cards.isNotEmpty() && cards.all { it.claimed }
}

class MissionsViewModel(
    private val progressRepository: ProgressRepository,
    private val missionRepository: MissionRepository
) : ViewModel() {

    val state: StateFlow<MissionsUiState> = combine(
        missionRepository.entries,
        missionRepository.setIndex,
        progressRepository.progress
    ) { entries, setIndex, progress ->
        val cards = SampleData.missionsFor(setIndex).map { mission ->
            val entry = entries.firstOrNull { it.missionId == mission.id }
            MissionCard(
                mission = mission,
                progress = entry?.progress ?: 0,
                claimed = entry?.claimed ?: false
            )
        }
        MissionsUiState(cards = cards, coins = progress.coins)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MissionsUiState())

    fun claim(missionId: Int) {
        val reward = missionRepository.claim(missionId)
        if (reward > 0) progressRepository.grantCoins(reward)
    }

    fun drawNewSet() {
        missionRepository.drawNewSet()
    }
}
