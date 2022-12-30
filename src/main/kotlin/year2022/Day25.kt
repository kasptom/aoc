package year2022

import aoc.IAocTaskKt
import kotlin.math.pow

class Day25 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_25.txt"

    override fun solvePartOne(lines: List<String>) {
        val snafus = lines.map { Snafu.parse(it) }
        val snafuSum = snafus.reduce { acc, snafu -> acc + snafu }
        println(snafuSum.snafu)
    }

    data class Snafu(val decimal: Long, val snafu: String) {

        companion object {
            fun parse(snafuStr: String): Snafu {
                val snafuValues = snafuStr.chunked(1).map { symbolToValue[it]!! }
                    .reversed()
                    .mapIndexed { idx, digitValue -> digitValue.toDouble() * 5.0.pow(idx.toDouble()) }

                val snafuValue = snafuValues.sumOf { it }
                    .toLong()
                return Snafu(snafuValue, snafuStr)
            }
        }

        override fun toString(): String = "dec: $decimal, snafu: $snafu)"

        // 1=-0-2  plus 2=0=
        operator fun plus(other: Snafu): Snafu {
            var first = snafu.chunked(1).reversed()  // least to most significant
            var second = other.snafu.chunked(1).reversed()

            if (first.size < second.size) {
                val tmp = first
                first = second
                second = tmp
            }

            var counter = 0
            val decValues = mutableListOf<Int>()
            for (idx in second.indices) {
                decValues += symbolToValue[first[idx]]!! + symbolToValue[second[idx]]!!
                counter++
            }

            while (counter < first.size) {
                decValues += symbolToValue[first[counter]]!!
                counter++
            }

            for (idx in 0 until decValues.size) {
                val digitValue = decValues[idx]
                if (digitValue >= 3) {
                    decValues[idx] = digitValue - 5
                    decValues[idx + 1] = decValues[idx + 1] + 1
                } else if (digitValue <= -3) {
                    decValues[idx] = 5 + digitValue
                    decValues[idx + 1] = decValues[idx + 1] - 1
                }
            }
            var result = ""
            for (digit in decValues) {
                val newSnafuDigit = valueToSymbol[digit]
                result = newSnafuDigit + result
            }

            return Snafu(decimal + other.decimal, result)
        }
    }

    override fun solvePartTwo(lines: List<String>) = println("⭐")

    companion object {
        val symbolToValue = mapOf("-" to -1, "=" to -2, "1" to 1, "2" to 2, "0" to 0)
        val valueToSymbol = mapOf(-1 to "-", -2 to "=", 1 to "1", 2 to "2", 0 to "0")
    }
}
