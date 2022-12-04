package year2022

import aoc.IAocTaskKt

class Day04 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_04.txt"

    override fun solvePartOne(lines: List<String>) =
        lines.toSectionPairs()
            .count { (first, second) -> first.contains(second) || second.contains(first) }
            .let(::println)

    override fun solvePartTwo(lines: List<String>) =
        lines.toSectionPairs()
            .count { (first, second) -> first.overlaps(second) }
            .let(::println)

    data class Section(val from: Int, val to: Int) {
        fun contains(other: Section): Boolean = from <= other.from && other.to <= to
        fun overlaps(other: Section): Boolean = from <= other.to && to >= other.from

        companion object {
            fun parse(fromTo: String): Section = fromTo.split("-")
                .map(String::toInt)
                .zipWithNext()
                .single()
                .let { (from, to) -> Section(from = from, to = to) }
        }
    }

    private fun List<String>.toSectionPairs() = map { line ->
        line.split(",")
            .map(Section.Companion::parse)
            .zipWithNext()
            .single()
    }
}
