package year2024

import aoc.IAocTaskKt
import year2024.Day16.Reindeer.Companion.DIR_TO_ROTATIONS
import year2024.Day16.Reindeer.Direction
import year2024.Day16.Reindeer.Direction.E
import year2024.Day16.Reindeer.Direction.N
import year2024.Day16.Reindeer.Direction.S
import year2024.Day16.Reindeer.Direction.W
import java.util.*
import kotlin.math.sqrt

class Day16 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_16.txt"

    override fun solvePartOne(lines: List<String>) {
        val (points, target, reindeer) = setup(lines)
        val shortest: Int = shortestPath(points, reindeer, target).toValue()
        println(shortest)
    }

    private fun setup(lines: List<String>): Triple<MutableSet<Point>, Point, Reindeer> {
        val grid = lines.map { it.toCharArray() }.toTypedArray()
        val points = mutableSetOf<Point>()
        var reindeerPoint = Point(-1, -1)
        var target = Point(-1, -1)
        for (y in grid.indices) {
            for (x in grid[0].indices) {
                val point = Point(x, y)
                if (grid.valueAt(point) in setOf('.', 'S', 'E')) {
                    points.add(point)
                }
                if (grid.valueAt(point) == 'S') {
                    reindeerPoint = point
                } else if (grid.valueAt(point) == 'E') {
                    target = point
                }
            }
        }
        val reindeer = Reindeer(reindeerPoint, E)
        return Triple(points, target, reindeer)
    }

    private fun shortestPath(nodes: Set<Point>, initialReindeer: Reindeer, target: Point): Cost {
        val visited = mutableSetOf<State>()
        val queue = PriorityQueue<StateWithCost>()

        val initialState: State = initialReindeer.getState()
        queue.add(StateWithCost(initialState, Cost(0, 0)))

        while (queue.isNotEmpty()) {
            val (state, cost) = queue.remove()
            visited.add(state)

            if (state.point == target) {
                return cost
            }

            val neighs: List<State> = state.point.getNeighs(visited, nodes)
            val neighCosts: List<Cost> = neighs.map { it.toCost(cost, state) }
            queue.addAll(neighs.zip(neighCosts).map { (n, c) ->
                StateWithCost(n, c)
            })
        }
        return Cost(-1, -1)
    }

    private fun shortestPathsCount(nodes: Set<Point>, initialReindeer: Reindeer, target: Point, shortest: Cost): Int {
        val visited = mutableSetOf<State>()
        val queue = PriorityQueue<StateWithCostAndPath>()
        val nodesOnPaths = mutableSetOf<Point>()

        val initialState: State = initialReindeer.getState()
        queue.add(StateWithCostAndPath(initialState, Cost(0, 0), listOf(initialState.point)))

        while (queue.isNotEmpty()) {
            val (state, cost, path) = queue.remove()
            visited.add(state)

            if (cost.toValue() > shortest.toValue()) {
                break
            }

            if (state.point == target) {
                nodesOnPaths += path
                continue
            }

            val neighs: List<State> = state.point.getNeighs(visited, nodes)
            val neighCosts: List<Cost> = neighs.map { it.toCost(cost, state) }
            queue.addAll(neighs.zip(neighCosts).map { (n, c) ->
                StateWithCostAndPath(n, c, path + n.point)
            })
        }
        return nodesOnPaths.size
    }

    override fun solvePartTwo(lines: List<String>) {
        val (points, target, reindeer) = setup(lines)
        val shortest: Cost = shortestPath(points, reindeer, target)
        val count: Int = shortestPathsCount(points, reindeer, target, shortest)
        println(count)
    }

    data class StateWithCost(val state: State, val cost: Cost) : Comparable<StateWithCost> {
        override fun compareTo(other: StateWithCost): Int {
            if (cost.toValue() != other.cost.toValue()) {
                return cost.toValue().compareTo(other.cost.toValue())
            }
            return state.compareTo(other.state)
        }
    }

    data class StateWithCostAndPath(val state: State, val cost: Cost, val path: List<Point>) :
        Comparable<StateWithCostAndPath> {
        override fun compareTo(other: StateWithCostAndPath): Int {
            if (cost.toValue() != other.cost.toValue()) {
                return cost.toValue().compareTo(other.cost.toValue())
            }
            if (state != other.state) {
                return state.compareTo(other.state)
            }
            return path.size.compareTo(other.path.size)
        }
    }

    data class Point(val x: Int, val y: Int) : Comparable<Point> {
        fun getNeighs(visited: Set<State>, nodes: Set<Point>): List<State> {
            val states = listOf(
                State(this + UP, N),
                State(this + DOWN, S),
                State(this + LEFT, W),
                State(this + RIGHT, E)
            )
            return states.filter { it !in visited && it.point in nodes }
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

        private operator fun minus(other: Point): Point {
            return Point(x - other.x, y - other.y)
        }
    }

    data class State(val point: Point, val direction: Direction) : Comparable<State> {
        fun toCost(cost: Cost, state: State): Cost {
            val rotations = DIR_TO_ROTATIONS.getOrDefault(Pair(state.direction, direction), 0)
            val steps = 1
            return cost + Cost(steps, rotations)
        }

        override fun compareTo(other: State): Int {
            if (point != other.point) {
                return point.compareTo(other.point)
            }
            return direction.compareTo(other.direction)
        }
    }

    data class Cost(val steps: Int, val rotations: Int) {
        fun toValue(): Int = steps + rotations * 1000
        operator fun plus(cost: Cost): Cost = Cost(steps + cost.steps, rotations + cost.rotations)
    }

    data class Reindeer(
        val pos: Point,
        val direction: Direction,
        val stepsCount: Int = 0,
        val rotationsCount: Int = 0,
    ) {
        fun getState(): State = State(pos, direction)

        enum class Direction {
            N, S, W, E
        }

        companion object {
            val DIR_TO_ROTATIONS = mapOf(
                Pair(N, E) to 1,
                Pair(N, W) to 1,
                Pair(N, S) to 2,
                Pair(E, N) to 1,
                Pair(E, S) to 1,
                Pair(E, W) to 2,
                Pair(S, E) to 1,
                Pair(S, W) to 1,
                Pair(S, N) to 2,
                Pair(W, S) to 1,
                Pair(W, N) to 1
            )
        }
    }

    private fun Array<CharArray>.valueAt(point: Point): Char {
        return this[point.y][point.x]
    }
}
