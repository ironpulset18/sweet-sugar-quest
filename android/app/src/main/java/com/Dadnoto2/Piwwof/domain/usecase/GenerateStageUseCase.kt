package com.Dadnoto2.Piwwof.domain.usecase

import com.Dadnoto2.Piwwof.core.config.GameConfig
import com.Dadnoto2.Piwwof.domain.model.Bloom
import com.Dadnoto2.Piwwof.domain.model.Board
import com.Dadnoto2.Piwwof.domain.model.BoardCell
import com.Dadnoto2.Piwwof.domain.model.Stage
import kotlin.random.Random

data class GeneratedStage(
    val board: Board,
    val seed: Long,
    val usedFallback: Boolean
)

class GenerateStageUseCase(
    private val fallbackBoard: (Stage, TileIds) -> Board
) {

    fun seedFor(stageIndex: Int, attempt: Int): Long =
        (stageIndex * GameConfig.SEED_STAGE_PRIME) xor (attempt * GameConfig.SEED_ATTEMPT_PRIME)

    operator fun invoke(stage: Stage, attempt: Int, ids: TileIds): GeneratedStage {
        val seed = seedFor(stage.index, attempt)
        val weights = weightsFor(stage)
        for (pass in 0 until GameConfig.GENERATION_ATTEMPTS) {
            val random = Random(seed + pass)
            val board = build(stage, weights, random, ids)
            if (isSolvable(board)) return GeneratedStage(board, seed, false)
        }
        return GeneratedStage(fallbackBoard(stage, ids), seed, true)
    }

    private fun build(
        stage: Stage,
        weights: List<Pair<Bloom, Int>>,
        random: Random,
        ids: TileIds
    ): Board {
        val total = GameConfig.COLS * GameConfig.ROWS
        val palette = Bloom.entries
        val pool = ArrayList<Bloom>(total)
        val share = total / palette.size
        for (bloom in palette) repeat(share) { pool.add(bloom) }
        while (pool.size < total) pool.add(pickWeighted(weights, random))
        pool.shuffle(random)

        val cells = ArrayList<BoardCell>(total)
        for (i in 0 until total) {
            cells.add(BoardCell(id = ids.take(), bloom = pool[i], crustLayers = 0))
        }
        val board = Board(GameConfig.COLS, GameConfig.ROWS, cells)

        val crustSlots = ArrayList<Int>()
        for (row in 1 until GameConfig.ROWS - 2) {
            for (col in 0 until GameConfig.COLS) crustSlots.add(board.index(row, col))
        }
        crustSlots.shuffle(random)
        val crustCount = minOf(stage.startCrust, GameConfig.MAX_CRUST_CELLS)
        val chosen = ArrayList<Int>()
        for (slot in crustSlots) {
            if (chosen.size >= crustCount) break
            if (chosen.any { board.neighbours(it).contains(slot) }) continue
            chosen.add(slot)
        }
        for (slot in chosen) {
            cells[slot] = BoardCell(id = ids.take(), bloom = cells[slot].bloom, crustLayers = 2)
        }
        return board.copy(cells = cells)
    }

    private fun isSolvable(board: Board): Boolean {
        if (board.crustCount() > GameConfig.MAX_CRUST_CELLS) return false
        if (board.clusterCount(2) < GameConfig.MIN_GROUPS_IN_BOARD) return false
        for (bloom in Bloom.entries) {
            if (board.countOf(bloom) < GameConfig.MIN_CELLS_PER_BLOOM) return false
        }
        return true
    }
}
