package year2022

import aoc.IAocTaskKt
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day15 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_15.txt"

    override fun solvePartOne(lines: List<String>) {
        val sensors: List<Sensor> = lines.map { Sensor.parse(it) }
        val y = if (getFileName().contains("test")) 20 else 2000000
        val result = countNotPossiblePositions(sensors, y)
        println(result - 1)
    }

    private fun countNotPossiblePositions(sensors: List<Sensor>, y: Int): Int {
        val getNotPossiblePositions = sensors.map { it.toNotPossiblePositionsFor(y) }
            .flatten()
            .toSet()
        return getNotPossiblePositions.count()
    }

    override fun solvePartTwo(lines: List<String>) {
        val sensors: List<Sensor> = lines.map { Sensor.parse(it) }
        val maxDistance = if (getFileName().contains("test")) 20 else 4000000
//        val distressBeaconPosition = findPositionInRange(sensors, 20)
        val distressBeaconPosition = findPositionInRange(sensors, maxDistance)
        println(distressBeaconPosition)
        val freq: Long = distressBeaconPosition.getTuningFrequency()
        println(freq)
    }

    private fun findPositionInRange(sensors: List<Sensor>, maxDistance: Int): Point {
        for (y in 0..maxDistance) { // TODO
//        for (y in maxDistance..maxDistance) {
            val notOverlappingRanges: List<RowRange> =
                sensors.map { it.toLimitedNotPossiblePositionsFor(y, maxDistance) }
                    .filter { it != RowRange.empty() }
                    .fold(emptyList()) { notOverlapping, next ->
                        val overlappingWithNext = notOverlapping.filter { it.overlaps(next) }
                        val merged: RowRange = (overlappingWithNext + next).toRange()
                        val result = (notOverlapping - overlappingWithNext.toSet()) + listOf(merged)
                        result
                    }
            if (notOverlappingRanges.count() != 1) {
                val coordsSorted = notOverlappingRanges.flatMap { listOf(it.fromX, it.toX) }
                    .sorted()
                val x = coordsSorted[1] + 1
                return Point(x, y)
            }
        }
        return Point(0, 0)
    }

    // Sensor at x=2, y=18: closest beacon is at x=-2, y=15
    data class Sensor(val position: Point, val closestBeacon: Point) {
        fun toNotPossiblePositionsFor(y: Int): List<Point> {
            val notPossible = mutableListOf<Point>()
//            if (closestBeacon.y == y) notPossible += closestBeacon
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

        fun toLimitedNotPossiblePositionsFor(y: Int, maxX: Int): RowRange {
            mutableListOf<Point>()
            val closestManhattan = position.manhattan(closestBeacon)
            val verticalDistance = abs(y - position.y)
            if (verticalDistance > closestManhattan) return RowRange.empty()

            val horizontalDistance = (closestManhattan - verticalDistance)
            return RowRange(
                max(0, position.x - (horizontalDistance)),
                min(position.x + (horizontalDistance), maxX),
                y
            )
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
        fun getTuningFrequency(): Long {
            return 4000000L * x.toLong() + y.toLong()
        }
    }

    data class RowRange(var fromX: Int, var toX: Int, val y: Int) {
        fun extendWith(pos: Point) {

        }

        fun overlaps(other: RowRange): Boolean {
            // (StartA <= EndB) and (EndA >= StartB)
            val minB = other.fromX
            val maxB = other.toX
            val minA = fromX
            val maxA = toX
            return minA <= maxB && maxA >= minB
        }


        companion object {
            fun empty(): RowRange {
                return RowRange(-1, -1, -1)
            }
        }
    }
}

private fun List<Day15.RowRange>.toRange(): Day15.RowRange {
    val minX = this.map { it.fromX }.minOf { it }
    val maxX = this.map { it.toX }.maxOf { it }
    return Day15.RowRange(minX, maxX, this[0].y)
}
