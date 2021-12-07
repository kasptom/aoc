package year2021

import aoc.IAocTaskKt
import kotlin.math.abs

class Day07 : IAocTaskKt {
    override fun getFileName(): String = "aoc2021/input_07.txt"

    override fun solvePartOne(lines: List<String>) {
        val crabPositions = lines[0].split(",")
            .filter(String::isNotBlank)
            .map(Integer::parseInt)
        val min = crabPositions.minOf { it }
        val max = crabPositions.maxOf { it }

        val pos = (min..max).minOf { crabPositions.sumOf { cp -> abs(it - cp) } }
        println(pos)
    }

    override fun solvePartTwo(lines: List<String>) {
        val crabPositions = lines[0].split(",")
            .filter(String::isNotBlank)
            .map(Integer::parseInt)
        val min = crabPositions.minOf { it }
        val max = crabPositions.maxOf { it }

        val pos = (min..max).minOf { crabPositions.sumOf { cp -> cost2(it, cp) } }
        println(pos)
    }

    private fun cost2(it: Int, cp: Int): Int {
        val range = abs(it - cp)
        return (1 + range) * range / 2
    }
}
