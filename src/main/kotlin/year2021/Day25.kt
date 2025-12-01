package year2021

import aoc.IAocTaskKt
import utils.ANSI_GREEN
import utils.ANSI_RESET
import utils.ANSI_YELLOW


const val EMPTY_SPACE = "."
const val SPACE_LEFT_BY_EAST_CUCUMBER = "X"
const val SPACE_LEFT_BY_SOUTH_CUCUMBER = "Y"
const val EAST_CUCUMBER = ">"
const val SOUTH_CUCUMBER = "v"
const val MOVED_SOUTH_CUCUMBER = "S"
const val MOVED_EAST_CUCUMBER = "E"

class Day25 : IAocTaskKt {
    var width = 0
    var height = 0

    override fun getFileName() = "aoc2021/input_25.txt"

    override fun solvePartOne(lines: List<String>) {
        val map = lines.map { line -> line.chunked(1).toMutableList() }
        width = map[0].size
        height = map.size

        val leftEdgeQueue = mutableListOf<MutableList<String>>()
        val topEdgeQueue = mutableListOf<MutableList<String>>()

        repeat(width) { topEdgeQueue.add(mutableListOf()) }
        repeat(height) { leftEdgeQueue.add(mutableListOf()) }

//        displayMap(map, 0)

        var step = 0
        while (movableEastCucumberExists(map)) {
            step++
            moveEastCucumbers(map)
            moveSouthCucumbers(map)
//            displayMap(map, step)
            renameMovedCucumbers(map)
        }
        println(step + 1)
    }

    private fun movableEastCucumberExists(map: List<List<String>>): Boolean {
        for (row in map) {
            for (colIdx in row.indices) {
                val cell = row[colIdx]
                if (cell == EAST_CUCUMBER && colIdx < row.size - 1 && row[colIdx + 1] == EMPTY_SPACE) {
                    return true
                } else if (cell == EAST_CUCUMBER && colIdx == row.size - 1 && row[0] == EMPTY_SPACE) {
                    return true
                }
            }
        }
        return false
    }

    private fun renameMovedCucumbers(map: List<MutableList<String>>) {
        for (rowIdx in map.indices) {
            for (colIdx in map[0].indices) {
                when {
                    map[rowIdx][colIdx] == MOVED_EAST_CUCUMBER -> map[rowIdx][colIdx] = EAST_CUCUMBER
                    map[rowIdx][colIdx] == MOVED_SOUTH_CUCUMBER -> map[rowIdx][colIdx] = SOUTH_CUCUMBER
                    map[rowIdx][colIdx] == SPACE_LEFT_BY_SOUTH_CUCUMBER -> map[rowIdx][colIdx] = EMPTY_SPACE
                    map[rowIdx][colIdx] == SPACE_LEFT_BY_EAST_CUCUMBER -> map[rowIdx][colIdx] = EMPTY_SPACE
                }
            }
        }
    }

    private fun moveEastCucumbers(map: List<MutableList<String>>) {
        for (rowIdx in map.indices) {
            val row = map[rowIdx]
            for (colIdx in row.indices) {
                val cell = row[colIdx]
                if (cell == EAST_CUCUMBER && colIdx < row.size - 1 && row[colIdx + 1] == EMPTY_SPACE) {
                    row[colIdx + 1] = MOVED_EAST_CUCUMBER
                    row[colIdx] = SPACE_LEFT_BY_EAST_CUCUMBER
                } else if (cell == EAST_CUCUMBER && colIdx == row.size - 1 && map[rowIdx][0] == EMPTY_SPACE) {
                    map[rowIdx][0] = MOVED_EAST_CUCUMBER
                    row[colIdx] = SPACE_LEFT_BY_EAST_CUCUMBER
                }
            }
        }
    }

    private fun moveSouthCucumbers(map: List<MutableList<String>>) {
        for (colIdx in map[0].indices) {
            for (rowIdx in map.indices) {
                val cell = map[rowIdx][colIdx]
                if (cell == SOUTH_CUCUMBER && rowIdx < map.size - 1 && (map[rowIdx + 1][colIdx] == EMPTY_SPACE || map[rowIdx + 1][colIdx] == SPACE_LEFT_BY_EAST_CUCUMBER)) {
                    map[rowIdx + 1][colIdx] = MOVED_SOUTH_CUCUMBER
                    map[rowIdx][colIdx] = SPACE_LEFT_BY_SOUTH_CUCUMBER
                } else if (cell == SOUTH_CUCUMBER && rowIdx == map.size - 1 && (map[0][colIdx] == EMPTY_SPACE || map[0][colIdx] == SPACE_LEFT_BY_EAST_CUCUMBER)) {
                    map[0][colIdx] = MOVED_SOUTH_CUCUMBER
                    map[rowIdx][colIdx] = SPACE_LEFT_BY_SOUTH_CUCUMBER
                }
            }
        }
    }


    @Suppress("unused")
    private fun displayMap(map: List<List<String>>, step: Int) {
        when (step) {
            0 -> println("Initial state:")
            1 -> println("After 1 step:")
            else -> println("After $step steps:")
        }
        for (row in map) {
            for (column in row) {
                when (column) {
                    MOVED_EAST_CUCUMBER -> print("$ANSI_GREEN>$ANSI_RESET")
                    SPACE_LEFT_BY_EAST_CUCUMBER -> print("${ANSI_GREEN}.$ANSI_RESET")
                    MOVED_SOUTH_CUCUMBER -> print("${ANSI_YELLOW}v$ANSI_RESET")
                    SPACE_LEFT_BY_SOUTH_CUCUMBER -> print("${ANSI_YELLOW}.$ANSI_RESET")
                    else -> print(column)
                }
            }
            println()
        }
        println()
    }

    override fun solvePartTwo(lines: List<String>) {
        println("part two")
    }
}