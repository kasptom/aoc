package year2025

import aoc.IAocTaskKt

class Day01 : IAocTaskKt {
    override fun getFileName(): String =  "aoc2025/input_01.txt"

    override fun solvePartOne(lines: List<String>) {
        var x = 50
        var pass = 0
        for (line in lines) {
            val value = line.substring(1).toInt()
            if (line.startsWith("L")) {
                x -= value
                while (x < 0) {
                    x += 100
                }
                x %= 100
            } else {
                x += value
                x %= 100
            }
            if (x == 0) {
                pass++
            }
            println("line: $line, value: $value, x: $x, pass: $pass")
        }
        println("OUTPUT: $pass <--")
    }

    override fun solvePartTwo(lines: List<String>) {
        println("part 2 2025")
    }
}