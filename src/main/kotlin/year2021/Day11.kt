package year2021

import aoc.IAocTaskKt

class Day11 : IAocTaskKt {
    override fun getFileName() = "aoc2021/input_11.txt"

    private val dxs = arrayOf(-1, 0, 1, -1, 1, -1, 0, 1)
    private val dys = arrayOf(-1, -1, -1, 0, 0, 1, 1, 1)
    var flashed: List<MutableList<Boolean>> = emptyList()

    override fun solvePartOne(lines: List<String>) {
        val octopuses = lines.map { it.chunked(1).map(String::toInt).toMutableList() }
        flashed = lines.map { it.chunked(1).map { false }.toMutableList() }
        val flashes = countFlashesAfter100Steps(octopuses)
        println(flashes)
    }

    private fun countFlashesAfter100Steps(octopuses: List<MutableList<Int>>): Int {
        var flashes = 0
        for (step in 1..100) {
            incrementAndVisit(octopuses)
            flashes += countFlashes()
//            println("After step $step:"); display(octopuses)
        }
        return flashes
    }

    private fun incrementAndVisit(octopuses: List<MutableList<Int>>) {
        octopuses.forEach { row -> row.indices.forEach { row[it]++ } }
        flashed.forEach { row -> row.indices.forEach { row[it] = false } }

        for (x in 0 until octopuses[0].size) {
            for (y in octopuses.indices) {
                recursivelyVisit(x, y, octopuses)
            }
        }

        for (x in 0 until octopuses[0].size) {
            for (y in octopuses.indices) {
                if (flashed[y][x]) {
                    octopuses[y][x] = 0
                }
            }
        }
    }

    private fun countFlashes(): Int = flashed.flatten().count { it }

    private fun recursivelyVisit(x: Int, y: Int, octopuses: List<MutableList<Int>>) {
        if (octopuses[y][x] > 9 && !flashed[y][x]) {
            flashed[y][x] = true
            for (idx in dxs.indices) {
                val neighX = x + dxs[idx]
                val neighY = y + dys[idx]
                if (isInRange(neighX, neighY, octopuses)) {
                    recursivelyIncrementAndVisit(neighX, neighY, octopuses)
                }
            }
        }
    }

    private fun recursivelyIncrementAndVisit(x: Int, y: Int, octopuses: List<MutableList<Int>>) {
        octopuses[y][x]++
        recursivelyVisit(x, y, octopuses)
    }

    private fun isInRange(x: Int, y: Int, octopuses: List<MutableList<Int>>) =
        isInRange(y, octopuses.size) && isInRange(x, octopuses[0].size)

    private fun isInRange(pos: Int, size: Int): Boolean = pos in 0 until size

    @Suppress("unused")
    private fun display(grid: List<MutableList<Int>>) {
        for (row in grid) {
            for (x in 0 until grid[0].size) {
                print(row[x])
            }
            println()
        }
        println()
    }

    override fun solvePartTwo(lines: List<String>) {
        val octopuses = lines.map { it.chunked(1).map(String::toInt).toMutableList() }
        flashed = lines.map { it.chunked(1).map { false }.toMutableList() }
        val syncStep = findSyncStep(octopuses)
        println(syncStep)
    }

    private fun findSyncStep(octopuses: List<MutableList<Int>>): Int {
        var step = 0
        while (true) {
            step++
            incrementAndVisit(octopuses)
            if (octopuses.flatten().all { it == 0 }) {
                return step
            }
        }
    }
}