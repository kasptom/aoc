package year2025

import aoc.IAocTaskKt

class Day03 : IAocTaskKt {
    override fun getFileName(): String = "aoc2025/input_03.txt"

    override fun solvePartOne(lines: List<String>) {
        val maxJoltages = lines
            .filter { it.isNotBlank() }
            .map {
            Pair(it, maxJoltage(it, 2))
        }
            // .onEach { println(it) }
            .map { it.second.toLong() }
        println(maxJoltages.sum())
    }

    override fun solvePartTwo(lines: List<String>) {
        val maxJoltages = lines
            .filter { it.isNotBlank() }
            .map {
                Pair(it, maxJoltage(it, 12))
            }
            // .onEach { println(it) }
            .map { it.second.toLong() }
        println(maxJoltages.sum())
    }

    private fun maxJoltage(digits: String, length: Int): String {
        val n = digits.length
        val result = StringBuilder()
        var start = 0
        for (i in 0 until length) {
            val end = n - (length - i) + 1
            var maxChar = '0'
            var maxIdx = start
            for (j in start until end) {
                if (digits[j] > maxChar) {
                    maxChar = digits[j]
                    maxIdx = j
                }
            }
            result.append(maxChar)
            start = maxIdx + 1
        }
        return result.toString()
    }
}
