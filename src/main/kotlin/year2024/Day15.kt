package year2024

import aoc.IAocTaskKt

class Day15 : IAocTaskKt {
        override fun getFileName(): String = "aoc2024/input_15.txt"
//    override fun getFileName(): String = "aoc2024/input_15_test_2.txt"

    override fun solvePartOne(lines: List<String>) {
        val separatorIdx = lines.indexOfFirst { it.isBlank() }
        val map: Array<CharArray> = lines.subList(0, separatorIdx)
            .map { it.toCharArray() }
            .toTypedArray()

        val moves = lines.subList(separatorIdx, lines.size)
            .map { it.chunked(1) }
            .flatten()

        println(moves)

        var robot: Point = findRobot(map)
        map.setValue(robot, '.')
        println(map.print(robot))

//        println("Initial state:")
//        println(map.print(robot))

        for (code in moves) {
//            println("Move $code:")
            val move = Point.fromMoveCode(code)
            robot = map.move(robot, move)
//            println(map.print(robot))
        }

        val gpsSum = map.gpsSum()
        println(gpsSum)
    }

    private fun findRobot(map: Array<CharArray>): Point {
        for (y in map.indices) {
            for (x in map[0].indices) {
                val pos = Point(x, y)
                if (map.valueAt(pos) == '@') {
                    return pos
                }
            }
        }
        return Point(-1, -1)
    }

    override fun solvePartTwo(lines: List<String>) {
        if (lines.isEmpty()) println("empty lines") else println(lines.size)
    }

    private fun Array<CharArray>.print(robot: Point): Any {
        var result = ""
        for (y in this.indices) {
            for (x in this[0].indices) {
                val point = Point(x, y)
                if (robot == point) {
                    result += "@"
                } else {
                    result += this.valueAt(point)
                }
            }
            result += "\n"
        }
        return result
    }

    data class Point(var x: Int, var y: Int) {
        fun gpsValue(): Long {
            return 100L * y + x
        }

        operator fun plus(move: Point): Point = Point(x + move.x, y + move.y)

        companion object {
            fun fromMoveCode(code: String): Point = when (code) {
                "<" -> Point(-1, 0)
                ">" -> Point(1, 0)
                "v" -> Point(0, 1)
                "^" -> Point(0, -1)
                else -> throw Exception("Unknown move code")
            }
        }

    }

    private fun Array<CharArray>.move(robot: Point, move: Point): Point {
        val frontView: CharArray = this.view(robot, move)
        val emptySpaceIdx = frontView.indexOfFirst { it == '.' }
        if (emptySpaceIdx == -1) {
            return robot
        }
        for (i in (emptySpaceIdx - 1) downTo 0) {
            frontView[i + 1] = frontView[i]
        }
        frontView[0] = '.'
        this.fill(robot, move, frontView)
        return robot + move
    }

    private fun Array<CharArray>.valueAt(pos: Point): Char = this[pos.y][pos.x]

    private fun Array<CharArray>.gpsSum(): Long {
        var sum = 0L
        for (y in this.indices) {
            for (x in this.indices) {
                val point = Point(x, y)
                if (this.valueAt(point) == 'O') {
                    sum += point.gpsValue()
                }
            }
        }
        return sum
    }

    private fun Array<CharArray>.view(robot: Point, move: Point): CharArray {
        val elements = mutableListOf<Char>()
        var position = robot + move
        while (this.valueAt(position) != '#') {
            elements += this.valueAt(position)
            position += move
        }
        val result = elements.toCharArray()
        return result
    }

    private fun Array<CharArray>.setValue(pos: Point, c: Char) {
        this[pos.y][pos.x] = c
    }

    private fun Array<CharArray>.fill(robot: Point, move: Point, view: CharArray) {
        var position = robot + move
        for (i in view.indices) {
            this.setValue(position, view[i])
            position += move
        }
    }
}
