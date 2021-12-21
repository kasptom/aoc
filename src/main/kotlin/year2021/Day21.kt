package year2021

import aoc.IAocTaskKt


class Day21 : IAocTaskKt {
    override fun getFileName() = "aoc2021/input_21.txt"

    override fun solvePartOne(lines: List<String>) {
        val (player1, player2) = lines.map{line -> line.replace("Player ","")
            .replace(" starting position: ", ",")
            .split(",")
            .map(String::trim)
            .map(String::toInt)
        }.map { Player(it[0], it[1], 0)}

        val players = listOf(player1, player2)
        val scores = mutableListOf(0, 0)

        var counter = 1
        val dice = IntArray(100) { 0 }.map { counter++ }

        counter = 0
        var timesRolled = 0

        while (players.map { it.score }.all { it < 1000}) {
            val result = updateScore(counter, players, dice)
            counter = result.first
            timesRolled += result.second
        }

        println(dice)
        val minScore = minOf(player1.score, player2.score)
        println("$minScore * $timesRolled = ${minScore * timesRolled}")
    }

    private fun updateScore(oldCounter: Int, players: List<Player>, dice: List<Int>): Pair<Int, Int> {
        var counter = oldCounter
        var timesRolled = 0
        for (idx in players.indices) {
            val player = players[idx]
            val moves = mutableListOf<Int>()

            repeat(3) {
                moves += dice[counter]
                counter = (counter + 1) % dice.size
            }
            timesRolled += 3

            player.position = player.position + moves.sumOf { it }
            if (player.position > 10) player.position = (player.position - 1) % 10 + 1

            player.score += player.position

            println("Player ${player.id} rolls $moves and moves to space ${player.position} for a total score ${player.score}")
            if (player.score >= 1000) break
        }
        return Pair(counter, timesRolled)
    }

    data class Player(val id: Int, var position: Int, var score: Int)

    override fun solvePartTwo(lines: List<String>) {

    }


}
