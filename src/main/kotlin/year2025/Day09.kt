package year2025

import aoc.IAocTaskKt
import kotlin.math.abs

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
        val points = lines.map(Point::parse)
        var maxSurface = 0L

        for (i in points.indices) {
            for (j in (i + 1) until points.size) {
                val a = points[i]
                val b = points[j]

                if (rectangleIsWithinShoelace(a, b, points)) {
                    maxSurface = maxSurface.coerceAtLeast(a * b)
                }
            }
        }
        println(maxSurface)
    }

    private fun rectangleIsWithinShoelace(a: Point, b: Point, points: List<Point>): Boolean {
        val x1 = kotlin.math.min(a.x, b.x)
        val x2 = kotlin.math.max(a.x, b.x)
        val y1 = kotlin.math.min(a.y, b.y)
        val y2 = kotlin.math.max(a.y, b.y)

        if (x1 == x2 || y1 == y2) return false

        val corners = listOf(
            Point(x1, y1),
            Point(x2, y1),
            Point(x2, y2),
            Point(x1, y2)
        )

        return corners.all { corner -> pointInOrOnPolygon(corner, points) }
    }

    private fun pointInOrOnPolygon(point: Point, polygon: List<Point>): Boolean {
        for (i in polygon.indices) {
            val p1 = polygon[i]
            val p2 = polygon[(i + 1) % polygon.size]
            if (isPointOnLineSegment(point, p1, p2)) {
                return true
            }
        }

        return pointInPolygonInteger(point, polygon)
    }

    private fun pointInPolygonInteger(point: Point, polygon: List<Point>): Boolean {
        var inside = false
        var p1 = polygon.last()

        for (p2 in polygon) {
            if ((p2.y > point.y) != (p1.y > point.y)) {
                val crossX = (p1.x - p2.x) * (point.y - p2.y) / (p1.y - p2.y) + p2.x
                if (point.x < crossX) {
                    inside = !inside
                }
            }
            p1 = p2
        }

        return inside
    }

    private fun isPointOnLineSegment(point: Point, p1: Point, p2: Point): Boolean {
        val crossProduct = (point.y - p1.y) * (p2.x - p1.x) - (point.x - p1.x) * (p2.y - p1.y)
        if (crossProduct != 0L) {
            return false
        }

        return point.x >= kotlin.math.min(p1.x, p2.x) &&
                point.x <= kotlin.math.max(p1.x, p2.x) &&
                point.y >= kotlin.math.min(p1.y, p2.y) &&
                point.y <= kotlin.math.max(p1.y, p2.y)
    }

    data class Point(val x: Long, val y: Long) : Comparable<Point> {
        override fun compareTo(other: Point): Int {
            if (x != other.x) return x.compareTo(other.x)
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
