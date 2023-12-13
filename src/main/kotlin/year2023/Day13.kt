package year2023

import aoc.IAocTaskKt
import kotlin.math.min

class Day13 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_13.txt"

    override fun solvePartOne(lines: List<String>) {
        val patterns = lines.fold(emptyList<List<String>>()) { acc, next ->
            if (acc.isEmpty()) acc + listOf(listOf(next))
            else if (next.isBlank()) acc + listOf(emptyList())
            else {
                val result = acc.subList(0, acc.size - 1)
                val updatedLastList = acc.last() + next
                result + listOf(updatedLastList)
            }
        }
//            .onEach { println(it) }
            .map(Pattern::create)
            .onEach { println(it) }

//        patterns.onEachIndexed { idx, it ->
//            println("pattern: $idx")
//            println(it.printByRows())
//            println("H: ${it.horizontalLineIx()}, V: ${it.verticalLineIdx()}")
//            println()
//        }
        println(patterns.sumOf { it.verticalLineIdx() + 100 * it.horizontalLineIx() })
    }

    override fun solvePartTwo(lines: List<String>) {
        val patterns = lines.fold(emptyList<List<String>>()) { acc, next ->
            if (acc.isEmpty()) acc + listOf(listOf(next))
            else if (next.isBlank()) acc + listOf(emptyList())
            else {
                val result = acc.subList(0, acc.size - 1)
                val updatedLastList = acc.last() + next
                result + listOf(updatedLastList)
            }
        }
            .map(Pattern::create)

        val unsmudged = patterns.map(Pattern::unSmudged)

        println(unsmudged.zip(patterns)
//            .onEach {
//                (unsmug, pattern) ->
//                            println("unsmug H: ${unsmug.horizontalLineIx()}, V: ${unsmug.verticalLineIdx()}")
//                            println("pattern H: ${pattern.horizontalLineIx()}, V: ${pattern.verticalLineIdx()}")
//            }
            .sumOf { (unsmug, pattern) ->
//                val vert = unsmug.verticalLineIdx()
//                val hor = unsmug.horizontalLineIx()
//                if (vert != 0 && hor == 0) vert
//                else if (vert == 0 && hor != 0) hor * 100
//                else { // both non zero
//                    val prevVert = pattern.verticalLineIdx()
//                    val prevHor = pattern.horizontalLineIx()
//                    if (prevVert != 0) hor * 100
//                    else vert
//                }
                val prevHor = pattern.horizontalLineIx() - 1
                val prevVer = pattern.verticalLineIdx() - 1
                val newResult = Pair(unsmug.horizontalLineIx(
                    differentThan = if (prevHor != -1) pattern.rows[prevHor] else ""
                ),
                    unsmug.verticalLineIdx(
                        differentThan = if (prevVer != -1) pattern.cols[prevVer] else ""
                    )
                )
                val (hor, ver) = newResult
                ver + hor * 100
            })
    }

    data class Pattern(val rows: List<String>, val cols: List<String>) {
        fun printByRows(): String {
            var result = rows.joinToString("\n")
            return result
        }

        fun verticalLineIdx(differentThan: String = ""): Int {
            val lineIndices = (cols.windowed(2).mapIndexed { idx, (left, right) -> Triple(idx, left, right) })
                .filter { (_, left, right) -> left == right }
                .filter { (idx, left, _) -> left != differentThan }
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

        fun horizontalLineIx(differentThan: String = ""): Int {
            val lineIndices = (rows.windowed(2).mapIndexed { idx, (up, down) -> Triple(idx, up, down) })
                .filter { (_, up, down) -> up == down }
                .filter { (idx, up, _) -> up != differentThan }
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

        private fun withSwitchedValueAt(x: Int, y: Int): Pattern {
            val newRows = rows.mapIndexed { idx, row ->
                if (idx == y) {
                    val replacement = if (row[x] == '.') "#" else "."
                    val newRow = row.split("").filter(String::isNotEmpty)
                        .toMutableList()
                    newRow[x] = replacement
                    newRow.joinToString("")
                } else row
            }
            return create(newRows)
        }

        fun unSmudged(): Pattern {
            val original = Pair(horizontalLineIx(), verticalLineIdx())
            for (y in rows.indices) {
                for (x in rows[0].indices) {
                    val unSmudged = withSwitchedValueAt(x, y)
                    val prevHor = horizontalLineIx() - 1
                    val prevVer = verticalLineIdx() - 1
                    val newResult = Pair(unSmudged.horizontalLineIx(
                        differentThan = if (prevHor != -1) rows[prevHor] else ""
                    ),
                        unSmudged.verticalLineIdx(
                            differentThan = if (prevVer != -1) cols[prevVer] else ""
                        )
                    )
                    if (newResult != Pair(0, 0)
//                        && newResult != original || newResult == original && hasDifferentLine(unSmudged)
                        ) {
//                        println("unsmugged $x, $y --> $newResult" )
//                        println("before")
//                        println(printByRows())
//                        println("after")
//                        println(unSmudged.printByRows())
                        return unSmudged
                    }
                }
            }
//            return this
            throw IllegalStateException("could not find unsmudged mirror \n${printByRows()}")
        }

        private fun hasDifferentLine(pattern: Pattern): Boolean {
            val hor = horizontalLineIx() - 1
            val ver = verticalLineIdx() - 1
            return if (hor != -1) pattern.rows[hor] != rows[hor]
            else pattern.cols[ver] != cols[ver]
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
