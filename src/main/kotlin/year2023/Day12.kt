package year2023

import aoc.IAocTaskKt

class Day12 : IAocTaskKt {
    override fun getFileName() = "aoc2023/input_12.txt"

    override fun solvePartOne(lines: List<String>) {
        val conditions = lines
//            .filter { it.startsWith("?#?#?#?#?#?#?#? ") }
            .map(Condition::parse)

        val configsCounts = conditions.map { it.configsCount() }

        conditions.zip(configsCounts)
            .onEach { (condition, count) -> println("$condition --> $count")}

        println(configsCounts.sumOf { it })
    }

    override fun solvePartTwo(lines: List<String>) {
        println("second")
    }

    data class Condition(val springs: List<String>, val sizes: List<Int>) {
        private val joined = springs.joinToString("")

        fun configsCount(): Int {
            if (springs.all { it != "?" }) return 1
            val currentIdx = 0
            if (springs[currentIdx] == "?") {
                val firstVariant = springs.toMutableList()
                val secondVariant = springs.toMutableList()
                firstVariant[currentIdx] = "#"
                secondVariant[currentIdx] = "."
                return countConfigs(currentIdx + 1, firstVariant, sizes) +
                        countConfigs(currentIdx + 1, secondVariant, sizes)
            }
            return countConfigs(currentIdx + 1, springs.toMutableList(), sizes)
        }

        private fun countConfigs(currentIdx: Int, partiallyFixedSprings: MutableList<String>, sizes: List<Int>): Int {
//            println("partially fixed: $partiallyFixedSprings")
            val groupCounts = partiallyFixedSprings.groupsToFirstQuestionMark()
            if (partiallyFixedSprings.all { it != "?" }) {
                return if (groupCounts != sizes) 0
                else 1
            }
            if (groupCounts.size > sizes.size) return 0
//            if (groupCounts != sizes.subList(0, groupCounts.size)) return 0

            if (partiallyFixedSprings[currentIdx] == "?") {
                val firstVariant = partiallyFixedSprings.toMutableList()
                val secondVariant = partiallyFixedSprings.toMutableList()
                firstVariant[currentIdx] = "#"
                secondVariant[currentIdx] = "."
                return countConfigs(currentIdx + 1, firstVariant, sizes) +
                        countConfigs(currentIdx + 1, secondVariant, sizes)
            }
            return countConfigs(currentIdx + 1, partiallyFixedSprings, sizes)
        }

        override fun toString(): String {
            return "($joined, $sizes)"
        }


        companion object {
            @JvmStatic
            fun main(args: Array<String>) {
                println(listOf("#", ".", "#", "#", "?").groupsToFirstQuestionMark())
                println(listOf("#", ".", "#", "#", "#").groupsToFirstQuestionMark())
                println(listOf("#", ".", "?", ".", "#", "#", "#").groupsToFirstQuestionMark())
            }

            fun parse(line: String): Condition {
                val (springRaw, sizesRaw) = line.split(" ")
                val spring = springRaw.split("").filter(String::isNotEmpty)
                val sizes = sizesRaw.split(",").filter(String::isNotEmpty).map(String::toInt)
                return Condition(spring, sizes)
            }
        }
    }
}

fun List<String>.groupsToFirstQuestionMark(): List<Int> {
    val groups = mutableListOf<Int>()
    var currentGroupSize = 0
    var lastIdx = 0
    for (idx in indices) {
        lastIdx = idx
        if (this[idx] == "#") currentGroupSize++
        if (this[idx] == "?") {
            if (currentGroupSize != 0) groups += currentGroupSize
            currentGroupSize = 0
            break
        }
        if (this[idx] == ".") {
            if (currentGroupSize != 0) groups += currentGroupSize
            currentGroupSize = 0
        }
    }
    if (lastIdx == size - 1 && currentGroupSize != 0) groups += currentGroupSize
//    println("groups to sizes: $this --> $groups")
    return groups
}


