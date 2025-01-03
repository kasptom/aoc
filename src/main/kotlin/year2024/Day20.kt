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
        val allowedCheatCost = 2..2
        val cheats = solve(shortest, allowedCheatCost)
        println(cheats)
    }

    override fun solvePartTwo(lines: List<String>) {
        val (points, start, end) = setup(lines)
        val shortest = shortestPath(points, start, end)
        val allowedCheatCost = 2..20
        val cheats = solve(shortest, allowedCheatCost)
        println(cheats)
    }

    private fun solve(shortest: PointWithCostAndPath, allowedCheatCost: IntRange): Int {
        val path = shortest.path

        val cheatPoints = mutableSetOf<Pair<Point, Point>>()
        val pointToIndex = path.associateWith { path.indexOf(it) }
        val cheatSizeToCount = mutableMapOf<Int, Int>()

        for (i in path.indices) {
            for (j in (i + 1) until path.size) {
                val point = path[i]
                val other = path[j]
                if (point.cheatCost(other) in allowedCheatCost) {
                    val diff = pointToIndex[other]!! - pointToIndex[point]!! - point.cheatCost(other)
                    if (diff >= THRESHOLD) {
                        cheatPoints.add(Pair(point, other))
                        cheatSizeToCount.putIfAbsent(diff, 0)
                        cheatSizeToCount.computeIfPresent(diff) { _, c -> c + 1 }
                    }
                }
            }
        }
        return cheatPoints.size
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

        fun cheatCost(other: Point): Int {
            return abs(x - other.x) + abs(y - other.y)
        }

        private operator fun minus(other: Point): Point {
            return Point(x - other.x, y - other.y)
        }
    }

    data class PointWithCostAndPath(val point: Point, val cost: Cost, val path: List<Point>) :
        Comparable<PointWithCostAndPath> {
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

    companion object {
        private const val THRESHOLD = 100
    }
}
