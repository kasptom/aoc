package year2021

import aoc.IAocTaskKt
import utils.transpose

class Day03 : IAocTaskKt {
    override fun getFileName(): String = "aoc2021/input_03.txt"

    override fun solvePartOne(lines: List<String>) {
        val bitColumns: List<List<Int>> = lines
            .map { it.split("").filter(String::isNotEmpty).map(String::toInt) }
            .transpose()

        val gammaStr = bitColumns
            .map { column -> column.count { it == 1 } > column.count { it == 0 } }
            .map { moreOnes -> if (moreOnes) 1 else 0 }
            .joinToString("")

        val gamma = gammaStr.toInt(2)

        val epsilon = gammaStr.map { if (it == '0') '1' else '0' }
            .joinToString("")
            .toInt(2)

        val powerConsumption = gamma * epsilon
        println("$gamma * $epsilon = $powerConsumption")
    }

    override fun solvePartTwo(lines: List<String>) {
        val oxygenOnePredicate: (Int, Int) -> Boolean = { onesCount, zerosCount -> onesCount >= zerosCount }
        val oxygenRating = findBinNumber(lines, oxygenOnePredicate).toInt(2)

        val carbonOnePredicate: (Int, Int) -> Boolean = { onesCount, zerosCount -> onesCount < zerosCount }
        val carbonRating = findBinNumber(lines, carbonOnePredicate).toInt(2)

        val lifeSupportRating = oxygenRating * carbonRating
        println("$oxygenRating * $carbonRating = $lifeSupportRating")
    }

    private fun findBinNumber(
        binaryNumbers: List<String>,
        selectDigitOnePredicate: (Int, Int) -> Boolean
    ): String {
        var digitPos = 0
        val matchingNumbers = binaryNumbers.toMutableList()
        while (matchingNumbers.size != 1) {
            val (zerosCount, onesCount) = countZerosAndOnesAt(matchingNumbers, digitPos)
            val bit = if (selectDigitOnePredicate(onesCount, zerosCount)) '1' else '0'
            matchingNumbers.removeIf { number -> number[digitPos] != bit }
            digitPos++
        }
        return matchingNumbers.first()
    }

    private fun countZerosAndOnesAt(binaryNumbers: List<String>, column: Int): Pair<Int, Int> =
        binaryNumbers
            .map { binary -> binary[column] }
            .partition { it == '0' }
            .run { Pair(first.size, second.size) }
}
