package year2025

import aoc.IAocTaskKt

class Day04 : IAocTaskKt {
    override fun getFileName(): String = "aoc2025/input_04.txt"

    override fun solvePartOne(lines: List<String>) {
        var accessible = 0
        val grid = lines.map { it.chunked(1) }
        for (x in 0 until grid[0].size) {
            for (y in 0 until grid.size) {
                if (grid[y][x] == "@" && countAdjacent(grid, x, y) < 4) {
                    accessible++
                }
            }
        }
        println(accessible)
    }

    private fun countAdjacent(grid: List<List<String>>, x: Int, y: Int): Int {
        var count = 0
        for (idx in DX.indices) {
            val neighY = y + DY[idx]
            val neighX = x + DX[idx]
            if (neighX in 0 until grid[0].size && neighY in 0 until grid.size && grid[neighY][neighX] == "@") {
                count++
            }
        }
        return count
    }

    override fun solvePartTwo(lines: List<String>) {
        TODO("Not yet implemented")
    }

    companion object {
        val DX = arrayOf(-1, 0, 1, -1, 1, -1, 0, 1)
        val DY = arrayOf(-1, -1, -1, 0, 0, 1, 1, 1)
    }
}
