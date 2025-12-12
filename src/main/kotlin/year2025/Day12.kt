package year2025

import aoc.IAocTaskKt
import utils.transpose

class Day12 : IAocTaskKt {
    override fun getFileName(): String = "aoc2025/input_12_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val lastPresentIdx = lines.indexOfLast { it.isBlank() }
        val presentsBlock = lines.subList(0, lastPresentIdx + 1)
        val regions = lines.subList(lastPresentIdx + 1, lines.size)
            .map { Region.parse(it) }

        val presents = presentsBlock.fold(mutableListOf(mutableListOf<String>())) { acc, next ->
            if (next.isBlank()) {
                acc.add(mutableListOf())
            } else {
                acc.last().add(next)
            }
            acc
        }.dropLast(1)
            .map { Present.parse(it) }

        presents.onEach {
            println(it)
        }
        regions.onEach {
            println(it)
        }
    }

    data class Present(val idx: Int, val patterns: List<List<List<Char>>>) {
        companion object {
            fun parse(lines: List<String>): Present {
                val idx = lines[0].replace(":", "").trim()
                val patterns = lines.drop(1).map { row ->
                    row.toList()
                }.let {
                    val zero = it
                    val ninety = it.transpose()
                    val oneEighty = ninety.transpose()
                    val twoSeventy = oneEighty.transpose()
                    listOf(zero, ninety, oneEighty, twoSeventy)
                }

                return Present(idx.toInt(), patterns)
            }
        }
    }

    data class Region(val width: Int, val height: Int, val requirements: List<Int>) {
        companion object {
            fun parse(line: String): Region {
                val (dimsStr, requirementsStr) = line.split(": ")
                val (width, height) = dimsStr.split("x").map(String::toInt)
                val requirements = requirementsStr.split(" ").map(String::toInt)
                return Region(width, height, requirements)
            }
        }
    }

    override fun solvePartTwo(lines: List<String>) {
        TODO("Not yet implemented")
    }
}