package year2025

import aoc.IAocTaskKt
import java.util.*
import kotlin.math.sqrt

class Day08 : IAocTaskKt {
    override fun getFileName(): String = "aoc2025/input_08.txt"

    override fun solvePartOne(lines: List<String>) {
        val connections = if (getFileName() == "aoc2025/input_08_test.txt") 10 else 1000
        val points = lines.map { line -> Point.parse(line) }.toSet()
        points.groupBy { point -> point.equclideanDistance(points.minOf { it }).toInt() }
        val pointPairToDistance = mutableMapOf<Pair<Point, Point>, Double>()
        val distanceToPair = TreeMap<Double, MutableList<Pair<Point, Point>>>()
        for (point in points) {
            for (other in points) {
                if (point == other) {
                    continue
                }
                val key = point.combineWith(other)
                if (pointPairToDistance.containsKey(key).not()) {
                    val distance = point.equclideanDistance(other)
                    pointPairToDistance[key] = distance
                    distanceToPair.putIfAbsent(distance, mutableListOf())
                    distanceToPair[distance]!!.add(key)
                }
            }
        }
//        distanceToPair.forEach { (distance, pairs) -> println("$distance -> $pairs") }
        val topConnections = mutableListOf<Pair<Point, Point>>()

        (1..connections).forEach { _ ->
            val (dist, list) = distanceToPair.firstEntry()
            val first = list.removeFirst()
            if (list.isEmpty()) {
                distanceToPair.remove(dist)
            }
            topConnections.add(first)
        }
        println(topConnections)
    }

    override fun solvePartTwo(lines: List<String>) {
        TODO("Not yet implemented")
    }

    data class Point(val x: Int, val y: Int, val z: Int) : Comparable<Point> {
        fun equclideanDistance(other: Point): Double {
            val distX = (x - other.x) * (x - other.x).toDouble()
            val distY = (y - other.y) * (y - other.y).toDouble()
            val distZ = (z - other.z) * (z - other.z).toDouble()
            return sqrt(distX + distY + distZ)
        }


        companion object {
            fun parse(input: String): Point {
                val (x, y, z) = input.split(",").map(String::trim).map(String::toInt)
                return Point(x, y, z)
            }
        }

        override fun compareTo(other: Point): Int {
            if (x != other.x) {
                return x.compareTo(other.x)
            } else if (y != other.y) {
                return y.compareTo(other.y)
            }
            return z.compareTo(other.z)
        }

        fun combineWith(other: Point): Pair<Point, Point> = if (this > other) {
            Pair(this, other)
        } else if (this < other) {
            Pair(other, this)
        } else throw Exception("Cannot combine points with same coordinates")

        override fun toString(): String = "$x,$y,$z"
    }
}
