package year2021

import aoc.IAocTaskKt
import kotlin.math.max

class Day13 : IAocTaskKt {
    var gridWidth = 0
    var gridHeight = 0

    override fun getFileName(): String = "aoc2021/input_13.txt"

    override fun solvePartOne(lines: List<String>) {
        val splitLineIdx = lines.indexOfFirst { it.startsWith("fold") }
        val points = lines.subList(0, splitLineIdx - 1)
            .map(Point::parse)
        gridHeight = points.maxOf { it.y } + 1
        gridWidth = points.maxOf { it.x } + 1

        println("H=$gridHeight, W=$gridWidth")

        val grid = Array(gridHeight) { IntArray(gridWidth) { 0 } }

        for (point in points) {
            grid[point.y][point.x] = 1
        }
//        println("initial grid")
//        display(grid)
        println()

        val folds: List<Pair<String, Int>> = lines.subList(splitLineIdx, lines.size)
            .map { line ->
                Pair(
                    if (line.contains("x")) "x" else "y", line
                        .replace("fold along y=", "")
                        .replace("fold along x=", "").toInt()
                )
            }

        val firstFold = folds[0]

        println(points)
        for (fold in folds) {
            val newGrid = if (fold.first == "x") foldX(points, grid, fold.second)
            else foldY(points, grid, firstFold.second)

//            display(grid)
            println()
            val sum = newGrid.toList()
                .flatMap { it.toList() }
                .count { it != 0 }
            println(sum)
            break
        }

    }

    private fun foldX(points: List<Point>, grid: Array<IntArray>, lineIdx: Int): Array<IntArray> {
//        println("folding along x=$lineIdx")
        val newGridWidth = lineIdx
        val newGridHeight = grid.size
        val newGrid = Array(newGridHeight) { IntArray(newGridWidth) { 0 } }
        for (point in points) {
            if (point.x < lineIdx) {
                newGrid[point.y][point.x] = max(newGrid[point.y][point.x], grid[point.y][point.x])
            } else {
                newGrid[point.y][2 * lineIdx - point.x] =
                    max(grid[point.y][point.x], newGrid[point.y][2 * lineIdx - point.x])
            }
        }
        gridWidth = newGridWidth
        gridHeight = newGridHeight
        return newGrid
    }

    private fun foldY(points: List<Point>, grid: Array<IntArray>, lineIdx: Int): Array<IntArray> {
//        println("folding along y=$lineIdx")
        val newGridWidth = grid[0].size
        val newGridHeight = lineIdx
        val newGrid = Array(newGridHeight) { IntArray(newGridWidth) { 0 } }

        for (point in points) {
            if (point.y < lineIdx) {
                newGrid[point.y][point.x] = max(newGrid[point.y][point.x], grid[point.y][point.x])
            } else {
                newGrid[2 * lineIdx - point.y][point.x] =
                    max(grid[point.y][point.x], newGrid[2 * lineIdx - point.y][point.x])
            }
        }
        gridWidth = newGridWidth
        gridHeight = newGridHeight
        return newGrid
    }

    override fun solvePartTwo(lines: List<String>) {
        val splitLineIdx = lines.indexOfFirst { it.startsWith("fold") }
        var points = lines.subList(0, splitLineIdx - 1)
            .map(Point::parse)
        gridHeight = points.maxOf { it.y } + 1
        gridWidth = points.maxOf { it.x } + 1

        println("H=$gridHeight, W=$gridWidth")

        var grid = Array(gridHeight) { IntArray(gridWidth) { 0 } }

        for (point in points) {
            grid[point.y][point.x] = 1
        }
//        println("initial grid")
//        display(grid)
//        println()

        val folds: List<Pair<String, Int>> = lines.subList(splitLineIdx, lines.size)
            .map { line ->
                Pair(
                    if (line.contains("x")) "x" else "y", line
                        .replace("fold along y=", "")
                        .replace("fold along x=", "").toInt()
                )
            }

        println(points)
        for (fold in folds) {
            val newGrid = if (fold.first == "x") foldX(points, grid, fold.second)
            else foldY(points, grid, fold.second)
            grid = newGrid
            points = mutableListOf()

            for (y in 0 until gridHeight) {
                for (x in 0 until gridWidth) {
                    if (grid[y][x] != 0) points.add(Point(x, y))
                }
            }

//            display(grid)
//            println()
        }
        display(grid)
    }

    @Suppress("unused")
    private fun display(grid: Array<IntArray>) {
        for (row in grid) {
            for (x in 0 until grid[0].size) {
                print(if (row[x] == 0) "." else "#")
            }
            println()
        }
        println()
    }

    data class Point(val x: Int, val y: Int) {
        companion object {
            fun parse(coords: String): Point {
                val (x, y) = coords.split(",").map(String::trim).map(String::toInt)
                return Point(x, y)
            }
        }
    }
}