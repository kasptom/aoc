package year2023

import aoc.IAocTaskKt

class Day09 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_09.txt"

    override fun solvePartOne(lines: List<String>) {
        lines.map(History::parse)
            .sumOf(History::extrapolate)
            .let(::println)
    }

    override fun solvePartTwo(lines: List<String>) {
        lines.map(History::parse)
            .sumOf(History::extrapolate2)
            .let(::println)
    }

    data class History(val initial: List<Int>) {
        fun extrapolate(): Int {
            val pyramid = createPyramid()
            pyramid.reversed()
                .windowed(2)
                .forEach { (first, second) -> second.add(first.last() + second.last()) }
            return pyramid.first().last()
        }

        fun extrapolate2(): Int {
            val pyramid = createPyramid()
            pyramid.reversed()
                .windowed(2)
                .forEach { (first, second) -> second.add(0, second.first() - first.first()) }
            return pyramid.first().first()
        }

        private fun createPyramid(): MutableList<MutableList<Int>> {
            val generatedRows = mutableListOf<MutableList<Int>>()
            var nextRow = initial.windowed(2)
                .map { (first, second) -> second - first }
                .toMutableList()
            generatedRows.add(initial.toMutableList())
            generatedRows.add(nextRow)
            while (!nextRow.all { it == 0 }) {
                nextRow = nextRow.windowed(2)
                    .map { (first, second) -> second - first }
                    .toMutableList()
                generatedRows.add(nextRow)
            }
            return generatedRows
        }

        companion object {
            fun parse(line: String): History {
                val initial = line.split(" ")
                    .filter(String::isNotEmpty)
                    .map { it.toInt() }
                return History(initial)
            }
        }
    }
}
