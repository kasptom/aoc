package year2024

import aoc.IAocTaskKt

class Day08 : IAocTaskKt {
//    override fun getFileName(): String = "aoc2024/input_08.txt"
     override fun getFileName(): String = "aoc2024/input_08.txt"

    override fun solvePartOne(lines: List<String>) {
        val grid: Array<CharArray> = lines.map { it.toCharArray() }.toTypedArray()
        val freqToPositions: MutableMap<Char, MutableSet<Point>> = mutableMapOf()
        for (y in grid.indices) {
            for (x in grid[0].indices) {
                if (grid[y][x] != '.') {
                    freqToPositions.putIfAbsent(grid[y][x], mutableSetOf())
                    freqToPositions[grid[y][x]]!!.add(Point(x, y))
                }
            }
        }

        val freqToLocations: MutableMap<Char, MutableSet<Point>> = mutableMapOf()
        for (freq in freqToPositions.keys) {
            freqToLocations.putIfAbsent(freq, mutableSetOf())
            val points = freqToPositions[freq]!!
            val points2 = freqToPositions[freq]!!
            for (a in points) {
                for (b in points2) {
                    if (a != b) {
                        freqToLocations[freq]!!.addAll(a.generateAntinodes(b, grid))
                    }
                }
            }
        }
        println(freqToLocations.values.flatten().distinct().size)
    }

    override fun solvePartTwo(lines: List<String>) {
        val grid: Array<CharArray> = lines.map { it.toCharArray() }.toTypedArray()
        val freqToPositions: MutableMap<Char, MutableSet<Point>> = mutableMapOf()
        for (y in grid.indices) {
            for (x in grid[0].indices) {
                if (grid[y][x] != '.') {
                    freqToPositions.putIfAbsent(grid[y][x], mutableSetOf())
                    freqToPositions[grid[y][x]]!!.add(Point(x, y))
                }
            }
        }

        val freqToLocations: MutableMap<Char, MutableSet<Point>> = mutableMapOf()
        for (freq in freqToPositions.keys) {
            freqToLocations.putIfAbsent(freq, mutableSetOf())
            val points = freqToPositions[freq]!!
            val points2 = freqToPositions[freq]!!
            for (a in points) {
                for (b in points2) {
                    if (a != b) {
                        freqToLocations[freq]!!.addAll(a.generateAntinodes2(b, grid))
                    }
                }
            }
        }
        println(freqToLocations.values.flatten().distinct().size)
    }

    data class Point(val x: Int, val y: Int) {
        fun generateAntinodes(other: Point, grid: Array<CharArray>): Set<Point> {
            val minX = 0
            val maxX = grid[0].size - 1
            val minY = 0
            val maxY = grid.size - 1
            val min = Point(minX, minY)
            val max = Point(maxX, maxY)


            val diff = this - other
            val antinodes = mutableSetOf<Point>()

            var point = this - diff
            if (point.isInRange(min, max)) {
                antinodes.add(point)
                point -= diff
            }
            point = this + diff
            if (point.isInRange(min, max)) {
                antinodes.add(point)
                point += diff
            }
            point = other - diff
            if (point.isInRange(min, max)) {
                antinodes.add(point)
                point -= diff
            }
            point = other + diff
            if (point.isInRange(min, max)) {
                antinodes.add(point)
                point += diff
            }
            return antinodes - setOf(this, other)
        }

        fun generateAntinodes2(other: Point, grid: Array<CharArray>): Set<Point> {
            val minX = 0
            val maxX = grid[0].size - 1
            val minY = 0
            val maxY = grid.size - 1
            val min = Point(minX, minY)
            val max = Point(maxX, maxY)


            val diff = this - other
            val antinodes = mutableSetOf<Point>()

            var point = this - diff
            while (point.isInRange(min, max)) {
                antinodes.add(point)
                point -= diff
            }
            point = this + diff
            while (point.isInRange(min, max)) {
                antinodes.add(point)
                point += diff
            }
            point = other - diff
            while (point.isInRange(min, max)) {
                antinodes.add(point)
                point -= diff
            }
            point = other + diff
            while (point.isInRange(min, max)) {
                antinodes.add(point)
                point += diff
            }
            return antinodes // - setOf(this, other)
        }

        private fun isInRange(min: Point, max: Point): Boolean = x in min.x..max.x && y in min.y..max.y

        private operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)

        private operator fun minus(other: Point): Point = Point(x - other.x, y - other.y)
    }
}

private fun Array<CharArray>.print(antinodes: Set<Day08.Point>) {
    for (y in this.indices) {
        for (x in this[0].indices) {
            val point = Day08.Point(y, x)
            if (point in antinodes) {
                print("#")
            } else {
                print(this[y][x])
            }
        }
        println()
    }
}
