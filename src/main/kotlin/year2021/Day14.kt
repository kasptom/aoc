package year2021

import aoc.IAocTaskKt

class Day14 : IAocTaskKt {
    override fun getFileName(): String = "aoc2021/input_14.txt"

    override fun solvePartOne(lines: List<String>) {
        solve(lines, 10)
    }

    private fun solve(lines: List<String>, repeats: Int) {
        val polymer = lines[0].windowed(2)
            .groupingBy { it }
            .eachCount()
            .mapValues { it.value.toLong() }
            .toMutableMap()

        val rules = lines.subList(2, lines.size)
            .map(Rule::parse)
            .groupBy { it.from }
            .mapValues { it.value.first().to }

        (1..repeats).forEach { step ->
            val newPolymer = mutableMapOf<String, Long>()
            polymer.forEach { (pair, count) ->
                if (rules.containsKey(pair)) {
                    val left = pair[0] + rules[pair]!!
                    val right = rules[pair]!! + pair[1]
                    newPolymer.putIfAbsent(left, 0)
                    newPolymer.putIfAbsent(right, 0)
                    newPolymer[left] = newPolymer[left]!! + count
                    newPolymer[right] = newPolymer[right]!! + count
                }
            }
            polymer.clear()
            polymer.putAll(newPolymer)
        }
        val result = polymer.map { listOf(Pair(it.key[0], it.value), Pair(it.key[1], it.value)) }
            .flatten()
            .groupBy { it.first }
            .mapValues { it.value.sumOf { charCount -> charCount.second } }
            .toMutableMap()

        val firstChar = lines[0].first()
        val lastChar = lines[0].last()

        result[firstChar] = result[firstChar]!! + 1
        result[lastChar] = result[lastChar]!! + 1

        val minCount = result.values.minOf { it } / 2
        val maxCount = result.values.maxOf { it } / 2

        println(polymer)
        println(rules)
        println(result)
        println("$maxCount - $minCount = ${maxCount - minCount}")
    }


    data class Rule(val from: String, val to: String) {
        companion object {
            fun parse(line: String): Rule {
                val (from, to) = line.split(" -> ")
                return Rule(from, to)
            }
        }
    }


    override fun solvePartTwo(lines: List<String>) {
        solve(lines, 40)
    }
}