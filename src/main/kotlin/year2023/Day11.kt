package year2023

import aoc.IAocTaskKt
import kotlin.math.abs


class Day11 : IAocTaskKt{
    override fun getFileName() = "aoc2023/input_11.txt"

    override fun solvePartOne(lines: List<String>) {
        val galaxies: MutableList<MutableList<String>> = lines.map { it.split("").filter(String::isNotEmpty).toMutableList() }
            .toMutableList()
        val columnsWithNoGalaxies = galaxies.indices
            .filter { idx -> galaxies.map { it[idx] }.all { it == "." } }
            .reversed()

        galaxies.print()

        println(columnsWithNoGalaxies)
        val rowsWithNoGalaxies = galaxies.indices.filter { galaxies[it].all { it == "." } }
            .reversed()

        for (row in rowsWithNoGalaxies) {
            galaxies.add(row, galaxies[row].toMutableList())
        }

        for (column in columnsWithNoGalaxies) {
            galaxies.forEach { row ->
                row.add(column, ".")
            }
        }

        galaxies.print()

        val points = mutableListOf<Point>()
        for (y in galaxies.indices) {
            for (x in galaxies.first().indices) {
                val point = Point(x, y)
                if (galaxies.getAt(point) == "#") {
                    points.add(point)
                }
            }
        }
        println(points)
        val sum = points.sumOf { point -> points.sumOf { other -> point.dist(other) }} / 2
        println(sum)
    }

    data class Point(val x: Int, val y: Int) {
        fun dist(other: Point): Int = abs(x - other.x) + abs(y - other.y)
    }

    override fun solvePartTwo(lines: List<String>) {
        println("second aa")
    }

    private fun List<List<String>>.print() {
        for (row in this) {
            for (cell in row) {
                print(cell)
            }
            println()
        }
    }

    private fun List<List<String>>.getAt(point: Point): String {
        return this[point.y][point.x]
    }
}
