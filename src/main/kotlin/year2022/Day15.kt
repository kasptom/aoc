package year2022

import aoc.IAocTaskKt
import kotlin.math.abs

class Day15 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_15.txt"

    override fun solvePartOne(lines: List<String>) {
        val sensors: List<Sensor> = lines.map { Sensor.parse(it) }
        val result = countNotPossiblePositions(sensors, 2000000)
        println(result - 1)
    }

    private fun countNotPossiblePositions(sensors: List<Sensor>, y: Int): Int {
        val getNotPossiblePositions = sensors.map { it.toNotPossiblePositionsFor(y) }
            .flatten()
            .toSet()
        return getNotPossiblePositions.count()
    }

    override fun solvePartTwo(lines: List<String>) {
        println("??")
    }

    // Sensor at x=2, y=18: closest beacon is at x=-2, y=15
    data class Sensor(val position: Point, val closestBeacon: Point) {
        fun toNotPossiblePositionsFor(y: Int): List<Point> {
            val notPossible = mutableListOf<Point>()
            if (closestBeacon.y == y) notPossible += closestBeacon
            val closestManhattan = position.manhattan(closestBeacon)
            val verticalDistance = abs(y - position.y)
            if (verticalDistance > closestManhattan) return emptyList()

            val pointOnYLineDirectlyBelowOrAbove = Point(position.x, y)
            notPossible.add(pointOnYLineDirectlyBelowOrAbove)
            for (i in 1..(closestManhattan - verticalDistance)) {
                notPossible.add(Point(pointOnYLineDirectlyBelowOrAbove.x + i, y))
                notPossible.add(Point(pointOnYLineDirectlyBelowOrAbove.x - i, y))
            }
            return notPossible
        }

        companion object {
            fun parse(line: String): Sensor {
                val cleanedUp = line.replace("Sensor at ", "")
                    .replace(": closest beacon is at ", ";")
                val (positionStr, closestBeaconStr) = cleanedUp.split(";")
                val position = parsePosition(positionStr)
                val closestBeacon = parsePosition(closestBeaconStr)

                return Sensor(position, closestBeacon)
            }

            private fun parsePosition(positionStr: String): Point {
                val (x, y) = positionStr.replace("x=", "")
                    .replace("y=", "")
                    .split(", ")
                    .map { it.toInt() }
                    .zipWithNext()
                    .single()
                return Point(x, y)
            }
        }
    }
    data class Point(val x: Int, val y: Int) {
        override fun toString(): String {
            return "($x, $y)"
        }

        fun manhattan(other: Point): Int = abs(other.x - x) + abs(other.y - y)
    }
}