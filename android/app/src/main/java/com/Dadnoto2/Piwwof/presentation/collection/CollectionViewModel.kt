package com.Dadnoto2.Piwwof.presentation.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Dadnoto2.Piwwof.core.config.GameConfig
import com.Dadnoto2.Piwwof.data.sample.SampleData
import com.Dadnoto2.Piwwof.domain.model.GardenBloom
import com.Dadnoto2.Piwwof.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class BloomSlot(
    val bloom: GardenBloom,
    val owned: Boolean
)

data class CollectionUiState(
    val slots: List<BloomSlot> = emptyList(),
    val owned: Int = 0,
    val total: Int = GameConfig.BLOOM_SLOTS,
    val tier: Int = 0
)

class CollectionViewModel(
    progressRepository: ProgressRepository
) : ViewModel() {

    val state: StateFlow<CollectionUiState> = progressRepository.progress.map { progress ->
        CollectionUiState(
            slots = SampleData.blooms.map { BloomSlot(it, it.index in progress.ownedBlooms) },
            owned = progress.ownedBlooms.size,
            tier = progress.gardenTier
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CollectionUiState())
}
