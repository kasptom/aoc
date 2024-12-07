package year2024

import aoc.IAocTaskKt

class Day07 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_07.txt"
    // override fun getFileName(): String = "aoc2024/input_07_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val equations = lines.map { Equation.parse(it) }
//        println(equations)
        equations.filter { it.isSolvable() }
            .sumOf { it.expected }
            .let { println(it) }
    }

    override fun solvePartTwo(lines: List<String>) {
        val equations = lines.map { Equation.parse(it) }
//        println(equations)
        equations.filter { it.isSolvable2() }
            .sumOf { it.expected }
            .let { println(it) }
    }
}

data class Equation(val expected: Long, val components: List<Long>) {
    fun isSolvable(): Boolean {
        val placements = components.size - 1
        return isSolvable("+", placements) || isSolvable("*", placements)
    }

    private fun isSolvable(ops: String, placements: Int): Boolean {
        if (ops.length == placements) {
            return evaluate(ops, components) == expected
        }
        return isSolvable(ops + "+", placements) || isSolvable(ops + "*", placements)
    }

    fun isSolvable2(): Boolean {
        val placements = components.size - 1
        return isSolvable2("+", placements) || isSolvable2("*", placements) || isSolvable2("|", placements)
    }

    private fun isSolvable2(ops: String, placements: Int): Boolean {
        if (ops.length == placements) {
            return evaluate(ops, components) == expected
        }
        return isSolvable2(ops + "+", placements) || isSolvable2(ops + "*", placements) || isSolvable2(ops + "|", placements)
    }

    fun evaluate(ops: String, components: List<Long>): Long {
        val operations = ops.split("").filter { it.isNotEmpty() }
        var result = components.first()
        var idx = 1
        for (operation in operations) {
            if (operation == "+") {
                result += components[idx++]
            } else if (operation == "*") {
                result *= components[idx++]
            } else if (operation == "|") {
                result = ("" + result + components[idx++]).toLong()
            } else {
                throw IllegalStateException("Unexpected operation $operation")
            }
        }
        return result
    }

    companion object {
        fun parse(line: String): Equation {
            val (test, componentsStr) = line.split(": ")
            return Equation(test.toLong(), componentsStr.split(" ").map { it.toLong() })
        }
    }
}
