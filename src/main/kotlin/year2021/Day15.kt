package year2021

import aoc.IAocTaskKt
import java.util.PriorityQueue

class Day15 : IAocTaskKt {
    override fun getFileName() = "aoc2021/input_15.txt"

    val dxs = arrayOf(-1, 0, 1, 0)
    val dys = arrayOf(0, 1, 0, -1)

    override fun solvePartOne(lines: List<String>) {
        val grid = lines.map { it.chunked(1).map(String::toInt) }
//        display(grid)
        val start = Point(0, 0)
        val (cost, path) = dijkstra(grid, start)
//        displayPath(grid, path)
        println(cost)
    }

    private fun isInRange(x: Int, y: Int, grid: List<List<Int>>) =
        isInRange(y, grid.size) && isInRange(x, grid[0].size)

    private fun isInRange(pos: Int, size: Int): Boolean = pos in 0 until size

    override fun solvePartTwo(lines: List<String>) {
        val startTime = System.currentTimeMillis()
        val grid = lines.map { it.chunked(1).map(String::toInt) }
        val biggerGrid = mutableListOf<MutableList<Int>>()
        grid.forEach { biggerGrid.add(it.toMutableList()) }

        for (repeat in 1..4) {
            for (rowIdx in grid.indices) {
                val nextChunk = grid[rowIdx]
                    .map { it + repeat }
                    .map { if (it > 9) it - 9 else it }
                biggerGrid[rowIdx].addAll(nextChunk)
            }
        }
        for (repeat in 1..4) {
            for (rowIdx in grid.indices) {
                val toCopy = biggerGrid[rowIdx]
                    .map { it + repeat }
                    .map { if (it > 9) it - 9 else it }
                biggerGrid.add(toCopy.toMutableList())
            }
        }
        val start = Point(0, 0)
        val (cost, path) = dijkstra(biggerGrid, start)
//        displayPath(biggerGrid, path)
        println(cost)
        val duration = System.currentTimeMillis() - startTime
        println("Part 2 execution time: ${duration}ms")
    }

    private fun dijkstra(
        grid: List<List<Int>>,
        start: Point
    ): Pair<Int, List<Point>> {
        val queue = PriorityQueue<Pair<Point, Int>>(compareBy { it.second })
        val distances = mutableMapOf<Point, Int>()
        val visited = mutableSetOf<Point>()
        val childToParent = mutableMapOf<Point, Point>()

        for (y in grid.indices) {
            for (x in grid[0].indices) {
                val point = Point(x, y)
                distances[point] = if (point == start) 0 else Int.MAX_VALUE
            }
        }

        queue.add(Pair(start, 0))
        val target = Point(grid[0].size - 1, grid.size - 1)

        while (queue.isNotEmpty()) {
            val (current, _) = queue.poll()
            if (current in visited) {
                continue
            }
            if (current == target) {
                break
            }

            visited.add(current)

            val currentDistance = distances[current]!!
            for (idx in dxs.indices) {
                val nextPoint = Point(current.x + dxs[idx], current.y + dys[idx])
                if (!isInRange(nextPoint.x, nextPoint.y, grid) || nextPoint in visited) {
                    continue
                }

                val newDistance = currentDistance + grid[nextPoint.y][nextPoint.x]

                if (newDistance < distances[nextPoint]!!) {
                    distances[nextPoint] = newDistance
                    childToParent[nextPoint] = current
                    queue.add(Pair(nextPoint, newDistance))
                }
            }
        }

        val path = mutableListOf<Point>()
        var last = Point(grid[0].size - 1, grid.size - 1)
        while (start != last) {
            path.add(last)
            last = childToParent[last]!!
        }
        path.reverse()

        return Pair(distances[Point(grid[0].size - 1, grid.size - 1)]!!, path)
    }

    private fun displayPath(grid: List<List<Int>>, path: List<Point>) {
        for (y in grid.indices) {
            for (x in 0 until grid[0].size) {
                if (path.contains(Point(x, y))) {
                    print("\u001b[31m${grid[y][x]}\u001b[0m")
                } else {
                    print(grid[y][x])
                }
            }
            println()
        }
        println()
    }

    data class Point(val x: Int, val y: Int) {
        override fun toString(): String {
            return "($x, $y)"
        }
    }

    @Suppress("unused")
    private fun display(grid: List<List<Int>>) {
        for (row in grid) {
            for (x in 0 until grid[0].size) {
                print(row[x])
            }
            println()
        }
        println()
    }
}
