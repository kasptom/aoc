package year2023

import aoc.IAocTaskKt

class Day22 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_22.txt"

    override fun solvePartOne(lines: List<String>) {
        val bricks = lines.map(Brick::parse)
//            .sortedBy { it.min.z }
        bricks.onEach { println(it) }

        if (bricks.size == 7) {
            ('A'..'G').zip(bricks).forEach { (label, brick) ->
                brick.label = "$label"
            }
        }

        val tower = Tower(bricks.toMutableList())
        println("bricks count: " + tower.bricks.size)
//        println(tower.printX())
//        println()
//        println(tower.printY())
        tower.moveBricksToBottom()
        println("moved to bottom")
//        println(tower.printX())
//        println()
//        println(tower.printY())
        println("bricks count: " + tower.bricks.size)

        val removable: MutableSet<Brick> = mutableSetOf()
        for (brick in tower.bricks) {
            if (tower.bricks.any { it != brick && it.isInRange(brick) }) {
                throw IllegalStateException()
            }
        }

        for (brickIdx in tower.bricks.indices) {
            println("checking brick $brickIdx")
            val brick = tower.bricks[brickIdx]
            val prev = tower.bricks.filter { it != brick }.toMutableList()
            val newTower = tower.copy(bricks = prev.toMutableList())

            if (!newTower.moveBricks()) {
                removable += brick
            }
        }
        println(removable.size)
    }

    override fun solvePartTwo(lines: List<String>) {
        println("Not yet implemented")
    }


    data class Point(val x: Int, val y: Int, val z: Int) {
        override fun toString(): String = "($x, $y, $z)"
        operator fun minus(other: Point): Point = Point(x - other.x, y - other.y, z - other.z)
        operator fun plus(other: Point): Point {
            return Point(x + other.x, y + other.y, z + other.z)
        }
    }

    data class Brick(val min: Point, val max: Point, var label: String = "X") {



        fun volume(): Long {
            val volume = (max - min).run { (x + 1) * (y + 1) * (z + 1) }.toLong()
//            if (volume < 0) throw IllegalStateException("volume < 0: $volume for $this")
            return volume
        }

        fun isInRange(other: Brick): Boolean {
            // (StartA <= EndB) and (EndA >= StartB)
            val minB = other.min
            val maxB = other.max
            val minA = min
            val maxA = max
            return minA.x <= maxB.x && maxA.x >= minB.x
                    && minA.y <= maxB.y && maxA.y >= minB.y
                    && minA.z <= maxB.z && maxA.z >= minB.z
        }

        override fun toString(): String = "($min..$max)"
        fun isVisibleOnX(point: Point): Boolean {
            return point.x in min.x..max.x && point.z in min.z..max.z
        }

        fun isVisibleOnY(point: Point): Boolean {
            return point.y in min.y..max.y && point.z in min.z..max.z
        }

        fun moveDown(): Brick {
            val newMin = min + Point(0, 0, -1)
            val newMax = max + Point(0, 0, -1)
            return Brick(newMin, newMax, label)
        }

        fun moveUp(): Brick {
            val newMin = min + Point(0, 0, 1)
            val newMax = max + Point(0, 0, 1)
            return Brick(newMin, newMax, label)
        }

        companion object {
            fun parse(line: String): Brick {
                val (fromRaw, toRaw) = line.split("~")
                val from = fromRaw.split(",").filter(String::isNotEmpty)
                    .map { it.toInt() }
                    .toPoint()

                val to = toRaw.split(",").filter(String::isNotEmpty)
                    .map { it.toInt() }
                    .toPoint()

                return Brick(from, to)
            }
        }
    }

    data class Tower(val bricks: MutableList<Brick>) {
        val minX = bricks.minOf { it.min.x }
        val maxX = bricks.maxOf { it.max.x }

        val minY = bricks.minOf { it.min.y }
        val maxY = bricks.maxOf { it.max.y }

        val minZ = bricks.minOf { it.min.z }
        val maxZ = bricks.maxOf { it.max.z }



        fun printX(): String {
            var result = ""
            result += " x \n"
            for (x in minX..maxX) {
                result += "$x"
            }
            result += "\n"
            for (z in maxZ downTo 1) {
                for (x in minX..maxX) {
                    val point = Point(x, 0, z)
                    val foundBricks = bricks.filter {
                        it.isVisibleOnX(point)
                    }
                    if (foundBricks.size == 1) {
                        result += foundBricks.first().label
                    } else if (foundBricks.size > 1) {
                        result += "?"
                    } else {
                        result += "."
                    }
                }
                result += " $z"
                if (z == 5) {
                    result += " z"
                }
                result += "\n"
            }
            result += "--- 0\n"
            return result
        }

        fun printY(): String {
            var result = ""
            result += " y \n"
            for (x in minY..maxY) {
                result += "$x"
            }
            result += "\n"
            for (z in maxZ downTo 1) {
                for (y in minY..maxY) {
                    val point = Point(0, y, z)
                    val foundBricks = bricks.filter {
                        it.isVisibleOnY(point)
                    }
                    if (foundBricks.size == 1) {
                        result += foundBricks.first().label
                    } else if (foundBricks.size > 1) {
                        result += "?"
                    } else {
                        result += "."
                    }
                }
                result += " $z"
                if (z == 5) {
                    result += " z"
                }
                result += "\n"
            }
            result += "--- 0\n"
            return result
        }

        fun moveBricksToBottom() {
            var canMove = true
            while (canMove) {
                canMove = moveBricks()
            }
        }

        fun moveBricks(): Boolean {
            val newBricks = mutableListOf<Brick>()
            newBricks.addAll(bricks)
            var moved = false
            for (brick in bricks) {
                val movedBrick = brick.moveDown()
                if (movedBrick.min.z == 0) {
                    // invalid position
                    continue
                }
                if (bricks.filter { it != brick }.any { it.isInRange(movedBrick) }) {
                    continue
                }
                moved = true
                newBricks.remove(brick)
                newBricks.add(movedBrick)
            }
            bricks.clear()
//            bricks.addAll(newBricks.sortedBy { it.min.z })
            bricks.addAll(newBricks)
            return moved
        }
    }
}

private fun List<Int>.toPoint(): Day22.Point {
    if (size != 3) throw IllegalStateException("size must be 3")
    val (x, y, z) = this
    return Day22.Point(x, y, z)
}
