package year2024

import aoc.IAocTaskKt
import utils.except
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day12 : IAocTaskKt {
//        override fun getFileName(): String = "aoc2024/input_12.txt"
//    override fun getFileName(): String = "aoc2024/input_12_test.txt"
    override fun getFileName(): String = "aoc2024/input_12.txt"

    override fun solvePartOne(lines: List<String>) {
        val grid: Array<CharArray> = lines.map { it.toCharArray() }.toTypedArray()
        val areas = mutableListOf<MutableSet<Point>>()

        var visited = mutableSetOf<Point>()
        for (y in grid.indices) {
            for (x in grid[y].indices) {
                var point = Point(x, y)
                if (point !in visited) {
                    val newArea = mutableSetOf(point)
                    visit(point, visited, newArea, grid)
                    areas.add(newArea)
                }
            }
        }
        var result = 0

        for (area in areas) {
            val size = area.size
            val perimeter: Int = area.perimeter(grid)
            println("AREA: ${area.first()} ${grid.valueAt(area.first())}, perim $perimeter, size $size")
            result += size * perimeter
        }
        println(result)
    }

    private fun visit(point: Point, visited: MutableSet<Point>, newArea: MutableSet<Point>, grid: Array<CharArray>) {
        val neighs = point.neighs(grid)
        for (neigh in neighs) {
            if (!neigh.isInRange(grid)) {
                continue
            }
                if (neigh !in visited && grid.valueAt(neigh) == grid.valueAt(point)) {
                    visited.add(neigh)
                    newArea.add(neigh)
                    visit(neigh, visited, newArea, grid)
                }
        }
    }

    override fun solvePartTwo(lines: List<String>) {
        val grid: Array<CharArray> = lines.map { it.toCharArray() }.toTypedArray()
        val areas = mutableListOf<MutableSet<Point>>()

        var visited = mutableSetOf<Point>()
        for (y in grid.indices) {
            for (x in grid[y].indices) {
                var point = Point(x, y)
                if (point !in visited) {
                    val newArea = mutableSetOf(point)
                    visit(point, visited, newArea, grid)
                    areas.add(newArea)
                }
            }
        }
        var result = 0

        for (area in areas) {
            val size = area.size
            val sides: Int = area.sides(grid)
            println("AREA: ${area.first()} ${grid.valueAt(area.first())}, sides $sides, size $size")
            result += size * sides
        }
        println(result)
    }


    data class Point(val x: Int, val y: Int) {
        fun neighs(grid: Array<CharArray>): Set<Point> {
            val neighs = mutableSetOf<Point>()

            for (idx in DX.indices) {
                val next = this + Point(DX[idx], DY[idx])
//                if (next.isInRange(min, max)) {
                    neighs.add(next)
//                }
            }
            return neighs
        }

        private fun getGridRange(grid: Array<CharArray>): Pair<Point, Point> {
            val minX = 0
            val maxX = grid[0].size - 1
            val minY = 0
            val maxY = grid.size - 1
            val min = Point(minX, minY)
            val max = Point(maxX, maxY)
            return Pair(min, max)
        }

        fun isInRange(min: Point, max: Point): Boolean = x in min.x..max.x && y in min.y..max.y

        operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)

        private operator fun minus(other: Point): Point = Point(x - other.x, y - other.y)
        fun isInRange(grid: Array<CharArray>): Boolean {
            val (min, max) = getGridRange(grid)
            return isInRange(min, max)
        }
    }

    companion object {
        val DX = arrayOf(0, 1, 0, -1)
        val DY = arrayOf(-1, 0, 1, 0)
    }
}

private fun MutableSet<Day12.Point>.sides(grid: Array<CharArray>): Int {
    var YEdges = mutableListOf<YEdge>()
    var XEdges = mutableListOf<XEdge>()

    for (point in this) {
        val up = point + Day12.Point(0, -1)
        val down = point + Day12.Point(0, 1)
        val left = point + Day12.Point(-1, 0)
        val right = point + Day12.Point(1, 0)
        if (up !in this) {
            YEdges.add(YEdge(up.y, up.x, up.x, -1))
        }
        if (down !in this) {
            YEdges.add(YEdge(down.y, down.x, down.x, 1))
        }
        if (left !in this) {
            XEdges.add(XEdge(left.x, left.y, left.y, -1))
        }
        if (right !in this) {
            XEdges.add(XEdge(right.x, right.y, right.y, 1))
        }
    }

    var prevSize = -1
    var size = YEdges.size + XEdges.size

    while (prevSize != size) {
        prevSize = size
        YEdges = mergeY(YEdges)
        XEdges = mergeX(XEdges)
        size = YEdges.size + XEdges.size
    }

    return size
}


fun mergeY(YEdges: MutableList<YEdge>): MutableList<YEdge> {
    var updated: MutableList<YEdge> = mutableListOf<YEdge>()
    for (edge in YEdges) {
        var others = YEdges.except(edge)
        var adjacents = others.filter { edge.isAdjacentTo(it) }
        if (adjacents.isNotEmpty()) {
            val adjacent = adjacents.first()
            val merged = edge.mergeWith(adjacent)
            updated.addAll(YEdges.except(edge).except(adjacent))
            updated.add(merged)
            return updated
        }
    }
    return YEdges
}

fun mergeX(XEdges: MutableList<XEdge>): MutableList<XEdge> {
    var updated: MutableList<XEdge> = mutableListOf<XEdge>()
    for (edge in XEdges) {
        var others = XEdges.except(edge)
        var adjacents = others.filter { edge.isAdjacentTo(it) }
        if (adjacents.isNotEmpty()) {
            val adjacent = adjacents.first()
            val merged = edge.mergeWith(adjacent)
            updated.addAll(XEdges.except(edge).except(adjacent))
            updated.add(merged)
            return updated
        }
    }
    return XEdges
}

data class YEdge(val x: Int, val fromY: Int, val toY: Int, val dir: Int) {
    fun isAdjacentTo(it: YEdge): Boolean {
        return it.x == x && dir == it.dir && (toY + 1 == it.fromY
                || it.toY + 1 == fromY)
    }

    fun mergeWith(other: YEdge): YEdge {
        val newFromY = min(fromY, other.fromY)
        val newToY = max(toY, other.toY)
        return YEdge(x, newFromY, newToY, dir)
    }
}

data class XEdge(val y: Int, val fromX: Int, val toX: Int, val dir: Int) {
    fun isAdjacentTo(it: XEdge): Boolean {
        return it.y == y && dir == it.dir && (toX + 1 == it.fromX
                || it.toX + 1 == fromX)
    }

    fun mergeWith(other: XEdge): XEdge {
        val newFromX = min(fromX, other.fromX)
        val newToX = max(toX, other.toX)
        return XEdge(y, newFromX, newToX, dir)
    }
}

private fun MutableSet<Day12.Point>.perimeter(grid: Array<CharArray>): Int {
    var perimeter = 0
    for (point in this) {
        val neighs = point.neighs(grid)
            .filter { it !in this }
        perimeter += neighs.size
    }
    return perimeter
}

private fun Array<CharArray>.valueAt(pos: Day12.Point): Char {
    return this[pos.y][pos.x]
}

private fun Array<CharArray>.print(antinodes: Set<Day12.Point>) {
    for (y in this.indices) {
        for (x in this[0].indices) {
            val point = Day12.Point(y, x)
            if (point in antinodes) {
                print("#")
            } else {
                print(this[y][x])
            }
        }
        println()
    }
}
