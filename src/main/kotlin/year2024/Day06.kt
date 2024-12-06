package year2024

import aoc.IAocTaskKt
import year2024.Day06.Companion.DX
import year2024.Day06.Companion.DY
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
        if (lines.isEmpty()) println("empty lines") else println(lines.size)
    }

    companion object {
        val DX = arrayOf(0, 1, 0, -1)
        val DY = arrayOf(-1, 0, 1, 0)
        val DIR_TO_IDX = mapOf('^' to 0, '>' to 1, 'v' to 2, '<' to 3)
        val STATES = setOf('^', '>', 'v', '<')
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
    fun move(grid: Array<CharArray>): Point {
        val nextPos = pos.next(dirIdx)

        if (nextPos.isInRange(grid).not()) {
            this.pos = nextPos
            return pos
        }

        if (grid.valueAt(nextPos) == '.') {
            visited.add(nextPos)
            this.pos = nextPos
            return pos
        }

        if (grid.valueAt(nextPos) == '#') {
            dirIdx += 1
            dirIdx %= 4
            pos
            return pos
        }
        throw IllegalStateException("unknown state")
    }

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
