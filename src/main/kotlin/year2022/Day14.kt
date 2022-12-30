package year2022

import aoc.IAocTaskKt
import year2022.Day14.Grid.GridCellValue.*
import year2022.Day14.Point
import java.lang.Integer.max
import java.lang.Integer.min

val SAND_SOURCE_POINT = Point(500, 0)
val SAND_MOVES = listOf(
    Point(0, 1), Point(-1, 1), Point(1, 1)
)

class Day14 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_14.txt"

    override fun solvePartOne(lines: List<String>) {
        val parsedLines = lines
            .map { Line.parse(it) }
            .flatten()
//        println(parsedLines)
        val grid = Grid.create(parsedLines)
        grid.drawLines(parsedLines)
//        println(grid.print())

        var fallingSandPosition = SAND_SOURCE_POINT
        while (grid.sandDidNotFallOut(fallingSandPosition)) {
            fallingSandPosition = grid.step(fallingSandPosition)
        }
        println(grid.countSandUnits())
    }

    override fun solvePartTwo(lines: List<String>) {
        val parsedLines = lines
            .map { Line.parse(it) }
            .flatten()
//        println(parsedLines)
        val oldGrid = Grid.create(parsedLines)

        val bottomLine = Line(
            from = Point(oldGrid.minCell.x - 1000, oldGrid.maxCell.y + 1),
            to = Point(oldGrid.maxCell.x + 1000, oldGrid.maxCell.y + 1)
        )

        val newLines = parsedLines + listOf(bottomLine)
        val grid = Grid.create(newLines)
        grid.drawLines(newLines)
//        println(grid.print())

        var fallingSandPosition = SAND_SOURCE_POINT

        while (grid.getCellAt(SAND_SOURCE_POINT).value != SAND) {
            fallingSandPosition = grid.step(fallingSandPosition)
        }
//        println(grid.print())
        println(grid.countSandUnits())
    }

    data class Line(val from: Point, val to: Point) {
        fun isVertical(): Boolean = from.x == to.x

        companion object {
            fun parse(linesRow: String): List<Line> {
                return linesRow
                    .split(" -> ")
                    .windowed(2)
                    .map { parseSingleLine(it) }
            }

            private fun parseSingleLine(fromToPairs: List<String>): Line {
//                println(fromToPairs)
                val (fromPoint, toPoint) = fromToPairs.map { fromToPair ->
//                    println("fromToPair: $fromToPair")
                    val (from, to) = fromToPair.split(",")
                        .map { it.toInt() }
                        .zipWithNext()
                        .single()
                    Point(from, to)
                }.zipWithNext()
                    .single()

                return Line(fromPoint, toPoint)
            }
        }

        override fun toString(): String = "$from -> $to"
        fun minX(): Int = min(from.x, to.x)
        fun maxX(): Int = max(from.x, to.x)
        fun minY(): Int = min(from.y, to.y)
        fun maxY(): Int = max(from.y, to.y)
    }

    data class Point(val x: Int, val y: Int) {
        override fun toString(): String = "($x, $y)"
        operator fun plus(move: Point): Point {
            return Point(x + move.x, y + move.y)
        }
    }

    data class Grid(val grid: List<List<GridCell>>, val minCell: Point, val maxCell: Point) {
        fun print(): Any {
//            println("min cell: $minCell, max cell: $maxCell")
            return grid.joinToString("") {
                it.joinToString("") { cell ->
                    if (cell.position == SAND_SOURCE_POINT) SAND_SOURCE.code
                    else cell.value.code
                } + "\n"
            }
        }

        fun drawLines(lines: List<Line>) {
            for (line in lines) {
                if (line.isVertical()) {
                    drawVerticalLine(line)
                } else {
                    drawHorizontalLine(line)
                }
            }
        }

        private fun drawHorizontalLine(line: Line) {
            val y = line.from.y
            for (x in line.minX()..line.maxX()) {
                val gridCell: GridCell = getCellAt(Point(x, y))
                gridCell.value = ROCK
            }
        }

        fun getCellAt(point: Point): GridCell = grid[point.y - minCell.y][point.x - minCell.x]

        private fun drawVerticalLine(line: Line) {
            val x = line.from.x
            for (y in line.minY()..line.maxY()) {
                val gridCell: GridCell = getCellAt(Point(x, y))
                gridCell.value = ROCK
            }
        }

        fun sandDidNotFallOut(fallingSandPosition: Point): Boolean {
            return fallingSandPosition.y < maxCell.y
        }
        fun countSandUnits(): Int = grid.flatten()
            .count { it.value == SAND }

        fun step(fallingSandPosition: Point): Point {
            val nextPosition = findNextPosition(fallingSandPosition)
            return if (nextPosition != null) {
                nextPosition
            } else {
                getCellAt(fallingSandPosition).value = SAND
                SAND_SOURCE_POINT
            }
        }

        private fun findNextPosition(fallingSandPosition: Point): Point? {
            for (move in SAND_MOVES) {
                val nextPosition = fallingSandPosition + move
                if (getCellAt(nextPosition).value != AIR) {
                    continue
                } else return nextPosition
            }
            return null
        }

        companion object {
            fun create(lines: List<Line>): Grid {
                val maxX = (lines
                    .flatMap { listOf(it.from, it.to) } + SAND_SOURCE_POINT)
                    .maxOf { it.x } + 1 // 1 cell for the margin
                val maxY = (lines
                    .flatMap { listOf(it.from, it.to) } + SAND_SOURCE_POINT)
                    .maxOf { it.y } + 1

                val minX = (lines
                    .flatMap { listOf(it.from, it.to) } + SAND_SOURCE_POINT)
                    .minOf { it.x } - 1 // 1 cell for the margin
                val minY = (lines
                    .flatMap { listOf(it.from, it.to) } + SAND_SOURCE_POINT)
                    .minOf { it.y } - 1

                val grid = (minY..maxY).map { y ->
                    (minX..maxX).map { x -> GridCell(position = Point(x, y), value = AIR) }
                }
                return Grid(grid = grid, minCell = Point(minX, minY), maxCell = Point(maxX, maxY))
            }
        }

        data class GridCell(val position: Point, var value: GridCellValue)

        enum class GridCellValue(val code: String) {
            ROCK("#"), AIR("."), SAND("o"), SAND_SOURCE("+")
        }
    }
}