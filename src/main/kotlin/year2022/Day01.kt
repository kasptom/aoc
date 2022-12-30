package year2022

import aoc.IAocTaskKt

class Day01 : IAocTaskKt{
    override fun getFileName(): String = "aoc2022/input_01.txt"

    override fun solvePartOne(lines: List<String>) {
        val caloriesPerElf = caloriesPerElf(lines)
        val maxCalories = caloriesPerElf.maxOf { it }
        println(maxCalories)
    }

    private fun caloriesPerElf(lines: List<String>) =
        lines.fold(mutableListOf(mutableListOf<Int>())) { elvesCalories, line ->
            if (line.isNotEmpty()) {
                elvesCalories.last().add(line.toInt())
            } else {
                elvesCalories.add(mutableListOf())
            }
            elvesCalories
        }.map { it.sum() }

    override fun solvePartTwo(lines: List<String>) {
        println(caloriesPerElf(lines).sortedDescending().subList(0, 3)
            .sumOf { it })
    }
}
