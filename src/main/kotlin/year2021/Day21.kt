package year2021

import aoc.IAocTaskKt
import java.math.BigInteger


class Day21 : IAocTaskKt {
    override fun getFileName() = "aoc2021/input_21.txt"

    override fun solvePartOne(lines: List<String>) {
        val players = players(lines)
        val dice = createDice()

        var counter = 0
        var timesRolled = 0

        while (players.map { it.score }.all { it < 1000 }) {
            val result = updateScore(counter, players, dice)
            counter = result.first
            timesRolled += result.second
        }

        println(dice)
        val minScore = players.map { it.score }.minOf { it }
        println("$minScore * $timesRolled = ${minScore * timesRolled}")
    }

    private fun createDice(size: Int = 100): List<Int> {
        var counter = 1
        return IntArray(size) { 0 }.map { counter++ }
    }

    private fun players(lines: List<String>): List<Player> {
        val (player1, player2) = lines.map { line ->
            line.replace("Player ", "")
                .replace(" starting position: ", ",")
                .split(",")
                .map(String::trim)
                .map(String::toInt)
        }.map { Player(it[0], it[1], 0) }

        return listOf(player1, player2)
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

            val move = moves.sumOf { it }
            updatePlayer(player, move)

//            println("Player ${player.id} rolls $moves and moves to space ${player.position} for a total score ${player.score}")
            if (player.score >= 1000) break
        }
        return Pair(counter, timesRolled)
    }

    data class Player(val id: Int, var position: Int, var score: Int) {
        fun wonDiracGame(): Boolean = score >= 21
        override fun toString(): String {
            return "($id, pos=$position, pts=$score)"
        }
    }

    data class TripleTossResult(val firstPlayerSum: Int, val secondPlayerSum: Int) {
        override fun toString(): String {
            return "(P1=$firstPlayerSum, P2=$secondPlayerSum)"
        }
    }

    private val tripleTosses: Map<TripleTossResult, BigInteger> = createDiracTosses()

    private val winsCounter: MutableMap<Int, BigInteger> = mutableMapOf(1 to BigInteger.valueOf(0), 2 to BigInteger.valueOf(0))
    private val globalUniverseToOccurrences: MutableMap<List<Player>, BigInteger> =
        mutableMapOf() // configuration to its occurrences

    override fun solvePartTwo(lines: List<String>) {
        val players = players(lines)

        println(tripleTosses)
        globalUniverseToOccurrences[players] = BigInteger.valueOf(1)

        playDirac()

        if (players != players.map { it.copy() }) throw IllegalStateException("NOPE")

        println("$winsCounter")
        val score1 = winsCounter[1]!!
        val score2 = winsCounter[2]!!

        println("${maxOf(score1 / BigInteger.valueOf(27), score2)}")
    }

    private fun playDirac() {
        if (globalUniverseToOccurrences.isEmpty()) return

        while (globalUniverseToOccurrences.keys.size > 0) {
            val players = globalUniverseToOccurrences.keys.first()
            val universeToOccurrences = mutableMapOf<List<Player>, BigInteger>()
            for (toss in tripleTosses.keys) {
                val (player1, player2) = players.map { it.copy() }
                updatePlayer(player1, toss.firstPlayerSum)
                updatePlayer(player2, toss.secondPlayerSum)
                universeToOccurrences[listOf(player1, player2)] = tripleTosses[toss]!!
            }

            // update
            for (newUniverse in universeToOccurrences.keys) {
                val (player1, player2) = newUniverse

                val result = globalUniverseToOccurrences[players]!! * universeToOccurrences[newUniverse]!!
                if (player1.wonDiracGame()) {
                    winsCounter[player1.id] = winsCounter[player1.id]!! + result
                } else if (player2.wonDiracGame()) {
                    winsCounter[player2.id] = winsCounter[player2.id]!! + result
                } else {
                    globalUniverseToOccurrences.putIfAbsent(newUniverse, BigInteger.valueOf(0))
                    globalUniverseToOccurrences[newUniverse] = globalUniverseToOccurrences[newUniverse]!! + result
                }
            }
            globalUniverseToOccurrences.remove(players)
//            println("from $players to $newUniverses")
//            println(sameOutComeUniverses)

//            println("${sameOutComeUniverses.count()} with total sum of ${sameOutComeUniverses.values.sumOf { it }}")
            playDirac()
        }
    }

    private fun updatePlayer(player: Player, move: Int) {
        player.position = player.position + move
        if (player.position > 10) player.position = (player.position - 1) % 10 + 1

        player.score += player.position
    }

    fun createDiracTosses(): Map<TripleTossResult, BigInteger> {
        val sumToOccurrences: MutableMap<TripleTossResult, BigInteger> = mutableMapOf()

        for (a in 1..3) {
            for (b in 1..3) {
                for (c in 1..3) {
                    for (d in 1..3) {
                        for (e in 1..3) {
                            for (f in 1..3) {
                                val p1Sum = a + b + c
                                val p2Sum = d + e + f
                                val tossResult = TripleTossResult(p1Sum, p2Sum)
                                sumToOccurrences.putIfAbsent(tossResult, BigInteger.valueOf(0))
                                sumToOccurrences[tossResult] = sumToOccurrences[tossResult]!! + BigInteger.valueOf(1)
                            }
                        }
                    }
                }
            }
        }
        return sumToOccurrences
    }
}
