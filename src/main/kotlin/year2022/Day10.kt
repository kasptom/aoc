package year2022

import aoc.IAocTaskKt

class Day10 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_10.txt"

    override fun solvePartOne(lines: List<String>) {
        /**
         * For now, consider the signal strength (the cycle number multiplied by the value of the X register)
         * during the 20th cycle and every 40 cycles after that (that is, during the
         * 20th, 60th, 100th, 140th, 180th, and 220th cycles).
         */
        val cycles = lines.mapIndexed { idx, line -> Operation.parse(idx + 1, line) }
            .map { op -> (1..op.cyclesCount).map { op.copy(cyclesCount = 1) } }
            .flatten()

        var xValue = 1
        val cycleToSignalStrength = mutableMapOf<Int, Int>()
        var cycleCounter = 0

        for (cycleIdx in cycles.indices) {
            cycleCounter++
            val operation = cycles[cycleIdx]

            val nextOperation = if (cycleIdx + 1 <= cycles.size - 1) cycles[cycleIdx + 1] else Operation(-1, "placeholder", 0, 0)

            val xValueAfterCycle = xValue + (if (operation.id != nextOperation.id) operation.opValue else 0 )
            if (cycleCounter % 20 == 0) {
                cycleToSignalStrength[cycleCounter] = cycleCounter * xValue
            }
            xValue = xValueAfterCycle
            println("operation: $operation, X: $xValue")
        }
        println(cycleToSignalStrength)
        println(cycleToSignalStrength.filter { it.key in setOf(20, 60, 100, 140, 180, 220) }
            .values
            .sumOf { it })
    }

    data class Operation(val id: Int, val opName: String, val opValue: Int, val cyclesCount: Int) {


        companion object {
            fun parse(idx: Int, line: String): Operation {
                return if (line == "noop") Operation(idx, line, 0, cyclesCount = 1)
                else {
                    val value = line.split(" ")[1].toInt()
                    Operation(idx, "addx", value, cyclesCount = 2)
                }
            }
        }

        override fun toString(): String {
            return "OP(id=$id, '$opName', $opValue)"
        }
    }

    override fun solvePartTwo(lines: List<String>) {
        println("??")
    }
}