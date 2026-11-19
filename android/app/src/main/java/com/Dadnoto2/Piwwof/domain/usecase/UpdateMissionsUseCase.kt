package com.Dadnoto2.Piwwof.domain.usecase

import com.Dadnoto2.Piwwof.domain.model.Mission
import com.Dadnoto2.Piwwof.domain.model.MissionKind
import com.Dadnoto2.Piwwof.domain.model.MissionProgress
import com.Dadnoto2.Piwwof.domain.model.RoundStats

class UpdateMissionsUseCase {

    fun gainFor(mission: Mission, stats: RoundStats): Int = when (mission.kind) {
        MissionKind.BurstBloom -> mission.bloom?.let { stats.burstByBloom[it] ?: 0 } ?: 0
        MissionKind.CrackCrust -> stats.crustCracked
        MissionKind.FinishWithMoves -> if (stats.won && stats.movesLeft >= mission.target) mission.target else 0
        MissionKind.BloomBursts -> stats.bloomBursts
        MissionKind.NoWiltStrike -> if (stats.won && stats.wiltStrikes == 0) 1 else 0
        MissionKind.BigCluster -> if (stats.largestCluster >= mission.target) mission.target else 0
    }

    operator fun invoke(
        active: List<Mission>,
        current: List<MissionProgress>,
        stats: RoundStats
    ): List<MissionProgress> = active.map { mission ->
        val existing = current.firstOrNull { it.missionId == mission.id }
            ?: MissionProgress(mission.id, 0, false)
        if (existing.claimed) {
            existing
        } else {
            val gained = (existing.progress + gainFor(mission, stats)).coerceAtMost(mission.target)
            existing.copy(progress = gained)
        }
    }
}
