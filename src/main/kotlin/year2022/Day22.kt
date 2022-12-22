package year2022

import aoc.IAocTaskKt
import year2022.Day22.MonkeyMove.Companion.NEXT_DIR_ANTICLOCKWISE
import year2022.Day22.MonkeyMove.Companion.NEXT_DIR_CLOCKWISE
import year2022.Day22.MonkeyMove.FacingDirection
import year2022.Day22.MonkeyMove.FacingDirection.*
import year2022.Day22.MonkeyPosition
import year2022.Day22.MonkeyPosition.Companion.ANSI_GREEN
import year2022.Day22.MonkeyPosition.Companion.ANSI_RESET

typealias MonkeyGrid = List<List<Day22.MonkeyCell>>

class Day22 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_22.txt"

    // too high 162156
    override fun solvePartOne(lines: List<String>) {
        val blankLineIdx = lines.indexOfFirst { it.isEmpty() }
        val map = lines.subList(0, blankLineIdx)
            .let { mapLines -> MonkeyMap.parse(mapLines) }
        val moves = lines.subList(blankLineIdx + 1, lines.size)
            .single()
            .replace(Regex("(\\d+)"), "\$1,")
            .replace(Regex("([RL])"), "\$1,")
            .split(",")
            .filter { it.isNotEmpty() }
            .map { MonkeyMove.parse(it) }

        var position = MonkeyPosition(map.firstAvailableAtRow(1), facing = RIGHT)
        println(map.display(position))
        println(moves)

        for (move in moves) {
            map.mark(position)
            position = position.change(move, map)
        }

        println(position)
        println(map.display(position))
        println(map.moveHistory)
        println(position.password())
    }

    override fun solvePartTwo(lines: List<String>) {
        println("Not yet implemented")
    }

    data class MonkeyMap(val grid: MonkeyGrid, val moveHistory: MutableList<MonkeyPosition> = mutableListOf()) {
        fun display(monkeyPosition: MonkeyPosition): Any {
            return grid.display(monkeyPosition, moveHistory)
        }

        fun firstAvailableAtRow(row: Int): Point {
            return grid[row - 1].first { it.state != " " }.pos
        }

        fun mark(position: MonkeyPosition) {
            moveHistory.add(position)
        }

        fun minColumnAt(row: Int): Point {
            return grid[row - 1].first { it.state != " " }.pos
        }

        fun maxColumnAt(row: Int): Point {
            return grid[row - 1].last { it.state != " " }.pos
        }

        fun minRowAt(currentCol: Int): Point {
            return grid
                .filter { row -> currentCol - 1 < row.size }
                .map { row -> row[currentCol - 1] }.first { it.state != " " }.pos
        }

        fun maxRowAt(currentCol: Int): Point {
            return grid
                .filter { row -> currentCol - 1 < row.size }
                .map { row -> row[currentCol - 1] }
                .last { it.state != " " }.pos
        }

        operator fun get(pos: Point): MonkeyCell {
            return grid[pos.y - 1][pos.x - 1]
        }

//        fun clearLine(pos: Point, facing: FacingDirection): Boolean {
//            return when (facing) {
//                UP, DOWN -> grid[pos.y - 1].none { it.state == "#" }
//                LEFT, RIGHT -> grid
//                    .filter { rows -> pos.x - 1 < rows.size }
//                    .map { rows -> rows[pos.x - 1] }.none { it.state == "#" }
//            }
//        }

        companion object {
            fun parse(mapLines: List<String>): MonkeyMap {
                val grid = mapLines
                    .mapIndexed { yIdx, line ->
                        line.chunked(1)
                            .mapIndexed { xIdx, cell -> MonkeyCell(Point(xIdx + 1, yIdx + 1), cell) }
                    }
                return MonkeyMap(grid)
            }
        }
    }

    data class MonkeyCell(val pos: Point, val state: String)

    sealed interface MonkeyMove {
        enum class FacingDirection(val delta: Point, val value: Int, val arrow: String) {
            UP(Point(0, -1), 3, "^"),
            DOWN(Point(0, 1), 1, "v"),
            LEFT(Point(-1, 0), 2, "<"),
            RIGHT(Point(1, 0), 0, ">");

            override fun toString(): String = arrow
            fun opposite(): FacingDirection = when (this) {
                UP -> DOWN
                DOWN -> UP
                LEFT -> RIGHT
                RIGHT -> LEFT
            }
        }

        companion object {
            fun parse(moveCode: String): MonkeyMove {
//                println("parsing $moveCode")
                return if (moveCode.startsWith("R") || moveCode.startsWith("L")) {
                    MonkeyRotation(moveCode[0].toString(), 1)
                } else {
                    MonkeyWalk(moveCode.toInt())
                }
            }

            val NEXT_DIR_CLOCKWISE = mapOf(
                UP to listOf(UP, RIGHT, DOWN, LEFT),
                RIGHT to listOf(RIGHT, DOWN, LEFT, UP),
                DOWN to listOf(DOWN, LEFT, UP, RIGHT),
                LEFT to listOf(LEFT, UP, RIGHT, DOWN),
            )

            val NEXT_DIR_ANTICLOCKWISE = mapOf(
                UP to listOf(UP, LEFT, DOWN, RIGHT),
                LEFT to listOf(LEFT, DOWN, RIGHT, UP),
                DOWN to listOf(DOWN, RIGHT, UP, LEFT),
                RIGHT to listOf(RIGHT, UP, LEFT, DOWN),
            )
        }
    }

    data class MonkeyWalk(val tilesCount: Int) : MonkeyMove {
        override fun toString(): String = "️👣$tilesCount"

        fun getNextPosition(map: MonkeyMap, current: MonkeyPosition, facing: FacingDirection): Point? {
            var next = current.pos + facing.delta
            when (facing) {
                UP, DOWN -> {
                    val minCell = map.minRowAt(current.pos.x)
                    val maxCell = map.maxRowAt(current.pos.x)
                    if (next.y < minCell.y) {
                        next = maxCell
                    } else if (maxCell.y < next.y) {
                        next = minCell
                    }
                }
                LEFT, RIGHT -> {
                    val minCell = map.minColumnAt(current.pos.y)
                    val maxCell = map.maxColumnAt(current.pos.y)
                    if (next.x < minCell.x) {
                        next = maxCell
                    } else if (maxCell.x < next.x) {
                        next = minCell
                    }
                }
            }
            return if (map[next].state == ".") next else null
        }
    }

    data class MonkeyRotation(val direction: String, val times: Int) : MonkeyMove {
        override fun toString(): String = if (direction == "L") "◀⌚" else "⌚▶"
    }

    data class MonkeyPosition(val pos: Point, val facing: FacingDirection) {

        fun change(move: MonkeyMove, map: MonkeyMap): MonkeyPosition {
            print("move: $move, ")
            val newPosition = when (move) {
                is MonkeyRotation -> {
                    val times = move.times % 4
                    val rotationMap = if (move.direction == "R") NEXT_DIR_CLOCKWISE else NEXT_DIR_ANTICLOCKWISE
                    this.copy(
                        pos = pos,
                        facing = rotationMap[facing]!![times]
                    )
                }

                is MonkeyWalk -> {
//                    if (map.clearLine(pos, facing)) {
//                        println("all line clear")
//                        val notClipped = this + move.tilesCount
//                        when (facing) {
//                            UP, DOWN -> clipVertical(notClipped, map, facing)
//                            LEFT, RIGHT -> clipHorizontal(notClipped, map, facing)
//                        }
//                    } else {
                        println("moving until wall reached")
                        moveUntilWall(map, move, facing)
//                    }
                }
            }
            println("position change: $this --$move--> $newPosition")
//            println(map.display(newPosition))
            return newPosition
        }

        private fun moveUntilWall(map: MonkeyMap, move: MonkeyWalk, facing: FacingDirection): MonkeyPosition {
            var finalPosition = this
            var stepsCounter = 0
            var next = move.getNextPosition(map, finalPosition, facing)
            while (next != null && stepsCounter < move.tilesCount) {
                finalPosition = this.copy(pos = next)
                next = move.getNextPosition(map, finalPosition, facing)
                stepsCounter++
            }

            return finalPosition
        }

        private fun clipHorizontal(notClipped: MonkeyPosition, map: MonkeyMap, facing: FacingDirection): MonkeyPosition {
            val currentRow = notClipped.pos.y
            val minColumn = map.minColumnAt(currentRow)
            val maxColumn = map.maxColumnAt(currentRow)
            val rowWidth = maxColumn.x - minColumn.x + 1
            var clipped = if (notClipped.pos.x > maxColumn.x) {
                val exceededCellsCount = notClipped.pos.x - maxColumn.x - 1
                val correctXPos = minColumn + Point(exceededCellsCount % rowWidth, 0)
                notClipped.copy(pos = correctXPos)
            } else if (notClipped.pos.x < minColumn.x) {
                val exceededCellsCount = minColumn.x - notClipped.pos.x - 1
                val correctXPos = maxColumn - Point(exceededCellsCount % rowWidth, 0)
                notClipped.copy(pos = correctXPos)
            } else return notClipped
            while (map[clipped.pos].state == "#") {
                clipped -= facing.delta
            }

            return clipHorizontal(clipped, map, facing.opposite())
        }

        private fun clipVertical(notClipped: MonkeyPosition, map: MonkeyMap, facing: FacingDirection): MonkeyPosition {
            val currentCol = notClipped.pos.x
            val minRow: Point = map.minRowAt(currentCol)
            val maxRow: Point = map.maxRowAt(currentCol)
            val colWidth = maxRow.y - minRow.y + 1
            var clipped = if (notClipped.pos.y > maxRow.y) {
                val exceededRowsCount = notClipped.pos.y - maxRow.y - 1
                val correctYPos = minRow + Point(0, exceededRowsCount % colWidth)
                notClipped.copy(pos = correctYPos)
            } else if (notClipped.pos.y < minRow.y) {
                val exceededRowsCount = minRow.y - notClipped.pos.y - 1
                val correctYPos = maxRow - Point(0, exceededRowsCount % colWidth)
                notClipped.copy(pos = correctYPos)
            } else return notClipped

            while (map[clipped.pos].state == "#") {
                clipped -= facing.delta
            }

            return clipVertical(clipped, map, facing.opposite())
        }

        private operator fun plus(tilesCount: Int): MonkeyPosition =
            MonkeyPosition(pos + facing.delta * tilesCount, facing)

        private operator fun minus(delta: Point): MonkeyPosition = copy(pos = pos - delta)

        operator fun plus(delta: Point): MonkeyPosition = copy(pos = pos + delta)

        fun password(): Int = 1000 * pos.y + 4 * pos.x + facing.value
        override fun toString(): String {
            return "🐵: $pos $facing"
        }


        companion object {
            const val ANSI_RESET = "\u001B[0m"
            const val ANSI_GREEN = "\u001B[32m"
        }
    }

    data class Point(val x: Int, val y: Int) {
        operator fun times(tilesCount: Int): Point = Point(x * tilesCount, y * tilesCount)
        operator fun plus(point: Point): Point = Point(x + point.x, y + point.y)
        operator fun minus(point: Point): Point = Point(x - point.x, y - point.y)
        override fun toString(): String = "($x, $y)"
    }
}

private fun MonkeyGrid.display(monkeyPosition: MonkeyPosition, moveHistory: MutableList<MonkeyPosition>): String {
    return this.joinToString("\n") { row ->
        row.joinToString("") {
            val foundHistoryEntry = moveHistory.firstOrNull { hist -> hist.pos == it.pos }
            if (it.pos == monkeyPosition.pos) "$ANSI_GREEN${monkeyPosition.facing.arrow}$ANSI_RESET"
            else foundHistoryEntry?.facing?.arrow ?: it.state
        }
    }
}
