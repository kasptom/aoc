package year2025

import aoc.IAocTaskKt
import kotlin.collections.flatten

class Day02 : IAocTaskKt{
    override fun getFileName(): String = "aoc2025/input_02.txt"

    override fun solvePartOne(lines: List<String>) {
        val invalidIds = lines.first().trim().split(",").map {
            findIvalidIds(it.trim())
        }
        println(invalidIds)
        val sum = invalidIds.flatten().sum()
        println(sum)
    }

    private fun findIvalidIds(it: String): List<Long> {
        val (fromStr, toStr) = it.split("-")
        val (from, to) = Pair(fromStr.toLong(), toStr.toLong())
        val invalid = mutableListOf<Long>()
        for (i in from..to) {
            val idStr = i.toString()
            if (idStr.length % 2 == 1) {
                continue
            }
            if (idStr.take(idStr.length / 2) == idStr.substring(idStr.length / 2)) {
                invalid.add(idStr.toLong())
            }
        }
        return invalid
    }

    override fun solvePartTwo(lines: List<String>) {
        println("part 2 - 02")
    }
}
