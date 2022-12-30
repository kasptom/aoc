package year2022

import aoc.IAocTaskKt

class Day03 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_03.txt"

    override fun solvePartOne(lines: List<String>) {
        lines.asSequence()
            .map {
                it.chunked(it.length / 2)
                    .let { compartments ->
                        compartments[0].first { item -> compartments[1].contains(item) }
                    }
            }.map { commonItem -> commonItem.toPriority() }
            .sumOf { it }
            .let { println(it) }
    }

    override fun solvePartTwo(lines: List<String>) {
        lines.asSequence()
            .windowed(3, 3)
            .map { compartments ->
                compartments.map { it.toList() }
                    .flatten()
                    .distinct()
                    .first { item ->
                        compartments.all { compartment -> compartment.contains(item) }
                    }
            }.map { commonItem -> commonItem.toPriority() }
            .sumOf { it }
            .let { println(it) }
    }

    private fun Char.toPriority() = (code - 'a'.code + 1).let {
        if (it < 0) it + 58
        else it
    }
}