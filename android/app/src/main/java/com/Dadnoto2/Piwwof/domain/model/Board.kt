package com.Dadnoto2.Piwwof.domain.model

data class BoardCell(
    val id: Long,
    val bloom: Bloom?,
    val crustLayers: Int = 0
) {
    val isCrust: Boolean get() = crustLayers > 0
    val isPlayable: Boolean get() = bloom != null && crustLayers == 0
}

data class Board(
    val cols: Int,
    val rows: Int,
    val cells: List<BoardCell>
) {
    fun index(row: Int, col: Int): Int = row * cols + col

    fun at(row: Int, col: Int): BoardCell = cells[index(row, col)]

    fun rowOf(index: Int): Int = index / cols

    fun colOf(index: Int): Int = index % cols

    fun neighbours(index: Int): List<Int> {
        val row = rowOf(index)
        val col = colOf(index)
        val out = ArrayList<Int>(4)
        if (row > 0) out.add(index(row - 1, col))
        if (row < rows - 1) out.add(index(row + 1, col))
        if (col > 0) out.add(index(row, col - 1))
        if (col < cols - 1) out.add(index(row, col + 1))
        return out
    }

    fun clusterAt(index: Int): Set<Int> {
        val origin = cells[index]
        if (!origin.isPlayable) return emptySet()
        val target = origin.bloom
        val seen = LinkedHashSet<Int>()
        val stack = ArrayDeque<Int>()
        stack.addLast(index)
        seen.add(index)
        while (stack.isNotEmpty()) {
            val current = stack.removeLast()
            for (n in neighbours(current)) {
                if (n in seen) continue
                val cell = cells[n]
                if (cell.isPlayable && cell.bloom == target) {
                    seen.add(n)
                    stack.addLast(n)
                }
            }
        }
        return seen
    }

    fun clusterCount(minSize: Int): Int {
        val visited = HashSet<Int>()
        var count = 0
        for (i in cells.indices) {
            if (i in visited || !cells[i].isPlayable) continue
            val group = clusterAt(i)
            visited.addAll(group)
            if (group.size >= minSize) count++
        }
        return count
    }

    fun countOf(bloom: Bloom): Int = cells.count { it.isPlayable && it.bloom == bloom }

    fun crustCount(): Int = cells.count { it.isCrust }

    fun dominantBloom(): Bloom? = Bloom.entries
        .map { it to countOf(it) }
        .filter { it.second > 0 }
        .maxByOrNull { it.second }
        ?.first

    fun largestCluster(): Set<Int> {
        val visited = HashSet<Int>()
        var best: Set<Int> = emptySet()
        for (i in cells.indices) {
            if (i in visited || !cells[i].isPlayable) continue
            val group = clusterAt(i)
            visited.addAll(group)
            if (group.size > best.size) best = group
        }
        return if (best.size >= 2) best else emptySet()
    }
}
