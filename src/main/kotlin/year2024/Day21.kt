package year2024

import aoc.IAocTaskKt
import year2024.Day21.Pad.Companion.DIR_PAD
import year2024.Day21.Pad.Companion.NUM_PAD
import year2024.Day21.Point
import java.util.*
import kotlin.math.min
import kotlin.math.sqrt

class Day21 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_21.txt"
    // override fun getFileName(): String = "aoc2024/input_21_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val costs = lines.map { getNumCodeLength(it, 0, 2) }
        val complexities = costs.mapIndexed { idx, length -> Complexity.toComplexity(numPadCode = lines[idx], length) }
        println(complexities.sumOf { it.value() })
    }

    fun getNumCodeLength(code: String, depth: Int, maxDepth: Int): Int {
        val prefixedCode = "A$code"
        var length = 0
        for (idx in 1 until prefixedCode.length) {
            val (prev, curr) = prefixedCode.substring(idx - 1, idx + 1).map { it }
            length += getDirCodeLength(prev, curr, NUM_PAD, depth, maxDepth)
        }
        return length
    }

    private fun getDirCodeLength(prev: Char, curr: Char, pad: Array<CharArray>, depth: Int, maxDepth: Int): Int {
        if (depth == maxDepth) {
            return Pad().getMovementPaths("$prev$curr", pad).minOf { it.length }
        }
        val options = Pad().getMovementPaths("$prev$curr", pad)

        var minLength = Integer.MAX_VALUE

        for (option in options) {
            val prefixedOption = "A$option"
            var length = 0
            for (idx in 1 until prefixedOption.length) {
                val (nextPrev, nextCurr) = prefixedOption.substring(idx - 1, idx + 1).map { it }
                length += getDirCodeLength(nextPrev, nextCurr, DIR_PAD, depth + 1, maxDepth)
            }
            minLength = min(minLength, length)
        }

        return minLength
    }

    override fun solvePartTwo(lines: List<String>) {
        if (lines.isEmpty()) println("empty lines") else println(lines.size)
    }

    class Pad {
        fun getMovementPaths(dirPadCode: String, pad: Array<CharArray>): List<String> {
            val prefixedNumPadCode = dirPadCode
            val paths = mutableListOf("")

            for (idx in 1 until prefixedNumPadCode.length) {
                val start = pad.positionOf(prefixedNumPadCode[idx - 1])
                val end = pad.positionOf(prefixedNumPadCode[idx])
                val pathsPoints = Point.shortestPathsFrom(start, end, pad)

                val newPaths = mutableListOf<String>()
                for (path in paths) {
                    for (pointPath in pathsPoints) {
                        val newPath = path + pointPath.windowed(size = 2, step = 1)
                            .map { (prev, curr) -> Point.toDirButton(prev, curr) }
                            .joinToString("") + "A"
                        newPaths += newPath
                    }
                }
                paths.clear()
                paths.addAll(newPaths)
            }
            return paths
        }

        companion object {
            val DIR_PAD = arrayOf(
                charArrayOf(' ', '^', 'A'),
                charArrayOf('<', 'v', '>')
            )

            val NUM_PAD = arrayOf(
                charArrayOf('7', '8', '9'),
                charArrayOf('4', '5', '6'),
                charArrayOf('1', '2', '3'),
                charArrayOf(' ', '0', 'A'),
            )
        }
    }

    data class Point(val x: Int, val y: Int) : Comparable<Point> {
        override fun compareTo(other: Point): Int {
            if (dist() != other.dist()) {
                return dist().compareTo(other.dist())
            }
            if (x != other.x) {
                return x.compareTo(other.x)
            }
            return y.compareTo(other.y)
        }

        private fun dist(): Double = sqrt(0.0 + x * x + y * y)

        fun neighs(): List<Point> {
            val candidates = listOf(
                this + right,
                this + down,
                this + left,
                this + up,
            )
            return candidates
        }

        private operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)

        fun inRange(grid: Array<CharArray>): Boolean = inRange(grid[0].size, grid.size)

        fun inRange(width: Int, height: Int): Boolean {
            return x in 0 until width && y in 0 until height
        }

        operator fun minus(other: Point): Point = Point(x - other.x, y - other.y)

        companion object {
            val up = Point(0, -1)
            val down = Point(0, 1)
            val left = Point(-1, 0)
            val right = Point(1, 0)

            fun toDirButton(prev: Point, curr: Point): Char {
                val diff = curr - prev
                return when (diff) {
                    up -> '^'
                    down -> 'v'
                    left -> '<'
                    right -> '>'
                    else -> throw IllegalStateException("Not a dir diff $curr - $prev =$diff")
                }
            }

            fun shortestPathsFrom(start: Point, end: Point, numPad: Array<CharArray>): List<List<Point>> {
                val queue = TreeSet<PointDistPath>()
                queue.add(PointDistPath(start, 0, listOf(start)))

                var shortest = Integer.MAX_VALUE
                val shortestPaths = mutableListOf<List<Point>>()

                while (queue.isNotEmpty()) {
                    val (node, dist, path) = queue.pollFirst()
                    if (dist > shortest) {
                        break
                    }

                    if (node == end) {
                        shortestPaths.add(path)
                        shortest = path.size
                    }

                    val neighs = node.neighs()
                        .filter { it.inRange(numPad) }
                        .filter { numPad.valueAt(it) != ' ' }

                    queue.addAll(
                        neighs.map { PointDistPath(it, dist + 1, path + it) }
                    )
                }
                return shortestPaths.toList()
            }
        }
    }

    data class PointDistPath(val point: Point, val cost: Int, val path: List<Point>) :
        Comparable<PointDistPath> {
        override fun compareTo(other: PointDistPath): Int {
            if (cost != other.cost) {
                return cost.compareTo(other.cost)
            }
            if (point != other.point) {
                return point.compareTo(other.point)
            }
            if (path.size != other.path.size) {
                path.size.compareTo(other.path.size)
            }
            for (idx in path.indices) {
                if (path[idx] != other.path[idx]) {
                    return path[idx].compareTo(other.path[idx])
                }
            }
            return 0
        }
    }

    data class Complexity(val length: Int, val numericPart: Int) {
        fun value(): Int = length * numericPart

        companion object {
            fun toComplexity(numPadCode: String, length: Int): Complexity {
                val numericPart = numPadCode.replace("A", "")
                    .toInt()
                return Complexity(length, numericPart)
            }
        }
    }
}

private fun Array<CharArray>.positionOf(c: Char): Point {
    for (y in this.indices) {
        for (x in this[y].indices) {
            val point = Point(x, y)
            if (this.valueAt(point) == c) {
                return point
            }
        }
    }
    throw IllegalStateException("could not find the char $c")
}

private fun Array<CharArray>.valueAt(pos: Point): Char {
    return this[pos.y][pos.x]
}
