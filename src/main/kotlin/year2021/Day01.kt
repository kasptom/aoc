package year2021

import aoc.IAocTaskKt

class Day01 : IAocTaskKt {

    override fun getFileName() = "aoc2021/input_01.txt"
    override fun solvePartOne(lines: List<String>) {
        val depths = lines.map { Integer.parseInt(it) }
        var increases = 0
        for (idx in 1 until depths.size) {
            if (depths[idx - 1] < depths[idx]) increases++
        }
        println(increases)
    }

    override fun solvePartTwo(lines: List<String>) {
        val depths = lines.map { Integer.parseInt(it) }
        var increases = 0
        var prevSum = depths[0] + depths[1] + depths[2]
        for (idx in 1..depths.size - 3) {
            val sum = depths[idx] + depths[idx + 1] + depths[idx + 2]
            if (sum > prevSum) increases++
            prevSum = sum
        }
        println(increases)
    }
}