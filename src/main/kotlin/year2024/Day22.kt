package year2024

import aoc.IAocTaskKt
import kotlin.math.max

class Day22 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_22.txt"
    // override fun getFileName(): String = "aoc2024/input_22_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val secretNumbers = lines.map { it.toLong() }.toMutableList()
        val simTime = 2000
        for (idx in secretNumbers.indices) {
            secretNumbers[idx] = simulate(secretNumbers[idx], simTime)
        }
        println(secretNumbers.sum())
    }

    private fun simulate(initial: Long, simTime: Int): Long {
        var newSecret = initial
        repeat(simTime) {
            newSecret = mixAndPrune(newSecret, 64)
            newSecret = mixAndPrune(newSecret, 32)
            newSecret = mixAndPrune(newSecret, 2048)
        }
        return newSecret
    }

    private fun mixAndPrune(number: Long, base: Int): Long {
        val result = if (base == 32) number / base else number * base
        val newSecret = result.xor(number)
        return newSecret % 16777216
    }

    override fun solvePartTwo(lines: List<String>) {
        val secretNumbers = lines.map { it.toLong() }.toMutableList()
        val simTime = 2000
        val pricesHistory = lines.map { (0..simTime).map { 0L }.toLongArray() }.toTypedArray()
        val changes = lines.map { (1..simTime).map { 0L }.toLongArray() }.toTypedArray()

        for (sellerIdx in secretNumbers.indices) {
            pricesHistory[sellerIdx][0] = secretNumbers[sellerIdx] % 10
            repeat(simTime) { time ->
                val output = simulate(secretNumbers[sellerIdx], 1)
                pricesHistory[sellerIdx][time + 1] = output % 10
                secretNumbers[sellerIdx] = output
            }
        }
        for (sellerIdx in lines.indices) {
            for (idx in 1 .. simTime) {
                changes[sellerIdx][idx - 1] = pricesHistory[sellerIdx][idx] - pricesHistory[sellerIdx][idx - 1]
            }
        }
        val possibleSequences = mutableSetOf<Sequence>()

        for (rowIdx in changes.indices) {
            val row = changes[rowIdx]
            val sequences = row.toList().windowed(4, 1)
                .map { (a, b, c, d) -> Sequence(a, b, c, d) }
            possibleSequences.addAll(sequences)
        }
        println(possibleSequences.size)
        var mostBananas = 0L
        for (sequence in possibleSequences) {
            val bananasGot = getBananasForSequence(sequence, pricesHistory, changes)
            if (mostBananas < bananasGot) {
                mostBananas = max(mostBananas, bananasGot)
            }
        }
        println(mostBananas)
    }

    private fun getBananasForSequence(
        sequence: Sequence,
        pricesHistory: Array<LongArray>,
        changes: Array<LongArray>,
    ): Long {
        var bananasCollected = 0L

        for (sellerIdx in changes.indices) {
            val change = changes[sellerIdx]
            for (timeIdx in 4..change.lastIndex) {
                val currentSequence = Sequence(change[timeIdx - 3], change[timeIdx - 2], change[timeIdx - 1], change[timeIdx])
                if (sequence == currentSequence) {
                    val price = pricesHistory[sellerIdx][timeIdx + 1]
                    bananasCollected += price
                    break
                }
            }
        }
        return bananasCollected
    }

    data class Sequence(val a: Long, val b: Long, val c: Long, val d: Long)
}
