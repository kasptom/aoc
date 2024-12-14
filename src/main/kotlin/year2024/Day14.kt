package year2024

import aoc.IAocTaskKt
import kotlin.math.abs

class Day14 : IAocTaskKt {
    //    override fun getFileName(): String = "aoc2024/input_14.txt"
    override fun getFileName(): String = "aoc2024/input_14.txt"
//    override fun getFileName(): String = "aoc2024/input_14_one_robot_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val robots = lines.map { Robot.parse(it) }
        val quadrantWidth = if (getFileName().endsWith("test.txt")) 11 else 101
        val quadrantHeight = if (getFileName().endsWith("test.txt")) 7 else 103
        val time = 100
        val movedRobots = robots.map { it.move(time, quadrantWidth, quadrantHeight) }
        val quadrants = Quadrants(quadrantWidth, quadrantHeight)
        println(quadrants.print(movedRobots))
        println(quadrants)
        val counts = quadrants.count(movedRobots)
        println(robots)
        println(movedRobots)
        println(counts)
        println(counts.multiply())
    }

    override fun solvePartTwo(lines: List<String>) {
        val robots = lines.map { Robot.parse(it) }
        val quadrantWidth = if (getFileName().endsWith("test.txt")) 11 else 101
        val quadrantHeight = if (getFileName().endsWith("test.txt")) 7 else 103

        val quadrants = Quadrants(quadrantWidth, quadrantHeight)
//        quadrants.print(robots)

        var time = 1
//        val output = File("C:\\Users\\Tomek\\Dev\\aoc\\src\\main\\resources\\aoc2024\\output_14.txt")
        while (time < 10405) { // 10403 - cycle (robots == movedRobots)
            time++
            val movedRobots = robots.map { it.move(time, quadrantWidth, quadrantHeight) }

            val boardPrintOut = quadrants.print(movedRobots)
            // First attempt
//            output.appendText("----------------------------------------------------------------------")
//            output.appendText("time: $time\n")
//            output.appendText(quadrants.print(movedRobots))
//            output.appendText("----------------------------------------------------------------------")

            // Second - when you already know what to look for
            if (boardPrintOut.contains("11111111")) {
                println(boardPrintOut)
                println(time)
                break
            }
        }
    }

    data class Robot(val pos: Point, val v: Point) {
        fun move(time: Int, width: Int, height: Int): Robot { // NOTE: teleport to the other side
            val newPos = pos + v * time
            val moved = copy(pos = newPos, v = v)
            val teleported = moved.teleport(width, height)
            return teleported
        }

        private fun teleport(width: Int, height: Int): Robot {
            val newX = if (pos.x < 0) {
                (width - abs(pos.x) % width) % width
            } else if (pos.x >= width) {
                pos.x % width
            } else {
                pos.x
            }
            val newY = if (pos.y < 0) {
                (height - abs(pos.y) % height) % height
            } else if (pos.y >= height) {
                pos.y % height
            } else {
                pos.y
            }
            return copy(pos = Point(newX, newY))
        }

        fun isInRange(xRange: IntRange, yRange: IntRange): Boolean {
            return pos.x in xRange && pos.y in yRange
        }

        override fun toString(): String {
            return "🤖(p=$pos, v=$v)"
        }


        companion object {
            // p=27,86 v=65,-30
            fun parse(input: String): Robot {
                val (posStr, velStr) = input.split(" ")
                val (posX, posY) = posStr.substring(2).split(",").map { it.toInt() }
                val (velX, velY) = velStr.substring(2).split(",").map { it.toInt() }
                return Robot(Point(posX, posY), Point(velX, velY))
            }
        }
    }

    /**
    first second
    third fourth
     */
    data class Quadrants(val width: Int, val height: Int) { // 101 (0..100) 103 (0..102)
        private val firstRangeX = 0 until width / 2
        private val firstRangeY = 0 until height / 2
        private val secondRangeX = (width / 2 + 1) until width
        private val secondRangeY = 0 until height / 2
        private val thirdRangeX = 0 until width / 2
        private val thirdRangeY = (height / 2 + 1) until height
        private val fourthRangeX = (width / 2 + 1) until width
        private val fourthRangeY = (height / 2 + 1) until height

        fun count(movedRobots: List<Robot>): Counts {
            val first = movedRobots.count { it.isInRange(firstRangeX, firstRangeY) }
            val second = movedRobots.count { it.isInRange(secondRangeX, secondRangeY) }
            val third = movedRobots.count { it.isInRange(thirdRangeX, thirdRangeY) }
            val fourth = movedRobots.count { it.isInRange(fourthRangeX, fourthRangeY) }
            return Counts(first, second, third, fourth)
        }

        override fun toString(): String {
            return "Quadrants(firstRangeX=$firstRangeX, firstRangeY=$firstRangeY, secondRangeX=$secondRangeX, secondRangeY=$secondRangeY, thirdRangeX=$thirdRangeX, thirdRangeY=$thirdRangeY, fourthRangeX=$fourthRangeX, fourthRangeY=$fourthRangeY)"
        }

        fun print(robots: List<Robot>): String {
            var result = ""
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val count = robots.count { it.pos == Point(x, y) }
                    if (count > 0) {
                        result += count
                    } else {
                        result += "."
                    }
                }
                result += "\n"
            }
            return result
        }

        data class Counts(val first: Int, val second: Int, val third: Int, val fourth: Int) {
            fun multiply(): Long {
                return first.toLong() * second * third * fourth
            }
        }


    }

    data class Point(val x: Int, val y: Int) {
        operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)
        operator fun times(time: Int): Point = Point(x * time, y * time)
        override fun toString(): String {
            return "($x, $y)"
        }
    }
}
