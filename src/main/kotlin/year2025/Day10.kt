package year2025

import aoc.IAocTaskKt

class Day10 : IAocTaskKt {
    override fun getFileName(): String = "aoc2025/input_10_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val machines = lines.map(Machine::parse)
        println(machines)
    }

    override fun solvePartTwo(lines: List<String>) {
        TODO("Not yet implemented")
    }

    data class Machine(val lightDiagram: List<Indicator>, val schematics: List<List<Int>>, val joltReqs: List<Int>) {
        companion object {
            fun parse(line: String): Machine {
                val (strDiagram, strSchema, strReqs) = line.split("] (", ") {")
                val diagram = strDiagram.substring(1)
                    .chunked(1)
                    .map {
                        when (it) {
                            "#" -> Indicator.ON
                            "." -> Indicator.OFF
                            else -> throw IllegalArgumentException(it)
                        }
                    }
                val schema = strSchema.split(") (")
                    .map { it.split(",").map(String::toInt) }

                val reqs = strReqs
                    .substring(0, strReqs.length - 1)
                    .split(",")
                    .map { it.toInt() }
                return Machine(diagram, schema, reqs)
            }
        }
    }

    enum class Indicator { ON, OFF }
}