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
        val rounds = lines.map(Round::parse)
        val score = rounds.sumOf { it.score2 }
        println(score)
    }

    enum class Shape(
        val symbol: String, val score: Int,
        val wins: String, val loses: String, val draws: String,
    ) {
        ROCK(symbol = "X", score = 1, wins = "C", loses = "B", draws = "A"),
        PAPER(symbol = "Y", score = 2, wins = "A", loses = "C", draws = "B"),
        SCISSORS(symbol = "Z", score = 3, wins = "B", loses = "A", draws = "C"),
        ROCK2(symbol = "A", score = 1, wins = "Z", loses = "Y", draws = "X"),
        PAPER2(symbol = "B", score = 2, wins = "X", loses = "Z", draws = "Y"),
        SCISSORS2(symbol = "C", score = 3, wins = "Y", loses = "X", draws = "Z");

        companion object {
            fun getShapeBySymbol(symbol: String): Shape {
                return values().first { it.symbol == symbol }
            }
        }
    }

    enum class Decision(val symbol: String, val score: Int) {
        WIN("Z", 6), LOSE("X", 0), DRAW("Y", 3)
    }

    data class Round(val opponentShape: Shape, val myShape: Shape) {
        val score: Int = when (opponentShape.symbol) {
            myShape.wins -> myShape.score + 6
            myShape.loses -> myShape.score + 0
            myShape.draws -> myShape.score + 3
            else -> throw IllegalStateException("unknown case")
        }

        val score2: Int = when (val decision = Decision.values().first { it.symbol == myShape.symbol }) {
            Decision.WIN -> Shape.getShapeBySymbol(opponentShape.loses).score + decision.score
            Decision.LOSE -> Shape.getShapeBySymbol(opponentShape.wins).score + decision.score
            Decision.DRAW -> Shape.getShapeBySymbol(opponentShape.draws).score + decision.score
        }

        companion object {
            fun parse(line: String): Round {
                val (opponentShape, shape) = line.split(" ")
                return Round(
                    opponentShape = Shape.getShapeBySymbol(opponentShape),
                    myShape = Shape.getShapeBySymbol(shape),
                )
            }
        }
    }
}