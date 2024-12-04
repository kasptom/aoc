package year2024

import aoc.IAocTaskKt
import java.util.regex.Pattern

class Day04 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_04_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val vertical = lines
        val horizontal = lines[0].indices.map { idx ->
            lines.map { it[idx] }.joinToString("")
        }
        val vertRev = vertical.map { it.reversed() }
        val horRev = horizontal.map { it.reversed() }

        val word = Pattern.compile("XMAS")
        val all = vertical + horizontal + vertRev + horRev
        println(horizontal)
        println(horRev)
        println(vertical)
        println(vertRev)
        val counts = all.map { it.countWord(word) }
        println(counts.sum())
    }

    override fun solvePartTwo(lines: List<String>) {
        if (lines.isEmpty()) println("empty lines 4") else println(lines.size)
    }
}

private fun String.countWord(word: Pattern): Long {
    val count = word.matcher(this)
        .results()
        .count()
    return count
}
