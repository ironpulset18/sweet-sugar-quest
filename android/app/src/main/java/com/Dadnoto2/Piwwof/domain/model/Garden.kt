package com.Dadnoto2.Piwwof.domain.model

enum class MissionKind {
    BurstBloom,
    CrackCrust,
    FinishWithMoves,
    BloomBursts,
    NoWiltStrike,
    BigCluster
}

data class Mission(
    val id: Int,
    val titleRes: Int,
    val descRes: Int,
    val kind: MissionKind,
    val bloom: Bloom?,
    val target: Int,
    val reward: Int
)

data class MissionProgress(
    val missionId: Int,
    val progress: Int,
    val claimed: Boolean
)

data class MissionCard(
    val mission: Mission,
    val progress: Int,
    val claimed: Boolean
) {
    val complete: Boolean get() = progress >= mission.target
}

data class RewardChest(
    val id: Int,
    val nameRes: Int,
    val cost: Int,
    val bloomIndex: Int,
    val spriteSlot: Int
)

data class GardenBloom(
    val index: Int,
    val nameRes: Int,
    val bloom: Bloom
)

data class GameSettings(
    val colourBlindMarkers: Boolean = false,
    val reducedMotion: Boolean = false,
    val hintGlow: Boolean = true
)

data class PlayerProgress(
    val coins: Int = 0,
    val unlockedStage: Int = 1,
    val bestScore: Int = 0,
    val starsByStage: Map<Int, Int> = emptyMap(),
    val bestScoreByStage: Map<Int, Int> = emptyMap(),
    val ownedBlooms: Set<Int> = emptySet(),
    val tutorialSeen: Boolean = false,
    val dataWasReset: Boolean = false
) {
    val totalStars: Int get() = starsByStage.values.sum()
    val gardenTier: Int get() = ownedBlooms.size / 5
}
