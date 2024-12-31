package year2024

import aoc.IAocTaskKt
import utils.except
import kotlin.math.max
import kotlin.math.min

class Day12 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_12.txt"

    override fun solvePartOne(lines: List<String>) {
        val grid: Array<CharArray> = lines.map { it.toCharArray() }.toTypedArray()
        val areas = getAreas(grid)
        var result = 0

        for (area in areas) {
            val size = area.size
            val perimeter: Int = area.perimeter()
//            println("AREA: ${area.first()} ${grid.valueAt(area.first())}, perim $perimeter, size $size")
            result += size * perimeter
        }
        println(result)
    }

    private fun visit(point: Point, visited: MutableSet<Point>, newArea: MutableSet<Point>, grid: Array<CharArray>) {
        val neighs = point.neighs()
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
        val areas = getAreas(grid)
        var result = 0

        for (area in areas) {
            val size = area.size
            val sides: Int = area.sides()
//            println("AREA: ${area.first()} ${grid.valueAt(area.first())}, sides $sides, size $size")
            result += size * sides
        }
        println(result)
    }

    private fun getAreas(grid: Array<CharArray>): MutableList<MutableSet<Point>> {
        val areas = mutableListOf<MutableSet<Point>>()

        val visited = mutableSetOf<Point>()
        for (y in grid.indices) {
            for (x in grid[y].indices) {
                val point = Point(x, y)
                if (point !in visited) {
                    val newArea = mutableSetOf(point)
                    visit(point, visited, newArea, grid)
                    areas.add(newArea)
                }
            }
        }
        return areas
    }


    data class Point(val x: Int, val y: Int) {
        fun neighs(): Set<Point> {
            val neighs = mutableSetOf<Point>()

            for (idx in DX.indices) {
                val next = this + Point(DX[idx], DY[idx])
                neighs.add(next)
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

private fun MutableSet<Day12.Point>.sides(): Int {
    var xEdges = mutableListOf<Edge>()
    var yEdges = mutableListOf<Edge>()

    for (point in this) {
        val up = point + Day12.Point(0, -1)
        val down = point + Day12.Point(0, 1)
        val left = point + Day12.Point(-1, 0)
        val right = point + Day12.Point(1, 0)
        if (up !in this) {
            xEdges.add(Edge(up.y, up.x, up.x, -1))
        }
        if (down !in this) {
            xEdges.add(Edge(down.y, down.x, down.x, 1))
        }
        if (left !in this) {
            yEdges.add(Edge(left.x, left.y, left.y, -1))
        }
        if (right !in this) {
            yEdges.add(Edge(right.x, right.y, right.y, 1))
        }
    }

    var prevSize = -1
    var size = xEdges.size + yEdges.size

    while (prevSize != size) {
        prevSize = size
        xEdges = merge(xEdges)
        yEdges = merge(yEdges)
        size = xEdges.size + yEdges.size
    }

    return size
}


fun merge(edges: MutableList<Edge>): MutableList<Edge> {
    val updated: MutableList<Edge> = mutableListOf<Edge>()
    for (edge in edges) {
        val others = edges.except(edge)
        val adjacents = others.filter { edge.isAdjacentTo(it) }
        if (adjacents.isNotEmpty()) {
            val adjacent = adjacents.first()
            val merged = edge.mergeWith(adjacent)
            updated.addAll(edges.except(edge).except(adjacent))
            updated.add(merged)
            return updated
        }
    }
    return edges
}

data class Edge(val axis: Int, val from: Int, val to: Int, val dir: Int) {
    fun isAdjacentTo(it: Edge): Boolean {
        return it.axis == axis && dir == it.dir && (to + 1 == it.from
                || it.to + 1 == from)
    }

    fun mergeWith(other: Edge): Edge {
        val newFromY = min(from, other.from)
        val newToY = max(to, other.to)
        return Edge(axis, newFromY, newToY, dir)
    }
}

private fun MutableSet<Day12.Point>.perimeter(): Int {
    var perimeter = 0
    for (point in this) {
        val neighs = point.neighs()
            .filter { it !in this }
        perimeter += neighs.size
    }
    return perimeter
}

private fun Array<CharArray>.valueAt(pos: Day12.Point): Char {
    return this[pos.y][pos.x]
}
