package year2024

import aoc.IAocTaskKt
import utils.except
import java.util.*
import kotlin.math.sqrt

class Day18 : IAocTaskKt {
//    override fun getFileName(): String = "aoc2024/input_18.txt"
     override fun getFileName(): String = "aoc2024/input_18.txt"

    override fun solvePartOne(lines: List<String>) {
        val (width, height, bytes) = if (getFileName() == "aoc2024/input_18.txt") {
            Triple(71, 71, 1024)
        } else {
            Triple(7, 7, 12)
        }
        val maxSize = if (getFileName() == "aoc2024/input_18.txt") {
            70
        } else {
            7
        }

        val grid: Array<CharArray> = (1..height)
            .map { (1..width).map { '.' }.toCharArray() }
            .toTypedArray()

        val allObstacles = lines.map { line ->
            val (x, y) = line.split(",").map { it.toInt() }
            Point(x, y)
        }.toList()

        val obstacles = allObstacles.subList(0, bytes).toMutableSet()

        println(grid.print(obstacles, emptyList()))


        val start = Point(0, 0)
        val target = Point(width - 1, height - 1)

        val toAdd = allObstacles.except(obstacles)

        for (obstacle in toAdd) {
            obstacles += obstacle
            val visited = mutableSetOf(start)
            val path = shortestPath(visited, start, target, obstacles, width, height, maxSize)
            if (path == -1) {
                println("${obstacle.x},${obstacle.y}")
                return
            }
        }
//        println(grid.print(obstacles, path))
    }

    private fun shortestPath(
        visited: MutableSet<Point>,
        start: Point,
        target: Point,
        obstacles: Set<Point>,
        width: Int,
        height: Int,
        maxSize: Int
    ): Int {
        val queue: TreeSet<State> = TreeSet()
        queue.add(State(start, 1))
        while (queue.isNotEmpty()) {
            val (current, path) = queue.pollFirst()!!
            visited.add(current)
            if (current == target) {
                return path
            }

            val neighs = current.neighs()
            val inRangeNeighs = neighs.filter { n -> n.inRange(width, height) && n !in obstacles && n !in visited }
            val states = inRangeNeighs.map { n -> State(n, path + 1) }
            queue.addAll(states)
            while (queue.size > maxSize) {
                queue.pollLast()
            }
        }
        return -1
    }

    override fun solvePartTwo(lines: List<String>) {
        if (lines.isEmpty()) println("empty lines") else println(lines.size)
    }

    data class Point(val x: Int, val y: Int) : Comparable<Point> {
        override fun compareTo(other: Point): Int {
            if (dist() != other.dist()) {
                return dist().compareTo(other.dist())
            }
            if (x != other.x) {
                return x.compareTo(other.x)
            }
            return y.compareTo(other.y)
        }

        private fun dist(): Double = sqrt(0.0 + x * x + y * y)
        fun neighs(): List<Point> {
            val candidates = listOf(this + up, this + down, this + left, this + right)
            return candidates
        }

        private operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)
        fun inRange(width: Int, height: Int): Boolean {
            return x in 0 until width && y in 0 until height
        }

        companion object {
            val up = Point(0, -1)
            val down = Point(0, 1)
            val left = Point(-1, 0)
            val right = Point(1, 0)
        }
    }

    data class State(val point: Point, val pathSize: Int) : Comparable<State> {
        override fun compareTo(other: State): Int {
            if (pathSize != other.pathSize) {
                return pathSize.compareTo(other.pathSize)
            }
            return other.point.compareTo(point)
        }
    }
}

private fun Array<CharArray>.print(obstacles: Set<Day18.Point>, path: List<Day18.Point>): String {
    var result = ""
    for (y in this.indices) {
        for (x in this[0].indices) {
            var point = Day18.Point(x, y)
            if (point in obstacles) {
                result += '#'
            } else if (point in path) {
                result += 'O'
            } else {
                result += '.'
            }
        }
        result += "\n"
    }
    return result
}
