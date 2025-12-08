package year2025

import aoc.IAocTaskKt
import java.util.*
import kotlin.math.sqrt

class Day08 : IAocTaskKt {
    override fun getFileName(): String = "aoc2025/input_08.txt"

    override fun solvePartOne(lines: List<String>) {
        val connections = if (getFileName() == "aoc2025/input_08_test.txt") 10 else 1000
        val (_, distanceToPair, groups) = setup(lines)
        var counter = 0

        while (counter < connections) {
            val connection = getNextConnection(distanceToPair)
            counter++
            addConnection(groups, connection)
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
        val (points, distanceToPair, groups) = setup(lines)
        var counter = 0

        while (true) {
            val connection = getNextConnection(distanceToPair)
            counter++

            if (addConnection(groups, connection)) {
                continue
            }

            if (groups.size == 1 && groups.first().size == points.size) {
                println(connection.first.x.toLong() * connection.second.x.toLong())
                break
            }
        }
    }

    private fun addConnection(
        groups: MutableList<MutableSet<Point>>,
        connection: Pair<Point, Point>,
    ): Boolean {
        val existingGroup = groups.firstOrNull { it.contains(connection.first) }
        val existingSecondGroup = groups.firstOrNull { it.contains(connection.second) }

        if (existingGroup != null && existingSecondGroup != null && existingGroup === existingSecondGroup) {
            return true
        }

        if (existingGroup != null && existingSecondGroup != null) {
            groups.remove(existingSecondGroup)
            existingGroup.addAll(existingSecondGroup)
        } else if (existingGroup != null) {
            existingGroup.add(connection.second)
        } else if (existingSecondGroup != null) {
            existingSecondGroup.add(connection.first)
        } else {
            val set = mutableSetOf<Point>()
            set.add(connection.first)
            set.add(connection.second)
            groups.add(set)
        }
        return false
    }

    private fun setup(lines: List<String>): Triple<Set<Point>, TreeMap<Double, MutableList<Pair<Point, Point>>>, MutableList<MutableSet<Point>>> {
        val points = lines.map { line -> Point.parse(line) }.toSet()
        points.groupBy { point -> point.euclideanDistance(points.minOf { it }).toInt() }
        val pointPairToDistance = mutableMapOf<Pair<Point, Point>, Double>()
        val distanceToPair = TreeMap<Double, MutableList<Pair<Point, Point>>>()
        for (point in points) {
            for (other in points) {
                if (point == other) {
                    continue
                }
                val key = point.combineWith(other)
                if (pointPairToDistance.containsKey(key).not()) {
                    val distance = point.euclideanDistance(other)
                    pointPairToDistance[key] = distance
                    distanceToPair.putIfAbsent(distance, mutableListOf())
                    distanceToPair[distance]!!.add(key)
                }
            }
        }
        //        distanceToPair.forEach { (distance, pairs) -> println("$distance -> $pairs") }

        val groups = mutableListOf<MutableSet<Point>>()
        return Triple(points, distanceToPair, groups)
    }

    data class Point(val x: Int, val y: Int, val z: Int) : Comparable<Point> {
        fun euclideanDistance(other: Point): Double {
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
