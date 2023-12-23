package year2023

import aoc.IAocTaskKt

class Day23 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_23.txt"

    override fun solvePartOne(lines: List<String>) {
        val grid = Grid.parse(lines)
        println(grid.print())
        println()
        grid.setupSections()

        grid.findLongestPath()
        println(grid.bestPath)
        println(grid.maxSize)
    }

    override fun solvePartTwo(lines: List<String>) {
        val grid = Grid.parse(lines)
        println(grid.print())
        println()

        grid.slopesEnabled = false
        grid.setupSections()

        grid.findLongestPath()
        println(grid.bestPath)
        println(grid.maxSize)
    }

    data class Grid(val grid: List<List<String>>, val start: Point, val end: Point, var slopesEnabled: Boolean = true) {
        val pointToJump: MutableMap<Point, Jump> = mutableMapOf()
        var bestPath: List<Jump> = emptyList()

        var crossroadsPoints: List<Point> = emptyList()
        var maxSize = 0
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
        }

        fun findLongestPath(): Int {
            val startJump = pointToJump[start]!!
            visit(listOf(startJump), startJump.length)
            return maxSize
        }

        private fun visit(jumps: List<Jump>, size: Int) {
//            println("current path size: ${path.size}")
//            println(print(possibleMoves))
//            visited.addAll(possibleMoves)
            val lastJump = jumps.last()

            if (lastJump.end == end) {
//                println("reached end in ${jumps.size} jumps (${size})")
                if (maxSize < size - 1) {
                    maxSize = size - 1
                    bestPath = jumps
                    if (bestPath.distinct().size != bestPath.size) throw IllegalStateException("wrong path size")
//                    println("current longest: ${maxSize}")
                }
                return
            }

            val nextJumps = getNextJumps(lastJump, jumps)
            for (jump in nextJumps) {
                visit(jumps + jump, size + jump.length)
            }
        }

        private fun getNextJumps(jump: Jump, jumps: List<Jump>): List<Jump> {
            val neighs = getNeighbours(jump.end, emptyList())
            return neighs.filter {
                pointToJump.containsKey(it)
            }.map {
                pointToJump[it]!!
            }.filter {
                it !in jumps
            }
        }

        fun getNeighbours(
            step: Point,
            path: List<Point>,
        ) = Direction.values()
            .map { step + it }
            .filter {
                it.isInRange(grid) &&
                        it.isAvailable(grid) &&
                        (!slopesEnabled || step.hasRequiredDirection(grid, it))
            }
            .filter { it !in path }

        fun setupSections() {
            if (slopesEnabled) {
                getAllPoints()
                    .forEach {
                        pointToJump[it] = Jump(it, it, 1)
                    }
                return
            }

            crossroadsPoints = getAllPoints()
                .filter { getNeighbours(it, emptyList()).size > 2 }
                .sorted()
                .toList()
            println("crossroad points: $crossroadsPoints")

            for (point in crossroadsPoints) {
                val jumpStarts = getNeighbours(point, emptyList())
                pointToJump[point] = Jump(point, point, 1)
                for (jumpStart in jumpStarts) {
                    if (jumpStart in crossroadsPoints) throw IllegalStateException("jump start in crossroad points")
                    var visited = mutableListOf(jumpStart, point)
                    var jumpEnd = getNeighbours(jumpStart, visited).first()
                    while (true) {
                        visited += jumpEnd
                        if (getNeighbours(jumpEnd, visited).size == 1 && getNeighbours(
                                jumpEnd,
                                visited
                            ).first() !in crossroadsPoints
                        ) {
                            jumpEnd = getNeighbours(jumpEnd, visited).first()
                        } else {
                            break
                        }
                    }
//                    visited = visited.distinct().toMutableList()
                    val sec1 = Jump(start = jumpStart, end = jumpEnd, visited.size - 1)
                    pointToJump[jumpStart] = sec1
                    val sec2 = Jump(start = jumpEnd, end = jumpStart, visited.size - 1)
                    pointToJump[jumpEnd] = sec2
//                    println("creating sections from point $jumpStart: $sec1, $sec2")
                }
            }
            pointToJump
                .entries
                .sortedBy { (k, _) -> k }
                .onEach {
                    println(it)
                }
        }

        private fun getAllPoints() = grid.flatMapIndexed { yIdx, _ ->
            grid[yIdx].mapIndexed { xIdx, _ -> Point(xIdx, yIdx) }
        }.filter { grid.valueAt(it) != "#" }
    }

    data class Jump(val start: Point, val end: Point, val length: Int) {
        override fun toString(): String {
            return "$start -> $end ($length)"
        }
    }

    data class Point(val x: Int, val y: Int) : Comparable<Point> {
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
        override fun compareTo(other: Point): Int {
            if (x - other.x != 0) return x - other.x
            return y - other.y
        }

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
