package year2024

import aoc.IAocTaskKt


class Day02 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_02.txt"

    override fun solvePartOne(lines: List<String>) {
        lines.map { line ->
            line.split(" ")
                .map { it.toInt() }
        }.count { it.isSafe() }
            .let { println(it) }
    }

    override fun solvePartTwo(lines: List<String>) {
        lines.map { line ->
            line.split(" ")
                .map { it.toInt() }
        }.count { it.isSafeWithDampener() }
            .let { println(it) }
    }
}

private fun List<Int>.isSafe(): Boolean = mapIndexed { idx, value -> idx == 0 || this[idx - 1] in (value + 1)..(value + 3) }
        .all { it }
            || mapIndexed { idx, value -> idx == 0 || this[idx - 1] in (value - 3) until value }
        .all { it }

private fun List<Int>.isSafeWithDampener(): Boolean = List(size) { deletedIdx ->
    filterIndexed { idx, _ -> idx != deletedIdx }
        .isSafe()
}.any { it }
