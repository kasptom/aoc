package year2025

import aoc.IAocTaskKt

class Day03 : IAocTaskKt {
    override fun getFileName(): String = "aoc2025/input_03.txt"

    override fun solvePartOne(lines: List<String>) {
        val maxJoltages = lines
            .filter { it.isNotBlank() }
            .map {
            Pair(it, maxJoltage(it))
        }
            .onEach { println(it) }
            .map { it.second }
        println(maxJoltages.sum())
    }

    private fun maxJoltage(line: String): Int {
        val digits = line.chunked(1)
            .filter { it.isNotBlank() }
            .map(String::toInt)
        var maxValue = digits[0] * 10 + digits[1]
        for (i in 0 until digits.size) {
            for (j in i + 1 until digits.size) {
                val value = 10 * digits[i] + digits[j]
                if (value > maxValue) {
                    maxValue = value
                }
            }
        }
        return maxValue
    }

    override fun solvePartTwo(lines: List<String>) {
        println("TODO")
    }

}
