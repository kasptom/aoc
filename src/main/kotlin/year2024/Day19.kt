package year2024

import aoc.IAocTaskKt

class Day19 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_19.txt"
    // override fun getFileName(): String = "aoc2024/input_19_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val patterns = lines[0].split(", ")
            .filter { it.isNotBlank() }

        val designs = lines.subList(2, lines.size)

        designs.count { isPossible(it, patterns) }
            .let { println(it) }
    }

    override fun solvePartTwo(lines: List<String>) {
        val patterns = lines[0].split(", ")
            .filter { it.isNotBlank() }

        val designs = lines.subList(2, lines.size)

        val memo  = mutableMapOf<String, Long>()
        designs.sumOf { countPossible(it, patterns, memo) }
            .let { println(it) }
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
        return false
    }

    private fun countPossible(
        design: String,
        patterns: List<String>,
        memo: MutableMap<String, Long>
    ): Long {
        if (memo.containsKey(design)) {
            return memo[design]!!
        }
        if (design.isEmpty()) {
            return 1
        }
        var options = 0L
        for (pattern in patterns) {
            if (design.startsWith(pattern)) {
                options += countPossible(design.substring(pattern.length), patterns, memo)
            }
        }
        memo[design] = options
        return options
    }
}
