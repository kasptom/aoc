package year2024

import aoc.IAocTaskKt

class Day22 : IAocTaskKt {
    //    override fun getFileName(): String = "aoc2024/input_22.txt"
    override fun getFileName(): String = "aoc2024/input_22.txt"

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
            for (idx in 1..simTime) {
                changes[sellerIdx][idx - 1] = pricesHistory[sellerIdx][idx] - pricesHistory[sellerIdx][idx - 1]
            }
        }
        val possibleSequencesAndCosts = mutableMapOf<Sequence, List<Long>>()

        for (sellerIdx in secretNumbers.indices) {
            val sellerSequencesAndCost = mutableMapOf<Sequence, Long>()
            val sellerChanges = changes[sellerIdx].toList()
            val sellerHistory = pricesHistory[sellerIdx].toList()
            sellerChanges
                .zip(sellerHistory.subList(1, sellerHistory.size))
                .windowed(4, 1)
                .map { (p1, p2, p3, p4) ->
                    val (a, _) = p1
                    val (b, _) = p2
                    val (c, _) = p3
                    val (d, cost) = p4
                    Pair(Sequence(a, b, c, d), cost)
                }.onEach { (seq, cost) ->
                    sellerSequencesAndCost.putIfAbsent(seq, cost)
                }

            for ((seq, cost) in sellerSequencesAndCost) {
                possibleSequencesAndCosts.putIfAbsent(seq, emptyList())
                possibleSequencesAndCosts.computeIfPresent(seq) { _, costs -> costs + cost }
            }
        }

//        println(possibleSequencesAndCosts[Sequence(-2,1,-1,3)]) // FIXME value for secret "2" is missing

        possibleSequencesAndCosts.entries.maxByOrNull { it.value.sum() }!!
//            .also { println(it) }
            .let { println(it.value.sum()) }
    }

    data class Sequence(val a: Long, val b: Long, val c: Long, val d: Long)
}
