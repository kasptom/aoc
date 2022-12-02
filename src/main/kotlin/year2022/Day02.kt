package year2022

import aoc.IAocTaskKt

class Day02 : IAocTaskKt {

    override fun getFileName(): String = "aoc2022/input_02.txt"

    override fun solvePartOne(lines: List<String>) {
        val rounds = lines.map(Round::parse)
        val score = rounds.sumOf { it.score }
        println(score)
    }

    override fun solvePartTwo(lines: List<String>) {
        TODO("Not yet implemented")
    }

    enum class Shape(val symbol: String, val score: Int,
                     val wins: String, val loses: String, val draws: String) {
        ROCK(symbol = "X", score = 1, wins = "C", loses = "B", draws = "A"),
        PAPER(symbol = "Y", score = 2, wins = "A", loses = "C", draws = "B"),
        SCISSORS(symbol = "Z", score = 3, wins = "B", loses = "A", draws = "C"),
        ROCK2(symbol = "A", score = 1, wins = "Z", loses = "Y", draws = "X"),
        PAPER2(symbol = "B", score = 2, wins = "X", loses = "Z", draws = "Y"),
        SCISSORS2(symbol = "C", score = 3, wins = "Y", loses = "X", draws = "Z")
    }

    data class Round(val opponentShape: Shape, val myShape: Shape) {
        val score: Int = when(opponentShape.symbol) {
            myShape.wins -> myShape.score + 6
            myShape.loses -> myShape.score + 0
            myShape.draws -> myShape.score + 3
            else -> throw IllegalStateException("unknown case")
        }


        companion object {
            fun parse(line: String): Round {
                val (opponentShape, shape) = line.split(" ")
                return Round(
                    opponentShape = Shape.values().first { it.symbol == opponentShape },
                    myShape = Shape.values().first { it.symbol == shape },
                )
            }
        }
    }
}