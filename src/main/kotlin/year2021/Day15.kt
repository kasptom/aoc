package year2021

import aoc.IAocTaskKt

class Day15 : IAocTaskKt {
    override fun getFileName() = "aoc2021/input_15.txt"

    val dxs = arrayOf(0, 1)
    val dys = arrayOf(1, 0)
    val pointToMinCost = mutableMapOf<Point, Int>()
    val maxSum = Int.MAX_VALUE / 2

    override fun solvePartOne(lines: List<String>) {
        val grid = lines.map { it.chunked(1).map(String::toInt) }
        display(grid)
        val start = Point(0, 0)
        println(findLowestTotalRiskPath(grid, mutableListOf(start), start) - grid[0][0])
    }

    private fun findLowestTotalRiskPath(
        grid: List<List<Int>>,
        currentPath: MutableList<Point>,
        currentPoint: Point
    ): Int {
        val (x, y) = currentPoint
        if (y == grid.size - 1 && x == grid[0].size - 1) {
            return grid[y][x]
        }

        val (prevX, prevY) = currentPath.last()
        var minCost = maxSum
        for (idx in dxs.indices) {
            val nextX = x + dxs[idx]
            val nextY = y + dys[idx]
            if (isInRange(nextX, nextY, grid) && (nextX != prevX || nextY != prevY)) {
                val nextPoint = Point(nextX, nextY)
                val subPathCost =
                    if (pointToMinCost.containsKey(nextPoint)) {
                        pointToMinCost[nextPoint]!!
                    } else {
                        currentPath.add(nextPoint)
                        val cost = findLowestTotalRiskPath(grid, currentPath, nextPoint)
                        currentPath.removeLast()
                        cost
                    }
                if (subPathCost < minCost) {
                    minCost = subPathCost
                }
            }
            pointToMinCost[currentPoint] = minCost + grid[currentPoint.y][currentPoint.x]
        }
        return pointToMinCost[currentPoint]!!
    }

    private fun isInRange(x: Int, y: Int, grid: List<List<Int>>) =
        isInRange(y, grid.size) && isInRange(x, grid[0].size)

    private fun isInRange(pos: Int, size: Int): Boolean = pos in 0 until size

    override fun solvePartTwo(lines: List<String>) {
        TODO("Not yet implemented")
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