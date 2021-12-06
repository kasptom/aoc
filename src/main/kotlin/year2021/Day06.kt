package year2021

import aoc.IAocTaskKt
import year2021.Day06.LanternFish.Companion.PERIOD
import year2021.Day06.LanternFish.Companion.YOUNG_PERIOD

class Day06 : IAocTaskKt {
    override fun getFileName(): String = "aoc2021/input_06.txt"

    override fun solvePartOne(lines: List<String>) {
        val fish: List<LanternFish> = lines[0].split(",").filter(String::isNotBlank).map(String::trim)
            .map(String::toInt)
            .map { LanternFish(it) }
        val simulationTime = 80

        val population: MutableList<LanternFish> = fish.toMutableList()
//        println("day 0: ${population.map { it.daysToReproduce }}")
        for (day in 1..simulationTime) {
            val newborns = mutableListOf<LanternFish>()
            newborns.addAll(population.map { it.step() }.flatten())
            population.addAll(newborns)
//                println("day $day: ${population.map { it.daysToReproduce }.joinToString(",")}")
        }
//        println(population.map { it.daysToReproduce }.joinToString(","))
        println(population.size)
    }

    override fun solvePartTwo(lines: List<String>) {
        var population: Map<LanternFish, Long> = lines[0].split(",")
            .asSequence()
            .filter(String::isNotBlank)
            .map(String::trim)
            .map(String::toInt)
            .map(::LanternFish)
            .groupingBy { it }
            .eachCount()
            .mapValues { it.value.toLong() }

        val simulationTime = 256
        for (day in 1..simulationTime) {
            val newPopulation = population
                .filterKeys { it != LanternFish(0) }
                .mapKeys {
                    val newborn = it.key.step()
                    if (newborn.isEmpty()) it.key else newborn.first()
                }
                .toMutableMap()
            newPopulation[LanternFish(PERIOD)] = newPopulation.getOrDefault(LanternFish(PERIOD), 0) +
                        population.getOrDefault(LanternFish(0), 0)
            newPopulation[LanternFish(YOUNG_PERIOD)] = population.getOrDefault(LanternFish(0), 0)
            population = newPopulation
        }
//        println(population)
        println(population.values.sum())
    }


    data class LanternFish(var daysToReproduce: Int = YOUNG_PERIOD) {
        fun step(): List<LanternFish> {
            return if (daysToReproduce == 0) {
                daysToReproduce = PERIOD
                listOf(LanternFish())
            } else {
                daysToReproduce--
                listOf()
            }
        }

        companion object {
            const val PERIOD: Int = 6
            const val YOUNG_PERIOD: Int = PERIOD + 2
        }
    }
}
