package year2025

import aoc.IAocTaskKt
import java.util.*
import kotlin.math.sqrt

class Day08 : IAocTaskKt {
    override fun getFileName(): String = "aoc2025/input_08_test.txt"

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

        val groups = mutableListOf<MutableSet<Point>>()
        var counter = 0

        while (counter < connections) {
            val connection = getNextConnection(distanceToPair)
            val existingGroup = groups.firstOrNull { it.contains(connection.first) }
            val existingSecondGroup = groups.firstOrNull { it.contains(connection.second) }
            if (existingGroup != null && existingSecondGroup != null && existingGroup.first() == existingSecondGroup.first()) {
                continue
            }

            counter++
            if (existingGroup != null && existingSecondGroup != null) {
                groups.remove(existingSecondGroup)
                existingGroup.addAll(existingSecondGroup)
            } else
                if (existingGroup != null){
                existingGroup.add(connection.second)
            } else if (existingSecondGroup != null) {
                existingSecondGroup.add(connection.first)
            } else {
                val set = mutableSetOf<Point>()
                set.add(connection.first)
                set.add(connection.second)
                groups.add(set)
            }
        }
        val topThree = groups.sortedByDescending { it.size }
            .subList(0, 3)
        println(topThree)
        println(topThree.map { it.size })
        println(topThree.map { it.size }.fold(1) { acc, i -> acc * i })
    }

    private fun getNextConnection(distanceToPair: TreeMap<Double, MutableList<Pair<Point, Point>>>): Pair<Point, Point> {
        val (dist, list) = distanceToPair.firstEntry()
        val first = list.removeFirst()
        if (list.isEmpty()) {
            distanceToPair.remove(dist)
        }
        return first
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
