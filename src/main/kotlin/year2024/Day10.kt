package year2024

import aoc.IAocTaskKt
import year2024.Day10.Point

class Day10 : IAocTaskKt {
        override fun getFileName(): String = "aoc2024/input_10.txt"
//    override fun getFileName(): String = "aoc2024/input_10_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val visited = mutableSetOf<Point>()
        val score = getScore(lines, visited)
        println(score)
    }

    override fun solvePartTwo(lines: List<String>) {
        val score = getScore(lines, null)
        println(score)
    }

    private fun getScore(
        lines: List<String>,
        visited: MutableSet<Point>?,
    ): Int {
        val grid: Array<Array<Int>> = lines.map { row ->
            row.chunked(1).map { it.toInt() }.toTypedArray()
        }.toTypedArray()

        val scores = mutableListOf<Int>()
        for (y in grid.indices) {
            for (x in grid[y].indices) {
                val point = Point(x, y)
                if (grid.valueAt(point) == 0) {
                    visited?.clear()
                    val score = getTrailheadScore(grid, point, visited)
                    scores.add(score)
                }
            }
        }
        val score = scores.sum()
        return score
    }

    private fun getTrailheadScore(grid: Array<Array<Int>>, point: Point, visited: MutableSet<Point>?): Int {
        visited?.add(point)
        if (grid.valueAt(point) == 9) {
            return 1
        }
        var score = 0
        for (neigh in point.neighbours(grid)) {
            if (visited == null || neigh !in visited) {
                score += getTrailheadScore(grid, neigh, visited)
            }
        }
        return score
    }

    data class Point(val x: Int, val y: Int) {
        fun isInRange(grid: Array<Array<Int>>): Boolean = x >= 0 && x < grid[0].size && y >= 0 && y < grid.size
        fun neighbours(grid: Array<Array<Int>>): List<Point> {
            val neighs = mutableListOf<Point>()
            for (idx in DX.indices) {
                val neigh = this + Point(DX[idx], DY[idx])

                if (neigh.isInRange(grid) && grid.valueAt(neigh) == grid.valueAt(this) + 1) {
                    neighs.add(neigh)
                }
            }
            return neighs
        }

        private operator fun plus(point: Point): Point = Point(x + point.x, y + point.y)
    }

    companion object {
        val DX = arrayOf(-1, 0, 1, 0)
        val DY = arrayOf(0, -1, 0, 1)
    }
}

private fun Array<Array<Int>>.valueAt(pos: Point): Int {
    return this[pos.y][pos.x]
}
