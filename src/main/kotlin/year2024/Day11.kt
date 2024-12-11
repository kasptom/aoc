package year2024

import aoc.IAocTaskKt

class Day11 : IAocTaskKt {
    //    override fun getFileName(): String = "aoc2024/input_11.txt"
    override fun getFileName(): String = "aoc2024/input_11.txt"

    override fun solvePartOne(lines: List<String>) {
        val stones = lines[0].split(" ").map { it.toLong() }
        var stoneToCount = mutableMapOf<Long, Long>()
        for (stone in stones) {
            stoneToCount[stone] = 1
        }
        for (i in 1..25) {
            stoneToCount = simulate(stoneToCount)
        }
        stoneToCount.values.sum().let { println(it) }
    }

    private fun simulate(stoneToCount: MutableMap<Long, Long>): MutableMap<Long, Long> {
        val nextGeneration = mutableMapOf<Long, Long>()

        for (stone in stoneToCount.keys) {
            val newStones: List<Long> = generateFrom(stone)
            val count = stoneToCount[stone]!!
            for (newStone in newStones) {
                nextGeneration.putIfAbsent(newStone, 0)
                nextGeneration.computeIfPresent(newStone) { _, c -> c + count}
            }
        }
        return nextGeneration
    }

    private fun generateFrom(stone: Long): List<Long> {
        if (stone == 0L) {
            return listOf(1L)
        }
        val digitsCount = stone.digitsCount()
        if (digitsCount % 2 == 0) {
            val stoneStr = stone.toString()
            val left = stoneStr.substring(0, digitsCount / 2).toLong()
            val right = stoneStr.substring(digitsCount / 2).toLong()
            return listOf(left, right)
        }
        return listOf(stone * 2024)
    }

    override fun solvePartTwo(lines: List<String>) {
        val stones = lines[0].split(" ").map { it.toLong() }
        var stoneToCount = mutableMapOf<Long, Long>()
        for (stone in stones) {
            stoneToCount[stone] = 1
        }
        for (i in 1..75) {
            stoneToCount = simulate(stoneToCount)
        }
        stoneToCount.values.sum().let { println(it) }
    }
}

private fun Long.digitsCount(): Int {
    var count = 0
    if (this == 0L) {
        return 1;
    }
    var value = this
    while (value != 0L) {
        value /= 10
        count++
    }
    return count
}
