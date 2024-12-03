package year2024

import aoc.IAocTaskKt
import java.util.regex.Pattern
import java.util.stream.Collectors

class Day03 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_03.txt"

    override fun solvePartOne(lines: List<String>) {
        val pattern = Pattern.compile("mul\\(\\d{1,3},\\d{1,3}\\)")

//        val testInput = "xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))"
        val mullsLines = lines.map { input -> pattern.matcher(input)
            .results()
            .map { it.group() }
            .collect(Collectors.toList())
        }

        val pairs = mullsLines.map { mull -> mull.map { it.replace("mul(", "") }
            .map { it.replace(")", "") }
            .map { it.split(",") }
            .map { (x, y) -> Pair(x.toLong(), y.toLong()) }}
            .flatten()

        println(pairs.sumOf { (x, y) -> x * y })
    }

    override fun solvePartTwo(lines: List<String>) {
        val pattern = Pattern.compile("mul\\(\\d{1,3},\\d{1,3}\\)|do\\(\\)|don't\\(\\)")

//        val testInput = "xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))"
        val mulls = lines.map { input -> pattern.matcher(input)
            .results()
            .map { it.group() }
            .collect(Collectors.toList())
        }.flatten()


        var result = 0L
        var compute: Boolean? = null
        for (op in mulls) {
            if (op == "do()") {
                compute = true
                continue
            }
            if (compute != null && op == "don't()") {
                compute = false
                continue
            }
            if (compute == null || compute == true) {
                val pair = op.replace("mul(", "")
                    .replace(")", "")
                    .split(",")
                    .let { (x, y) -> Pair(x.toLong(), y.toLong()) }
                result += pair.let { (x, y) -> x * y }
            }
        }

        println(result)
    }
}