package year2025

import aoc.IAocTaskKt

class Day06 : IAocTaskKt {
    override fun getFileName(): String = "aoc2025/input_06.txt"

    override fun solvePartOne(lines: List<String>) {
        val values = lines.subList(0, lines.size - 1)
            .map {
                it.split("\\s+".toRegex())
                    .filter { line -> line.isNotBlank() }
                    .map(String::toLong)
            }
        val ops = lines.last()
            .split("\\s+".toRegex())

        val results = ops.mapIndexed { idx, op ->
            when (op) {
                "+" -> return@mapIndexed values.sumOf { it[idx] }
                "*" -> return@mapIndexed values.fold(1L) { a, b -> a * b[idx] }
                else -> throw IllegalArgumentException("Unknown op $op")
            }
        }

        println(results.sumOf { it })
    }

    override fun solvePartTwo(lines: List<String>) {
        TODO("Not yet implemented")
    }
}