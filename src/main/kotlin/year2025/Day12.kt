package year2025

import aoc.IAocTaskKt
import utils.rotateClockwise

class Day12 : IAocTaskKt {
    override fun getFileName(): String = "aoc2025/input_12.txt"

    override fun solvePartOne(lines: List<String>) {
        val lastPresentIdx = lines.indexOfLast { it.isBlank() }
        val presentsBlock = lines.subList(0, lastPresentIdx + 1)
        val regions = lines.subList(lastPresentIdx + 1, lines.size)
            .map { Region.parse(it) }

        val presents = presentsBlock.fold(mutableListOf(mutableListOf<String>())) { acc, next ->
            if (next.isBlank()) {
                acc.add(mutableListOf())
            } else {
                acc.last().add(next)
            }
            acc
        }.dropLast(1)
            .map { Present.parse(it) }
            .associateBy { it.idx }

        val orientations: Map<Int, List<List<Pair<Int, Int>>>> = presents.mapValues { (_, present) ->
            present.uniqueOrientations()
        }

        var solvable = 0
        regions.forEach { region ->
            if (canFitRegion(region, orientations)) solvable++
        }
        println(solvable)
    }

    data class Present(val idx: Int, val grid: List<List<Char>>) {
        companion object {
            fun parse(lines: List<String>): Present {
                val idx = lines[0].replace(":", "").trim().toInt()
                val grid = lines.drop(1).map { it.toList() }
                return Present(idx, grid)
            }
        }

        fun uniqueOrientations(): List<List<Pair<Int, Int>>> {
            val seen = HashSet<String>()
            val result = mutableListOf<List<Pair<Int, Int>>>()

            fun addOrientation(grid: List<List<Char>>) {
                val cells = mutableListOf<Pair<Int, Int>>()
                for (y in grid.indices) {
                    for (x in grid[0].indices) {
                        if (grid[y][x] == '#') {
                            cells.add(x to y)
                        }
                    }
                }
                if (cells.isEmpty()) {
                    return
                }

                val minX = cells.minOf { it.first }
                val minY = cells.minOf { it.second }

                val norm = cells.map { (x, y) -> (x - minX) to (y - minY) }
                    .sortedWith(compareBy({ it.second }, { it.first }))

                val key = norm.joinToString(";") { "${it.first},${it.second}" }

                if (seen.add(key)) {
                    result.add(norm)
                }
            }

            var nextGrid = grid
            repeat(4) {
                addOrientation(nextGrid)
                nextGrid = nextGrid.rotateClockwise()
            }
            return result
        }
    }

    data class Region(val width: Int, val height: Int, val requirements: List<Int>) {
        companion object {
            fun parse(line: String): Region {
                val (dimsStr, requirementsStr) = line.split(": ")
                val (width, height) = dimsStr.split("x").map(String::toInt)
                val requirements = requirementsStr.split(" ").map(String::toInt)
                return Region(width, height, requirements)
            }
        }
    }

    private fun canFitRegion(region: Region, orientations: Map<Int, List<List<Pair<Int, Int>>>>): Boolean {
        val w = region.width
        val h = region.height
        var totalCellsNeeded = 0
        for ((idx, cnt) in region.requirements.withIndex()) {
            if (cnt == 0) {
                continue
            }
            val orients = orientations[idx]!!
            val size = orients.first().size
            totalCellsNeeded += size * cnt
        }
        if (totalCellsNeeded > w * h) {
            return false
        }

        val remaining = region.requirements.toIntArray()
        val occupied = java.util.BitSet(w * h)
        val memo = HashSet<String>()

        fun firstFree(): Int {
            val i = occupied.nextClearBit(0)
            return if (i >= w * h) -1 else i
        }

        fun key(): String {
            val bits = occupied.toLongArray().joinToString(",")
            val rest = remaining.joinToString(",")
            return "$bits|$rest"
        }

        fun remainingCellsNeeded(): Int {
            var sum = 0
            for (pIdx in remaining.indices) {
                val count = remaining[pIdx]
                if (count == 0) {
                    continue
                }
                val size = orientations[pIdx]!!.first().size
                sum += size * count
            }
            return sum
        }

        fun canPlaceAt(anchorIndex: Int, shape: List<Pair<Int, Int>>): List<IntArray> {
            val results = ArrayList<IntArray>()
            val ax = anchorIndex % w
            val ay = anchorIndex / w
            for ((sx, sy) in shape) {
                val dx = ax - sx
                val dy = ay - sy
                var ok = true
                val cells = IntArray(shape.size)
                var k = 0
                for ((cx, cy) in shape) {
                    val x = cx + dx
                    val y = cy + dy
                    if (x !in 0 until w || y < 0 || y >= h) {
                        ok = false
                        break
                    }
                    val idx = y * w + x
                    if (occupied.get(idx)) {
                        ok = false
                        break
                    }
                    cells[k++] = idx
                }
                if (ok) {
                    results.add(cells)
                }
            }
            return results
        }

        val presentOrder = region.requirements.indices
            .sortedByDescending { idx -> orientations[idx]?.firstOrNull()?.size ?: 0 }

        fun dfs(): Boolean {
            if (remaining.all { it == 0 }) {
                return true
            }

            val freeCells = w * h - occupied.cardinality()
            if (remainingCellsNeeded() > freeCells) {
                return false
            }

            val i = firstFree()
            if (i == -1) {
                return false
            }

            val memoKey = key()
            if (!memo.add(memoKey)) {
                return false
            }

            for (pIdx in presentOrder) {
                if (remaining[pIdx] == 0) {
                    continue
                }
                val orients = orientations[pIdx]!!

                for (shape in orients) {
                    for (cells in canPlaceAt(i, shape)) {
                        for (c in cells) {
                            occupied.set(c)
                        }
                        remaining[pIdx]--
                        if (dfs()) {
                            return true
                        }

                        remaining[pIdx]++
                        for (c in cells) {
                            occupied.clear(c)
                        }
                    }
                }
            }

            occupied.set(i)
            val ok = dfs()
            occupied.clear(i)
            return ok
        }

        return dfs()
    }

    override fun solvePartTwo(lines: List<String>) {
        println("⭐")
    }
}