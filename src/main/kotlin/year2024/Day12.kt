package year2024

import aoc.IAocTaskKt

class Day12 : IAocTaskKt {
        override fun getFileName(): String = "aoc2024/input_12.txt"
//    override fun getFileName(): String = "aoc2024/input_12_test.txt"

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

        private operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)

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
