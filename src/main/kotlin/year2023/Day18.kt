package year2023

import aoc.IAocTaskKt
import year2023.Day18.Direction.*

class Day18 : IAocTaskKt {
    override fun getFileName() = "aoc2023/input_18.txt"

    override fun solvePartOne(lines: List<String>) {
        val moves = lines.map(Move::parse)
        val width = 2 * (moves.filter { it.dir == RIGHT }.sumOf { it.steps } + moves.filter { it.dir == LEFT }.sumOf { it.steps })
        val height = 2 * (moves.filter { it.dir == DOWN }.sumOf { it.steps } + moves.filter { it.dir == UP }.sumOf { it.steps })
        val grid = Grid((0..height).map { (0..width).map { "." }.toMutableList() }.toList(), width, height)
        for (move in moves) {
            grid.move(move)
        }
        println(grid.print())
        println(grid.countSpace())
    }

    override fun solvePartTwo(lines: List<String>) {
        println("Not yet implemented")
    }

    data class Point(val x: Int, val y: Int) {
        fun isInRange(map: List<List<String>>): Boolean = x >= 0 && y >= 0 && x < map[0].size && y < map.size
        operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)
        operator fun plus(dir: Direction): Point {
            return when (dir) {
                UP -> this + Point(0, -1)
                DOWN -> this + Point(0, 1)
                LEFT -> this + Point(-1, 0)
                RIGHT -> this + Point(1, 0)
            }
        }

        override fun toString(): String = "($x, $y)"
        operator fun plus(move: Move): Point {
            return this + ((Point(0, 0) + move.dir) * move.steps)
        }

        operator fun times(k: Int): Point {
            return Point(x * k, y * k)
        }
    }

    data class Grid(val map: List<MutableList<String>>, val width: Int, val height: Int) {
        fun move(move: Move) {
            prevPoint = currPoint
            currPoint = prevPoint
            for (step in 1..move.steps) {
                map[currPoint.y][currPoint.x] = "#"
                currPoint += move.dir
            }
        }

        val start = Point(width / 2, height / 2)
        var currPoint = start
        var prevPoint = start

        fun print() = map
            .mapIndexed { yIdx, row ->
                row
                    .mapIndexed { xIdx, col -> col }
            }
            .joinToString("\n") { it.joinToString("") }

        fun countSpace(): Int {
            val expStart = start + Point(1, 1)
            val neighs = MOVES.map { expStart + it }.filter { it.isInRange(map) }
            val toExpand = neighs.filter { map.valueAt(it) == "." }.distinct().toMutableList()
            while (toExpand.isNotEmpty()) {
                for (cell in toExpand) {
                    map[cell.y][cell.x] = "#"
                }
                val newToExpand = toExpand.flatMap { toExp -> MOVES.map { toExp + it }.filter { it.isInRange(map) }
                    .filter { map.valueAt(it) == "." }
                }
                toExpand.clear()
                toExpand.addAll(newToExpand.distinct())
            }
            return map.flatten().count { it == "#" }
        }

    }

    enum class Direction(val dir: String) {
        UP("U"), DOWN("D"), LEFT("L"), RIGHT("R");

        fun opposite(): Direction = when (this) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT
        }
    }

    data class Move(val dir: Direction, val steps: Int, val rest: String) {
        companion object {
            fun parse(line: String): Move {
                val (dirRaw, stepRaw, restRaw) = line.split(" ").filter(String::isNotEmpty)
                val dir = Direction.values().first { it.dir == dirRaw }
                return Move(dir, stepRaw.toInt(), restRaw)
            }
        }
    }

    companion object {
        val DX: List<Int> = listOf(1, 0, -1, 0)
        val DY: List<Int> = listOf(0, 1, 0, -1)
        val MOVES: List<Point> = DX.zip(DY).map { (x, y) -> Point(x, y) }
    }
}

fun List<List<String>>.valueAt(pos: Day18.Point): String = this[pos.y][pos.x]

