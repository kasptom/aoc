package year2023

import aoc.IAocTaskKt
import year2023.Day21.Direction.*

class Day21 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_21.txt"

    override fun solvePartOne(lines: List<String>) {
        val grid = Grid.parse(lines)
        println(grid.print())
        println(grid.visit(steps = 64).size)
        println(grid.visited.size)
    }

    override fun solvePartTwo(lines: List<String>) {
        print("Not yet implemented")
    }

    data class Grid(val grid: List<List<String>>, val start: Point) {
        val visited = mutableSetOf<Point>()

        fun print(): String = grid.joinToString("\n") { it.joinToString("") }
        fun print(points: Set<Point>): String = grid.indices.joinToString("\n") {yIdx -> grid[yIdx]
            .indices
            .map { xIdx -> if (Point(xIdx, yIdx) in points) "O" else grid.valueAt(Point(xIdx, yIdx))}
            .joinToString("")
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
//            visited.clear()
//            visited.add(pos)

            val possibleMoves = Direction.values()
                .map { pos + it }
                .filter { it.isInRange(grid) && it.isAvailable(grid) }
                .toSet()

            return visit(possibleMoves, steps)
        }

        private fun visit(possibleMoves: Set<Point>, steps: Int): Set<Point> {
//            println("steps left: $steps")
//            println(print(possibleMoves))
//            visited.addAll(possibleMoves)
            if (steps == 1) {
                return possibleMoves
            }

            val nextMoves = possibleMoves.flatMap { move ->
                Direction.values()
                    .map { move + it }
                    .filter { it.isInRange(grid) && it.isAvailable(grid) }
//                    .filter { it !in visited }
            }.toSet()


            return visit(nextMoves, steps - 1)
        }
    }

    data class Point(val x: Int, val y: Int) {
        operator fun plus(dir: Direction): Point {
            return when (dir) {
                UP -> this + Point(0, -1)
                DOWN -> this + Point(0, 1)
                LEFT -> this + Point(-1, 0)
                RIGHT -> this + Point(1, 0)
            }
        }

        operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)
        fun isInRange(grid: List<List<String>>): Boolean = x >= 0 && y >= 0 && x < grid[0].size && y < grid.size
        fun isAvailable(grid: List<List<String>>): Boolean = grid.valueAt(this) == "." || grid.valueAt(this) == "S"
    }

    enum class Direction {
        UP, DOWN, LEFT, RIGHT;
    }

}

fun List<List<String>>.valueAt(pos: Day21.Point): String = this[pos.y][pos.x]
