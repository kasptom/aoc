package year2024

import aoc.IAocTaskKt
import kotlin.math.abs

class Day01 : IAocTaskKt {
    override fun getFileName(): String =  "aoc2024/input_01.txt"

    override fun solvePartOne(lines: List<String>) {
        val a = lines.map { line -> line.split(" ").first().toInt() }.sorted()
        val b = lines.map { line -> line.split(" ").last().toInt() }.sorted()
        val result = a.zip(b) { x, y -> abs(x - y) }.sum()
        println(result)
    }

    override fun solvePartTwo(lines: List<String>) {
        val a = lines.map { line -> line.split(" ").first().toInt() }.sorted()
        val b = lines.map { line -> line.split(" ").last().toInt() }.sorted()

        var occurrences = 0
        for (num in a) {
            occurrences += num * b.count { it == num }
        }
        println(occurrences)
    }
}