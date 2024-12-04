package year2024

import aoc.IAocTaskKt

class Day04 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_04_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val grid: Array<CharArray> = lines.map { it.toCharArray() }.toTypedArray()
        var xmasCount = 0
        for (i in grid.indices) {
            for (j in grid[0].indices) {
                for (dir in DX.indices) {
                    xmasCount += if (xmasFound(DX[dir], DY[dir], j, i, grid)) 1 else 0
                }
            }
        }
        println(xmasCount)
    }

    private fun xmasFound(dx: Int, dy: Int, x: Int, y: Int, grid: Array<CharArray>): Boolean {
        val word = "XMAS"
        val width = grid[0].size
        val height = grid.size
        val found = word.indices.map { offset ->
            val newX = x + dx * offset
            val newY = y + dy * offset
            if (newX in 0 until width && newY in 0 until height) {
                grid[newY][newX]
            } else {
                '.'
            }
        }.joinToString("")
        return found == word
    }

    override fun solvePartTwo(lines: List<String>) {
        val grid: Array<CharArray> = lines.map { it.toCharArray() }.toTypedArray()
        var xmasCount = 0
        for (i in grid.indices) {
            for (j in grid[0].indices) {
                xmasCount += if (xmasFound2(j, i, grid)) 1 else 0
            }
        }
        println(xmasCount)
    }

    private fun xmasFound2(x: Int, y: Int, grid: Array<CharArray>): Boolean {
        if (grid[y][x] != 'A') {
            return false
        }
        val width = grid[0].size
        val height = grid.size
        var mCount = 0
        var sCount = 0
        for (idx in DX2.indices) {
            val dx = DX2[idx]
            val dy = DY2[idx]
            val newX = x + dx
            val newY = y + dy
            if (newX in 0 until width && newY in 0 until height) {
                mCount += if (grid[newY][newX] == 'M') 1 else 0
                sCount += if (grid[newY][newX] == 'S') 1 else 0
            }
        }
        return mCount == 2 && sCount == 2
    }

    companion object {
        val DX = arrayOf(-1, 0, 1, -1, 1, -1, 0, 1)
        val DY = arrayOf(-1, -1, -1, 0, 0, 1, 1, 1)
        val DX2 = arrayOf(-1, 1, -1, 1)
        val DY2 = arrayOf(-1, -1, 1, 1)
    }
}
