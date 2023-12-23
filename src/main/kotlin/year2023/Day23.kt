package year2023

import aoc.IAocTaskKt

class Day23 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_23.txt"

    override fun solvePartOne(lines: List<String>) {
        val grid = Grid.parse(lines)
        println(grid.print())
        println()
        var prevShortest = grid.shortestPath()
        var nextShortest = grid.shortestPath(prevShortest.size)
        while (prevShortest != nextShortest) {
            prevShortest = nextShortest
            nextShortest = grid.shortestPath(prevShortest.size)
        }
        println(grid.print(prevShortest.toSet()))
        println(nextShortest.size)
    }

    override fun solvePartTwo(lines: List<String>) {
        println("Not yet implemented")
    }

    data class Grid(val grid: List<List<String>>, val start: Point, val end: Point) {
        var currentShortestPath: List<Point> = emptyList()
        fun print(): String = grid.joinToString("\n") { it.joinToString("") }
        fun print(points: Set<Point>): String {
            var result = ""
            for (y in grid.indices) {
                for (x in grid[0].indices) {
                    val value = grid.valueAt(Point(x, y))
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

        companion object {
            fun parse(lines: List<String>): Grid {
                val grid = lines.map { it.split("").filter(String::isNotEmpty) }
                val start = Point(1, 0)
                val end = Point(grid[0].size - 2, grid.size - 1)
                return Grid(grid, start, end)
            }

//            fun findStart(grid: List<List<String>>): Point {
//                for (yIdx in grid.indices) {
//                    for (xIdx in grid[0].indices) {
//                        val point = Point(xIdx, yIdx)
//                        if (grid.valueAt(point) == "S") {
//                            return point
//                        }
//                    }
//                }
//                return Point(-1, -1)
//            }
        }

        fun shortestPath(minLength: Int = 0): List<Point> {
            val pos = start
            val path = mutableListOf(pos)

            val possibleMoves = Direction.values()
                .map { pos + it }
                .filter { it.isInRange(grid) && it.isAvailable(grid) && pos.hasRequiredDirection(grid, it) }
                .toSet()

            for (move in possibleMoves) {
                visit(path, move, minLength)
            }
            return currentShortestPath
        }

        private fun visit(path: List<Point>, step: Point, minLength: Int) {
//            println("step: $step")
//            println(print(possibleMoves))
//            visited.addAll(possibleMoves)
            if (step == end) {
                val currentMax = currentShortestPath.size
                println("reached end with path.size: ${path.size} current longest: $currentMax, min required: $minLength")
                if (path.size > currentShortestPath.size && path.size > minLength) {
                    currentShortestPath = path
                }
            }

            val nextSteps = Direction.values()
                    .map { step + it }
                    .filter { it.isInRange(grid) && it.isAvailable(grid) && step.hasRequiredDirection(grid, it) }
                    .filter { it !in path }

            for (next in nextSteps) {
                visit(path + next, next, minLength)
            }
        }
    }

    data class Point(val x: Int, val y: Int) {
        operator fun plus(dir: Direction): Point {
            return when (dir) {
                Direction.UP -> this + Point(0, -1)
                Direction.DOWN -> this + Point(0, 1)
                Direction.LEFT -> this + Point(-1, 0)
                Direction.RIGHT -> this + Point(1, 0)
            }
        }

        operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)

        //        fun isInRange(grid: List<List<String>>): Boolean = x >= 0 && y >= 0 && x < grid[0].size && y < grid.size
        fun isAvailable(grid: List<List<String>>): Boolean {
            return grid.valueAt(this) == "." || grid.valueAt(this) in SLOPES
        }

        fun isInRange(grid: List<List<String>>): Boolean = x >= 0 && y >= 0 && x < grid[0].size && y < grid.size

        override fun toString(): String {
            return "($x, $y)"
        }

        fun hasRequiredDirection(grid: List<List<String>>, point: Point): Boolean {
            val value = grid.valueAt(this)
            return when (value) {
                ">" -> point == this + Direction.RIGHT
                "<" -> point == this + Direction.LEFT
                "^" -> point == this + Direction.UP
                "v" -> point == this + Direction.DOWN
                else -> true
            }
        }
    }

    enum class Direction {
        UP, DOWN, LEFT, RIGHT;
    }

    companion object {
        val SLOPES = setOf(">", "<", "v", "^")
    }
}

fun List<List<String>>.valueAt(pos: Day23.Point): String = this[pos.y][pos.x]
