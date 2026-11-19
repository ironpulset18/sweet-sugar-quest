package com.Dadnoto2.Piwwof.domain.usecase

import com.Dadnoto2.Piwwof.domain.model.Board
import kotlin.random.Random

class SpreadCrustUseCase {

    operator fun invoke(board: Board, random: Random): Pair<Board, Boolean> {
        val crusted = board.cells.indices.filter { board.cells[it].isCrust }
        if (crusted.isEmpty()) return board to false
        val candidates = LinkedHashSet<Int>()
        for (slot in crusted) {
            for (n in board.neighbours(slot)) {
                if (board.cells[n].isPlayable) candidates.add(n)
            }
        }
        if (candidates.isEmpty()) return board to false
        val target = candidates.elementAt(random.nextInt(candidates.size))
        val cells = board.cells.toMutableList()
        cells[target] = cells[target].copy(crustLayers = 1)
        return board.copy(cells = cells) to true
    }
}
