package com.Dadnoto2.Piwwof.presentation.game

import com.Dadnoto2.Piwwof.R
import com.Dadnoto2.Piwwof.core.config.GameConfig
import com.Dadnoto2.Piwwof.domain.model.Bloom

data class TileUi(
    val id: Long,
    val bloom: Bloom?,
    val crustLayers: Int
)

data class GoalUi(
    val bloom: Bloom,
    val done: Int,
    val target: Int
) {
    val complete: Boolean get() = done >= target
}

data class GameUiState(
    val loading: Boolean = true,
    val stageIndex: Int = 1,
    val stageNameRes: Int = R.string.stage_1,
    val tiles: List<TileUi> = emptyList(),
    val cols: Int = GameConfig.COLS,
    val goals: List<GoalUi> = emptyList(),
    val crustDone: Int = 0,
    val crustTarget: Int = 0,
    val movesLeft: Int = 0,
    val moveBudget: Int = 0,
    val score: Int = 0,
    val meter: Int = 0,
    val wilt: Int = 0,
    val rerollsLeft: Int = GameConfig.REROLL_LIMIT,
    val selected: Set<Int> = emptySet(),
    val bursting: Set<Int> = emptySet(),
    val hinted: Set<Int> = emptySet(),
    val wiltCell: Int = -1,
    val shakeToken: Int = 0,
    val paused: Boolean = false,
    val usedFallback: Boolean = false,
    val seed: Long = 0L,
    val colourBlindMarkers: Boolean = false,
    val reducedMotion: Boolean = false
) {
    val meterReady: Boolean get() = meter >= GameConfig.METER_MAX
    val meterFraction: Float get() = meter.toFloat() / GameConfig.METER_MAX.toFloat()
    val lowOnMoves: Boolean get() = movesLeft in 1..3
}
