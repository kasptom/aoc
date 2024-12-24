package year2024

import aoc.IAocTaskKt
import java.util.*

class Day24 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_24.txt"
    // override fun getFileName(): String = "aoc2024/input_24_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val wireToValue = mutableMapOf<String, Int>()
        val values = lines.subList(0, lines.indexOfFirst { it.isBlank() })
            .associate { wireValue ->
                val (wire, value) = wireValue.split(": ")
                Pair(wire, value.toInt())
            }
        wireToValue.putAll(values)

        val edges = lines.subList(lines.indexOfFirst { it.isBlank() } + 1, lines.size)
            .map { Edge.parse(it) }
        println(edges)
    }

    override fun solvePartTwo(lines: List<String>) {
        if (lines.isEmpty()) println("empty lines") else println(lines.size)
    }

    data class Edge(val left: String, val right: String, val operation: Operation, val dest: String) {
        enum class Operation {
            XOR, OR, AND
        }

        companion object {
            fun parse(leftRightDest: String): Edge {
                val (leftRight, dest) = leftRightDest.split(" -> ")
                val (left, operationStr, right) = leftRight.split(" ")
                val operation = Operation.valueOf(operationStr)
                return Edge(left, right, operation, dest)
            }
        }
    }
}
