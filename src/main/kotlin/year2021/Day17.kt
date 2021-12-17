package year2021

import aoc.IAocTaskKt
import kotlin.math.abs

class Day17 : IAocTaskKt {
    override fun getFileName(): String = "aoc2021/input_17.txt"

    // target area: x=156..202, y=-110..-69
    /**
     * Find the initial velocity that causes the probe to reach the highest y position and still eventually be
     * within the target area after any step. What is the highest y position it reaches on this trajectory?
     */
    private val velocitiesWithinRange = mutableSetOf<Velocity>()

    override fun solvePartOne(lines: List<String>) {
        val targetArea = lines[0].replace("target area: ", "")
        val (fromToX, fromToY) = targetArea.split(", ")
        val (fromX, toX) = fromToX.replace("x=", "").split("..").map { it.toInt() }
        val (fromY, toY) = fromToY.replace("y=", "").split("..").map { it.toInt() }
        println("$fromX <= x <= $toX, $fromY <= y <= $toY")

        var maxY = 0

        val magicNumber = listOf(fromX, toX, fromY, toY).map(::abs).maxOf{ it * 2}

        var vx = -magicNumber
        while (vx < magicNumber) {
            var vy = -magicNumber
            while (vy < magicNumber) {
                val initialVelocity = Velocity(vx, vy)
                var position = Position(0, 0)
//                println("$velocity $position")
                var currentSpeedMaxY = 0
                var wasWithinRange = false
                var velocity = initialVelocity.copy()
                var times = 0
                while (times < magicNumber) {
//                    println("$position, $velocity")
                    position += velocity
                    velocity = velocity.applyDrag(1)
                    if (position.y >= currentSpeedMaxY) {
                        currentSpeedMaxY = position.y
                    }
                    if (position.x in fromX..toX && position.y in fromY..toY) {
                        wasWithinRange = true
                        velocitiesWithinRange.add(initialVelocity)
                    }
                    times++
                }
                if (wasWithinRange && maxY < currentSpeedMaxY) {
                    println("max y improved: $currentSpeedMaxY for initial velocity $initialVelocity")
                    maxY = currentSpeedMaxY
                }
                vy++
            }
            vx++
        }
        println(maxY)
    }

    data class Velocity(val x: Int, val y: Int) {
        fun applyDrag(drag: Int): Velocity {
            val newX = if (x > 0) x - drag
            else if (x < 0) x + drag else 0
            return Velocity(newX, y - drag)
        }

        override fun toString() = "v=($x, $y)"
    }

    data class Position(val x: Int, val y: Int) {
        operator fun plus(v: Velocity): Position = Position(x + v.x, y + v.y)
        override fun toString() = "pos=($x, $y)"
    }

    override fun solvePartTwo(lines: List<String>) {
        println(velocitiesWithinRange.size)
    }
}