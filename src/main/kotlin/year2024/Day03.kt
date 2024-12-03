package year2024

import aoc.IAocTaskKt
import java.util.regex.Pattern
import java.util.stream.Collectors

class Day03 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_03.txt"

    override fun solvePartOne(lines: List<String>) {
        val pattern = Pattern.compile("mul\\(\\d{1,3},\\d{1,3}\\)")

//        val testInput = "xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))"
        val input = lines[0];
        val mulls = pattern.matcher(input)
            .results()
            .map { it.group() }
            .collect(Collectors.toList())

        val pairs = mulls.map { it.replace("mul(", "") }
            .map { it.replace(")", "") }
            .map { it.split(",") }
            .map { (x, y) -> Pair(x.toLong(), y.toLong()) }

        println(pairs.sumOf { (x, y) -> x * y })
    }

    override fun solvePartTwo(lines: List<String>) {
        if (lines.isEmpty()) println("empty lines") else println(lines.size)
    }
}