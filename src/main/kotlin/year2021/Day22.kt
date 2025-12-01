package year2021

import aoc.IAocTaskKt
import java.lang.Math.abs

/**
 * useful: https://threejs.org/editor/
 */
class Day22 : IAocTaskKt {
    override fun getFileName(): String = "aoc2021/input_22_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val operations = lines.map(Operation::parse)
        val withinInitRangeSum = sumOfLightsWithinInitRange(operations)
        println(withinInitRangeSum)
    }

    private fun updateGrid(operation: Operation, grid: MutableMap<Point3d, Operation.State>) {
        for (x in operation.x) {
            for (y in operation.y) {
                for (z in operation.z) {
                    val point = Point3d(x.toLong(), y.toLong(), z.toLong())
                    grid[point] = operation.state
                }
            }
        }
    }

    data class Operation(
        val state: State,
        val x: IntRange,
        val y: IntRange,
        val z: IntRange,
        val inInitRange: Boolean,
    ) {
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

    data class Point3d(val x: Long, val y: Long, val z: Long) {
        operator fun minus(other: Point3d) = Point3d(x - other.x, y - other.y, z - other.z)
        override fun toString(): String = "($x, $y, $z)"
    }

    override fun solvePartTwo(lines: List<String>) {
        val operations = lines
            .map(Operation::parse)
            .filter(Operation::inInitRange)

        val history = mutableListOf<Cuboid>()
        val cuboids = operations
            .map { op -> op.toCuboid() }
            .toMutableList()

        while (cuboids.firstOrNull()?.type == Operation.State.OFF) {
            cuboids.removeFirst()
        }

        var processedCuboidsTurnedOn = listOf(cuboids.removeFirst())
        history += processedCuboidsTurnedOn.first()

        while (cuboids.isNotEmpty()) {
            val previousVolume = processedCuboidsTurnedOn.sumOf { it.volume() }

            val nextCuboid = cuboids.removeFirst()

//            println("size: ${processedCuboidsTurnedOn.size}, to process: ${cuboids.size}")
//            println("processing $processedCuboidsTurnedOn <-- $nextCuboid")

            processedCuboidsTurnedOn = collide(processedCuboidsTurnedOn, nextCuboid)

            // CHECKING
            val currentVolume = processedCuboidsTurnedOn.sumOf { it.volume() }
            if (nextCuboid.isOff() && currentVolume > previousVolume) {
                throw IllegalStateException("Volume cannot grow after processing an OFF cuboid $currentVolume > $previousVolume" +
                        "\n history $history + $nextCuboid\"")
            } else if (nextCuboid.isOn() && currentVolume - previousVolume > nextCuboid.volume()) {
                throw IllegalStateException("Volume cannot grow more than the next ON cuboid volume ${currentVolume - previousVolume} > ${nextCuboid.volume()}" +
                        "\n next cuboid $nextCuboid" +
                        "\n current processed (${processedCuboidsTurnedOn.size}) $processedCuboidsTurnedOn" +
                        "\n history (${history.size}) $history + $nextCuboid")
            } else {
                val overlapping = findOverlappingCuboids(processedCuboidsTurnedOn)
                if (overlapping != null) {
                    val other =
                        processedCuboidsTurnedOn.firstOrNull { x -> x != overlapping && overlapping.isInRange(x) }
                    throw IllegalStateException("overlapping cuboids detected" +
                            "\n $overlapping vs " +
                            "\n $other" +
                            "\n history: $history + $nextCuboid")
                }
            }
//            println("processed $processedCuboidsTurnedOn")
            history += nextCuboid
        }

        val turnedOnSum = processedCuboidsTurnedOn.sumOf { it.volume() }
        println(turnedOnSum)
    }

    private fun sumOfLightsWithinInitRange(operations: List<Operation>): Long {
        val grid = mutableMapOf<Point3d, Operation.State>()
        for (operation in operations.filter { it.inInitRange }) {
            updateGrid(operation, grid)
        }

        return grid.values.count { it == Operation.State.ON }.toLong()
    }

    private fun Operation.toCuboid(): Cuboid {
        val minPoint = Point3d(x.first.toLong(), y.first.toLong(), z.first.toLong())
        val maxPoint = Point3d(x.last.toLong(), y.last.toLong(), z.last.toLong())
        return Cuboid(minPoint, maxPoint, type = state)
    }

    data class Cuboid(val min: Point3d, val max: Point3d, val type: Operation.State) {
        data class CollisionResult(val turnedOn: List<Cuboid>, val common: List<Cuboid>, val other: List<Cuboid>)

        fun collide(other: Cuboid): CollisionResult {
            if (this.isOff()) throw IllegalStateException("Only turned ON cubes are allowed")
            if (notInRange(other)) {
                return CollisionResult(listOf(this), emptyList(), listOf(other))
//                throw IllegalStateException("Cuboids $this and $other do not collide")
            }
            val subCuboids = createSmallerCuboids(this, other)
            if (subCuboids.any { it.notInRange(this) && it.notInRange(other) }) {
                throw IllegalStateException("out of range subCuboid")
            }

            val turnedOn = mutableListOf<Cuboid>()
            val common = mutableListOf<Cuboid>()
            val otherSubCuboids = mutableListOf<Cuboid>()

            for (subCuboid in subCuboids) {
                if (subCuboid.isInRange(this) && subCuboid.isInRange(other)) {
                    common.add(subCuboid.copy(type = other.type))
                } else if (subCuboid.isInRange(this)) {
                    turnedOn.add(subCuboid)
                } else {
                    otherSubCuboids.add(subCuboid.copy(type = other.type))
                }
            }

            return CollisionResult(turnedOn, common, otherSubCuboids)
        }

        fun isInRange(other: Cuboid): Boolean {
            // (StartA <= EndB) and (EndA >= StartB)
            val minB = other.min
            val maxB = other.max
            val minA = min
            val maxA = max
            return minA.x <= maxB.x && maxA.x >= minB.x
                    && minA.y <= maxB.y && maxA.y >= minB.y
                    && minA.z <= maxB.z && maxA.z >= minB.z
        }

        private fun notInRange(other: Cuboid) = !isInRange(other)

        private fun hasInvalidBoundaries() = min.x > max.x || min.y > max.y || min.z > max.z

        private fun createSmallerCuboids(cuboid: Cuboid, other: Cuboid): List<Cuboid> {
            val points = listOf(cuboid.min, cuboid.max, other.min, other.max)
            val xSorted = points.map { it.x }.sorted()
            val ySorted = points.map { it.y }.sorted()
            val zSorted = points.map { it.z }.sorted()

            val newCuboids = mutableListOf<Cuboid>()

            for ((xInd, x) in xSorted.windowed(2).withIndex()) {
                for ((yInd, y) in ySorted.windowed(2).withIndex()) {
                    for ((zInd, z) in zSorted.windowed(2).withIndex()) {
                        val minX = if (xInd == 2) x[0] + 1 else x[0]
                        val minY = if (yInd == 2) y[0] + 1 else y[0]
                        val minZ = if (zInd == 2) z[0] + 1 else z[0]

                        val maxX = if (xInd == 0) x[1] - 1 else x[1]
                        val maxY = if (yInd == 0) y[1] - 1 else y[1]
                        val maxZ = if (zInd == 0) z[1] - 1 else z[1]

                        val minPoint = Point3d(minX, minY, minZ)
                        val maxPoint = Point3d(maxX, maxY, maxZ)

                        val newCuboid = Cuboid(minPoint, maxPoint, Operation.State.ON)
                        newCuboids.add(newCuboid)
                    }
                }
            }
            return newCuboids
                .filter { it.volume() > 0 }
                .filter { it.isInRange(this) || it.isInRange(other) }
        }

        fun isOff() = type == Operation.State.OFF
        fun isOn() = type == Operation.State.ON

        fun volume(): Long {
            val volume = (max - min).run { (x + 1) * (y + 1) * (z + 1) }.toLong()
//            if (volume < 0) throw IllegalStateException("volume < 0: $volume for $this")
            return volume
        }

        override fun toString(): String {
            return "[$min..$max: $type]"
        }
    }

    private fun collide(turnedOnCuboids: List<Cuboid>, nextCuboid: Cuboid): List<Cuboid> {
        val processedOnCuboids = mutableListOf<Cuboid>()
        val nextPieces = mutableListOf(nextCuboid)

        for (turnedOnCuboid in turnedOnCuboids) {
            val updatedNextPieces = mutableListOf<Cuboid>()
            for (nextPiece in nextPieces) {
                val (remainingTurnedOn, common, remainingNext) = turnedOnCuboid.collide(nextPiece)
                if (nextCuboid.isOn()) {
                    processedOnCuboids.addAll(common)
                    updatedNextPieces.addAll(remainingNext)
                }
                processedOnCuboids.addAll(remainingTurnedOn)
            }
            nextPieces.run {
                clear()
                if (nextCuboid.isOn()) {
                    addAll(updatedNextPieces)
                } else add(nextCuboid)
            }
        }

        if (nextCuboid.isOn() && nextPieces.isNotEmpty()) {
            processedOnCuboids.addAll(nextPieces)
        }

        return processedOnCuboids
    }

    companion object {
        private fun findOverlappingCuboids(turnedOnCuboids: List<Cuboid>): Cuboid? =
            turnedOnCuboids.firstOrNull { cuboid ->
                turnedOnCuboids.any { otherCuboid ->
                    otherCuboid != cuboid && otherCuboid.isInRange(cuboid)
                }
            }
    }
}
