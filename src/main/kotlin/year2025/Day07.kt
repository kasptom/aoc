package year2025

import aoc.IAocTaskKt

class Day07 : IAocTaskKt {
    override fun getFileName(): String = "aoc2025/input_07.txt"

    override fun solvePartOne(lines: List<String>) {
        val grid = lines
            .map { it.chunked(1).toMutableList() }
            .toMutableList()

        val start = grid[0].indexOf("S")
        grid[1][start] = "|"
        var splits = 0
        for (i in 1 until grid.size) {
            for (j in 1 until grid[0].size - 1) {
                if (grid[i - 1][j] == "|" && grid[i][j] == ".") {
                    grid[i][j] = "|"
                } else if (grid[i - 1][j] == "|" && grid[i][j] == "^") {
                    grid[i][j - 1] = "|"
                    grid[i][j + 1] = "|"
                }
            }
        }
        for (i in 1 until grid.size) {
            for (j in 1 until grid[0].size - 1)
            if (grid[i][j] == "^" && grid[i - 1][j] == "|") {
                splits++
            }
        }
        println(splits)
    }

    override fun solvePartTwo(lines: List<String>) {
        TODO("Not yet implemented")
    }
}