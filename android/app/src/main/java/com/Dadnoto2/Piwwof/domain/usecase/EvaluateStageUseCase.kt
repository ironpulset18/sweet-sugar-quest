package com.Dadnoto2.Piwwof.domain.usecase

import com.Dadnoto2.Piwwof.domain.model.Bloom
import com.Dadnoto2.Piwwof.domain.model.Stage
import com.Dadnoto2.Piwwof.domain.model.StageOutcome
import com.Dadnoto2.Piwwof.domain.model.StageResult

class EvaluateStageUseCase {

    fun goalsMet(stage: Stage, burst: Map<Bloom, Int>, crustCracked: Int): Boolean {
        if (crustCracked < stage.crustGoal) return false
        return stage.goals.all { (burst[it.bloom] ?: 0) >= it.target }
    }

    fun starsFor(outcome: StageOutcome, movesLeft: Int): Int {
        if (outcome != StageOutcome.Win) return 0
        var stars = 1
        if (movesLeft >= 3) stars += 1
        if (movesLeft >= 6) stars += 1
        return stars
    }

    fun coinsFor(outcome: StageOutcome, stars: Int, movesLeft: Int): Int =
        if (outcome == StageOutcome.Win) 10 + stars * 15 + movesLeft * 2 else 0

    operator fun invoke(
        stage: Stage,
        outcome: StageOutcome,
        score: Int,
        bloomsBurst: Int,
        crustCracked: Int,
        movesLeft: Int,
        missionTitleRes: Int?
    ): StageResult {
        val stars = starsFor(outcome, movesLeft)
        return StageResult(
            stageIndex = stage.index,
            outcome = outcome,
            score = score,
            bloomsBurst = bloomsBurst,
            crustCracked = crustCracked,
            movesLeft = movesLeft,
            stars = stars,
            coins = coinsFor(outcome, stars, movesLeft),
            rewardBloomIndex = stage.rewardBloomIndex,
            missionTitleRes = missionTitleRes
        )
    }
}
