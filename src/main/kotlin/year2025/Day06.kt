package year2025

import aoc.IAocTaskKt

class Day06 : IAocTaskKt {
    override fun getFileName(): String = "aoc2025/input_06.txt"

    override fun solvePartOne(lines: List<String>) {
        val values = lines.subList(0, lines.size - 1)
            .map {
                it.split("\\s+".toRegex())
                    .filter { line -> line.isNotBlank() }
                    .map(String::toLong)
            }
        val ops = lines.last()
            .split("\\s+".toRegex())

        val results = ops.mapIndexed { idx, op ->
            when (op) {
                "+" -> values.sumOf { it[idx] }
                "*" -> values.fold(1L) { a, b -> a * b[idx] }
                else -> throw IllegalArgumentException("Unknown op $op")
            }
        }

        println(results.sumOf { it })
    }

    override fun solvePartTwo(lines: List<String>) {
        val maxLineLength = lines.subList(0, lines.size - 1)
            .maxOf { it.length }
        val numbersBlock = lines.subList(0, lines.size - 1)
            .map { if (it.length < maxLineLength) it.padEnd(maxLineLength, ' ') else it }

        val values = mutableListOf<List<Long>>()
        var numbers = mutableListOf<Long>()
        for (colIdx in 0 until maxLineLength) {
            val digitColumn = numbersBlock.map { row -> row[colIdx] }
                .joinToString("")

            if (digitColumn.any { it.isDigit() }) {
                numbers.add(digitColumn.trim().toLong())
            } else {
                values.add(numbers)
                numbers = mutableListOf()
            }
            if (colIdx == maxLineLength - 1) {
                values.add(numbers)
            }
        }

        val ops = lines.last()
            .split("\\s+".toRegex())

        val results = ops.mapIndexed { idx, op ->
            when (op) {
                "+" -> values[idx].sumOf { it }
                "*" -> values[idx].fold(1L) { a, b -> a * b }
                else -> throw IllegalArgumentException("Unknown op $op")
            }
        }

        println(results.sumOf { it })
    }
}