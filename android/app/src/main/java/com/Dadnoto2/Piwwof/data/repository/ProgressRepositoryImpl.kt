package com.Dadnoto2.Piwwof.data.repository

import com.Dadnoto2.Piwwof.core.config.GameConfig
import com.Dadnoto2.Piwwof.data.local.SugarPreferences
import com.Dadnoto2.Piwwof.domain.model.PlayerProgress
import com.Dadnoto2.Piwwof.domain.model.StageResult
import com.Dadnoto2.Piwwof.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProgressRepositoryImpl(
    private val prefs: SugarPreferences
) : ProgressRepository {

    private val state = MutableStateFlow(load())

    override val progress: StateFlow<PlayerProgress> = state.asStateFlow()

    private fun load(): PlayerProgress {
        if (prefs.isCorrupted()) {
            prefs.clearAll()
            return PlayerProgress(dataWasReset = true)
        }
        val stars = HashMap<Int, Int>()
        val bests = HashMap<Int, Int>()
        for (stage in 1..GameConfig.STAGE_COUNT) {
            val earned = prefs.int(SugarPreferences.starsKey(stage), 0)
            if (earned > 0) stars[stage] = earned
            val best = prefs.int(SugarPreferences.bestKey(stage), 0)
            if (best > 0) bests[stage] = best
        }
        val owned = prefs.text(SugarPreferences.KEY_OWNED, "")
            .split(',')
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()
        return PlayerProgress(
            coins = prefs.int(SugarPreferences.KEY_COINS, 0),
            unlockedStage = prefs.int(SugarPreferences.KEY_UNLOCKED, 1).coerceIn(1, GameConfig.STAGE_COUNT),
            bestScore = prefs.int(SugarPreferences.KEY_BEST, 0),
            starsByStage = stars,
            bestScoreByStage = bests,
            ownedBlooms = owned,
            tutorialSeen = prefs.bool(SugarPreferences.KEY_TUTORIAL, false),
            dataWasReset = false
        )
    }

    private fun persistOwned(owned: Set<Int>) {
        prefs.putText(SugarPreferences.KEY_OWNED, owned.sorted().joinToString(","))
    }

    override fun applyResult(result: StageResult) {
        val current = state.value
        val stars = HashMap(current.starsByStage)
        val bests = HashMap(current.bestScoreByStage)
        val owned = HashSet(current.ownedBlooms)
        var unlocked = current.unlockedStage
        var coins = current.coins

        if (result.score > (bests[result.stageIndex] ?: 0)) {
            bests[result.stageIndex] = result.score
            prefs.putInt(SugarPreferences.bestKey(result.stageIndex), result.score)
        }
        if (result.isWin) {
            if (result.stars > (stars[result.stageIndex] ?: 0)) {
                stars[result.stageIndex] = result.stars
                prefs.putInt(SugarPreferences.starsKey(result.stageIndex), result.stars)
            }
            if (result.stageIndex + 1 > unlocked && result.stageIndex < GameConfig.STAGE_COUNT) {
                unlocked = result.stageIndex + 1
                prefs.putInt(SugarPreferences.KEY_UNLOCKED, unlocked)
            }
            if (owned.add(result.rewardBloomIndex)) persistOwned(owned)
            coins += result.coins
            prefs.putInt(SugarPreferences.KEY_COINS, coins)
        }
        val best = maxOf(current.bestScore, result.score)
        if (best != current.bestScore) prefs.putInt(SugarPreferences.KEY_BEST, best)

        state.value = current.copy(
            coins = coins,
            unlockedStage = unlocked,
            bestScore = best,
            starsByStage = stars,
            bestScoreByStage = bests,
            ownedBlooms = owned
        )
    }

    override fun spendCoins(amount: Int): Boolean {
        val current = state.value
        if (current.coins < amount) return false
        val coins = current.coins - amount
        prefs.putInt(SugarPreferences.KEY_COINS, coins)
        state.value = current.copy(coins = coins)
        return true
    }

    override fun grantCoins(amount: Int) {
        val current = state.value
        val coins = current.coins + amount
        prefs.putInt(SugarPreferences.KEY_COINS, coins)
        state.value = current.copy(coins = coins)
    }

    override fun plantBloom(index: Int) {
        val current = state.value
        val owned = HashSet(current.ownedBlooms)
        if (!owned.add(index)) return
        persistOwned(owned)
        state.value = current.copy(ownedBlooms = owned)
    }

    override fun markTutorialSeen() {
        if (state.value.tutorialSeen) return
        prefs.putBool(SugarPreferences.KEY_TUTORIAL, true)
        state.value = state.value.copy(tutorialSeen = true)
    }

    override fun acknowledgeReset() {
        if (!state.value.dataWasReset) return
        state.value = state.value.copy(dataWasReset = false)
    }

    override fun resetGarden() {
        prefs.clearAll()
        state.value = PlayerProgress()
    }
}
