package year2025

import aoc.IAocTaskKt

class Day01 : IAocTaskKt {
    override fun getFileName(): String = "aoc2025/input_01.txt"

    override fun solvePartOne(lines: List<String>) {
        var x = 50
        var pass = 0
        for (line in lines) {
            val value = line.substring(1).toInt()
            val sign = if (line.startsWith("L")) -1 else 1
            x += sign * value
            x += 100
            x %= 100
            if (x == 0) {
                pass++
            }
//            println("The dial is rotated $line to point at $x")
        }
        println(pass)
    }

    override fun solvePartTwo(lines: List<String>) {
        var x = 50
        var pass = 0
        for (line in lines) {
            val value = line.substring(1).toInt()
            val sign = if (line.startsWith("L")) -1 else 1
            repeat(value) { _ ->
                x += sign
                if (x == 0 || x == 100) {
                    pass++
                }
                x += 100
                x %= 100
            }
        }
        println(pass)
    }
}