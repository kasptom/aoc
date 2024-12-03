package year2024

import aoc.IAocTaskKt
import java.util.regex.Pattern
import java.util.stream.Collectors

class Day03 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_03.txt"

    override fun solvePartOne(lines: List<String>) {
        val pattern = Pattern.compile("mul\\(\\d{1,3},\\d{1,3}\\)")
        val mulls = extract(lines, pattern).flatten()

        val pairs = mulls.map { it.mullToXy() }
        println(pairs.sumOf { (x, y) -> x * y })
    }

    override fun solvePartTwo(lines: List<String>) {
        val pattern = Pattern.compile("mul\\(\\d{1,3},\\d{1,3}\\)|do\\(\\)|don't\\(\\)")
        val mulls = extract(lines, pattern).flatten()

        val enabled = mutableListOf<Pair<Long, Long>>()

        var isEnabled: Boolean? = null
        for (op in mulls) {
            if (op == "do()") {
                isEnabled = true
            } else if (op == "don't()" && isEnabled != null && isEnabled == true) {
                isEnabled = false
            } else if (isEnabled == null || isEnabled == true){
                enabled += op.mullToXy()
            }
        }

        println(enabled.sumOf { (x, y) -> x * y })
    }

    private fun extract(
        lines: List<String>,
        pattern: Pattern,
    ) = lines.map { input ->
        pattern.matcher(input)
            .results()
            .map { it.group() }
            .collect(Collectors.toList())
    }
}

fun String.mullToXy() = replace("mul(", "")
    .replace(")", "")
    .split(",")
    .let { (x, y) -> Pair(x.toLong(), y.toLong()) }