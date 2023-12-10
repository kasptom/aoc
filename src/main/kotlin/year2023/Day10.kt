package year2023

import aoc.IAocTaskKt

class Day10 : IAocTaskKt {

    override fun getFileName(): String = "aoc2023/input_10.txt"
    // 215 too low

    override fun solvePartOne(lines: List<String>) {
        val grid: List<List<String>> = lines.map { it.split("").filter(String::isNotEmpty) }
        val start: Point = grid.findStart()
        println(start)
        grid.print()
        println("------")

        val mainPipe: List<Point> = grid.getLoopFrom(start)
        grid.print(mainPipe)
        println(mainPipe)
        println(mainPipe.size)
        println(mainPipe.size / 2)
    }

    override fun solvePartTwo(lines: List<String>) {
        val grid: List<List<String>> = lines.map { it.split("").filter(String::isNotEmpty) }
        val start: Point = grid.findStart()
        println(start)
        grid.print()
        println("------")

        val mainPipe: List<Point> = grid.getLoopFrom(start)
        grid.print(mainPipe)

        println("searching enclosed elements")
//        val enclosed = grid.findEnclosedIn(mainPipe)
//
//        grid.print(mainPipe, enclosed)
//        println(enclosed.size)
    }

    companion object {
        val DX: List<Int> = listOf(1, -1, 0, 0)
        val DY: List<Int> = listOf(0, 0, -1, 1)
        val MOVES: List<Point> = DX.zip(DY).map { (x, y) -> Point(x, y) }

        //        val NEIGH_X = listOf(-1, 0, 1, 1, 1, 0, -1, -1)
//        val NEIGH_Y = listOf(-1, -1, -1, 0, 1, 1, 1, 0)
//        val NEIGHS_CLOCKWISE = NEIGH_X.zip(NEIGH_Y).map { (x, y) -> Point(x, y) }
        val VEC_TO_PLUS_90: Map<Point, Point> = mapOf(
            Point(0, -1) to Point(1, 0),
            Point(1, 0) to Point(0, 1),
            Point(0, 1) to Point(-1, 0),
            Point(-1, 0) to Point(0, -1)
        )
        private const val ANSI_RESET = "\u001B[0m"
        private const val ANSI_GREEN = "\u001B[32m"
        private const val ANSI_PURPLE = "\u001B[35m"
    }

    data class Point(val x: Int, val y: Int) {
        operator fun plus(other: Point): Point {
            return Point(x + other.x, y + other.y)
        }

        operator fun minus(other: Point): Point {
            return Point(x - other.x, y - other.y)
        }

        fun isInRange(grid: List<List<String>>): Boolean {
            val height = grid.size
            val width = grid[0].size
            return x >= 0 && y >= 0 && x < width && y < height
        }
    }

    fun List<List<String>>.findStart(): Point {
        val y = this.indexOfFirst { it.contains("S") }
        val x = this[y].indexOfFirst { it == "S" }
        return Point(x, y)
    }

    fun List<List<String>>.print(marked: Point? = null) {
        for (y in this.indices) {
            val row = this[y]
            for (x in row.indices) {
                val cell = this[y][x]
                if (marked != Point(x, y)) {
                    print(cell)
                } else {
//                    print("$ANSI_GREEN$cell$ANSI_RESET")
                    print("❌")
                }
            }
            println()
        }
    }

    private fun List<List<String>>.getLoopFrom(start: Point): List<Point> {
        val currentPath = listOf(start).toMutableList()
        findNextPoint(this, currentPath)
        return currentPath
    }

    private fun List<List<String>>.getByPoint(point: Point): String {
        return this[point.y][point.x]
    }

    private fun findNextPoint(grid: List<List<String>>, currentPath: MutableList<Point>) {
        val lastPoint = currentPath.last()
        val nextMove = MOVES
            .asSequence()
            .map { lastPoint + it }
            .filter { it.isInRange(grid) }
            .filter { grid.getByPoint(it) != "." }
//            .filter { currentPath.size == 1 || currentPath[currentPath.size - 2] != it }
            .filter { !currentPath.contains(it) }
            .firstOrNull { pos ->
                val next = grid.getByPoint(pos)
                val lastPointSymbol = grid.getByPoint(lastPoint)
                when (lastPointSymbol) {
                    "S" -> next in listOf("-", "J", "7") && lastPoint.x + 1 == pos.x
                            || next in listOf("-", "F", "L") && lastPoint.x - 1 == pos.x
                            || next in listOf("|", "F", "7") && lastPoint.y - 1 == pos.y
                            || next in listOf("|", "J", "L") && lastPoint.y + 1 == pos.y
                    "-" -> next in listOf("-", "L", "F") && lastPoint.x - 1 == pos.x
                            || next in listOf("-", "J", "7") && lastPoint.x + 1 == pos.x
                    "|" -> next in listOf("|", "L", "J") && lastPoint.y + 1 == pos.y
                            || next in listOf("|", "F", "7") && lastPoint.y - 1 == pos.y

                    "J" -> next in listOf("-", "F", "L") && lastPoint.x - 1 == pos.x ||
                            next in listOf("|", "F", "7") && lastPoint.y - 1 == pos.y

                    "L" -> next in listOf("-", "J", "7") && lastPoint.x + 1 == pos.x ||
                            next in listOf("|", "F", "7") && lastPoint.y - 1 == pos.y

                    "F" -> next in listOf("-", "J", "7") && lastPoint.x + 1 == pos.x ||
                            next in listOf("|", "J", "L") && lastPoint.y + 1 == pos.y

                    "7" -> next in listOf("-", "L", "F") && lastPoint.x - 1 == pos.x ||
                            next in listOf("|", "J", "L") && lastPoint.y + 1 == pos.y

                    else -> {
                        grid.print(pos)
                        throw IllegalStateException("$lastPoint --> $lastPointSymbol")
                    }
                }
            }

        if (nextMove == null || grid.getByPoint(nextMove) == "S") {
            return
        }
        if (currentPath.contains(nextMove)) throw IllegalStateException("path cannot contain the next move")
        currentPath += nextMove
//        println("NEXT: $nextMove, pathSize: ${currentPath.size}")
//        grid.print(nextMove)
        findNextPoint(grid, currentPath)
    }

    private fun List<List<String>>.findEnclosedIn(mainPipe: List<Point>): Set<Point> {
        val pipeElements = mainPipe.toSet()
        val enclosed = mutableSetOf<Point>()
        for (part in mainPipe.windowed(2)) {
            val (prev, current) = part
            val diff = current - prev
            val neighbour = prev + VEC_TO_PLUS_90[diff]!!
//            println("marking: ${this.getByPoint(prev)} $prev -> ${this.getByPoint(current)} $current, diff: $diff")
//            this.print(listOf(prev, current), enclosed)

            if (neighbour !in pipeElements && neighbour.isInRange(this)) {
//                println("marked ${this.getByPoint(neighbour)}: $neighbour")
                enclosed += neighbour
            }
//            this.print(listOf(prev, current), enclosed)
        }
        return enclosed
    }

    fun List<List<String>>.print(path: List<Point>, enclosedInLoop: Set<Point> = emptySet()) {
        for (y in this.indices) {
            val row = this[y]
            for (x in row.indices) {
                val cell = this[y][x]
                val point = Point(x, y)
                when (point) {
                    in enclosedInLoop -> print("$ANSI_PURPLE$cell$ANSI_RESET")
                    !in path -> print(cell)
                    in path -> print("$ANSI_GREEN$cell$ANSI_RESET")
                }
            }
            println()
        }
    }
}
