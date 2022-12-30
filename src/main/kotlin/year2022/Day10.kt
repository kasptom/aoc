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
        val cycles = getCycles(lines)

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
        }
        println(cycleToSignalStrength)
        println(cycleToSignalStrength.filter { it.key in setOf(20, 60, 100, 140, 180, 220) }
            .values
            .sumOf { it })
    }

    private fun getCycles(lines: List<String>) =
        lines.mapIndexed { idx, line -> Operation.parse(idx + 1, line) }
            .map { op -> (1..op.cyclesCount).map { op.copy(cyclesCount = 1) } }
            .flatten()

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
        val cycles = getCycles(lines)
        val crt = CathodeRayTube()

        var sprintMiddlePosition = 1
        val cycleToSignalStrength = mutableMapOf<Int, Int>()
        var cycleCounter = 0

        for (cycleIdx in cycles.indices) {
            cycleCounter++
            val row = (cycleCounter - 1) / 40
            val column = (cycleCounter - 1) % 40
            val drawnPixel = Pixel(row, column, CathodeRayTube.OFF)
//            println("drawn pixel $drawnPixel")

            val operation = cycles[cycleIdx]
            val nextOperation = if (cycleIdx + 1 <= cycles.size - 1) cycles[cycleIdx + 1] else Operation(-1, "placeholder", 0, 0)

            val pixelsLitUpBySprite = listOf(sprintMiddlePosition - 1, sprintMiddlePosition, sprintMiddlePosition + 1)
            drawnPixel.state = if (drawnPixel.col in pixelsLitUpBySprite) CathodeRayTube.ON else CathodeRayTube.OFF

            val xValueAfterCycle = sprintMiddlePosition + (if (operation.id != nextOperation.id) operation.opValue else 0 )
            if (cycleCounter % 20 == 0) {
                cycleToSignalStrength[cycleCounter] = cycleCounter * sprintMiddlePosition
            }

            /**
             * If the sprite is positioned such that one of its three pixels is the pixel currently being drawn,
             * the screen produces a lit pixel (#); otherwise, the screen leaves the pixel dark (.)
             */
            sprintMiddlePosition = xValueAfterCycle
//            println("drawn pixel $drawnPixel")
            crt.updatePixel(drawnPixel)
        }

        println(crt.print())
    }

    class CathodeRayTube {
        private val display: List<MutableList<String>> = (1..HEIGHT).map {
            (1..WIDTH).map { OFF }.toMutableList()
        }

        fun print(): String {
            var result = ""
            for (row in display) {
                for (cell in row) {
                    result += cell
                }
                result += "\n"
            }
            return result
        }

        fun updatePixel(pixel: Pixel) {
            display[pixel.row][pixel.col] = pixel.state
        }

        companion object {
            const val WIDTH = 40
            const val HEIGHT = 6
            private const val ANSI_RESET = "\u001B[0m"
            private const val ANSI_GREEN = "\u001B[32m"
            const val ON = "$ANSI_GREEN#$ANSI_RESET"
            const val OFF = "#"
        }
    }

    data class Pixel(val row: Int, val col: Int, var state: String)
}