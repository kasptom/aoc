package year2025

import aoc.IAocTaskKt
import java.util.PriorityQueue
import kotlin.collections.get
import kotlin.div
import kotlin.text.toDouble

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

//        machines.onEach {
//            println(it)
//        }
        val fewestPresses = machines.map { it.getFewestPresses2() }
        println(fewestPresses)
        val sum = fewestPresses.sum()
        println(sum)
    }

    data class Machine(
        val lightDiagram: List<Indicator>,
        val buttons: List<List<Int>>,
        val joltReqs: List<Int>,
        val pressLimit: Int = 10,
    ) {
        private lateinit var memo: MutableMap<String, Int>

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

        private fun heuristic(state: List<Int>): Int {
            var sum = 0.0
            for (i in state.indices) {
                if (state[i] > joltReqs[i]) {
                    return Int.MAX_VALUE
                }
                sum += (joltReqs[i] - state[i]) / joltReqs[i].toDouble()
            }
            return sum.toInt()
        }

        fun getFewestPresses2(): Int {
            val start = joltReqs.map { 0 }
            val stateToDist: MutableMap<String, Int> = HashMap()
            val queue = PriorityQueue(
                compareBy<Node> { it.dist }
                    .thenBy { heuristic(it.state) }
            )

            val startKey = encode2(start)
            stateToDist[startKey] = 0
            queue.add(Node(start, 0))

            while (queue.isNotEmpty()) {
                val (state, dist) = queue.poll()
                val key = encode2(state)
                val bestKnown = stateToDist[key]
                if (bestKnown == null || dist != bestKnown) {
                    continue
                }

                for (button in buttons) {
                    val next = state.toMutableList()
                    var valid = true
                    for (idx in button) {
                        val joltage = next[idx] + 1
                        if (joltage > joltReqs[idx]) {
                            valid = false
                            break
                        }
                        next[idx] = joltage
                    }
                    if (!valid) {
                        continue
                    }

                    if (next == joltReqs) {
                        println("Fewer presses for $this: ${dist + 1}")
                        return dist + 1
                    }

                    val nextStateKey = encode2(next)
                    val nextDist = dist + 1
                    val prev = stateToDist[nextStateKey]

                    if (prev == null || nextDist < prev) {
                        stateToDist[nextStateKey] = nextDist
                        queue.add(Node(next, nextDist))
                    }
                }
            }
            return Int.MAX_VALUE
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

    enum class Indicator {
        ON, OFF;

        fun opposite(): Indicator = when (this) {
            ON -> OFF
            OFF -> ON
        }
    }

    data class Node(val state: List<Int>, val dist: Int)
}