package year2024

import aoc.IAocTaskKt
import kotlin.math.ceil

class Day13 : IAocTaskKt {
    //    override fun getFileName(): String = "aoc2024/input_13.txt"
    override fun getFileName(): String = "aoc2024/input_13.txt"

    override fun solvePartOne(lines: List<String>) {
        val clawMachine = lines.windowed(4, 4, true).map { clawLines ->
            ClawMachine.parse(clawLines)
        }
        clawMachine.onEach { println(it) }
            .map { it.smallestTokenCost() }
            .sumOf { it }
            .let { println(it) }
    }

    override fun solvePartTwo(lines: List<String>) {
        val clawMachine = lines.windowed(4, 4, true).map { clawLines ->
            ClawMachine.parse(clawLines)
        }
        clawMachine.onEach { println(it) }
            .map { it.modifyPrize() }
            .map { it.smallestTokenCost2() }
            .sumOf { it }
            .let { println(it) }
    }

    data class ClawMachine(val a: Point, val b: Point, val prize: Point) {
        fun smallestTokenCost(limit: Long = 100L): Long {
            var smallest = Long.MAX_VALUE
            var bRange =
                1..limit // Math.max(ceil(prize.x / a.x.toDouble()).toLong(), ceil(prize.y / a.y.toDouble()).toLong())
            var aRange =
                1..limit // Math.max(ceil(prize.x / b.x.toDouble()).toLong(), ceil(prize.y / b.y.toDouble()).toLong())

            for (bMul in bRange.reversed()) {
                for (aMul in aRange) {
                    val target = Point(0, 0) + a * aMul + b * bMul
                    if (prize == target) {
                        smallest = Math.min(smallest, aMul * A_COST + bMul * B_COST)
                    }
                }
            }
            return if (smallest != Long.MAX_VALUE) smallest else 0
        }

        fun smallestTokenCost2(): Long {
            var smallest = Long.MAX_VALUE
            var bRange = 1..Math.max(ceil(prize.x / a.x.toDouble()).toLong(), ceil(prize.y / a.y.toDouble()).toLong())
            var aRange = 1..Math.max(ceil(prize.x / b.x.toDouble()).toLong(), ceil(prize.y / b.y.toDouble()).toLong())

            for (bMul in bRange.reversed()) {
                for (aMul in aRange) {
                    val target = Point(0, 0) + a * aMul + b * bMul
                    if (prize == target) {
                        smallest = Math.min(smallest, aMul * A_COST + bMul * B_COST)
                    }
                }
            }
            return if (smallest != Long.MAX_VALUE) smallest else 0
        }

        fun modifyPrize(): ClawMachine {
            return copy(prize = prize + Point(10000000000000L, 10000000000000L))
        }

        companion object {
            fun parse(clawLines: List<String>): ClawMachine {
                val lines = clawLines.filter { it.isNotBlank() }
                val aButton = parseButton(lines[0])
                val bButton = parseButton(lines[1])
                val price = parsePrize(lines[2])
                return ClawMachine(aButton, bButton, price)
            }

            private fun parsePrize(s: String): Point {
                val (_, xyStr) = s.split(": ")
                val (xStr, yStr) = xyStr.split(", ")
                val x = xStr.substring(2).toLong()
                val y = yStr.substring(2).toLong()
                return Point(x, y)
            }

            private fun parseButton(s: String): Point {
                val buttonCoords = s.split(": ")
                val xyStr = buttonCoords[1]
                val (xStr, yStr) = xyStr.split(", ")
                val x = xStr.substring(2).toLong()
                val y = yStr.substring(2).toLong()
                return Point(x, y)
            }

            val A_COST = 3
            val B_COST = 1
        }
    }

    data class Point(val x: Long, val y: Long) {
        operator fun plus(other: Point): Point {
            return Point(x + other.x, y + other.y)
        }

        operator fun times(multiplier: Long): Point {
            return Point(multiplier * x, multiplier * y)
        }
    }
}

