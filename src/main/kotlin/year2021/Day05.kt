package year2021

import aoc.IAocTaskKt
import kotlin.math.abs

class Day05 : IAocTaskKt {
    override fun getFileName(): String = "aoc2021/input_05.txt"

    override fun solvePartOne(lines: List<String>) {
        val segments = lines.map(Segment::parse)
            .filter(Segment::isNotDiagonal)

        val marked: MutableMap<Point, Int> = mutableMapOf()
        segments.forEach { it.mark(marked) }

//        printGrid(theLines, marked)
        println(marked.values.count { it > 1 })
    }

    override fun solvePartTwo(lines: List<String>) {
        val segments = lines.map(Segment::parse)

        val marked: MutableMap<Point, Int> = mutableMapOf()

        segments.forEach { it.mark(marked) }
        println(marked.values.count { it > 1 })
    }

    data class Segment(val from: Point, val to: Point) {
        fun mark(marked: MutableMap<Point, Int>) {
//            println("marking $from $to")
            for (point in from..to) {
                marked.putIfAbsent(point, 0)
                marked[point] = marked[point]!! + 1
            }
        }

        fun isNotDiagonal() = from.x == to.x || from.y == to.y

        companion object {
            fun parse(input: String): Segment {
                val (from, to) = input.split("->")
                    .map(String::trim)
                    .map(Point::parse)
                return Segment(from, to)
            }
        }
    }

    data class Point(val x: Int, val y: Int) {
        operator fun rangeTo(to: Point): List<Point> {
            val delta = Point(
                x = if (x == to.x) 0 else (to.x - x) / abs(to.x - x),
                y = if (y == to.y) 0 else (to.y - y) / abs(to.y - y)
            )
            val result = mutableListOf<Point>()
            if (delta != ZERO) {
                result.add(this)
            }
            var current = this
            while (current != to) {
                current += delta
                result += current
            }
            return result.toList()
        }

        private operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)

        companion object {
            fun parse(coords: String): Point {
                val (x, y) = coords.split(",").map(String::trim).map(String::toInt)
                return Point(x, y)
            }

            val ZERO = Point(0, 0)
        }
    }

    @Suppress("unused")
    private fun printGrid(
        theLines: List<Segment>,
        marked: MutableMap<Point, Int>
    ) {
        fun yCoords(lines: List<Segment>) = lines.flatMap { listOf(it.from.y, it.to.y) }
        fun xCoords(lines: List<Segment>) = lines.flatMap { listOf(it.from.x, it.to.x) }

        val minX = xCoords(theLines).minOf { it }
        val maxX = xCoords(theLines).maxOf { it }

        val minY = yCoords(theLines).minOf { it }
        val maxY = yCoords(theLines).maxOf { it }

        val grid = Array(maxY + 1) { IntArray(maxX + 1) { 0 } }
        for (point in marked.keys) {
            grid[point.x][point.y] = marked[point]!!
        }

        println()
        for (y in minY..maxY) {
            for (x in minX..maxX) {
                (if (grid[x][y] == 0) "." else grid[x][y])
                    .let(::print)
            }
            println()
        }
    }
}