package year2024

import aoc.IAocTaskKt

class Day19 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_19.txt"
    // override fun getFileName(): String = "aoc2024/input_19_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val patterns = lines[0].split(", ")
            .filter { it.isNotBlank() }
        println()

        val designs = lines.subList(2, lines.size)

        designs.count { isPossible(it, patterns) }
            .let { println(it) }
    }

    override fun solvePartTwo(lines: List<String>) {
        if (lines.isEmpty()) println("empty lines") else println(lines.size)
    }

    private fun isPossible(design: String, patterns: List<String>): Boolean {
        if (design.isEmpty()) {
            return true
        }
        for (pattern in patterns) {
            if (design.startsWith(pattern) && isPossible(design.removePrefix(pattern), patterns)) {
                return true
            }
        }
        return false;
    }
}
