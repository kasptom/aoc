package year2024

import aoc.IAocTaskKt

class Day25 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_25.txt"
    // override fun getFileName(): String = "aoc2024/input_25_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val (locks, keys) = lines.windowed(7, 8)
            .partition { it.first() == "#####" }
        val lockCodes = locks.map {
            lock -> lock[0].indices.map { idx -> lock.count { it[idx] == '#' } - 1 }
        }
        val keyCodes = keys.map {
                key -> key[0].indices.map { idx -> key.count { it[idx] == '#' } - 1 }
        }

        var fits = 0
        for (key in lockCodes) {
            for (lock in keyCodes) {
                if (lock.indices.all { idx -> (lock[idx] + key[idx]) < 6 }) {
                    fits++
                }
            }
        }
        println(fits)
    }

    override fun solvePartTwo(lines: List<String>) {
        println("⭐")
    }
}
