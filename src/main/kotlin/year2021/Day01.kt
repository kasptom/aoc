package year2021

import aoc.IAocTaskKt

class Day01 : IAocTaskKt {

    override fun getFileName() = "aoc2021/input_01.txt"
    override fun solvePartOne(lines: List<String>) {
        val increases = lines.map { Integer.parseInt(it) }
            .windowed(2, 1) { prevCur -> if (prevCur[0] < prevCur[1]) 1 else 0 }
            .sumOf { it }
        println(increases)
    }

    override fun solvePartTwo(lines: List<String>) {
        val increases = lines.map { Integer.parseInt(it) }
            .windowed(4, 1) { if (it.subList(0, 3).sum() < it.subList(1, 4).sum()) 1 else 0 }
            .sumOf { it }
        println(increases)
    }
}