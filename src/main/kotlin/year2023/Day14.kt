package year2023

import aoc.IAocTaskKt

class Day14: IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_14.txt"

    override fun solvePartOne(lines: List<String>) {
        val grid = Grid.create(lines)
        println(grid.printByRows())
        val tilted = grid.tilt()
        println()
        println(tilted.printByRows())
        println(tilted.totalLoad())
    }

    override fun solvePartTwo(lines: List<String>) {
        TODO("Not yet implemented")
    }

    data class Grid(val rows: List<String>, val cols: List<String>) {
        companion object {
            fun create(rows: List<String>): Grid {
                val cols = rows.first().indices.map { idx ->
                    rows.map { row -> row[idx] }.joinToString("")
                }
                return Grid(rows, cols)
            }
        }

        fun printByRows(): String = rows.joinToString("\n")

        fun tilt(): Grid {
            var prevCols = cols
            var nextCols = cols.moveUp()

            while (prevCols != nextCols) {
                prevCols = nextCols
                nextCols = nextCols.moveUp()
            }
            val newRows = rows.indices.map { nextCols.map { col -> col[it] }.joinToString("") }
            return Grid(newRows, nextCols)
        }

        fun totalLoad(): Int {
            var totalLoad = 0
            for (col in cols) {
                val cells = col.split("").filter(String::isNotEmpty)
                val height = cells.size
                for (cellIdx in cells.indices) {
                    if (cells[cellIdx] == "O") totalLoad += height - cellIdx
                }
            }
            return totalLoad
        }
    }

    data class Point(val x: Int, val y: Int)
}

private fun List<String>.moveUp(): List<String> {
    val movedCols = mutableListOf<String>()
    for (col in this) {
        val moved = col.split("").filter(String::isNotEmpty)
            .toMutableList()
        for (idx in indices) {
            if (moved[idx] == "O" && idx - 1 >= 0 && moved[idx - 1] == ".") {
                moved[idx] = "."
                moved[idx - 1] = "O"
            }
        }
        val movedJoined = moved.joinToString("")
        movedCols += movedJoined
    }

    return movedCols
}
