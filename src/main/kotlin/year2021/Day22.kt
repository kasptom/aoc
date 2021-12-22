package year2021

import aoc.IAocTaskKt
import java.lang.Math.abs

class Day22 : IAocTaskKt {
    override fun getFileName(): String = "aoc2021/input_22.txt"

    override fun solvePartOne(lines: List<String>) {
        val operations = lines.map(Operation::parse)
        println(operations.size)

        val grid = mutableMapOf<Point3d, Operation.State>()

        for (operation in operations.filter { it.inInitRange }) {
            updateGrid(operation, grid)
        }

        println(grid.values.count { it == Operation.State.ON })
    }

    private fun updateGrid(operation: Operation, grid: MutableMap<Point3d, Operation.State>) {
        for (x in operation.x) {
            for (y in operation.y) {
                for (z in operation.z) {
                    val point = Point3d(x, y, z)
                    grid[point] = operation.state
                }
            }
        }
    }

    data class Operation(val state: State, val x: IntRange, val y: IntRange, val z: IntRange, val inInitRange: Boolean) {
        companion object {
            fun parse(line: String): Operation {
                val (stateStr, ranges) = line.split(" ")
                val (xRangeStr, yRangeStr, zRangeStr) = ranges.split(",")
                    .map {
                        it.replace("x=", "")
                            .replace("y=", "")
                            .replace("z=", "")
                    }

                val xRange = range(xRangeStr)
                val yRange = range(yRangeStr)
                val zRange = range(zRangeStr)

                val state = State.valueOf(stateStr.uppercase())
                val inRange = listOf(xRange.first, xRange.last, yRange.first, yRange.last, zRange.first, zRange.last)
                    .map(::abs)
                    .maxOf { it } <= 50

                return Operation(state, xRange, yRange, zRange, inRange)
            }

            private fun range(rangeStr: String): IntRange {
                val (from, to) = rangeStr.split("..").map(String::toInt)
                return from..to
            }
        }

        enum class State {
            ON, OFF
        }
    }

    data class Point3d(val x: Int, val y: Int, val z: Int)

    override fun solvePartTwo(lines: List<String>) {
        TODO("Not yet implemented")
    }
}