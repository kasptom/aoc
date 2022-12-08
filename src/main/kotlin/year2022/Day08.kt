package year2022

import aoc.IAocTaskKt

class Day08 : IAocTaskKt {
    private val dxs = arrayOf(0, -1, 1, 0)
    private val dys = arrayOf(-1, 0, 0, 1)

    override fun getFileName(): String = "aoc2022/input_08.txt"

    override fun solvePartOne(lines: List<String>) {
        val trees: Array<IntArray> = toTreesGrid(lines)
//        debugPrint(trees)

        var counter = 0

        for (x in 1 until trees[0].size - 1) {
            for (y in 1 until trees.size - 1) {
                if (isVisibleFromOutside(trees, x, y)) {
                    counter++
                }
            }
        }
        println(counter + trees.size * 2 + trees[0].size * 2 - 4)
    }

    @Suppress("unused")
    private fun debugPrint(trees: Array<IntArray>) {
        for (y in 1 until trees.size - 1) {
            for (x in 1 until trees[0].size - 1) {
                print(trees[y][x])
            }
            println()
        }
    }

    private fun isVisibleFromOutside(trees: Array<IntArray>, x: Int, y: Int): Boolean {
        for (idx in dxs.indices) {
            val dx = dxs[idx]
            val dy = dys[idx]
            if (isVisibleFromDirection(trees, x, y, dx, dy)) {
                return true
            }
        }
        return false
    }

    private fun isVisibleFromDirection(trees: Array<IntArray>, x: Int, y: Int, dx: Int, dy: Int): Boolean {
        return if (dx == 0) {
            val column = trees.map { row -> row[x] }.toList()
            if (dy == -1) {
                column.subList(y + 1, column.size).all { it < trees[y][x] }
            } else {
                column.subList(0, y).all { it < trees[y][x] }
            }
        } else {
            val row = trees[y].toList()
            if (dx == -1) {
                row.subList(x + 1, row.size).all { it < trees[y][x] }
            } else {
                row.subList(0, x).all { it < trees[y][x] }
            }
        }
    }

    override fun solvePartTwo(lines: List<String>) {
        val trees: Array<IntArray> = toTreesGrid(lines)

        var highestScenicScore = 0
        for (y in 1 until trees.size - 1) {
            for (x in 1 until trees[0].size - 1) {
                val score = getScenicScore(trees, x, y)
                if (score > highestScenicScore) {
                    highestScenicScore = score
                }
            }
        }
        println(highestScenicScore)
    }

    private fun toTreesGrid(lines: List<String>) = lines.map { row ->
        row.chunked(1)
            .map { it.toInt() }
            .toIntArray()
    }.toTypedArray()

    private fun getScenicScore(trees: Array<IntArray>, x: Int, y: Int): Int {
        var scenicScore = 1
        for (idx in dxs.indices) {
            val dx = dxs[idx]
            val dy = dys[idx]
            val score = scenicScoreInDirection(trees, x, y, dx, dy)
//            print("" + score + " ")
            scenicScore *= score
        }
//        println()
        return scenicScore
    }

    private fun scenicScoreInDirection(trees: Array<IntArray>, x: Int, y: Int, dx: Int, dy: Int): Int {
        var score = 0
        if (dx == 0) {
            val column = trees.map { row -> row[x] }.toList()
            if (dy == -1) {
                val section = column.subList(0, y).reversed()
                score = sectionScore(section, score, trees, y, x)
            } else {
                val section = column.subList(y + 1, column.size).toList()
                score = sectionScore(section, score, trees, y, x)
            }
        } else {
            val row = trees[y].toList()
            if (dx == -1) {
                val section = row.subList(0, x).reversed()
                score = sectionScore(section, score, trees, y, x)
            } else {
                val section = row.subList(x + 1, row.size).toList()
                score = sectionScore(section, score, trees, y, x)
            }
        }
        return score
    }

    private fun sectionScore(
        section: List<Int>,
        score: Int,
        trees: Array<IntArray>,
        y: Int,
        x: Int,
    ): Int {
        var sectionScore = score
        for (treeIdx in section.indices) {
            if (treeIdx == section.size - 1) {
                sectionScore++
                break
            } else if (section[treeIdx] < trees[y][x]) {
                sectionScore++
            } else { // equal height
                sectionScore++
                break
            }
        }
        return sectionScore
    }
}