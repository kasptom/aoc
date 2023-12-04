package year2023

import aoc.IAocTaskKt
import kotlin.math.pow

class Day04 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_04.txt"

    override fun solvePartOne(lines: List<String>) {
        val cards = lines.map(Card::parse)

        println(cards
            .onEach { println(it.getPoints()) }
            .sumOf { it.getPoints() })
    }

    override fun solvePartTwo(lines: List<String>) {
        TODO("Not yet implemented")
    }

    data class Card(val id: Int, val winning: List<Int>, val yours: List<Int>) {

        fun getPoints(): Long {
            val winCount = yours.count { it in winning }
            return 2.0.pow(winCount.toDouble() - 1).toLong()
        }

        companion object {
            fun parse(line: String): Card {
                val (id, numbers) = line.replace("Card ", "")
                    .split( Regex(":\\s"))

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
