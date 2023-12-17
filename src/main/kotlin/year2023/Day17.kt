package year2023

import aoc.IAocTaskKt

class Day17 : IAocTaskKt {
    // Clumsy Crucible
    override fun getFileName(): String = "aoc2023/input_17.txt" // 777 too high

    override fun solvePartOne(lines: List<String>) {
        val grid = Grid.create(lines)
        println(grid.print())
        grid.traverse()
        println(grid.printBestPath())
        println(grid.costFromStart
            .filter { (key, value) ->  key.pos == grid.endPosition()}
            .minOf { (k ,v) -> v } - grid.map.valueAt(Point(0, 0))
        )
    }

    override fun solvePartTwo(lines: List<String>) {
        println("Not yet implemented")
    }

    data class Point(val x: Int, val y: Int) {
        fun isInRange(map: List<List<Int>>): Boolean = x >= 0 && y >= 0 && x < map[0].size && y < map.size
        operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)
        operator fun plus(dir: Direction): Point {
            return when (dir) {
                Direction.UP -> this + Point(0, -1)
                Direction.DOWN -> this + Point(0, 1)
                Direction.LEFT -> this + Point(-1, 0)
                Direction.RIGHT -> this + Point(1, 0)
            }
        }

        override fun toString(): String = "($x, $y)"
    }

    data class Grid(val map: List<List<Int>>) {
        val costFromStart: MutableMap<Cursor, Int> = mutableMapOf()
        var leastCost = Int.MAX_VALUE
        var bestPath: List<Cursor> = emptyList()

        fun print(): String = map.joinToString("\n") { it.joinToString("") }
        fun printBestPath() = map
            .mapIndexed { yIdx, row ->
                row
                    .mapIndexed { xIdx, col -> if (bestPath.any { it.pos == Point(xIdx, yIdx) }) "#" else col }
            }
            .joinToString("\n") { it.joinToString("") }


        fun traverse() {
            val start = Point(0, 0)
            val possibleCursors = Direction.values()
                .map { Cursor(start, it, map.valueAt(start)) }

            for (startCursor in possibleCursors) {
                val path = listOf(startCursor)
                costFromStart[startCursor] = 0
                traverse(startCursor, path)
            }
        }

        fun endPosition(): Point = Point(map[0].size - 1, map.size - 1)

        fun traverse(cursor: Cursor, path: List<Cursor>): Int {
            if (path.count { it == cursor } > 1) {
//                println("repeated point")
                return Int.MAX_VALUE
            }
            if (cursor.pos == endPosition()) {
                val heatLoss = map.valueAt(cursor.pos)
                val currCost = path.sumOf{map.valueAt(it.pos)}

                if (leastCost > currCost) {
                    leastCost = currCost
                    bestPath = path
                }
                println("end position reached with heat loss: $heatLoss cost $currCost $cursor")
                costFromStart[cursor] = currCost
                return heatLoss
            }
            val nextCursors: List<Cursor> = cursor.generateNext(map)
            var bestResult = Int.MAX_VALUE
            for (next in nextCursors) {
                val costToNext = costFromStart.getOrDefault(next, Int.MAX_VALUE)
                val currentCostToNext = costFromStart[cursor]!! + next.heatLossAtCell
                if (costToNext > currentCostToNext) {
                    costFromStart[next] = currentCostToNext
                    val result = traverse(next, path + next)
                    if (result < bestResult) {
                        bestResult = result
                    }
                }
            }
            return bestResult
        }

        companion object {
            fun create(lines: List<String>): Grid {
                val map = lines.map { it.split("").filter(String::isNotEmpty).map(String::toInt) }
                return Grid(map)
            }
        }
    }

    data class Cursor(
        val pos: Point,
        val dir: Direction,
        val heatLossAtCell: Int,
        val directionCount: Int = 1,
        val isValid: Boolean = true,
    ) {
        fun generateNext(map: List<List<Int>>): List<Cursor> {
            val nextMoves = Direction.values().map { move(it, map) }
            return nextMoves.filter { it.isValid }
        }

        private fun move(newDir: Direction, map: List<List<Int>>): Cursor {
            if (newDir == dir.opposite()) {
                return Cursor(pos, newDir, Int.MAX_VALUE, 100, isValid = false)
            }

            val nextPos = pos + newDir
            if (!nextPos.isInRange(map)) {
                return Cursor(nextPos, newDir, Int.MAX_VALUE, 100, isValid = false)
            }
            val nextHeatLoss = map.valueAt(nextPos)
            val nextDirectionCount = if (dir == newDir) directionCount + 1 else 1

            if (nextDirectionCount > 3) {
                return Cursor(nextPos, newDir, Int.MAX_VALUE, 100, isValid = false)
            }
            return Cursor(nextPos, newDir, nextHeatLoss, nextDirectionCount)
        }

        override fun toString(): String = "($pos, $dir ($directionCount), H=$heatLossAtCell)"
    }

    enum class Direction(val dir: String) {
        UP("^"), DOWN("v"), LEFT("<"), RIGHT(">");

        fun opposite(): Direction = when (this) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT
        }
    }
}

fun List<List<Int>>.valueAt(pos: Day17.Point): Int = this[pos.y][pos.x]
