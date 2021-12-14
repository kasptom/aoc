package year2021

import aoc.IAocTaskKt

class Day14 : IAocTaskKt {
    override fun getFileName(): String = "aoc2021/input_14_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val polymer = lines[0].windowed(2)
            .groupingBy { it }
            .eachCount()
            .toMutableMap()

        val rules = lines.subList(1, lines.size)
            .map(Rule::parse)
            .groupBy { it.from }
            .mapValues { it.value.first().to }

        for (step in 1..10) {
            val newPolymer = polymer.toMutableMap()
            for (rule in rules) {
                if (polymer.containsKey(rule.key)) {
                    val parentCount = polymer[rule.key]!!
                    val left = rule.key[0] + rules[rule.key]!!
                    val right = rules[rule.key] + rule.key[1]
                    newPolymer.putIfAbsent(left, 0)
                    newPolymer.putIfAbsent(right, 0)
                    newPolymer[left] = newPolymer[left]!! + parentCount
                    newPolymer[right] = newPolymer[right]!! + parentCount
                }
            }
            polymer.clear()
            polymer.putAll(newPolymer)
        }
        val result = polymer.map { listOf(Pair(it.key[0], it.value), Pair(it.key[1], it.value)) }
            .flatten()
            .groupBy { it.first }
            .mapValues { it.value.sumOf { charCount -> charCount.second } }
            .toMap()

        val minCount = result.values.minOf { it }
        val maxCount = result.values.maxOf { it }

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
        TODO("Not yet implemented")
    }
}