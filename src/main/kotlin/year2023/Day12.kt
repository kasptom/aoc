package year2023

import aoc.IAocTaskKt
import kotlin.math.min

class Day12 : IAocTaskKt {
    override fun getFileName() = "aoc2023/input_12.txt"

    override fun solvePartOne(lines: List<String>) {
        val conditions = lines
            .map(Condition::parse)

        println(conditions.sumOf { it.configsCount() })
    }

    override fun solvePartTwo(lines: List<String>) {
        val conditions = lines
            .map(Condition::parse2)

        println(conditions.sumOf { it.configsCount() })
    }

    data class Condition(val springs: List<String>, val sizes: List<Int>) {
        private val configToSizes = mutableMapOf<Pair<List<String>, List<Int>>, Long>()
        private val joined = springs.joinToString("")

        fun configsCount(): Long = configsCount(springs, sizes, "")

        private fun configsCount(springs: List<String>, sizes: List<Int>, result: String): Long {
            if (springs.isEmpty()) {
                return if (sizes.isEmpty()) 1L else 0
            } else if (sizes.isEmpty()) {
                return if ("#" !in springs) 1L else 0L
            }
            val key = Pair(springs, sizes)
            if (key in configToSizes.keys) {
                return configToSizes[key]!!
            }
            var count = 0L
            val nextSymbol = springs.first()
            val nextSize = sizes.first()

            if (nextSymbol == "." || nextSymbol == "?") {
                count += configsCount(springs.subList(1, springs.size), sizes, "$result.")
            }
            val nextChunk = springs.subList(0, min(nextSize, springs.size))
            if ("." !in nextChunk && nextChunk.size == nextSize) {
                if (nextSize == springs.size) {
                    count += configsCount(emptyList(), sizes.subList(1, sizes.size), "$result#")
                } else if (nextSize < springs.size && springs.subList(nextSize, nextSize + 1).first() != "#") {
                    val nextSprings = springs.subList(nextSize + 1, springs.size)
                    count += configsCount(nextSprings, sizes.subList(1, sizes.size), "$result#")
                }
            }

            configToSizes[key] = count
            return count
        }


        override fun toString(): String = "($joined, $sizes)"


        companion object {
            fun parse(line: String): Condition {
                val (springRaw, sizesRaw) = line.split(" ")
                val spring = springRaw.split("").filter(String::isNotEmpty)
                val sizes = sizesRaw.split(",").filter(String::isNotEmpty).map(String::toInt)
                return Condition(spring, sizes)
            }

            fun parse2(line: String): Condition {
                val (springRaw, sizesRaw) = line.split(" ")
                val spring = springRaw.split("").filter(String::isNotEmpty)
                val sizes = sizesRaw.split(",").filter(String::isNotEmpty).map(String::toInt)
                val bigSpring = (1..4).flatMap { spring + "?" } + spring
                val bigSizes = (1..5).flatMap { sizes }

                return Condition(bigSpring, bigSizes)
            }
        }
    }
}
