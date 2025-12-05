package year2025

import aoc.IAocTaskKt

class Day05 : IAocTaskKt {
    override fun getFileName(): String = "aoc2025/input_05.txt"

    override fun solvePartOne(lines: List<String>) {
        val separator = lines.indexOfFirst { it.isBlank() }
        val ranges = lines.subList(0, separator)
            .map {
                val (from, to) = it.split("-")
                from.toLong()..to.toLong()
             }
        val values = lines.subList(separator + 1, lines.size)
            .map(String::toLong)
        println(values.count { v -> ranges.any { r -> v in r} })
    }

    override fun solvePartTwo(lines: List<String>) {
        TODO("Not yet implemented")
    }
}