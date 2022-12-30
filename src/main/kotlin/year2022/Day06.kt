package year2022

import aoc.IAocTaskKt

class Day06 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_06.txt"

    override fun solvePartOne(lines: List<String>) {
        val line = lines[0]
        val length = 4
        val startFrom = startFrom(line, length)
        println(startFrom)
    }

    override fun solvePartTwo(lines: List<String>) {
        val line = lines[0]
        val length = 14
        val startFrom = startFrom(line, length)
        println(startFrom)
    }

    private fun startFrom(line: String, length: Int): Int = line.chunked(1)
        .mapIndexed { idx, it -> Pair(idx, it) }
        .windowed(length)
        .first { group -> group.map { it.second }.distinct().count() == length }
//        .also { println(it) }
        .last().first + 1
}
