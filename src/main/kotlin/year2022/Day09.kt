package year2022

import aoc.IAocTaskKt
import utils.transpose
import year2022.Day09.CellState.*
import kotlin.math.abs

private const val GRID_WIDTH = 1000
private const val GRID_HEIGHT = 400
private val START_POSITION = Day09.Point(GRID_WIDTH / 2, GRID_HEIGHT / 2)
private val dxs = arrayOf(0, -1, 1, 0)
private val dys = arrayOf(-1, 0, 0, 1)
private val moveToIdx = mapOf("D" to 0, "L" to 1, "U" to 3, "R" to 2)

typealias RopeGrid = MutableList<MutableList<Day09.RopeCell>>

class Day09 : IAocTaskKt {

    override fun getFileName(): String = "aoc2022/input_09.txt"

    override fun solvePartOne(lines: List<String>) {
        val grid: RopeGrid = createRopeCellGrid()
        val moves = loadMoves(lines)

        val startCell = grid[START_POSITION.y][START_POSITION.x]
        val updatedStartCell = startCell.copy(visited = true, state = HEAD_AND_TAIL)
        grid[startCell.position.y][startCell.position.x] = updatedStartCell
        var headPosition = updatedStartCell.position
        var tailPosition = updatedStartCell.position
        for (move in moves) {
            repeat(move.length) {
                val (newHeadPosition, newTailPosition) = move.execute(grid, headPosition, tailPosition)
                headPosition = newHeadPosition
                tailPosition = newTailPosition
            }
        }
//        grid.print()
        grid.flatten().count(RopeCell::visited).let(::println)
    }

    data class RopeCell(val position: Point, var visited: Boolean = false, var state: CellState = EMPTY) {
        override fun toString(): String = state.code
    }

    data class Node(var position: Point, var type: CellState, var id: Int) {
        private var prevPosition = Point(-1, -1)
        override fun toString(): String = "(${position.x}, ${position.y}, $type, $id)"

        fun move(move: Move) {
            prevPosition = position
            position += move
        }

        fun getPrevPosition(): Point = prevPosition
        fun moveTowards(headPosition: Point) {
            val xDivisor = abs(position.x - headPosition.x)
            val yDivisor = abs(position.y - headPosition.y)
            val dx = if (xDivisor == 0) 0 else (headPosition.x - position.x) / xDivisor
            val dy = if (yDivisor == 0) 0 else (headPosition.y - position.y) / yDivisor
            position += Point(dx, dy)
        }
    }

    data class Move(val code: String, val dx: Int, val dy: Int, val length: Int) {
        override fun toString(): String = "== $code $length =="

        fun execute(grid: RopeGrid, headPosition: Point, tailPosition: Point): Pair<Point, Point> {
            val newHeadPosition = headPosition + Point(dx, dy)
            val headCell = grid[headPosition.y][headPosition.x]
            val tailCell = grid[tailPosition.y][tailPosition.x]
            val newHeadCell = grid[newHeadPosition.y][newHeadPosition.x]

            return if (tailPosition.notTouching(newHeadPosition)) {
                headCell.state = TAIL
                headCell.visited = true
                tailCell.state = EMPTY
                newHeadCell.state = HEAD
                Pair(newHeadPosition, headPosition)
            } else if (tailPosition.touching(newHeadPosition)) {
                newHeadCell.state = HEAD
                headCell.state = EMPTY
                Pair(newHeadPosition, tailPosition)
            } else throw IllegalStateException("unknown state")
        }

        fun execute2(grid: MutableList<MutableList<RopeCell>>, move: Move, nodes: List<Node>) {
            repeat(move.length) {
                val tailNodes = nodes.subList(1, nodes.size)
                nodes[0].move(move)

                for (tailNode in tailNodes) {
                    val head = nodes[tailNode.id - 1]
                    val headPosition = head.position
                    val tailPosition = tailNode.position
                    if (tailPosition.notTouching(headPosition)) {
                        tailNode.moveTowards(headPosition)
                    } else if (tailPosition.touching(headPosition)) {
                        tailNode.position = tailPosition
                    } else throw IllegalStateException("unknown state")
                }

                val lastNode = nodes.last()
                grid[lastNode.position.y][lastNode.position.x].visited = true
            }
        }
    }

    override fun solvePartTwo(lines: List<String>) {
        val grid: MutableList<MutableList<RopeCell>> = createRopeCellGrid()
        val moves = loadMoves(lines)
        val nodes = (0..9).map { Node(START_POSITION, TAIL, 0) }
            .toMutableList()

        for (idx in nodes.indices) {
            val node = nodes[idx]
            node.id = idx
            node.type = if (idx == 0) HEAD else TAIL
        }

        for (move in moves) {
            move.execute2(grid, move, nodes)
        }

//        grid.print2(nodes).let(::println)
        grid.flatten().count { it.visited }.let { println(it) }
    }

    private fun loadMoves(lines: List<String>) =
        lines.map { it.split(" ").zipWithNext().single() }
            .map { (code, offset) ->
                val dx = dxs[moveToIdx[code]!!]
                val dy = dys[moveToIdx[code]!!]
                Move(code, dx, dy, offset.toInt())
            }

    private fun createRopeCellGrid(): RopeGrid {
        val grid: RopeGrid = mutableListOf()

        for (y in 0 until GRID_HEIGHT) {
            grid.add(mutableListOf())
            for (x in 0 until GRID_WIDTH) {
                grid[y].add(RopeCell(position = Point(x, y)))
            }
        }
        val startCell = grid[START_POSITION.y][START_POSITION.x]
        val updatedStartCell = startCell.copy(visited = true, state = HEAD_AND_TAIL)
        grid[startCell.position.y][startCell.position.x] = updatedStartCell
        return grid
    }

    enum class CellState(val code: String) {
        HEAD("H"), TAIL("T"), EMPTY("."), HEAD_AND_TAIL("A"),
    }

    data class Point(val x: Int, val y: Int) {
        fun notTouching(other: Point): Boolean = abs(x - other.x) > 1 || abs(y - other.y) > 1
        fun touching(other: Point): Boolean = notTouching(other).not()
        operator fun plus(point: Point): Point = Point(x + point.x, y + point.y)
        operator fun plus(move: Move): Point = Point(x + move.dx, y + move.dy)
    }
}

@Suppress("unused")
private fun List<List<Day09.RopeCell>>.print2(
    nodes: List<Day09.Node>,
    onlyVisited: Boolean = false,
): String {
    var result = ""
    for (row in reversed()) {
        for (cell in row) {
            val nodesOnCell = nodes.filter { it.position == cell.position }
            val nodeTrailOnCell = nodes.filter { it.getPrevPosition() == cell.position }
            if (nodesOnCell.isEmpty() && nodeTrailOnCell.isEmpty()) {
                result += if (cell.position == START_POSITION) "s" else if (cell.visited) "#" else "."
            } else if (nodesOnCell.isNotEmpty() && !onlyVisited) {
                val node = nodesOnCell.first()
                result += if (node.type != HEAD) node.id else node.type.code
            }
        }
        result += "\n"
    }
    return result
}

@Suppress("unused")
private fun RopeGrid.print() {
    val transposed = transpose()
    for (row in transposed.reversed()) {
        for (col in row) {
            if (col.state != EMPTY) print(col.state.code)
            else print(if (col.visited) "#" else ".")
        }
        println()
    }
    println()
}
