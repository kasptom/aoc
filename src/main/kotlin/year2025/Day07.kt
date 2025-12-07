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
        val grid = lines
            .map { it.chunked(1).toMutableList() }
            .toMutableList()

        val start = grid[0].indexOf("S")
        grid[1][start] = "|"
        val memo = mutableMapOf<Pair<Int, Int>, Long>()
        val timelines = countTimelines(grid, 1, start, memo)
        println(timelines)
    }

    private fun countTimelines(
        grid: MutableList<MutableList<String>>,
        currentRow: Int,
        currentCol: Int,
        memo: MutableMap<Pair<Int, Int>, Long>,
    ): Long {
        if (currentRow == grid.size - 1) {
            return 1
        }
        if (memo.containsKey(Pair(currentRow, currentCol))) {
            return memo[Pair(currentRow, currentCol)]!!
        }
        var timelines = 0L
        if (grid[currentRow][currentCol] == "|" && grid[currentRow + 1][currentCol] == "^") {
            grid[currentRow + 1][currentCol - 1] = "|"
            timelines += countTimelines(grid, currentRow + 1, currentCol - 1, memo)
            grid[currentRow][currentCol + 1] = "|"
            timelines += countTimelines(grid, currentRow + 1, currentCol + 1, memo)
        } else {
            grid[currentRow + 1][currentCol] = "|"
            timelines += countTimelines(grid, currentRow + 1, currentCol, memo)
        }
        memo[Pair(currentRow, currentCol)] = timelines
        return timelines
    }
}