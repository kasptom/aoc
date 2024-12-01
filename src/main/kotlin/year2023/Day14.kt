package year2023

import aoc.IAocTaskKt

class Day14: IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_14.txt"

    override fun solvePartOne(lines: List<String>) {
        val grid = Grid.create(lines)
//        println(grid.printByRows())
        val tilted = grid.moveNorth()
//        println()
//        println(tilted.printByRows())
        println(tilted.totalLoad())
    }

    override fun solvePartTwo(lines: List<String>) {
        val grid = Grid.create(lines)
        var counter = 0L
        var cycled = grid
        val stateToCycleCount = mutableMapOf<Grid, Long>()
        var steps = 1000000000L
        var jump = 0L
        while (counter != steps) {
            counter++
//            println("After $counter cycles: ")
            cycled = cycled.cycle()
//            println(cycled.printByRows())
//            println()
            if (!stateToCycleCount.containsKey(cycled)) {
                stateToCycleCount[cycled] = counter
            } else {
//                println("cycle detected ${stateToCycleCount[cycled]} --> $counter")
                jump = counter - stateToCycleCount[cycled]!!
                break
            }
        }
        steps = (steps - counter) % jump
        counter = 0
        while (counter != steps) {
            cycled = cycled.cycle()
            counter++
        }

//        println()
//        println(cycled.printByRows())
        println(cycled.totalLoad())
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

        fun moveNorth(): Grid {
            var prevCols = cols
            var nextCols = cols.moveNorth()

            while (prevCols != nextCols) {
                prevCols = nextCols
                nextCols = nextCols.moveNorth()
            }
            val newRows = rows.indices.map { nextCols.map { col -> col[it] }.joinToString("") }
            return Grid(newRows, nextCols)
        }

        fun moveSouth(): Grid {
            var prevCols = cols
            var nextCols = cols.moveSouth()

            while (prevCols != nextCols) {
                prevCols = nextCols
                nextCols = nextCols.moveSouth()
            }
            val newRows = rows.indices.map { nextCols.map { col -> col[it] }.joinToString("") }
            return Grid(newRows, nextCols)
        }


        fun moveWest(): Grid {
            var prevRows = rows
            var nextRows = rows.moveWest()

            while (prevRows != nextRows) {
                prevRows = nextRows
                nextRows = nextRows.moveWest()
            }
            return create(nextRows)
        }

        fun moveEast(): Grid {
            var prevRows = rows
            var nextRows = rows.moveEast()

            while (prevRows != nextRows) {
                prevRows = nextRows
                nextRows = nextRows.moveEast()
            }
            return create(nextRows)
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

        fun cycle(): Grid {
            var updatedGrid = moveNorth()
//            println("NORTH")
//            println(updatedGrid.printByRows())
            updatedGrid = updatedGrid.moveWest()
//            println("WEST")
//            println(updatedGrid.printByRows())
            updatedGrid = updatedGrid.moveSouth()
//            println("SOUTH")
//            println(updatedGrid.printByRows())
            updatedGrid = updatedGrid.moveEast()
//            println("EAST")
//            println(updatedGrid.printByRows())
            return updatedGrid
        }
    }

    data class Point(val x: Int, val y: Int)
}

private fun List<String>.moveNorth(): List<String> {
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

private fun List<String>.moveSouth(): List<String> {
    val movedCols = mutableListOf<String>()
    for (col in this) {
        val moved = col.split("").filter(String::isNotEmpty)
            .toMutableList()
        for (idx in (moved.size - 1) downTo 0) {
            if (moved[idx] == "O" && idx + 1 < moved.size && moved[idx + 1] == ".") {
                moved[idx] = "."
                moved[idx + 1] = "O"
            }
        }
        val movedJoined = moved.joinToString("")
        movedCols += movedJoined
    }

    return movedCols
}

private fun List<String>.moveWest(): List<String> {
    val movedRows = mutableListOf<String>()
    for (row in this) {
        val moved = row.split("").filter(String::isNotEmpty)
            .toMutableList()
        for (idx in moved.indices) {
            if (moved[idx] == "O" && idx - 1 >= 0 && moved[idx - 1] == ".") {
                moved[idx] = "."
                moved[idx - 1] = "O"
            }
        }
        val movedJoined = moved.joinToString("")
        movedRows += movedJoined
    }

    return movedRows
}

private fun List<String>.moveEast(): List<String> {
    val movedRows = mutableListOf<String>()
    for (row in this) {
        val moved = row.split("").filter(String::isNotEmpty)
            .toMutableList()
        for (idx in (moved.size - 1) downTo 0) {
            if (moved[idx] == "O" && idx + 1 < moved.size && moved[idx + 1] == ".") {
                moved[idx] = "."
                moved[idx + 1] = "O"
            }
        }
        val movedJoined = moved.joinToString("")
        movedRows += movedJoined
    }

    return movedRows
}
