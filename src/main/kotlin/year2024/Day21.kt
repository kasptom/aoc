package year2024

import aoc.IAocTaskKt
import utils.permutations
import year2024.Day21.Point
import java.util.*
import kotlin.math.sqrt

class Day21 : IAocTaskKt {
    //    override fun getFileName(): String = "aoc2024/input_21.txt"
    override fun getFileName(): String = "aoc2024/input_21.txt"

    override fun solvePartOne(lines: List<String>) {
        val doorCodes = lines
        doorCodes
//            .onEach { println(it) }
            .map { numPadToDirPadPath(it) }
//            .onEach { println(it) }
            .map { paths -> paths.map { path -> dirPadToDirPad(path) }.flatten() }
            .map { paths -> paths.map { path -> dirPadToDirPad(path) }.flatten() }
//            .onEach { println(it) }
//            .onEachIndexed { idx, path -> println("${lines[idx]} --> $path ") }
            .mapIndexed { idx, paths -> paths.map { path -> toComplexity(lines[idx], path) } }
//            .onEachIndexed { idx, complexity -> println("${lines[idx]}: $complexity") }
            .map { complexities -> complexities.minOf { it } }
            .map { (len, code) -> len * code }
            .sum()
            .let { println(it) }


        // (num) --> (dir) --> (dir) --> (dir)
    }

    private fun toComplexity(numPadCode: String, code: String): Complexity {
        val length = code.length
        val numericPart = numPadCode.replace("A", "")
            .toInt()
        return Complexity(length, numericPart)
    }

    data class Complexity(val length: Int, val numericPart: Int): Comparable<Complexity> {
        override fun compareTo(other: Complexity): Int = if (numericPart != other.numericPart) {
            numericPart.compareTo(other.numericPart)
        } else {
            length.compareTo(other.length)
        }
    }

    private fun dirPadToDirPad(dirPadCode: String): List<String> {
        val dirPad = DirectionalPad()
        return dirPad.getMovementPaths(dirPadCode)
    }

    private fun numPadToDirPadPath(numPadCode: String): List<String> {
        val numPad = NumPad()
        return numPad.getMovementPaths(numPadCode)
    }

    override fun solvePartTwo(lines: List<String>) {
        if (lines.isEmpty()) println("empty lines") else println(lines.size)
    }

    data class DirectionalPad(val pos: Point = Point(2, 0)) {
        fun getMovementPaths(dirPadCode: String): List<String> {
            val prefixedNumPadCode = "A$dirPadCode"
            val paths = mutableListOf("")

            for (idx in 1 until prefixedNumPadCode.length) {
                val start = dirPad.positionOf(prefixedNumPadCode[idx - 1])
                val end = dirPad.positionOf(prefixedNumPadCode[idx])
                val pathsPoints = Point.shortestPathsFrom(start, end, dirPad)

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

        val dirPad = arrayOf(
            charArrayOf(' ', '^', 'A'),
            charArrayOf('<', 'v', '>')
        )
    }

    data class NumPad(val pos: Point = Point(2, 3)) {
        val numPad = arrayOf(
            charArrayOf('7', '8', '9'),
            charArrayOf('4', '5', '6'),
            charArrayOf('1', '2', '3'),
            charArrayOf(' ', '0', 'A'),
        )

        fun getMovementPaths(numPadCode: String): List<String> {
            val prefixedNumPadCode = "A$numPadCode"
            val paths = mutableListOf("")

            for (idx in 1 until prefixedNumPadCode.length) {
                val start = numPad.positionOf(prefixedNumPadCode[idx - 1])
                val end = numPad.positionOf(prefixedNumPadCode[idx])
                val pathsPoints = Point.shortestPathsFrom(start, end, numPad)

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


