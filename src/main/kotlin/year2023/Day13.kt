package year2023

import aoc.IAocTaskKt
import kotlin.math.min

class Day13 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_13.txt"

    override fun solvePartOne(lines: List<String>) {
        val patterns = lines.fold(emptyList<List<String>>()) {
            acc, next -> if (acc.isEmpty()) acc + listOf(listOf(next))
            else if (next.isBlank()) acc + listOf(emptyList())
            else {
                val result = acc.subList(0, acc.size - 1)
                val updatedLastList = acc.last() + next
                result + listOf(updatedLastList)
            }
        }.onEach { println(it) }
            .map(Pattern::create)
            .onEach { println(it) }

        patterns.onEachIndexed { idx, it ->
            println("pattern: $idx")
            println(it.printByRows())
            println("H: ${it.horizontalLineIx()}, V: ${it.verticalLineIdx()}")
            println()
        }
        println(patterns.sumOf { it.verticalLineIdx() +  100 * it.horizontalLineIx()})
    }

    override fun solvePartTwo(lines: List<String>) {
        println("Not yet implemented")
    }

    data class Pattern(val rows: List<String>, val cols: List<String>) {
        fun printByRows(): String {
            var result = rows.joinToString("\n")
            return result
        }

        fun verticalLineIdx(): Int {
            val lineIndices = (cols.windowed(2).mapIndexed {idx, (left, right) -> Triple(idx, left, right)})
                .filter { (_, left, right) -> left == right }
                .map { (idx, _, _) -> idx }

            return lineIndices.maxOfOrNull { checkVerticalLine(it) } ?: 0
        }

        private fun checkVerticalLine(lineIdx: Int): Int {
            if (lineIdx == -1) return 0

            var (left, right) = Pair(cols.subList(0, lineIdx + 1), cols.subList(lineIdx + 1, cols.size))
            val origLeftSize = left.size
            val origRightSize = right.size
            val size = min(left.size, right.size)
            left = left.reversed().subList(0, size)
            right = right.subList(0, size)
            if (left == right) {
        //                println(cols.subList(0, origLeftSize))
                return origLeftSize
            }
            return 0
        }

        fun horizontalLineIx(): Int {
            val lineIndices = (rows.windowed(2).mapIndexed {idx, (up, down) -> Triple(idx, up, down)})
                .filter { (_, up, down) -> up == down }
                .map { (idx, _, _) -> idx }

            return lineIndices.maxOfOrNull { checkHorizontalLine(it) } ?: 0
        }

        private fun checkHorizontalLine(lineIdx: Int): Int {
            if (lineIdx == -1) return 0

            var (up, down) = Pair(rows.subList(0, lineIdx + 1), rows.subList(lineIdx + 1, rows.size))
            val origUpSize = up.size
            val size = min(up.size, down.size)
            up = up.reversed().subList(0, size)
            down = down.subList(0, size)
            if (up == down) {
        //                println(rows.subList(0, origUpSize))
                return origUpSize
            }
            return 0
        }

        companion object {
            fun create(rows: List<String>): Pattern {
                val cols = rows.first().indices.map { idx ->
                    rows.map { row -> row[idx] }.joinToString("")
                }
                return Pattern(rows, cols)
            }
        }
    }

}
