package year2021

import aoc.IAocTaskKt

class Day09 : IAocTaskKt {
    override fun getFileName(): String = "aoc2021/input_09.txt"

    private val dxs = arrayOf(0, -1, 1, 0)
    private val dys = arrayOf(-1, 0, 0, 1)

    override fun solvePartOne(lines: List<String>) {
        val grid: List<List<Int>> = lines.map {
            it.chunked(1)
                .map(String::toInt)
        }
            .toList()


        var lowPointsSum = 0
        for (x in 0 until grid[0].size) {
            for (y in 0 until grid.size) {
                var allGreater = true
                for (idx in 0..3) {
                    val dx = dxs[idx]
                    val dy = dys[idx]
                    if (isInRange(x + dx, y + dy, grid)) {
                        val point = grid[y][x]
                        val neighbour = grid[y + dy][x + dx]
                        if (neighbour <= point) {
                            allGreater = false
                            break
                        }
                    }
                }
                if (allGreater) {
//                    println(grid[y][x])
                    lowPointsSum += grid[y][x] + 1
                }
            }
        }

        println(lowPointsSum)
    }

    private fun isInRange(x: Int, y: Int, grid: List<List<Int>>) = isInRange(x, grid[0].size) && isInRange(y, grid.size)

    private fun isInRange(pos: Int, size: Int): Boolean = pos in 0 until size

    override fun solvePartTwo(lines: List<String>) {
        val grid: List<List<Int>> = lines.map {
            it.chunked(1)
                .map(String::toInt)
        }.toList()
        val groups: List<MutableList<Int>> = lines.map {
            it.chunked(1)
                .map { 0 }
                .toMutableList()
        }.toList()

        var groupId = 1
        while (!onlyNinesAreNotInGroup(grid, groups)) {
            val (startX, startY) = findNotVisited(grid, groups)
            visit(startX, startY, groupId, grid, groups)
            groupId++
        }
//        display(groups)
        println(groups.flatten().sorted().filter { it != 0 }
            .groupingBy { it }
            .eachCount()
            .values
            .sorted()
            .reversed()
            .toList()
            .subList(0, 3)
            .reduce { x, y -> x * y }
        )

    }

    @Suppress("unused")
    private fun display(groups: List<MutableList<Int>>) {
        for (element in groups) {
            for (x in 0 until groups[0].size) {
                print(element[x])
            }
            println()
        }
    }

    private fun visit(startX: Int, startY: Int, groupId: Int, grid: List<List<Int>>, groups: List<MutableList<Int>>) {
        groups[startY][startX] = groupId
        for (idx in 0..3) {
            val nextX = startX + dxs[idx]
            val nextY = startY + dys[idx]
            if (isInRange(nextX, grid[0].size) && isInRange(
                    nextY,
                    grid.size
                ) && groups[nextY][nextX] == 0 && grid[nextY][nextX] != 9
            ) {
                visit(nextX, nextY, groupId, grid, groups)
            }
        }
    }

    private fun findNotVisited(grid: List<List<Int>>, groups: List<List<Int>>): Pair<Int, Int> {
        for (x in 0 until grid[0].size) {
            for (y in grid.indices) {
                if (grid[y][x] != 9 && groups[y][x] == 0) {
                    return Pair(x, y)
                }
            }
        }
        return Pair(-1, -1)
    }

    private fun onlyNinesAreNotInGroup(grid: List<List<Int>>, groups: List<List<Int>>): Boolean {
        for (x in 0 until grid[0].size) {
            for (y in grid.indices) {
                if (grid[y][x] != 9 && groups[y][x] == 0) {
                    return false
                }
            }
        }
        return true
    }
}
