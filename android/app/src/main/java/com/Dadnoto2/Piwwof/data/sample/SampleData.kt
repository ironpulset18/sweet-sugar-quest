package com.Dadnoto2.Piwwof.data.sample

import com.Dadnoto2.Piwwof.R
import com.Dadnoto2.Piwwof.core.config.GameConfig
import com.Dadnoto2.Piwwof.domain.model.Bloom
import com.Dadnoto2.Piwwof.domain.model.Board
import com.Dadnoto2.Piwwof.domain.model.BoardCell
import com.Dadnoto2.Piwwof.domain.model.GardenBand
import com.Dadnoto2.Piwwof.domain.model.GardenBloom
import com.Dadnoto2.Piwwof.domain.model.Mission
import com.Dadnoto2.Piwwof.domain.model.MissionKind
import com.Dadnoto2.Piwwof.domain.model.RewardChest
import com.Dadnoto2.Piwwof.domain.model.Stage
import com.Dadnoto2.Piwwof.domain.model.StageGoal
import com.Dadnoto2.Piwwof.domain.usecase.TileIds

object SampleData {

    val stages: List<Stage> = listOf(
        Stage(1, R.string.stage_1, GardenBand.Berry, 13, listOf(StageGoal(Bloom.Strawberry, 14)), 2, 2, 1),
        Stage(2, R.string.stage_2, GardenBand.Berry, 13, listOf(StageGoal(Bloom.Mint, 14)), 2, 2, 2),
        Stage(3, R.string.stage_3, GardenBand.Berry, 13, listOf(StageGoal(Bloom.Lemon, 15)), 3, 3, 3),
        Stage(4, R.string.stage_4, GardenBand.Berry, 13, listOf(StageGoal(Bloom.Blueberry, 15)), 3, 3, 4),
        Stage(5, R.string.stage_5, GardenBand.Citrus, 14, listOf(StageGoal(Bloom.Strawberry, 11), StageGoal(Bloom.Lemon, 11)), 4, 5, 5),
        Stage(6, R.string.stage_6, GardenBand.Citrus, 14, listOf(StageGoal(Bloom.Mint, 12), StageGoal(Bloom.Grape, 12)), 4, 5, 6),
        Stage(7, R.string.stage_7, GardenBand.Citrus, 14, listOf(StageGoal(Bloom.Lemon, 12), StageGoal(Bloom.Blueberry, 12)), 5, 6, 7),
        Stage(8, R.string.stage_8, GardenBand.Citrus, 14, listOf(StageGoal(Bloom.Grape, 13), StageGoal(Bloom.Strawberry, 13)), 5, 6, 8),
        Stage(9, R.string.stage_9, GardenBand.Cocoa, 15, listOf(StageGoal(Bloom.Mint, 13), StageGoal(Bloom.Blueberry, 13)), 6, 7, 9),
        Stage(10, R.string.stage_10, GardenBand.Cocoa, 15, listOf(StageGoal(Bloom.Grape, 14), StageGoal(Bloom.Lemon, 14)), 6, 8, 10),
        Stage(11, R.string.stage_11, GardenBand.Cocoa, 15, listOf(StageGoal(Bloom.Strawberry, 14), StageGoal(Bloom.Mint, 14)), 7, 8, 11),
        Stage(12, R.string.stage_12, GardenBand.Cocoa, 15, listOf(StageGoal(Bloom.Blueberry, 15), StageGoal(Bloom.Grape, 15)), 7, 9, 12)
    )

    val blooms: List<GardenBloom> = listOf(
        GardenBloom(1, R.string.bloom_1, Bloom.Strawberry),
        GardenBloom(2, R.string.bloom_2, Bloom.Lemon),
        GardenBloom(3, R.string.bloom_3, Bloom.Mint),
        GardenBloom(4, R.string.bloom_4, Bloom.Blueberry),
        GardenBloom(5, R.string.bloom_5, Bloom.Grape),
        GardenBloom(6, R.string.bloom_6, Bloom.Strawberry),
        GardenBloom(7, R.string.bloom_7, Bloom.Lemon),
        GardenBloom(8, R.string.bloom_8, Bloom.Mint),
        GardenBloom(9, R.string.bloom_9, Bloom.Grape),
        GardenBloom(10, R.string.bloom_10, Bloom.Lemon),
        GardenBloom(11, R.string.bloom_11, Bloom.Blueberry),
        GardenBloom(12, R.string.bloom_12, Bloom.Grape),
        GardenBloom(13, R.string.bloom_13, Bloom.Strawberry),
        GardenBloom(14, R.string.bloom_14, Bloom.Lemon),
        GardenBloom(15, R.string.bloom_15, Bloom.Blueberry)
    )

    val missionPool: List<Mission> = listOf(
        Mission(1, R.string.mission_1_title, R.string.mission_1_desc, MissionKind.BurstBloom, Bloom.Strawberry, 40, 60),
        Mission(2, R.string.mission_2_title, R.string.mission_2_desc, MissionKind.CrackCrust, null, 12, 60),
        Mission(3, R.string.mission_3_title, R.string.mission_3_desc, MissionKind.FinishWithMoves, null, 5, 70),
        Mission(4, R.string.mission_4_title, R.string.mission_4_desc, MissionKind.BloomBursts, null, 3, 80),
        Mission(5, R.string.mission_5_title, R.string.mission_5_desc, MissionKind.NoWiltStrike, null, 1, 90),
        Mission(6, R.string.mission_6_title, R.string.mission_6_desc, MissionKind.BigCluster, null, 7, 80)
    )

    val chests: List<RewardChest> = listOf(
        RewardChest(1, R.string.chest_1, 120, 13, 3),
        RewardChest(2, R.string.chest_2, 260, 14, 4),
        RewardChest(3, R.string.chest_3, 420, 15, 5)
    )

    fun missionsFor(setIndex: Int): List<Mission> {
        val size = missionPool.size
        return List(GameConfig.MISSION_SLOTS) { slot ->
            missionPool[((setIndex * GameConfig.MISSION_SLOTS) + slot) % size]
        }
    }

    fun bloomAt(index: Int): GardenBloom = blooms[(index - 1).coerceIn(0, blooms.size - 1)]

    fun stageAt(index: Int): Stage = stages[(index - 1).coerceIn(0, stages.size - 1)]

    fun fallbackBoard(stage: Stage, ids: TileIds): Board {
        val cells = ArrayList<BoardCell>(GameConfig.COLS * GameConfig.ROWS)
        for (row in 0 until GameConfig.ROWS) {
            for (col in 0 until GameConfig.COLS) {
                val slot = ((row / 2) * 2 + (col / 2) + row / 4) % Bloom.entries.size
                cells.add(BoardCell(id = ids.take(), bloom = Bloom.entries[slot], crustLayers = 0))
            }
        }
        val board = Board(GameConfig.COLS, GameConfig.ROWS, cells)
        val crust = minOf(stage.startCrust, 4)
        for (k in 0 until crust) {
            val slot = board.index(2 + k % 3, 1 + (k * 2) % 5)
            cells[slot] = cells[slot].copy(crustLayers = 2)
        }
        return board.copy(cells = cells)
    }
}
