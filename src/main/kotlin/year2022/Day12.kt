package year2022

import aoc.IAocTaskKt
import java.lang.IllegalStateException
import java.util.*

const val START = "S"
const val END = "E"
private val dxs = arrayOf(0, -1, 1, 0)
private val dys = arrayOf(-1, 0, 0, 1)

//private val moveToIdx = mapOf("D" to 0, "L" to 1, "U" to 3, "R" to 2)

typealias GridMap = List<List<Day12.MapCell>>

class Day12 : IAocTaskKt {
//    override fun getFileName(): String = "aoc2022/input_12.txt"
    override fun getFileName(): String = "aoc2022/input_12.txt"

    override fun solvePartOne(lines: List<String>) {
        val trackMap: GridMap = lines
            .mapIndexed { yIdx, row ->
                row.chunked(1).mapIndexed { xIdx, cell ->
                    val height = getCellHeight(cell)
                    val mapCell = MapCell(Point(xIdx, yIdx), cell, height)
                    mapCell
                }.toList()
            }.toList()

        val start = trackMap.flatten().first { it.value == START }

        println(trackMap.print())

        val result = findShortestPaths(start.position, trackMap).size

        println(result - 1)
    }

    /**
     * Finds the shortest paths (Dijkstra) from the root to all other nodes in the grid based labyrinth.
     * Collects the data about found keys
     *
     * @param start root of the paths
     * @return paths
     */
    private fun findShortestPaths(start: Point, trackMap: GridMap): List<Point> {
        val unvisitedCost = HashMap<Point, Int>()

        trackMap.flatten().forEach{ vertex: MapCell ->
            unvisitedCost[vertex.position] = Int.MAX_VALUE
        }
        val childToParent = HashMap<Point, Point>()
        var pathCost = 0
        var currentPosition: Point = start
        val endPosition: Point = trackMap.flatten().first { it.value == END }.position
        unvisitedCost[currentPosition] = pathCost
        while (unvisitedCost.isNotEmpty() && currentPosition != endPosition) {
            pathCost = unvisitedCost.remove(currentPosition)!!
            pathCost++
            for (moveIdx in dxs.indices) {
                val move = Point(dxs[moveIdx], dys[moveIdx])
                val currentCell = trackMap[currentPosition.y][currentPosition.x]
                val nextPosition = currentPosition + move
                if (!nextPosition.isLegal(currentCell, trackMap)) {
                    continue
                }
                val cost = unvisitedCost[nextPosition] ?: continue
                if (cost > pathCost) {
                    childToParent[nextPosition] = currentPosition
                    unvisitedCost[nextPosition] = pathCost
                }
            }

            currentPosition = getUnvisitedNodeWithLowestCost(unvisitedCost)
        }
        return mapToPath(childToParent, trackMap)
    }

    private fun mapToPath(childToParent: HashMap<Point, Point>, trackMap: GridMap): List<Point> {
        val endCell = trackMap.flatten().first { it.value == END }
        val path = mutableListOf<Point>()

        path.add(endCell.position)
        var currentCell = endCell

        while (currentCell.value != START) {
            val currentCellPosition: Point = childToParent[currentCell.position]!!
            currentCell = trackMap[currentCellPosition.y][currentCellPosition.x]
            path.add(currentCellPosition)
        }

        return path
    }

    private fun getUnvisitedNodeWithLowestCost(unvisitedCost: HashMap<Point, Int>): Point {
        val lowestCost = unvisitedCost.values.minOfOrNull { it } ?: Int.MAX_VALUE
        return if (lowestCost == Int.MAX_VALUE) {
            throw IllegalStateException("could not find path")
        } else unvisitedCost
            .keys
            .first { key: Point ->
                unvisitedCost[key] == lowestCost
            }
    }

    private fun getCellHeight(cell: String): Int = when (cell) {
        END -> 'z'.code
        START -> 'a'.code
        else -> cell[0].code
    }

    private fun getPossibleMovesFrom(
        here: MapCell,
        trackMap: List<List<MapCell>>,
    ): List<Point> {
        val options = mutableListOf<Point>()
        for (optionIdx in dys.indices) {
            val move = Point(dxs[optionIdx], dys[optionIdx])
            val nextPosition = here.position + move
            if (nextPosition.isLegal(here, trackMap)) {
                options.add(nextPosition)
            }
        }
        return options
    }

    override fun solvePartTwo(lines: List<String>) {
        val trackMap: GridMap = lines
            .mapIndexed { yIdx, row ->
                row.chunked(1).mapIndexed { xIdx, cell ->
                    val height = getCellHeight(cell)
                    val mapCell = MapCell(Point(xIdx, yIdx), cell, height)
                    mapCell
                }.toList()
            }.toList()

        val starts = trackMap.flatten().filter { it.value == START || it.value == "a"}

        var lowest = Int.MAX_VALUE
        for (start in starts) {
            val result = findShortestPaths(start.position, trackMap).size
            if (lowest > result) {
                lowest = result
            }
        }

        println(lowest - 1)
    }

    data class Point(val x: Int, val y: Int) {
        operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)
        fun isLegal(here: MapCell, trackMap: List<List<MapCell>>): Boolean {
            val maxY = trackMap.size - 1
            val maxX = trackMap[0].size - 1
            if (x < 0 || y < 0) return false
            if (x > maxX || y > maxY) return false
            val nextCell = trackMap[y][x]
            return (nextCell.height - here.height <= 1)
        }
    }

    data class MapCell(val position: Point, val value: String, val height: Int)
}

private fun GridMap.print(): String {
    var result = ""
    for (row in this) {
        for (cell in row) {
            result += cell.value
        }
        result += "\n"
    }
    return result
}
