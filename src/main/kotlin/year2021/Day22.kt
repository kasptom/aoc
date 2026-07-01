package year2021

import aoc.IAocTaskKt
import kotlin.math.abs

/**
 * Advent of Code 2021 - Day 22: Reactor Reboot.
 *
 * Solved with inclusion-exclusion over signed cuboids instead of splitting space
 * into sub-cuboids. See README.md in this package for the explanation of the fix.
 */
class Day22 : IAocTaskKt {
    override fun getFileName(): String = "aoc2021/input_22.txt"

    override fun solvePartOne(lines: List<String>) {
        val operations = lines.map(Operation::parse).filter(Operation::inInitRange)
        println(countOn(operations))
    }

    override fun solvePartTwo(lines: List<String>) {
        val operations = lines.map(Operation::parse)
        println(countOn(operations))
    }

    /**
     * Counts the number of lit cubes after applying every operation in order.
     *
     * Maintains a list of "signed" cuboids whose volumes sum to the currently lit
     * volume. For each new operation cuboid C:
     *  - for every already-placed cuboid E (with sign s) that intersects C, add the
     *    intersection with the opposite sign (-s) to cancel the double counting;
     *  - if C turns cubes ON, additionally add C itself with sign +1.
     * The final answer is the signed sum of all volumes.
     */
    fun countOn(operations: List<Operation>): Long {
        val placed = mutableListOf<SignedCuboid>()
        for (operation in operations) {
            val cuboid = operation.toCuboid()
            val additions = mutableListOf<SignedCuboid>()
            for ((existing, sign) in placed) {
                val intersection = cuboid.intersect(existing) ?: continue
                additions += SignedCuboid(intersection, -sign)
            }
            if (operation.state == Operation.State.ON) {
                additions += SignedCuboid(cuboid, 1)
            }
            placed += additions
        }
        return placed.sumOf { (cuboid, sign) -> sign * cuboid.volume() }
    }

    data class SignedCuboid(val cuboid: Cuboid, val sign: Int)

    data class Operation(
        val state: State,
        val x: IntRange,
        val y: IntRange,
        val z: IntRange,
        val inInitRange: Boolean,
    ) {
        fun toCuboid(): Cuboid = Cuboid(
            Point3d(x.first.toLong(), y.first.toLong(), z.first.toLong()),
            Point3d(x.last.toLong(), y.last.toLong(), z.last.toLong()),
        )

        companion object {
            fun parse(line: String): Operation {
                val (stateStr, ranges) = line.split(" ")
                val (xRangeStr, yRangeStr, zRangeStr) = ranges.split(",")
                    .map { it.replace("x=", "").replace("y=", "").replace("z=", "") }

                val xRange = range(xRangeStr)
                val yRange = range(yRangeStr)
                val zRange = range(zRangeStr)

                val state = State.valueOf(stateStr.uppercase())
                val inRange = listOf(xRange.first, xRange.last, yRange.first, yRange.last, zRange.first, zRange.last)
                    .map(::abs)
                    .max() <= 50

                return Operation(state, xRange, yRange, zRange, inRange)
            }

            private fun range(rangeStr: String): IntRange {
                val (from, to) = rangeStr.split("..").map(String::toInt)
                return from..to
            }
        }

        enum class State { ON, OFF }
    }

    data class Point3d(val x: Long, val y: Long, val z: Long) {
        override fun toString(): String = "($x, $y, $z)"
    }

    /**
     * An axis-aligned cuboid, inclusive on both [min] and [max] corners.
     */
    data class Cuboid(val min: Point3d, val max: Point3d) {
        /**
         * Returns the overlapping cuboid shared with [other], or null when they
         * do not intersect.
         */
        fun intersect(other: Cuboid): Cuboid? {
            val minX = maxOf(min.x, other.min.x)
            val minY = maxOf(min.y, other.min.y)
            val minZ = maxOf(min.z, other.min.z)
            val maxX = minOf(max.x, other.max.x)
            val maxY = minOf(max.y, other.max.y)
            val maxZ = minOf(max.z, other.max.z)
            if (minX > maxX || minY > maxY || minZ > maxZ) return null
            return Cuboid(Point3d(minX, minY, minZ), Point3d(maxX, maxY, maxZ))
        }

        fun volume(): Long =
            (max.x - min.x + 1) * (max.y - min.y + 1) * (max.z - min.z + 1)

        override fun toString(): String = "[$min..$max]"
    }
}
