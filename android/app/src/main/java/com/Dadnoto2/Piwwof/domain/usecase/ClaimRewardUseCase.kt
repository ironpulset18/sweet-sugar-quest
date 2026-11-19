package com.Dadnoto2.Piwwof.domain.usecase

import com.Dadnoto2.Piwwof.domain.model.PlayerProgress
import com.Dadnoto2.Piwwof.domain.model.RewardChest

data class ChestOffer(
    val chest: RewardChest,
    val affordable: Boolean,
    val alreadyOwned: Boolean
)

class ClaimRewardUseCase {

    fun offers(chests: List<RewardChest>, progress: PlayerProgress): List<ChestOffer> =
        chests.map { chest ->
            ChestOffer(
                chest = chest,
                affordable = progress.coins >= chest.cost && chest.bloomIndex !in progress.ownedBlooms,
                alreadyOwned = chest.bloomIndex in progress.ownedBlooms
            )
        }

    fun rebateFor(chest: RewardChest): Int = chest.cost / 10

    fun canOpen(chest: RewardChest, progress: PlayerProgress): Boolean =
        progress.coins >= chest.cost && chest.bloomIndex !in progress.ownedBlooms
}
