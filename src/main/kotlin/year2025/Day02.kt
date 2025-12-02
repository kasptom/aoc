package year2025

import aoc.IAocTaskKt
import kotlin.collections.flatten

class Day02 : IAocTaskKt{
    override fun getFileName(): String = "aoc2025/input_02.txt"

    override fun solvePartOne(lines: List<String>) {
        val invalidIds = lines.first().trim().split(",").map {
            findInvalidIds(it.trim())
        }
        // println(invalidIds)
        val sum = invalidIds.flatten().sum()
        println(sum)
    }

    private fun findInvalidIds(it: String): List<Long> {
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
        val invalidIds = lines.first().trim().split(",").map {
            findInvalidIds2(it.trim())
        }
        // println(invalidIds)
        val sum = invalidIds.flatten().sum()
        println(sum)
    }

    private fun findInvalidIds2(line: String): List<Long> {
        val (fromStr, toStr) = line.split("-")
        val (from, to) = Pair(fromStr.toLong(), toStr.toLong())
        val invalid = mutableListOf<Long>()
        for (i in from..to) {
            val idStr = i.toString()
            for (length in 1..(idStr.length / 2)) {
                if (idStr.length % length == 1) {
                    continue
                }
                if (idStr.take(length).repeat(idStr.length / length) == idStr) {
                    invalid.add(idStr.toLong())
                    break
                }
            }
        }
        return invalid
    }
}
