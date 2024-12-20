package year2024

import aoc.IAocTaskKt
import java.util.*
import kotlin.math.abs
import kotlin.math.sqrt

class Day20 : IAocTaskKt {
        override fun getFileName(): String = "aoc2024/input_20.txt"
//    override fun getFileName(): String = "aoc2024/input_20_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val (points, start, end) = setup(lines)
        val shortest = shortestPath(points, start, end)
        val path = shortest.path

        val threshold = 102
        var cheatPaths = 0
        val pointToIndex = path.map { Pair(it, path.indexOf(it))}
            .toMap()

        for (i in path.indices) {
            for (j in (i + 1) until path.size) {
                val point = path[i]
                val other = path[j]
                if (point.isInCheatRange(other)) {
                    val diff = pointToIndex[other]!! - pointToIndex[point]!!
                    if (diff >= threshold) {
                        cheatPaths++
                    }
                }
            }
        }
        println(cheatPaths)
    }

    override fun solvePartTwo(lines: List<String>) {
        val (points, start, end) = setup(lines)
        val shortest = shortestPath(points, start, end)
        val path = shortest.path

        val threshold = 100
        val cheatPoints = mutableSetOf<Pair<Point, Point>>()
        val pointToIndex = path.map { Pair(it, path.indexOf(it))}
            .toMap()

        println(path.size)
        for (i in path.indices) {
            for (j in (i + 1) until path.size) {
                val point = path[i]
                val other = path[j]
                if (point.isInCheatRange2(other)) {
                    val diff = pointToIndex[other]!! - pointToIndex[point]!!
                    if (diff >= threshold) {
                        cheatPoints.add(Pair(point, other))
                    }
                }
            }
        }
        println(cheatPoints.size)
    }

    private fun shortestPath(nodes: Set<Point>, start: Point, target: Point): PointWithCostAndPath {
        val visited = mutableSetOf<Point>()
        val queue = PriorityQueue<PointWithCostAndPath>()

        queue.add(PointWithCostAndPath(start, Cost(0), listOf(start)))

        while (queue.isNotEmpty()) {
            val pointCostPath = queue.remove()
            val (point, cost, path) = pointCostPath
            visited.add(point)

            if (point == target) {
                return pointCostPath
            }

            val neighs: List<Point> = point.getNeighs(visited, nodes)
            val neighCosts: List<Cost> = neighs.map { Cost(cost.steps + 1) }
            queue.addAll(neighs.zip(neighCosts).map { (n, c) ->
                PointWithCostAndPath(n, c, path + n)
            })
        }
        return PointWithCostAndPath(Point(-1, -1), Cost(-1), emptyList())
    }

    private fun setup(lines: List<String>): Triple<MutableSet<Point>, Point, Point> {
        val grid = lines.map { it.toCharArray() }.toTypedArray()
        val points = mutableSetOf<Point>()
        var start = Point(-1, -1)
        var end = Point(-1, -1)
        for (y in grid.indices) {
            for (x in grid[0].indices) {
                val point = Point(x, y)
                if (grid.valueAt(point) in setOf('.', 'S', 'E')) {
                    points.add(point)
                }
                if (grid.valueAt(point) == 'S') {
                    start = point
                } else if (grid.valueAt(point) == 'E') {
                    end = point
                }
            }
        }
        return Triple(points, start, end)
    }

    data class Point(val x: Int, val y: Int) : Comparable<Point> {
        fun getNeighs(visited: Set<Point>, nodes: Set<Point>): List<Point> {
            val states = listOf(
                this + UP,
                this + DOWN,
                this + LEFT,
                this + RIGHT,
            )
            return states.filter { it !in visited && it in nodes }
        }

        private operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)

        companion object {
            val UP = Point(0, -1)
            val DOWN = Point(0, 1)
            val LEFT = Point(-1, 0)
            val RIGHT = Point(1, 0)
        }

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

        fun isInCheatRange(other: Point): Boolean {
            return abs(x - other.x) == 2 && y == other.y || abs(y - other.y) == 2 && x == other.x
        }

        fun isInCheatRange2(other: Point): Boolean {
            return abs(x - other.x) + abs(y - other.y) in 2..20
        }

        private operator fun minus(other: Point): Point {
            return Point(x - other.x, y - other.y)
        }
    }

    data class PointWithCostAndPath(val point: Point, val cost: Cost, val path: List<Point>) : Comparable<PointWithCostAndPath> {
        override fun compareTo(other: PointWithCostAndPath): Int {
            if (cost.toValue() != other.cost.toValue()) {
                return cost.toValue().compareTo(other.cost.toValue())
            }
            return point.compareTo(other.point)
        }
    }

    data class Cost(val steps: Int) {
        fun toValue(): Int = steps
        operator fun plus(cost: Cost): Cost = Cost(steps + cost.steps)
    }

    private fun Array<CharArray>.valueAt(point: Point): Char {
        return this[point.y][point.x]
    }
}
