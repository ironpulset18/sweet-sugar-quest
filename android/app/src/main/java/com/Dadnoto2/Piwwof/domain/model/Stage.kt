package com.Dadnoto2.Piwwof.domain.model

enum class GardenBand { Berry, Citrus, Cocoa }

data class StageGoal(
    val bloom: Bloom,
    val target: Int
)

data class Stage(
    val index: Int,
    val nameRes: Int,
    val band: GardenBand,
    val moveBudget: Int,
    val goals: List<StageGoal>,
    val crustGoal: Int,
    val startCrust: Int,
    val rewardBloomIndex: Int
)

enum class StageOutcome { Win, OutOfMoves, Crushed }

data class StageResult(
    val stageIndex: Int,
    val outcome: StageOutcome,
    val score: Int,
    val bloomsBurst: Int,
    val crustCracked: Int,
    val movesLeft: Int,
    val stars: Int,
    val coins: Int,
    val rewardBloomIndex: Int,
    val missionTitleRes: Int?
) {
    val isWin: Boolean get() = outcome == StageOutcome.Win
}

data class RoundStats(
    val stageIndex: Int,
    val bloomsBurst: Int,
    val crustCracked: Int,
    val movesLeft: Int,
    val bloomBursts: Int,
    val wiltStrikes: Int,
    val largestCluster: Int,
    val burstByBloom: Map<Bloom, Int>,
    val won: Boolean
)
