package year2023

import aoc.IAocTaskKt

class Day10 : IAocTaskKt {

    override fun getFileName(): String = "aoc2023/input_10.txt"
    // 215 too low. 441, 438 wrong, 453 (= grid - left - path), shots: 447, 442
    // possible values: 443, 444, 445, 446, 448, 449, 450, 451, 452
    /*
    left neighs size: 5189
    right neighs size: 441
    grid size: 19600
    path size: 13958
    time 2nd part [s]: 3.287
     */

    override fun solvePartOne(lines: List<String>) {
        val grid: List<List<String>> = lines.map { it.split("").filter(String::isNotEmpty) }
        val start: Point = grid.findStart()

        val mainPipe: List<Point> = grid.getLoopFrom(start)
//        grid.print(mainPipe.toSet())
//        println(mainPipe)
//        println(mainPipe.size)
        println(mainPipe.size / 2)
    }

    override fun solvePartTwo(lines: List<String>) {
        val grid: List<List<String>> = lines.map { it.split("").filter(String::isNotEmpty) }
        val start: Point = grid.findStart()

        val mainPipe: List<Point> = grid.getLoopFrom(start)
        grid.print(mainPipe.toSet())

        println("searching enclosed elements")
        val (leftNeigh, rightNeigh) = grid.findNeighbours(mainPipe)

        grid.print(mainPipe.toSet(), Pair(leftNeigh, rightNeigh), numbers = false)
        val gridSize = grid.size * grid[0].size
        println("${ANSI_RED}left$ANSI_RESET neighs size: " + leftNeigh.size)
        println("${ANSI_BLUE}right$ANSI_RESET neighs size: " + rightNeigh.size)
        println("not assigned: ${gridSize - mainPipe.size - leftNeigh.size - rightNeigh.size}")
        println("grid size: $gridSize")
        println("path size: ${mainPipe.size} (half: ${mainPipe.size / 2}")

        val notAssigned = mutableSetOf<Point>()
        for (y in grid.indices) {
            for (x in grid[0].indices) {
                val point = Point(x, y)
                if (point !in mainPipe && point !in leftNeigh && point !in rightNeigh) {
                    notAssigned += point
                }
            }
        }
        println("not assigned: ${notAssigned.size}: $notAssigned")
    }

    companion object {
        val DX: List<Int> = listOf(1, 0, -1, 0)
        val DY: List<Int> = listOf(0, 1, 0, -1)
        val MOVES: List<Point> = DX.zip(DY).map { (x, y) -> Point(x, y) }

        val NEIGH_X = listOf(-1, 0, 1, -1, 1, -1, 0, 1)
        val NEIGH_Y = listOf(-1, -1, -1, 0, 0, 1, 1, 1)
        val NEIGHS = NEIGH_X.zip(NEIGH_Y).map { (x, y) -> Point(x, y) }
        val NEXT_CLOCKWISE: Map<Point, Point> = mapOf(
            Point(-1, -1) to Point(0, -1),
            Point(0, -1) to Point(1, -1),
            Point(1, -1) to Point(1, 0),
            Point(1, 0) to Point(1, 1),
            Point(1, 1) to Point(0, 1),
            Point(0, 1) to Point(-1, 1),
            Point(-1, 1) to Point(-1, 0),
            Point(-1, 0) to Point(-1, -1)
        )
        val PREV_CLOCKWISE: Map<Point, Point> = mapOf(
            Point(0, -1) to Point(-1, -1),
            Point(-1, -1) to Point(-1, 0),
            Point(-1, 0) to Point(-1, 1),
            Point(-1, 1) to Point(0, 1),
            Point(0, 1) to Point(1, 1),
            Point(1, 1) to Point(1, 0),
            Point(1, 0) to Point(1, -1),
            Point(1, -1) to Point(0, -1),
        )
        private const val ANSI_RESET = "\u001B[0m"
        private const val ANSI_RED = "\u001B[31m"
        private const val ANSI_BLUE = "\u001B[34m"
        private const val ANSI_GREEN = "\u001B[32m"
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

        override fun toString(): String {
            return "($x,$y)"
        }


    }

    private fun List<List<String>>.findStart(): Point {
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
            .filter { !currentPath.contains(it) }
            .firstOrNull { pos ->
                val next = grid.getByPoint(pos)
                val lastPointSymbol = grid.getByPoint(lastPoint)
                when (lastPointSymbol) {
                    "S" -> next in listOf("-", "J", "7") && lastPoint.x + 1 == pos.x
                            || next in listOf("|", "J", "L") && lastPoint.y + 1 == pos.y
                            || next in listOf("-", "F", "L") && lastPoint.x - 1 == pos.x
                            || next in listOf("|", "F", "7") && lastPoint.y - 1 == pos.y

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

    private fun List<List<String>>.findNeighbours(mainPipe: List<Point>): Pair<Set<Point>, Set<Point>> {
        val pipeElements = mainPipe.toSet()
        var rightNeighbours = mutableSetOf<Point>()
        var leftNeighbours = mutableSetOf<Point>()

        for (part in mainPipe.windowed(3)) {
            val (prev, current, next) = part
            val partRightNeighbours = getRightNeighs(prev, current, next, this)
            val partLeftNeighbours = getLeftNeighs(prev, current, next, this)
//            println("marking: ${this.getByPoint(prev)} $prev -> ${this.getByPoint(current)} $current")
//            this.print(listOf(prev, current).toSet(), Pair(partLeftNeighbours, partRightNeighbours))

            rightNeighbours += partRightNeighbours.filter { it !in pipeElements }
            leftNeighbours += partLeftNeighbours.filter { it !in pipeElements }

            if (partLeftNeighbours.any { it in partRightNeighbours }) throw IllegalStateException("some part elements are in both L & R groups: $partLeftNeighbours, $partRightNeighbours")
//            this.print(listOf(prev, current), enclosed)
        }
        var prevEnclosedSize = rightNeighbours.size
        rightNeighbours = rightNeighbours.extend(pipeElements, this)
        while (prevEnclosedSize < rightNeighbours.size) {
            prevEnclosedSize = rightNeighbours.size
            rightNeighbours = rightNeighbours.extend(pipeElements, this)
//            println(rightNeighbours.size)
        }
        prevEnclosedSize = leftNeighbours.size
        leftNeighbours = leftNeighbours.extend(pipeElements, this)
        while (prevEnclosedSize < leftNeighbours.size) {
            prevEnclosedSize = leftNeighbours.size
            leftNeighbours = leftNeighbours.extend(pipeElements, this)
//            println(leftNeighbours.size)
        }

        if (leftNeighbours.any { it in rightNeighbours }) {
            throw IllegalStateException(
                "some elements are in both L & R groups:" +
                        "\n ${leftNeighbours.filter { it in rightNeighbours }}"
            )
        }
        return Pair(leftNeighbours, rightNeighbours)
    }

    private fun getLeftNeighs(
        prev: Point,
        current: Point,
        next: Point,
        grid: List<List<String>>,
    ): Set<Point> {
        val leftNeighs = mutableSetOf<Point>()
        var diff = next - current
        for (i in 1..8) {
            val leftNeigh = current + PREV_CLOCKWISE[diff]!!
//            if (leftNeigh in debugPoints) {
//                println("here")
//                grid.print(setOf(prev, current, next), Pair(emptySet(), setOf(leftNeigh)))
//            }
            diff = PREV_CLOCKWISE[diff]!!
            if (leftNeigh == prev || leftNeigh == next) break

            if (leftNeigh.isInRange(grid)) {
//                println("marked ${grid.getByPoint(leftNeigh)}: $leftNeigh")
                leftNeighs += leftNeigh
//                grid.print(pipeElements, Pair(emptySet(), leftNeighs))
            }
        }
        return leftNeighs
    }

    private fun getRightNeighs(
        prev: Point,
        current: Point,
        next: Point,
        grid: List<List<String>>,
    ): Set<Point> {
        val rightNeighs = mutableSetOf<Point>()
        var diff = next - current
        for (i in 1..8) {
            val rightNeigh = current + NEXT_CLOCKWISE[diff]!!
//            if (rightNeigh in debugPoints) {
//                println("here")
//                grid.print(setOf(prev, current, next), Pair(emptySet(), setOf(rightNeigh)))
//            }

            diff = NEXT_CLOCKWISE[diff]!!
            if (rightNeigh == prev || rightNeigh == next) break

            if (rightNeigh.isInRange(grid)) {
//                println("marked ${grid.getByPoint(rightNeigh)}: $rightNeigh")
                rightNeighs += rightNeigh
//                grid.print(pipeElements, Pair(emptySet(), rightNeighs))
            }

        }
        return rightNeighs
    }

    private fun Set<Point>.extend(pipeElements: Set<Point>, grid: List<List<String>>): MutableSet<Point> {
        val updated = this.toMutableSet()
        for (point in this) {
            val neighbours = NEIGHS.map { it + point }
            updated += neighbours.filter { it !in pipeElements && it.isInRange(grid) }
        }
        return updated
    }

    fun List<List<String>>.print(
        path: Set<Point>,
        leftRightNeighs: Pair<Set<Point>, Set<Point>> = Pair(emptySet(), emptySet()),
        numbers: Boolean = false,
    ) {
        var counter = 0
        val numberedPath = path.map { 'a' + (counter++ % 26) }
        val (leftNeighs, rightNeighs) = leftRightNeighs
        for (y in this.indices) {
            val row = this[y]
            for (x in row.indices) {
                val cell = this[y][x]
                val point = Point(x, y)
                when (point) {
                    in leftNeighs -> print("$ANSI_RED$cell$ANSI_RESET")
                    in rightNeighs -> print("$ANSI_BLUE$cell$ANSI_RESET")
//                    in enclosedInLoop -> print("💎")
                    !in path -> print(cell)
                    in path -> if (numbers) print("$ANSI_GREEN${if (cell != "S") numberedPath[path.indexOf(point)] else "S"}$ANSI_RESET") else print(
                        "$ANSI_GREEN$cell$ANSI_RESET"
                    )
                }
            }
            println()
        }
    }
}
