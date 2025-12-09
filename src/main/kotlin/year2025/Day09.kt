package year2025

import aoc.IAocTaskKt
import kotlin.math.abs
import kotlin.math.max

class Day09 : IAocTaskKt {
    override fun getFileName(): String = "aoc2025/input_09.txt"

    override fun solvePartOne(lines: List<String>) {
        val points = lines.map(Point::parse)
        var maxSurface = 0L
        for (a in points) {
            for (b in points) {
                if (a != b) {
                    maxSurface = maxSurface.coerceAtLeast(a * b)
                }
            }
        }
        println(maxSurface)
    }

    override fun solvePartTwo(lines: List<String>) {
        TODO("Not yet implemented")
    }

    data class Point(val x: Long, val y: Long): Comparable<Point> {
        override fun compareTo(other: Point): Int {
            if(x != other.x) return x.compareTo(other.x)
            return y.compareTo(other.y)
        }

        companion object {
            fun parse(coords: String): Point {
                val (x, y) = coords.split(",").map(String::trim).map(String::toLong)
                return Point(x, y)
            }
        }

        operator fun times(other: Point): Long {
            return (abs(x - other.x) + 1L) * (abs(y - other.y) + 1L)
        }
    }
}