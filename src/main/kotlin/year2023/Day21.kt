package year2023

import aoc.IAocTaskKt
import year2023.Day21.Direction.DOWN
import year2023.Day21.Direction.LEFT
import year2023.Day21.Direction.RIGHT
import year2023.Day21.Direction.UP
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow

class Day21 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_21.txt"

    override fun solvePartOne(lines: List<String>) {
        val grid = Grid.parse(lines)
        println(grid.print())
        println(grid.visit(steps = 6).size)
    }

    override fun solvePartTwo(lines: List<String>) {
        val grid = Grid.parse(lines)
        println(grid.print())

        val points = grid.visit(steps = 64)
        println()
        println(grid.print(points))
        println(points.size)

//        println(grid.countStepsInDiamond(5000))
        // 5146892932706899 -- too high
        // 636385126285344
        // 636391431970648 - incorrect :/
        println(grid.countStepsInDiamond(26501365))
//        println(grid.countStepsInDiamond(5000))
//        println(grid.visit(500).size) <-- test case
    }

    data class Grid(val grid: List<List<String>>, val start: Point) {
        fun print(): String = grid.joinToString("\n") { it.joinToString("") }
        fun print(points: Set<Point>): String {
            val minX = points.minOf { it.x }
            val maxX = points.maxOf { it.x }
            val minY = points.minOf { it.y }
            val maxY = points.maxOf { it.y }

            val width = grid[0].size
            val height = grid.size

            val megaGridMinX = -width * ceil(abs(minX).toDouble() / width).toInt()
            val megaGridMaxX = width * ceil(maxX.toDouble() / width).toInt()

            val megaGridMinY = -height * ceil(abs(minY).toDouble() / height).toInt()
            val megaGridMaxY = height * ceil(maxY.toDouble() / height).toInt()

            var result = ""
            for (y in megaGridMinY..megaGridMaxY) {
                for (x in megaGridMinX..megaGridMaxX) {
                    val value = grid.valueAt(Point(x, y).moduloPoint(grid))
//                    if (Point(x, y).isInRange(grid)) {
//                        result += ANSI_GREEN
//                    }
                    result += if (Point(x, y) in points) "O" else value
//                    if (Point(x, y).isInRange(grid)) {
//                        result += ANSI_RESET
//                    }
                }
                result += "\n"
            }
            return result
        }

        fun countStepsInDiamond(steps: Long): Long {
            val size = grid[0].size
//            require(steps % size == floor(size.toDouble() / 2).toLong()) { "no "}

            val gridRepeats = floor(steps.toDouble() / size).toLong() - 1L

            println(gridRepeats)

            val oddRepeats = (floor(gridRepeats.toDouble() / 2) * 2 + 1).pow(2).toLong()
            val evenRepeats = (floor((gridRepeats + 1.0) / 2) * 2).pow(2).toLong()

            println("$oddRepeats $evenRepeats")

            val oddCount = visit(size * 2 + 1).filter { it.isInRange(grid) }.size
            val evenCount = visit(size * 2).filter { it.isInRange(grid) }.size

            println("$oddCount $evenCount")

            val topCorner = copy(start = Point(start.x, size - 1)).visit(size - 1).filter { it.isInRange(grid) }.size.toLong()
            val rightCorner = copy(start = Point(0, start.y)).visit(size - 1).filter { it.isInRange(grid) }.size.toLong()
            val bottomCorner = copy(start = Point(start.x, 0)).visit(size - 1).filter { it.isInRange(grid) }.size.toLong()
            val leftCorner = copy(start = Point(size - 1, start.y)).visit(size - 1).filter { it.isInRange(grid) }.size.toLong()
            println("$topCorner $rightCorner $bottomCorner $leftCorner")

            val smallSteps = floor(size.toDouble() / 2).toInt() - 1
            val topRight = copy(start = Point(x = 0, y = size - 1))
                .visit(smallSteps).filter { it.isInRange(grid) }.size.toLong()
            val bottomRight = copy(start = Point(x = size - 1, y = size - 1))
                .visit(smallSteps).filter { it.isInRange(grid) }.size.toLong()
            val topLeft = copy(start = Point(x = 0, y = 0))
                .visit(smallSteps).filter { it.isInRange(grid) }.size.toLong()
            val bottomLeft = copy(start = Point(x = size - 1, y = 0))
                .visit(smallSteps).filter { it.isInRange(grid) }.size.toLong()
            println("$topRight $bottomRight $topLeft $bottomLeft")

            val largeSteps = (floor(size.toDouble() * 3.0) / 2).toInt() - 1
            val largeTopRight = copy(start = Point(x = 0, y = size - 1))
                .visit(largeSteps).filter { it.isInRange(grid) }.size.toLong()
            val largeBottomRight = copy(start = Point(x = size - 1, y = size - 1))
                .visit(largeSteps).filter { it.isInRange(grid) }.size.toLong()
            val largeTopLeft = copy(start = Point(x = 0, y = 0))
                .visit(largeSteps).filter { it.isInRange(grid) }.size.toLong()
            val largeBottomLeft = copy(start = Point(x = size - 1, y = 0))
                .visit(largeSteps).filter { it.isInRange(grid) }.size.toLong()

            println("$largeTopRight $largeBottomRight $largeTopLeft $largeBottomLeft")

            return listOf(
                oddRepeats * oddCount,
                evenRepeats * evenCount,
                topCorner, rightCorner, bottomCorner, leftCorner,
                (gridRepeats + 1) * (topRight + topLeft + bottomRight + bottomLeft),
                gridRepeats * (largeTopRight + largeTopLeft + largeBottomRight + largeBottomLeft),
            ).sum()
        }

        companion object {
            fun parse(lines: List<String>): Grid {
                val grid = lines.map { it.split("").filter(String::isNotEmpty) }
                val start = findStart(grid)
                return Grid(grid, start)
            }

            fun findStart(grid: List<List<String>>): Point {
                for (yIdx in grid.indices) {
                    for (xIdx in grid[0].indices) {
                        val point = Point(xIdx, yIdx)
                        if (grid.valueAt(point) == "S") {
                            return point
                        }
                    }
                }
                return Point(-1, -1)
            }
        }

        fun visit(steps: Int): Set<Point> {
            val pos = start

            val possibleMoves = Direction.values()
                .map { pos + it }
                .filter { it.isAvailable(grid) }
                .toSet()

            return visit(possibleMoves, 1, steps)
        }

        private fun visit(possibleMoves: Set<Point>, step: Int, maxSteps: Int): Set<Point> {
//            println("step: $step")
//            println(print(possibleMoves))
//            visited.addAll(possibleMoves)
            if (step == maxSteps) {
                return possibleMoves
            }

            val nextMoves = possibleMoves.flatMap { move ->
                Direction.values()
                    .map { move + it }
                    .filter { it.isAvailable(grid) }
//                    .filter { it !in visited }
            }.toSet()


            return visit(nextMoves, step + 1, maxSteps)
        }
    }

    data class Point(val x: Int, val y: Int) {
        fun moduloPoint(grid: List<List<String>>): Point {
            val width = grid[0].size
            val height = grid.size
            return Point(
                if (abs(x) % width == 0) 0 else if (x < 0) width - (abs(x) % width) else x % width,
                if (abs(y) % height == 0) 0 else if (y < 0) height - (abs(y) % height) else y % height
            )
        }

        operator fun plus(dir: Direction): Point {
            return when (dir) {
                UP -> this + Point(0, -1)
                DOWN -> this + Point(0, 1)
                LEFT -> this + Point(-1, 0)
                RIGHT -> this + Point(1, 0)
            }
        }

        operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)

        //        fun isInRange(grid: List<List<String>>): Boolean = x >= 0 && y >= 0 && x < grid[0].size && y < grid.size
        fun isAvailable(grid: List<List<String>>): Boolean {
            val moduloPoint = moduloPoint(grid)
            //            println("point $this, modulo: $moduloPoint (w x h = $width x $height)")
            return grid.valueAt(moduloPoint) == "." || grid.valueAt(moduloPoint) == "S"
        }

        fun isInRange(grid: List<List<String>>): Boolean = x >= 0 && y >= 0 && x < grid[0].size && y < grid.size

        override fun toString(): String {
            return "($x, $y)"
        }
    }

    enum class Direction {
        UP, DOWN, LEFT, RIGHT;
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println(-11 % 11)
        }

        const val ANSI_RESET = "\u001B[0m"
        const val ANSI_GREEN = "\u001B[32m"
        const val ANSI_RED = "\u001B[31m"
        const val ANSI_PURPLE = "\u001B[35m"
    }
}

fun List<List<String>>.valueAt(pos: Day21.Point): String = this[pos.y][pos.x]
