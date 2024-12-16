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
    //    override fun getFileName(): String = "aoc2024/input_16.txt"
    override fun getFileName(): String = "aoc2024/input_16.txt"

    override fun solvePartOne(lines: List<String>) {
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
        val shortest: Int = shortestPath(points, reindeer, target).toValue()
        println("shortest")
        println(shortest)
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
                println(cost)
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

    override fun solvePartTwo(lines: List<String>) {
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
        val shortest: Cost = shortestPath(points, reindeer, target)

        val count: Int = countNodesOnBestPaths(points, reindeer, target, shortest)

        println(count)
    }

    private fun countNodesOnBestPaths(
        points: Set<Point>,
        reindeer: Reindeer,
        target: Point,
        shortest: Cost
    ): Int {
        val path = mutableSetOf<Point>(reindeer.pos)
        val stateWithCost = StateWithCost(reindeer.getState(), Cost(0, 0))
        val (state, cost) = stateWithCost
        val visited = mutableSetOf(stateWithCost)
        val neighs = reindeer.pos.getNeighs2(path, points)
        val bestPathsPoints = mutableSetOf<Point>()
        for (neigh in neighs) {
            val neighCost = neigh.toCost(cost, state)
            visited.add(StateWithCost(neigh, neighCost))
            dfsNeigh(neigh, path + neigh.point, points, neighCost, shortest, target, bestPathsPoints)
        }
        return bestPathsPoints.size
    }

    private fun dfsNeigh(
        node: State,
        path: Set<Point>,
        points: Set<Point>,
        currentCost: Cost,
        shortest: Cost,
        target: Point,
        bestPathsPoints: MutableSet<Point>
    ) {
        if (currentCost.toValue() > shortest.toValue()) {
            return
        }
        if (node.point == target) {
            bestPathsPoints.addAll(path)
            return
        }
        val neighs = node.point.getNeighs2(path, points)
        for (neigh in neighs) {
            val neighCost = neigh.toCost(currentCost, node)
            dfsNeigh(neigh, path + neigh.point, points, neighCost, shortest, target, bestPathsPoints)
        }
    }

    data class StateWithCost(val state: State, val cost: Cost) : Comparable<StateWithCost> {
        override fun compareTo(other: StateWithCost): Int {
            if (cost.toValue() != other.cost.toValue()) {
                return cost.toValue().compareTo(other.cost.toValue())
            }
            return state.compareTo(other.state)
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

        fun getNeighs2(visited: Set<Point>, nodes: Set<Point>): List<State> {
            val states = listOf(
                State(this + UP, N),
                State(this + DOWN, S),
                State(this + LEFT, W),
                State(this + RIGHT, E)
            )
            return states.filter { it.point !in visited && it.point in nodes }
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
                return Integer.compare(x, other.x)
            }
            return Integer.compare(y, other.y)
        }

        fun dist(): Double = sqrt(0.0 + x * x + y * y)
        fun distanceTo(target: Point): Double = (this - target).dist()

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
            val DIR_TO_NEXT_CLOCKWISE = mapOf(N to E, E to S, S to W, W to N)
            val DIR_TO_NEXT_COUNTER = mapOf(N to W, W to S, S to E, E to N)
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
