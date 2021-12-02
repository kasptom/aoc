package year2021

import aoc.IAocTaskKt

class Day02 : IAocTaskKt {
    override fun getFileName(): String = "aoc2021/input_02.txt"

    override fun solvePartOne(lines: List<String>) {
        val moves = lines.map(Move::parse)
        val result = moves.filter { it.dir == "forward" }.sumOf{ it.value} *
                moves.filter { it.dir == "up" }.sumOf { it.value } -
                        moves.filter { it.dir == "down" }.sumOf{ it.value}
        println(result)
    }

    data class Move(val dir: String, val value: Int) {
        companion object {
            fun parse(line: String): Move {
                val (dir, valueStr) = line.split(" ")
                val value = Integer.parseInt(valueStr)
                return Move(dir, value)
            }
        }
    }

    override fun solvePartTwo(lines: List<String>) {
        val moves = lines.map(Move::parse)
        var aim = 0
        var depth = 0
        var horPos = 0
        for (move in moves) {
            if (move.dir == "up") aim += move.value
            if (move.dir == "down") aim -= move.value
            if (move.dir == "forward") {
                horPos += move.value
                depth += aim * move.value
            }
        }
        val result = horPos * depth
        println(result)
    }
}
