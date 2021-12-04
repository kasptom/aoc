package year2021

import aoc.IAocTaskKt
import year2021.Day02.Move.Direction.*

class Day02 : IAocTaskKt {
    override fun getFileName(): String = "aoc2021/input_02.txt"

    override fun solvePartOne(lines: List<String>) {
        val moves = lines.map(Move::parse)
        val submarine = moves.fold(Submarine(), Submarine::move)
        println(submarine.run { horPos * depth })
    }

    override fun solvePartTwo(lines: List<String>) {
        val moves = lines.map(Move::parse)
        val submarine = moves.fold(Submarine(), Submarine::move2)
        println(submarine.run { horPos * depth })
    }

    data class Move(val dir: Direction, val value: Int) {
        companion object {
            fun parse(line: String): Move {
                val (dir, valueStr) = line.split(" ")
                val value = Integer.parseInt(valueStr)
                return Move(Direction.valueOf(dir.uppercase()), value)
            }
        }

        enum class Direction {
            UP, DOWN, FORWARD
        }
    }

    data class Submarine(val aim: Int = 0, val depth: Int = 0, val horPos: Int = 0) {
        fun move(move: Move): Submarine = when (move.dir) {
            UP -> copy(depth = depth - move.value)
            DOWN -> copy(depth = depth + move.value)
            FORWARD -> copy(horPos = horPos + move.value)
        }

        fun move2(move: Move): Submarine = when (move.dir) {
            UP -> copy(aim = aim - move.value)
            DOWN -> copy(aim = aim + move.value)
            FORWARD -> copy(horPos = horPos + move.value, depth = depth + aim * move.value)
        }
    }
}
