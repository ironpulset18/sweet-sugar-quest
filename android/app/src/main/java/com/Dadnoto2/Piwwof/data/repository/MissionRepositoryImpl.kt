package com.Dadnoto2.Piwwof.data.repository

import com.Dadnoto2.Piwwof.data.local.SugarPreferences
import com.Dadnoto2.Piwwof.data.sample.SampleData
import com.Dadnoto2.Piwwof.domain.model.MissionProgress
import com.Dadnoto2.Piwwof.domain.model.RoundStats
import com.Dadnoto2.Piwwof.domain.repository.MissionRepository
import com.Dadnoto2.Piwwof.domain.usecase.UpdateMissionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MissionRepositoryImpl(
    private val prefs: SugarPreferences,
    private val updateMissions: UpdateMissionsUseCase
) : MissionRepository {

    private val setState = MutableStateFlow(prefs.int(SugarPreferences.KEY_MISSION_SET, 0))
    private val entryState = MutableStateFlow(load())

    override val entries: StateFlow<List<MissionProgress>> = entryState.asStateFlow()
    override val setIndex: StateFlow<Int> = setState.asStateFlow()

    private fun load(): List<MissionProgress> = SampleData.missionPool.map { mission ->
        MissionProgress(
            missionId = mission.id,
            progress = prefs.int(SugarPreferences.missionProgressKey(mission.id), 0),
            claimed = prefs.bool(SugarPreferences.missionClaimedKey(mission.id), false)
        )
    }

    private fun persist(entry: MissionProgress) {
        prefs.putInt(SugarPreferences.missionProgressKey(entry.missionId), entry.progress)
        prefs.putBool(SugarPreferences.missionClaimedKey(entry.missionId), entry.claimed)
    }

    override fun recordRound(stats: RoundStats) {
        val active = SampleData.missionsFor(setState.value)
        val updated = updateMissions(active, entryState.value, stats)
        val merged = entryState.value.map { existing ->
            updated.firstOrNull { it.missionId == existing.missionId } ?: existing
        }
        merged.forEach { persist(it) }
        entryState.value = merged
    }

    override fun claim(missionId: Int): Int {
        val mission = SampleData.missionPool.firstOrNull { it.id == missionId } ?: return 0
        val entry = entryState.value.firstOrNull { it.missionId == missionId } ?: return 0
        if (entry.claimed || entry.progress < mission.target) return 0
        val claimed = entry.copy(claimed = true)
        persist(claimed)
        entryState.value = entryState.value.map { if (it.missionId == missionId) claimed else it }
        return mission.reward
    }

    override fun drawNewSet() {
        val next = setState.value + 1
        prefs.putInt(SugarPreferences.KEY_MISSION_SET, next)
        setState.value = next
        val refreshed = entryState.value.map { entry ->
            val fresh = entry.copy(progress = 0, claimed = false)
            persist(fresh)
            fresh
        }
        entryState.value = refreshed
    }

    override fun resetMissions() {
        setState.value = 0
        prefs.putInt(SugarPreferences.KEY_MISSION_SET, 0)
        entryState.value = SampleData.missionPool.map { mission ->
            val fresh = MissionProgress(mission.id, 0, false)
            persist(fresh)
            fresh
        }
    }
}
