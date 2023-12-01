package year2023

import aoc.IAocTaskKt

class Day01 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_01.txt"

    override fun solvePartOne(lines: List<String>) {
        val digits = lines.map { line ->
            line.split("")
                .filter { it.matches(Regex("\\d")) }
        }.map { digits -> digits.first() + digits.last() }
            .sumOf { number -> number.toInt() }
        println(digits)
    }

    override fun solvePartTwo(lines: List<String>) {
        val digits = lines.map { line -> findFirstAndLastDigit(line) }
            .sumOf { (first, last) -> "$first$last".toInt() }
        println(digits)
    }

    private fun findFirstAndLastDigit(line: String): Pair<Int, Int> = Pair(line.findFirstDigit(), line.findLastDigit())

    private fun String.findFirstDigit(): Int {
        val firstDigitIndex = VALID_DIGITS
            .map { indexOf(it) }
            .filter { it != -1 }
            .minOf { it }

        return getIntAtIdx(firstDigitIndex)
    }

    private fun String.findLastDigit(): Int {
        val lastDigitIndex = VALID_DIGITS.maxOf { lastIndexOf(it) }

        return getIntAtIdx(lastDigitIndex)
    }

    private fun String.getIntAtIdx(digitStartIndex: Int): Int {
        val lastDigitName = substring(digitStartIndex)
        return if (lastDigitName.startsWith("one")) 1
        else if (lastDigitName.startsWith("two")) 2
        else if (lastDigitName.startsWith("three")) 3
        else if (lastDigitName.startsWith("four")) 4
        else if (lastDigitName.startsWith("five")) 5
        else if (lastDigitName.startsWith("six")) 6
        else if (lastDigitName.startsWith("seven")) 7
        else if (lastDigitName.startsWith("eight")) 8
        else if (lastDigitName.startsWith("nine")) 9
        else substring(digitStartIndex, digitStartIndex + 1).toInt()
    }

    companion object {
        val VALID_DIGITS = listOf(
            "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
            "1", "2", "3", "4", "5", "6", "7", "8", "9"
        )
    }
}