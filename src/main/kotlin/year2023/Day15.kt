package year2023

import aoc.IAocTaskKt

class Day15 : IAocTaskKt{
    override fun getFileName() = "aoc2023/input_15.txt"

    override fun solvePartOne(lines: List<String>) {
        val line = lines.first().trim().split(",").filter(String::isNotEmpty)
        println(line
            .onEach { println("$it --> ${it.hash()}") }
            .sumOf { it.hash() })
    }

    override fun solvePartTwo(lines: List<String>) {
        print("Not yet implemented")
    }

    private fun String.hash(): Long {
        var currentValue = 0
        val word = split("").filter(String::isNotEmpty)
        // Determine the ASCII code for the current character of the string.
        // Increase the current value by the ASCII code you just determined.
        // Set the current value to itself multiplied by 17.
        // Set the current value to the remainder of dividing itself by 256.
        return word.fold(0) { acc, letter -> ((acc + letter.first().code) * 17) % 256 }
    }
}


