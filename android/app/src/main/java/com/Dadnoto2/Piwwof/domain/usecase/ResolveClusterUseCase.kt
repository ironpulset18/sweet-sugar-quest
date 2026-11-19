package com.Dadnoto2.Piwwof.domain.usecase

import com.Dadnoto2.Piwwof.core.config.GameConfig
import com.Dadnoto2.Piwwof.domain.model.Bloom
import com.Dadnoto2.Piwwof.domain.model.Board
import com.Dadnoto2.Piwwof.domain.model.BoardCell
import com.Dadnoto2.Piwwof.domain.model.Stage
import kotlin.random.Random

data class ClusterOutcome(
    val valid: Boolean,
    val group: Set<Int>,
    val bloom: Bloom?,
    val cleared: Int,
    val crustCracked: Int,
    val crustMelted: Int,
    val score: Int,
    val meterGain: Int,
    val board: Board
)

class ResolveClusterUseCase {

    fun preview(board: Board, index: Int): Set<Int> {
        val group = board.clusterAt(index)
        return if (group.size >= 2) group else emptySet()
    }

    operator fun invoke(
        board: Board,
        stage: Stage,
        index: Int,
        random: Random,
        ids: TileIds
    ): ClusterOutcome {
        val group = board.clusterAt(index)
        if (group.size < 2) {
            return ClusterOutcome(false, emptySet(), null, 0, 0, 0, 0, 0, board)
        }
        val bloom = board.cells[index].bloom
        val weights = weightsFor(stage)
        val cells = board.cells.toMutableList()
        for (slot in group) {
            cells[slot] = cells[slot].copy(bloom = null)
        }
        val crust = crackCrustAround(cells, board, group, ids, random, weights)
        val settled = settleBoard(board.copy(cells = cells), weights, random, ids)
        val score = group.size * group.size * GameConfig.CLUSTER_SCORE_FACTOR
        val meterGain = if (group.size >= GameConfig.METER_MIN_CLUSTER) group.size else 0
        return ClusterOutcome(
            valid = true,
            group = group,
            bloom = bloom,
            cleared = group.size,
            crustCracked = crust.first,
            crustMelted = crust.second,
            score = score,
            meterGain = meterGain,
            board = settled
        )
    }

    fun reroll(board: Board, stage: Stage, random: Random, ids: TileIds): Board {
        val weights = weightsFor(stage)
        val loose = ArrayList<BoardCell>()
        for (cell in board.cells) if (!cell.isCrust && cell.bloom != null) loose.add(cell)
        val blooms = loose.map { it.bloom }.toMutableList()
        blooms.shuffle(random)
        var cursor = 0
        val cells = board.cells.toMutableList()
        for (i in cells.indices) {
            val cell = cells[i]
            if (cell.isCrust || cell.bloom == null) continue
            cells[i] = cell.copy(id = ids.take(), bloom = blooms[cursor])
            cursor += 1
        }
        return settleBoard(board.copy(cells = cells), weights, random, ids)
    }
}
