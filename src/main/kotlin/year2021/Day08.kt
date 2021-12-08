package year2021

import aoc.IAocTaskKt
import utils.permutations

class Day08 : IAocTaskKt {
    override fun getFileName(): String = "aoc2021/input_08.txt"

    override fun solvePartOne(lines: List<String>) {
        val entries = lines.map(Entry::parse)
        val uniqueSegmentDigitOccurrenceCount = entries
            .sumOf(Entry::uniqueSegmentSizeDigitsCount)
        println(uniqueSegmentDigitOccurrenceCount)
    }

    override fun solvePartTwo(lines: List<String>) {
        val entries = lines.map(Entry::parse)
        val outputsSum = entries
            .sumOf { it.outputValue() }
        println(outputsSum)
    }

    data class Entry(val patterns: List<String>, val outputs: List<String>) {
        companion object {
            fun parse(line: String): Entry {
                val (patternStr, outputStr) = line.split("|")
                    .filter(String::isNotBlank)
                    .map(String::trim)

                val patterns = patternStr.extractDigitCodes()
                val outputs = outputStr.extractDigitCodes()

                return Entry(patterns, outputs)
            }

            private fun String.extractDigitCodes(): List<String> = split(" ")
                .map(::alphabetically)

            private fun alphabetically(code: String): String = code
                .chunked(1)
                .sorted()
                .joinToString("")

            val SEGMENT_SIZE_TO_DIGITS = mapOf(
                2 to setOf(1),
                3 to setOf(7),
                4 to setOf(4),
                5 to setOf(2, 3, 5),
                6 to setOf(9, 6, 0),
                7 to setOf(8)
            )

            /**
             *   x1xx
             *  2    3
             *  x    x
             *   x4xx
             *  5    6
             *  x    x
             *   7xxx
             */
            val DIGIT_TO_SEGMENT = mapOf(
                0 to setOf(1, 2, 3, 5, 6, 7),
                1 to setOf(3, 6),
                2 to setOf(1, 3, 4, 5, 7),
                3 to setOf(1, 3, 4, 6, 7),
                4 to setOf(2, 3, 4, 6),
                5 to setOf(1, 2, 4, 6, 7),
                6 to setOf(1, 2, 4, 5, 6, 7),
                7 to setOf(1, 3, 6),
                8 to setOf(1, 2, 3, 4, 5, 6, 7),
                9 to setOf(1, 2, 3, 4, 6, 7),
            )

            val SEGMENT_PERMUTATIONS = "abcdefg".chunked(1).permutations()
        }

        // pt 1
        fun uniqueSegmentSizeDigitsCount(): Int = outputs
            .map(String::length)
            .map { SEGMENT_SIZE_TO_DIGITS[it]!! }
            .count { digits -> digits.size == 1 }

        // pt 2
        fun outputValue(): Int {
            val segmentDigits = (patterns + outputs).toSet()
            return SEGMENT_PERMUTATIONS
                .first { allDigitsValid(it, segmentDigits) }
                .let(::decode)
        }

        private fun allDigitsValid(configuration: List<String>, segmentDigits: Set<String>): Boolean =
            segmentDigits.all { it ->
                val letters = it.chunked(1)
                val segments = letters.map { configuration.indexOf(it) + 1 }.toSet()
                DIGIT_TO_SEGMENT.values.any { it == segments }
            }

        private fun decode(configuration: List<String>): Int {
            val decoded: Int = outputs.map { digitCode ->
                val letters = digitCode.chunked(1)
                val segments = letters.map { configuration.indexOf(it) + 1 }.toSet()
                DIGIT_TO_SEGMENT.keys.first { DIGIT_TO_SEGMENT[it]!! == segments }
            }.joinToString("")
                .toInt()
            return decoded
        }
    }
}
