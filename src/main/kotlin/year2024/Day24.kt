package year2024

import aoc.IAocTaskKt

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

        val wirePairToEdges = edges.groupBy { it.wirePair() }

        var prevCount = 0

        while (prevCount != wireToValue.size) {
            val wires = wireToValue.keys.toList()
            prevCount = wireToValue.size
            for (a in wires) {
                for (b in wires) {
                    val wirePair = setOf(a, b)
                    if (wirePairToEdges.containsKey(wirePair)) {
                        val edges = wirePairToEdges[wirePair]!!
                        for (edge in edges) {
                            val (wire, value) = edge.compute(wireToValue)
                            wireToValue[wire] = value
                        }
                    }
                }
            }
        }
        println("0011111101000".toInt(2))
        wireToValue.filter { (k, _) -> k.startsWith("z") }
            .keys
            .sortedDescending()
            .map { k -> wireToValue[k]!! }
            .joinToString("")
            .let { it.toLong(2) }
            .let { println(it) }
    }

    override fun solvePartTwo(lines: List<String>) {
        if (lines.isEmpty()) println("empty lines") else println(lines.size)
    }

    data class Edge(val left: String, val right: String, val operation: Operation, val dest: String) {
        fun wirePair(): Set<String> {
            return setOf(left, right)
        }

        fun compute(wireToValue: MutableMap<String, Int>): Pair<String, Int> {
            val leftVal = wireToValue[left]!!
            val rightVal = wireToValue[right]!!
            val result = operation.compute(leftVal, rightVal)
            return Pair(dest, result)
        }

        enum class Operation {
            XOR, OR, AND;

            fun compute(leftVal: Int, rightVal: Int): Int = when (this) {
                XOR -> leftVal.xor(rightVal)
                OR -> leftVal.or(rightVal)
                AND -> leftVal.and(rightVal)
            }
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
