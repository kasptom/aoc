package year2023

import aoc.IAocTaskKt

class Day17 : IAocTaskKt {
    // Clumsy Crucible
    override fun getFileName(): String = "aoc2023/input_17.txt"

    override fun solvePartOne(lines: List<String>) {
        val grid = Grid.create(lines)
//        println(grid.print())
        grid.traverse()
//        println(grid.printBestPath())
        println(grid.costFromStart
            .filter { (key, _) ->  key.pos == grid.endPosition()}
            .minOf { (_ ,v) -> v }
        )
    }

    override fun solvePartTwo(lines: List<String>) {
        val grid = Grid.create(lines)
//        println(grid.print())
        grid.traverse2()
//        println(grid.printBestPath())
        println(grid.costFromStart
            .filter { (key, _) ->  key.pos == grid.endPosition()}
            .minOf { (_,v) -> v }
        )
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
        var leastCost = 1000
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
                val nextCursors = startCursor.generateNext(map)
                for (next in nextCursors) {
                    traverse(startCursor, next, path + next)
                }
            }
        }

        fun traverse2() {
            val start = Point(0, 0)
            val possibleCursors = Direction.values()
                .map { Cursor(start, it, map.valueAt(start)) }

            for (startCursor in possibleCursors) {
                val path = listOf(startCursor)
                costFromStart[startCursor] = 0
                val nextCursors = startCursor.generateNext2(map)
                for (next in nextCursors) {
                    traverse2(startCursor, next, path + next)
                }
            }
        }


        fun endPosition(): Point = Point(map[0].size - 1, map.size - 1)

        fun traverse(prev: Cursor, cursor: Cursor, path: List<Cursor>): Int {
            if (path.count { it.pos == cursor.pos } > 1 || costFromStart[prev]!! > leastCost) {
//                println("repeated point")
                return Int.MAX_VALUE
            }
            if (cursor.pos == endPosition()) {
                val heatLoss = map.valueAt(cursor.pos)
                val currCost = costFromStart.getOrDefault(prev, 0) + cursor.heatLossAtCell

                if (leastCost > currCost) {
                    leastCost = currCost
                    bestPath = path
                }
//                println("end position reached with heat loss: $heatLoss cost $currCost $cursor")
                costFromStart[cursor] = currCost
                return heatLoss
            }
            val nextCursors: List<Cursor> = cursor.generateNext(map)
            var bestResult = Int.MAX_VALUE
            for (next in nextCursors) {
                val costToNext = costFromStart.getOrDefault(next, Int.MAX_VALUE)
                val currentCostToNext = costFromStart[prev]!! + cursor.heatLossAtCell + next.heatLossAtCell
                if (costToNext > currentCostToNext) {
                    costFromStart[next] = currentCostToNext
                    costFromStart[cursor] = costFromStart[prev]!! + cursor.heatLossAtCell
                    val result = traverse(cursor, next, path + next)
                    if (result < bestResult) {
                        bestResult = result
                    }
                }
            }
            return bestResult
        }

        fun traverse2(prev: Cursor, cursor: Cursor, path: List<Cursor>): Int {
            if (path.count { it == cursor } > 1 || costFromStart[prev]!! > leastCost) {
//                println("repeated point")
                return Int.MAX_VALUE
            }
            if (cursor.pos == endPosition()) {
                val currCost = costFromStart.getOrDefault(prev, 0) + cursor.heatLossAtCell

                if (leastCost > currCost && cursor.directionCount >= 4) {
                    leastCost = currCost
                    bestPath = path
//                    println("end position reached with heat loss: $heatLoss cost $currCost $cursor")
                    costFromStart[cursor] = currCost
                }
                return currCost
            }
            val nextCursors: List<Cursor> = cursor.generateNext2(map)
            var bestResult = Int.MAX_VALUE
            for (next in nextCursors) {
                val costToNext = costFromStart.getOrDefault(next, Int.MAX_VALUE)
                val currentCostToNext = costFromStart[prev]!! + cursor.heatLossAtCell + next.heatLossAtCell
                if (costToNext > currentCostToNext) {
                    costFromStart[cursor] = costFromStart[prev]!! + cursor.heatLossAtCell
                    val result = traverse2(cursor, next, path + next)
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

        fun generateNext2(map: List<List<Int>>): List<Cursor> {
            val nextMoves = Direction.values().map { move2(it, map) }
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

        private fun move2(newDir: Direction, map: List<List<Int>>): Cursor {
            if (newDir == dir.opposite()) {
                return Cursor(pos, newDir, Int.MAX_VALUE, 100, isValid = false)
            }

            val nextPos = pos + newDir
            if (!nextPos.isInRange(map)) {
                return Cursor(nextPos, newDir, Int.MAX_VALUE, 100, isValid = false)
            }
            val nextHeatLoss = map.valueAt(nextPos)
            val nextDirectionCount = if (dir == newDir) directionCount + 1 else 1

            if (nextDirectionCount > 10) {
                return Cursor(nextPos, newDir, Int.MAX_VALUE, 100, isValid = false)
            }
            if (directionCount < 4 && nextDirectionCount < 4 && dir != newDir) {
                return Cursor(nextPos, newDir, Int.MAX_VALUE, 100, isValid = false)
            }

            return Cursor(nextPos, newDir, nextHeatLoss, nextDirectionCount)
        }

        override fun toString(): String = "($pos, $dir ($directionCount), H=$heatLossAtCell)"
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Cursor) return false

            if (pos != other.pos) return false
            if (dir != other.dir) return false
            if (directionCount != other.directionCount) return false

            return true
        }

        override fun hashCode(): Int {
            var result = pos.hashCode()
            result = 31 * result + dir.hashCode()
            result = 31 * result + directionCount
            return result
        }
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
