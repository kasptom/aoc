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
        var maxIdx1 = 0
        var maxIdx2 = 1
        var i = 0
        while (i < digits.size - 1) {
            if (digits[i] > digits[maxIdx1]) {
                maxIdx1 = i
            }
            i++
        }
        var j = maxIdx1 + 1
        while (j < digits.size) {
            if (digits[j] > digits[maxIdx2]) {
                maxIdx2 = j
            }
            j++
        }
        return 10 * digits[maxIdx1] + digits[maxIdx2]
    }

    override fun solvePartTwo(lines: List<String>) {
        println("TODO")
    }

}
