package year2021

import aoc.IAocTaskKt

class Day14 : IAocTaskKt {
    override fun getFileName(): String = "aoc2021/input_14.txt"

    override fun solvePartOne(lines: List<String>) {
        var polymer = lines[0].chunked(1)

        val rules = lines.subList(1, lines.size)
            .map(Rule::parse)
            .groupBy { it.from }
            .mapValues { it.value.first().to }
            .toMutableMap()

//        rules.clear()

        for (step in 1..10) {

//            val left = rule.key[0] + rules[rule.key]!!
//            val right = rules[rule.key] + rule.key[1]
            val newPolymer = mutableListOf<String>()

            for (idx in 1 until polymer.size) {
                val key = polymer[idx - 1] + polymer[idx]
                if (rules.containsKey(key)) {
                    newPolymer.add(polymer[idx - 1])
                    newPolymer.add(rules[key]!!)
                } else {
                    newPolymer.add(polymer[idx])
                }
            }
            newPolymer.add(polymer.last())

            polymer = newPolymer
//            println(newPolymer)
        }
        val result = polymer
            .groupingBy { it }
            .eachCount()
            .toMap()

        val minCount = result.values.minOf { it }
        val maxCount = result.values.maxOf { it }

//        println(polymer)
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