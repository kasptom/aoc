package year2025

import aoc.IAocTaskKt

class Day10 : IAocTaskKt {
    override fun getFileName(): String = "aoc2025/input_10.txt"

    override fun solvePartOne(lines: List<String>) {
        val machines = lines.map(Machine::parse)

//        machines.onEach {
//            println(it)
//        }
        val fewestPresses = machines.map { it.getFewestPresses() }
        println(fewestPresses)
        if (fewestPresses.any { it == 10 }) {
            throw IllegalArgumentException("too low limit")
        }
        val sum = fewestPresses.sum()
        println(sum)
    }

    override fun solvePartTwo(lines: List<String>) {
        val machines = lines.map(Machine::parse)
            .map { it.copy(pressLimit = 1000) }

//        machines.onEach {
//            println(it)
//        }
        val fewestPresses = machines.map { it.getFewestPresses2() }
        println(fewestPresses)
        val sum = fewestPresses.sum()
        println(sum)
    }

    data class Machine(val lightDiagram: List<Indicator>, val buttons: List<List<Int>>, val joltReqs: List<Int>, val pressLimit: Int = 10) {
        private lateinit var memo: MutableMap<String, Int>
        private lateinit var memo2: MutableMap<String, Int>

        fun getFewestPresses(): Int {
            memo = HashMap()
            val lights = lightDiagram.map { Indicator.OFF }
            var minPresses = pressLimit
            for (buttonIdx in buttons.indices) {
                val newLights = lights.toMutableList()
                for (button in buttons[buttonIdx]) {
                    newLights[button] = newLights[button].opposite()
                }

                minPresses = minOf(minPresses, 1 + fewestPresses(newLights, buttonIdx, 1))
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
            val key = encode(lights) + "|" + buttonIdx + "|" + (pressLimit - pressCount)
            memo[key]?.let { return it }

            var minPresses = pressLimit

            for (nextButtonIdx in buttons.indices) {
                if (nextButtonIdx == buttonIdx) {
                    continue
                }
                val newLights = lights.toMutableList()
                for (button in buttons[nextButtonIdx]) {
                    newLights[button] = newLights[button].opposite()
                }

                minPresses = minOf(minPresses, 1 + fewestPresses(newLights, nextButtonIdx, pressCount + 1))
            }
            if (minPresses >= pressLimit) {
                memo[key] = pressLimit
                return pressLimit
            }
            memo[key] = minPresses
            return minPresses
        }

        private fun fewestPresses2(
            joltages: List<Int>,
            buttonIdx: Int,
        ): Int {
            if (joltages.indices.any { joltages[it] > joltReqs[it] }) {
                return pressLimit
            }
            if (joltages == joltReqs) {
                return 0
            }
            val key = encode2(joltages) + "|" + buttonIdx
            memo2[key]?.let { return it }

            var minPresses = pressLimit

            for (nextButtonIdx in buttons.indices) {
                if (nextButtonIdx == buttonIdx) {
                    continue
                }
                val newJoltages = joltages.toMutableList()
                for (button in buttons[nextButtonIdx]) {
                    newJoltages[button] = newJoltages[button] + 1
                }

                minPresses = minOf(minPresses, 1 + fewestPresses2(newJoltages, nextButtonIdx))
            }
            if (minPresses >= pressLimit) {
                memo2[key] = pressLimit
                return pressLimit
            }

            memo2[key] = minPresses
            return minPresses
        }


        private fun encode(lights: List<Indicator>): String {
            val sb = StringBuilder(lights.size)
            for (l in lights) {
                sb.append(if (l == Indicator.ON) '1' else '0')
            }
            return sb.toString()
        }

        private fun encode2(joltages: List<Int>): String {
            return joltages.joinToString(",")
        }

        fun getFewestPresses2(): Int {
            memo2 = HashMap()
            val joltageCounters = joltReqs.map { 0 }
            var minPresses = pressLimit
            for (buttonIdx in buttons.indices) {
                val newJoltages = joltageCounters.toMutableList()
                for (button in buttons[buttonIdx]) {
                    newJoltages[button] = newJoltages[button] + 1
                }

                minPresses = minOf(minPresses, 1 + fewestPresses2(newJoltages, buttonIdx))
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