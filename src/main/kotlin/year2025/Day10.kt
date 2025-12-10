package year2025

import aoc.IAocTaskKt

class Day10 : IAocTaskKt {
    override fun getFileName(): String = "aoc2025/input_10_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val machines = lines.map(Machine::parse)

        machines.onEach {
            println(it)
        }
        val fewestPresses = machines.map { it.getFewestPresses() }
        println(fewestPresses)
        if (fewestPresses.any { it == 10 }) {
            throw IllegalArgumentException("too low limit")
        }
        val sum = fewestPresses.sum()
        println(sum)
    }

    override fun solvePartTwo(lines: List<String>) {
        TODO("Not yet implemented")
    }

    data class Machine(val lightDiagram: List<Indicator>, val buttons: List<List<Int>>, val joltReqs: List<Int>, val pressLimit: Int = 10) {
        fun getFewestPresses(): Int {
            val lights = lightDiagram.map { Indicator.OFF }
            var minPresses = pressLimit
            for (buttonIdx in buttons.indices) {
                val newLights = lights.toMutableList()
                for (button in buttons[buttonIdx]) {
                    newLights[button] = lights[button].opposite()
                }

                minPresses = minOf(minPresses, fewestPresses(lights, buttonIdx, 0))
            }
            return minPresses
        }

        private fun fewestPresses(
            lights: List<Indicator>,
            buttonIdx: Int,
            pressCount: Int,
        ): Int {
            if (pressCount > pressLimit) {
                return pressLimit
            }
            if (lights == lightDiagram) {
                return 0
            }
            var minPresses = pressLimit

            for (nextButtonIdx in buttons.indices) {
                if (nextButtonIdx == buttonIdx) {
                    continue
                }
                val newLights = lights.toMutableList()
                for (button in buttons[nextButtonIdx]) {
                    newLights[button] = lights[button].opposite()
                }

                minPresses = minOf(minPresses, 1 + fewestPresses(newLights, nextButtonIdx, pressCount + 1))
            }
            if (minPresses >= pressLimit) {
                return pressLimit
            }
            return minPresses
        }

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
                val buttons = strSchema.split(") (")
                    .map { it.split(",").map(String::toInt) }

                val reqs = strReqs
                    .substring(0, strReqs.length - 1)
                    .split(",")
                    .map { it.toInt() }
                return Machine(diagram, buttons, reqs)
            }
        }
    }

    enum class Indicator { ON, OFF;

        fun opposite(): Indicator = when (this) {
            ON -> OFF
            OFF -> ON
        }
    }
}