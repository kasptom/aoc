package year2024

import aoc.IAocTaskKt
import year2024.Day06.Companion.DX
import year2024.Day06.Companion.DY
import year2024.Day06.Companion.IDX_TO_DIR
import year2024.Day06.Companion.STATES

class Day06 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_06.txt"

    override fun solvePartOne(lines: List<String>) {
        val grid: Array<CharArray> = lines.map { it.toCharArray() }.toTypedArray()
        val pos = grid.findPosition()
        println(pos)
        val guard = Guard(pos, DIR_TO_IDX[grid.valueAt(pos)]!!)
        grid.setValue(pos, '.')

        while (guard.pos.isInRange(grid)) {
//            println(guard.pos)
            guard.move(grid)
        }
        println(guard.visited.size)
    }

    override fun solvePartTwo(lines: List<String>) {
        val grid: Array<CharArray> = lines.map { it.toCharArray() }.toTypedArray()
        val pos = grid.findPosition()
        val guard = Guard(pos, DIR_TO_IDX[grid.valueAt(pos)]!!)
        grid.setValue(pos, '.')

        var places = 0
        for (x in grid[0].indices) {
            for (y in grid.indices) {
                val obstructionPos = Point(x, y)
                if (grid.valueAt(obstructionPos) == '.' && obstructionPos != guard.initialPos) {
                    grid.setValue(obstructionPos, 'O')
                } else {
                    continue
                }
                guard.reset()
                while (guard.pos.isInRange(grid)) {
                    guard.move(grid)
//                    grid.printState(guard)
                    if (guard.isStuck()) {
                        places++
                        break
                    }
                }
                grid.setValue(obstructionPos, '.')
            }
        }
        println(places)
    }

    companion object {
        val DX = arrayOf(0, 1, 0, -1)
        val DY = arrayOf(-1, 0, 1, 0)
        val DIR_TO_IDX = mapOf('^' to 0, '>' to 1, 'v' to 2, '<' to 3)
        val IDX_TO_DIR = mapOf(0 to '^', 1 to '>', 2 to 'v', 3 to '<')
        val STATES = setOf('^', '>', 'v', '<')
    }

}

private fun Array<CharArray>.printState(guard: Guard) {
    println("-----")
    for (y in this.indices) {
        for (x in this.indices) {
            val point = Point(x, y)
            if (guard.pos == point) {
                print(IDX_TO_DIR[guard.dirIdx])
            } else if (point in guard.visited) {
                print("x")
            } else {
                print(this.valueAt(point))
            }
        }
        println()
    }
}

private fun Array<CharArray>.setValue(pos: Point, c: Char) {
    this[pos.y][pos.x] = c
}

private fun Array<CharArray>.valueAt(pos: Point): Char {
    return this[pos.y][pos.x]
}

data class Point(val x: Int, val y: Int) {
    fun isInRange(grid: Array<CharArray>): Boolean = x >= 0 && x < grid[0].size && y >= 0 && y < grid.size
    fun next(dirIdx: Int): Point {
        val dx = DX[dirIdx]
        val dy = DY[dirIdx]
        val next = Point(x + dx, y + dy)
        return next
    }
}

data class Guard(var pos: Point, var dirIdx: Int, val visited: MutableSet<Point> = mutableSetOf(pos)) {
    var obstacleSeenCount = mutableMapOf<Point, Int>()
    val initialPos = pos
    private val initialDir = dirIdx

    fun move(grid: Array<CharArray>) {
        val nextPos = pos.next(dirIdx)
        if (nextPos.isInRange(grid).not()) {
            pos = nextPos
        } else if (grid.valueAt(nextPos) == '.') {
            visited.add(nextPos)
            pos = nextPos
        } else if (grid.valueAt(nextPos) == '#' || grid.valueAt(nextPos) == 'O') {
            obstacleSeenCount.putIfAbsent(nextPos, 0)
            obstacleSeenCount[nextPos] = obstacleSeenCount[nextPos]!! + 1
            dirIdx += 1
            dirIdx %= 4
        } else {
            throw IllegalStateException("unknown state")
        }
    }

    fun reset() {
        visited.clear()
        visited.add(initialPos)
        dirIdx = initialDir
        pos = initialPos
        obstacleSeenCount.clear()
    }

    fun isStuck(): Boolean = obstacleSeenCount.values.any { it > 4 }
}

private fun Array<CharArray>.findPosition(): Point {
    for (y in this.indices) {
        for (x in this[0].indices) {
            if (this[y][x] in STATES) {
                return Point(x, y)
            }
        }
    }
    return Point(-1, -1)
}
