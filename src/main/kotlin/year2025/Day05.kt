package year2025

import aoc.IAocTaskKt
import kotlin.collections.first
import kotlin.collections.last
import kotlin.math.max

class Day05 : IAocTaskKt {
    override fun getFileName(): String = "aoc2025/input_05.txt"

    override fun solvePartOne(lines: List<String>) {
        val separator = lines.indexOfFirst { it.isBlank() }
        val ranges = lines.subList(0, separator)
            .map {
                val (from, to) = it.split("-")
                from.toLong()..to.toLong()
             }
        val values = lines.subList(separator + 1, lines.size)
            .map(String::toLong)
        println(values.count { v -> ranges.any { r -> v in r} })
    }

    override fun solvePartTwo(lines: List<String>) {
        val separator = lines.indexOfFirst { it.isBlank() }
        val ranges = lines.subList(0, separator)
            .map {
                val (from, to) = it.split("-")
                Range(from.toLong(), to.toLong())
            }.sortedBy { it.from }
        val separatedRanges = ranges
            .fold(listOf<Range>()) {
                nonOverlapping, range -> range.mergeWith(nonOverlapping)
            }
        println(separatedRanges.sumOf { it.size() })
    }

    data class Range(val from: Long, val to: Long) {
        fun overlaps(other: Range): Boolean {
            val minB = other.from
            val maxB = other.to
            val minA = from
            val maxA = to
            return minA <= maxB && maxA >= minB
        }

        fun size() = to - from + 1

        fun mergeWith(sortedRanges: List<Range>): List<Range> {
            val firstOverlappingIdx = sortedRanges.indexOfFirst { overlaps(it) }
            val lastOverlappingIdx = sortedRanges.indexOfLast { overlaps(it) }
            return if (firstOverlappingIdx == -1) {
                sortedRanges + this
            } else {
                sortedRanges.subList(0, firstOverlappingIdx) +
                        merged(sortedRanges.subList(firstOverlappingIdx, lastOverlappingIdx + 1)) +
                        sortedRanges.subList(lastOverlappingIdx + 1, sortedRanges.size)
            }
        }

        private fun merged(ranges: List<Range>): List<Range> = if (ranges.isEmpty()) {
            listOf(this)
        } else {
            listOf(
                Range(ranges.first().from, max(ranges.last().to, to))
            )
        }
    }
}