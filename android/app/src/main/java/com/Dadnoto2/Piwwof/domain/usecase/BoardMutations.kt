package com.Dadnoto2.Piwwof.domain.usecase

import com.Dadnoto2.Piwwof.domain.model.Bloom
import com.Dadnoto2.Piwwof.domain.model.Board
import com.Dadnoto2.Piwwof.domain.model.BoardCell
import com.Dadnoto2.Piwwof.domain.model.Stage
import kotlin.random.Random

class TileIds(private var counter: Long = 0L) {
    fun take(): Long {
        counter += 1L
        return counter
    }
}

fun weightsFor(stage: Stage): List<Pair<Bloom, Int>> {
    val wanted = stage.goals.map { it.bloom }.toSet()
    return Bloom.entries.map { bloom -> bloom to if (bloom in wanted) 3 else 2 }
}

fun pickWeighted(weights: List<Pair<Bloom, Int>>, random: Random): Bloom {
    val total = weights.sumOf { it.second }
    var roll = random.nextInt(total)
    for ((bloom, weight) in weights) {
        roll -= weight
        if (roll < 0) return bloom
    }
    return weights.last().first
}

fun settleBoard(
    board: Board,
    weights: List<Pair<Bloom, Int>>,
    random: Random,
    ids: TileIds
): Board {
    val next = board.cells.toMutableList()
    for (col in 0 until board.cols) {
        var bottom = board.rows - 1
        val stack = ArrayList<BoardCell>()
        for (row in board.rows - 1 downTo 0) {
            val cell = board.at(row, col)
            if (cell.isCrust) {
                writeSegment(next, board, col, bottom, row + 1, stack, weights, random, ids)
                stack.clear()
                bottom = row - 1
            } else if (cell.bloom != null) {
                stack.add(cell)
            }
        }
        writeSegment(next, board, col, bottom, 0, stack, weights, random, ids)
    }
    return board.copy(cells = next)
}

private fun writeSegment(
    target: MutableList<BoardCell>,
    board: Board,
    col: Int,
    bottomRow: Int,
    topRow: Int,
    stack: List<BoardCell>,
    weights: List<Pair<Bloom, Int>>,
    random: Random,
    ids: TileIds
) {
    if (bottomRow < topRow) return
    var cursor = 0
    for (row in bottomRow downTo topRow) {
        val slot = board.index(row, col)
        target[slot] = if (cursor < stack.size) {
            stack[cursor]
        } else {
            BoardCell(id = ids.take(), bloom = pickWeighted(weights, random), crustLayers = 0)
        }
        cursor += 1
    }
}

fun crackCrustAround(cells: MutableList<BoardCell>, board: Board, cleared: Set<Int>, ids: TileIds, random: Random, weights: List<Pair<Bloom, Int>>): Pair<Int, Int> {
    val touched = LinkedHashSet<Int>()
    for (index in cleared) {
        for (n in board.neighbours(index)) {
            if (cells[n].isCrust) touched.add(n)
        }
    }
    var cracked = 0
    var melted = 0
    for (index in touched) {
        val cell = cells[index]
        val layers = cell.crustLayers - 1
        cracked += 1
        cells[index] = if (layers <= 0) {
            melted += 1
            BoardCell(id = ids.take(), bloom = pickWeighted(weights, random), crustLayers = 0)
        } else {
            cell.copy(crustLayers = layers)
        }
    }
    return cracked to melted
}
