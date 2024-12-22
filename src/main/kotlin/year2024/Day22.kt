package year2024

import aoc.IAocTaskKt

class Day22 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_22.txt"
    // override fun getFileName(): String = "aoc2024/input_22_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val secretNumbers = lines.map { it.toLong() }.toMutableList()
        val simTime = 2000
        for (idx in secretNumbers.indices) {
            secretNumbers[idx] = simulate(secretNumbers[idx], simTime)
        }
        println(secretNumbers.sum())
    }

    private fun simulate(initial: Long, simTime: Int): Long {
        var newSecret = initial
        repeat(simTime) {
            newSecret = mixAndPrune(newSecret, 64)
            newSecret = mixAndPrune(newSecret, 32)
            newSecret = mixAndPrune(newSecret, 2048)
        }
        return newSecret
    }

    private fun mixAndPrune(number: Long, base: Int): Long {
        val result = if (base == 32) number / base else number * base
        val newSecret = result.xor(number)
        return newSecret % 16777216
    }

    override fun solvePartTwo(lines: List<String>) {
        if (lines.isEmpty()) println("empty lines") else println(lines.size)
    }
}
