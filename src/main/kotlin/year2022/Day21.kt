package year2022

import aoc.IAocTaskKt
import year2022.Day21.Monkey.Operation.*

class Day21 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_21.txt"

    override fun solvePartOne(lines: List<String>) {
        val monkeys = lines.map { Monkey.parse(it) }
        val nameToMonkey = monkeys.groupBy { it.name }.mapValues { (_, v) -> v.single() }
        val root = nameToMonkey["root"]!!
        root.calculate(nameToMonkey)
        println(root.result)
    }


    override fun solvePartTwo(lines: List<String>) {
        TODO("Not yet implemented")
    }

    data class Monkey(val name: String, var result: Long?, val operation: Operation, val children: List<String>) {
        fun calculate(nameToMonkey: Map<String, Monkey>) {
            if (operation == YELL) return
            val childrenMonkeys = getChildrenMonkeys(nameToMonkey)
            for (child in childrenMonkeys) {
                if (child.result == null) child.calculate(nameToMonkey)
            }
            val childrenResults = childrenMonkeys.map { it.result!! }
            result = when (operation) {
                MULTIPLY -> childrenResults.reduce { a, b -> a * b }
                DIVIDE -> childrenResults.reduce { a, b -> a / b }
                ADD -> childrenResults.sum()
                SUBTRACT -> childrenResults.reduce { a, b -> a - b }
                else -> return
            }
        }

        private fun getChildrenMonkeys(nameToMonkey: Map<String, Monkey>): List<Monkey> {
            return children.map { nameToMonkey[it]!! }
        }

        companion object {
            fun parse(line: String): Monkey {
                val (name, operation) = line.split(": ")
                val operationElements = operation.split(" ")
                if (operationElements.size == 1) return Monkey(
                    name,
                    result = operationElements[0].toLong(),
                    YELL,
                    emptyList()
                )
                val operationCode = operationElements[1].let { code -> Operation.values().first { it.code == code } }
                val childrenNames = listOf(operationElements[0], operationElements[2])
                return Monkey(name, null, operationCode, childrenNames)
            }
        }

        enum class Operation(val code: String? = null) {
            YELL, MULTIPLY("*"), DIVIDE("/"), ADD("+"), SUBTRACT("-")
        }
    }
}