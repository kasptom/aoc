package year2024

import aoc.IAocTaskKt
import utils.except
import year2024.Day15.Item.Type

class Day15 : IAocTaskKt {
    //        override fun getFileName(): String = "aoc2024/input_15.txt"
    override fun getFileName(): String = "aoc2024/input_15_test_3.txt"

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
        val separatorIdx = lines.indexOfFirst { it.isBlank() }
        val map: Array<CharArray> = lines.subList(0, separatorIdx)
            .map { it.map { wider(it) }.flatten().toCharArray() }
            .toTypedArray()

        val moves = lines.subList(separatorIdx, lines.size)
            .map { it.chunked(1) }
            .flatten()

        println(moves)

        var robot: Point = findRobot(map)
        var items: List<Item> = findItems(map)
        map.clear()

        println("Initial state:")
        println(map.print(robot, items))
        for (code in moves) {
            val move = Point.fromMoveCode(code)
            val (i, r) = items.moveItems(robot, move)
            robot = r
            items = i
            println("Move $code:")
            println(map.print(robot, items))
        }

        val gpsSum = items.gpsSum()
        println(gpsSum)
    }

    private fun findItems(map: Array<CharArray>): List<Item> {
        val items = mutableListOf<Item>()
        for (y in map.indices) {
            for (x in map[0].indices) {
                val point = Point(x, y)
                if (point.x % 2 == 0 && map.valueAt(point) == '#') {
                    items += Item(point, point + Point(1, 0), Type.WALL)
                } else if (map.valueAt(point) == '[') {
                    items += Item(point, point + Point(1, 0), Type.BOX)
                }
            }
        }
        return items
    }

    private fun wider(symbol: Char): List<Char> {
        return when (symbol) {
            '#' -> listOf('#', '#')
            '.' -> listOf('.', '.')
            'O' -> listOf('[', ']')
            '@' -> listOf('@', '.')
            else -> throw IllegalArgumentException("Unknown symbol: $symbol")
        }
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

    data class Item(val left: Point, val right: Point, val type: Type) {
        enum class Type {
            BOX, WALL
        }

        fun mapRepresentation(idx: Int): String = when (type) {
            Type.BOX -> "[]".substring(idx, idx + 1)
            Type.WALL -> "##".substring(idx, idx + 1)
        }

        fun touchesX(other: Item): Boolean =
            right + Point(1, 0)  == other.left || other.right + Point(1, 0) == this.left

        fun move(move: Point): Item {
            return copy(left = left + move, right = right + move)
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

    private fun Array<CharArray>.clear() {
        for (y in this.indices) {
            for (x in this[0].indices) {
                this[y][x] = '.'
            }
        }
    }

    private fun Array<CharArray>.print(robot: Point, items: List<Item>): String {
        var result = ""
        for (y in this.indices) {
            for (x in this[0].indices) {
                val point = Point(x, y)
                if (robot == point) {
                    result += "@"
                } else if (items.any { it.left == point }) {
                    result += items.first { it.left == point }.mapRepresentation(0)
                } else if (items.any { it.right == point} ) {
                    result += items.first { it.right == point }.mapRepresentation(1)
                } else {
                    result += '.'
                }
            }
            result += "\n"
        }
        return result
    }

    private fun List<Item>.moveItems(robot: Point, move: Point): Pair<List<Item>, Point> {
        val nextPosition = robot + move

        if (this.none { it.left == nextPosition || it.right == nextPosition }) {
            return Pair(this, nextPosition)
        }

        if (move.x == -1) { // left shift horizontal
            val touched = this.first { it.right == nextPosition }
            val affected = findAffectedHorizontal(this.except(touched), touched, move)
            if (affected.any { it.type == Type.WALL }) {
                return Pair(this, robot)
            }

            val moved = affected.map { it.move(move) }
            val updated = this.except(affected.toSet()) + moved
            return Pair(updated, nextPosition)
        }

        return Pair(this, robot)
    }

    private fun findAffectedHorizontal(other: List<Item>, touched: Item, move: Point): List<Item> {
        val toCheck = other
            .filter { it.left.y == touched.left.y && it.left.x.compareTo(touched.left.x) == move.x }
            .toMutableList()
        val affected = mutableListOf(touched)
        while (toCheck.any { affected.any { af -> af.touchesX(it) } }) {
            val touchedItems = toCheck.filter { tc -> affected.any { af -> tc.touchesX(af) } }
            affected.addAll(touchedItems)
            toCheck.removeAll(touchedItems)
        }
        return affected
    }

    private fun List<Item>.gpsSum(): Long {
        var sum = 0L
        for (item in this) {
            if (item.type == Type.BOX) {
                sum += 100 * item.left.y + item.left.x
            }
        }
        return sum
    }
}
