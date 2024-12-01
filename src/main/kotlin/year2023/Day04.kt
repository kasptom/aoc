package year2023

import aoc.IAocTaskKt
import kotlin.math.pow

class Day04 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_04.txt"

    override fun solvePartOne(lines: List<String>) {
        val cards = lines.map(Card::parse)

        println(cards
//            .onEach { println(it.getPoints()) }
            .sumOf { it.getPoints() })
    }

    override fun solvePartTwo(lines: List<String>) {
        val initialCards = lines.map(Card::parse)
            .sortedBy { it.id }

        val cardIdToCount = initialCards.groupBy { it.id }
            .mapValues { 1 }.toMutableMap()

        for (card in initialCards) {
            val winCount = card.countWins()
            val step = cardIdToCount[card.id] ?: 1

            val idsToIncrement = ((card.id + 1)..(card.id + winCount)).toList()
            for (idToIncrement in idsToIncrement) {
                cardIdToCount[idToIncrement] = cardIdToCount[idToIncrement]!! + step
            }
        }
        val sum = cardIdToCount.values.sum()

        println(sum)
    }

    data class Card(val id: Int, val winning: List<Int>, val yours: List<Int>) {

        fun getPoints(): Long {
            val winCount = countWins()
            return 2.0.pow(winCount.toDouble() - 1).toLong()
        }

        fun countWins(): Int = yours.count { it in winning }

        companion object {
            fun parse(line: String): Card {
                val (id, numbers) = line.replace("Card ", "")
                    .split(Regex(":\\s"))

                val (winningRaw, yoursRaw) = numbers.split(" | ")

                val winning = winningRaw.split(Regex("\\s"))
                    .filter { it.isNotEmpty() }
                    .map { it.toInt() }
                val yours = yoursRaw.split(Regex("\\s"))
                    .filter { it.isNotEmpty() }
                    .map { it.toInt() }

                return Card(id.trim().toInt(), winning, yours)
            }
        }
    }
}
