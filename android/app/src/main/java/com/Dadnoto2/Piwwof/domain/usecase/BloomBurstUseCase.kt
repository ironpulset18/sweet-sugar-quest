package com.Dadnoto2.Piwwof.domain.usecase

import com.Dadnoto2.Piwwof.core.config.GameConfig
import com.Dadnoto2.Piwwof.domain.model.Bloom
import com.Dadnoto2.Piwwof.domain.model.Board
import com.Dadnoto2.Piwwof.domain.model.Stage
import kotlin.random.Random

data class SweepOutcome(
    val charged: Boolean,
    val group: Set<Int>,
    val clearedByBloom: Map<Bloom, Int>,
    val cleared: Int,
    val crustCracked: Int,
    val score: Int,
    val board: Board
)

class BloomBurstUseCase {

    operator fun invoke(
        board: Board,
        stage: Stage,
        priority: List<Bloom>,
        random: Random,
        ids: TileIds
    ): SweepOutcome {
        val wanted = priority.firstOrNull { board.countOf(it) > 0 }
            ?: board.dominantBloom()
            ?: return empty(board)
        val group = board.cells.indices.filter { board.cells[it].isPlayable && board.cells[it].bloom == wanted }.toSet()
        if (group.isEmpty()) return empty(board)
        return sweep(board, stage, group, random, ids, true, GameConfig.BURST_SCORE_FACTOR)
    }

    fun shower(
        board: Board,
        stage: Stage,
        random: Random,
        ids: TileIds
    ): SweepOutcome {
        val bottom = board.rows - 1
        val group = (0 until board.cols)
            .map { board.index(bottom, it) }
            .filter { board.cells[it].isPlayable }
            .toSet()
        if (group.isEmpty()) return empty(board)
        return sweep(board, stage, group, random, ids, false, GameConfig.CLUSTER_SCORE_FACTOR)
    }

    private fun sweep(
        board: Board,
        stage: Stage,
        group: Set<Int>,
        random: Random,
        ids: TileIds,
        charged: Boolean,
        factor: Int
    ): SweepOutcome {
        val weights = weightsFor(stage)
        val cells = board.cells.toMutableList()
        val tally = HashMap<Bloom, Int>()
        for (slot in group) {
            val bloom = cells[slot].bloom
            if (bloom != null) tally[bloom] = (tally[bloom] ?: 0) + 1
            cells[slot] = cells[slot].copy(bloom = null)
        }
        val crust = crackCrustAround(cells, board, group, ids, random, weights)
        val settled = settleBoard(board.copy(cells = cells), weights, random, ids)
        return SweepOutcome(
            charged = charged,
            group = group,
            clearedByBloom = tally,
            cleared = group.size,
            crustCracked = crust.first,
            score = group.size * factor,
            board = settled
        )
    }

    private fun empty(board: Board) =
        SweepOutcome(false, emptySet(), emptyMap(), 0, 0, 0, board)
}
