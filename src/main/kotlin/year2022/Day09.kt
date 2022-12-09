package year2022

import aoc.IAocTaskKt
import kotlin.math.abs

class Day09 : IAocTaskKt {
    private val dxs = arrayOf(-1, 0, 1, -1, 1, -1, 0, 1)
    private val dys = arrayOf(-1, -1, -1, 0, 0, 1, 1, 1)
    private val moveToIdx = mapOf("L" to 3, "U" to 6, "R" to 4, "D" to 1)

    override fun getFileName(): String = "aoc2022/input_09.txt"

    override fun solvePartOne(lines: List<String>) {
        val gridSize = 2000
        val startPosition = Pair(gridSize / 2, gridSize / 2)
        val grid: MutableList<MutableList<RopeCell>> = mutableListOf()

        for (y in 0 until gridSize) {
            grid.add(mutableListOf())
            for (x in 0 until gridSize) {
                grid[y].add(RopeCell(x = x, y = y,
                    tailVisitCount = 0,
                    CellState.EMPTY
                ))
            }
        }
        val startCell = grid[startPosition.first / 2][startPosition.second / 2]
        val updatedStartCell = startCell.copy(tailVisitCount = 1, state = CellState.HEAD_AND_TAIL)
        grid[startCell.x][startCell.y] = updatedStartCell

        val moves = lines.map { it.split(" ").zipWithNext().single() }
            .map { (code, offset) ->
                val dx = dxs[moveToIdx[code]!!]
                val dy = dys[moveToIdx[code]!!]
                Move(code, dx, dy, offset.toInt())
            }
        moves.onEach { println(it) }
        grid.print()
        var headPosition = updatedStartCell.getPosition()
        var tailPosition = updatedStartCell.getPosition()
        for (move in moves) {
            for (times in 1..move.length) {
                val (newHeadPosition, newTailPosition) = move(grid, move, headPosition, tailPosition)
                headPosition = newHeadPosition
                tailPosition = newTailPosition
                println(move.toString() + " " + "$times / ${move.length}")
                grid.print()
            }
        }
        grid.print()
        grid.flatten().count { it.tailVisitCount > 0 }.let { println(it) }
    }

    fun move(grid: MutableList<MutableList<RopeCell>>, move: Move, headPosition: Point, tailPosition: Point): Pair<Point, Point> {
        val newHeadPosition = Point(
            x = headPosition.x + move.dx,
            y = headPosition.y + move.dy
        )
        val headCell = grid[headPosition.x][headPosition.y]
        val tailCell = grid[tailPosition.x][tailPosition.y]
        val newHeadCell = grid[newHeadPosition.x][newHeadPosition.y]

        if (tailPosition.notTouching(newHeadPosition) && tailPosition.diagonal(newHeadPosition)) {
            headCell.state = CellState.TAIL
            headCell.tailVisitCount++

            tailCell.state = CellState.EMPTY
            newHeadCell.state = CellState.HEAD

            return Pair(newHeadPosition, headPosition)
        } else if(tailPosition.notTouching(newHeadPosition) && tailPosition.diagonal(newHeadPosition).not()) {
            headCell.state = CellState.TAIL
            headCell.tailVisitCount++

            newHeadCell.state = CellState.HEAD
            tailCell.state = CellState.EMPTY

            return Pair(newHeadPosition, headPosition)
        } else if (tailPosition.touching(newHeadPosition)){
            newHeadCell.state = CellState.HEAD
            headCell.state = CellState.EMPTY
            return Pair(newHeadPosition, tailPosition)
        } else throw IllegalStateException("unknown state")
//        if (headPosition == tailPosition) { // initial state
//            headCell.state = CellState.TAIL
//            newHeadCell.state = CellState.HEAD
//            return Pair(newHeadPosition, headPosition)
//        } else if(headPosition.y == tailPosition.y && (move.code == "L" || move.code == "R")) {
//            headCell.state = CellState.TAIL
//            tailCell.state = CellState.EMPTY
//            headCell.tailVisitCount++
//            newHeadCell.state = CellState.HEAD
//            return Pair(newHeadPosition, headPosition)
//        } else if(headPosition.x == tailPosition.x && (move.code == "U" || move.code == "D")) {
//            headCell.state = CellState.TAIL
//            tailCell.state = CellState.EMPTY
//            headCell.tailVisitCount++
//            newHeadCell.state = CellState.HEAD
//            return Pair(newHeadPosition, headPosition)
//        } else if (headPosition.y == tailPosition.y && (move.code == "U" || move.code == "D")) {
//            headCell.state = CellState.EMPTY
//            newHeadCell.state = CellState.HEAD
//            return Pair(newHeadPosition, tailPosition) // diff
//        } else if (headPosition.x == tailPosition.x && (move.code == "L" || move.code == "R")) {
//            headCell.state = CellState.EMPTY
//            newHeadCell.state = CellState.HEAD
//            return Pair(newHeadPosition, tailPosition) // diff
//        } else if (headPosition.x != tailPosition.x && headPosition.y != tailPosition.y){ // switch in diagonal position
//            headCell.state = CellState.TAIL
//            tailCell.state = CellState.EMPTY
//            newHeadCell.state = CellState.HEAD
//            return Pair(newHeadPosition, headPosition)
//        } else throw IllegalStateException("not handled case")
    }

    data class RopeCell(val x: Int, val y: Int, var tailVisitCount: Int = 0, var state: CellState) {
        override fun toString(): String = state.code
        fun getPosition(): Point = Point(x, y)
    }

    data class Move(val code: String, val dx: Int, val dy: Int, val length: Int)

    enum class CellState(val code: String) {
        HEAD("H"), TAIL("T"), EMPTY("."), HEAD_AND_TAIL("A")
    }

    data class Point(val x: Int, val y: Int) {
        fun notTouching(other: Point): Boolean = abs(x - other.x) > 1 || abs(y - other.y) > 1
        fun touching(other: Point): Boolean = notTouching(other).not()
        fun diagonal(other: Point): Boolean = x != other.x && y != other.y
    }

    override fun solvePartTwo(lines: List<String>) {
        TODO("Not yet implemented")
    }
}

private fun List<List<Day09.RopeCell>>.print() {
//    val transposed = this.transpose()
//    for (row in transposed.reversed()) {
//        for (col in row) {
//            if (col.state != Day09.CellState.EMPTY) print(col.state.code)
//            else print(if (col.tailVisitCount > 0) "#" else ".")
//        }
//        println()
//    }
//    println()
}
