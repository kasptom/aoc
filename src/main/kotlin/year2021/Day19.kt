package year2021

import aoc.IAocTaskKt
import kotlin.math.abs
import java.util.Objects

// Beacon Scanner
val INITIAL_ROTATION: List<String> = listOf("x", "y", "z")

class Day19 : IAocTaskKt {
    override fun getFileName() = "aoc2021/input_19.txt"

    /**
     * The submarine has automatically summarized the relative positions of beacons detected by each scanner (your puzzle input)
     *
     * the scanners do not know their own position
     *
     * The scanners and beacons map a single contiguous 3d region. This region can be reconstructed by finding pairs
     * of scanners that have overlapping detection regions such that there are at least 12 beacons that
     * both scanners detect within the overlap.
     *
     * Unfortunately, there's a second problem: the scanners also don't know their rotation or facing direction.
     *
     * Due to magnetic alignment, each scanner is rotated some integer number of 90-degree turns around all of the x, y, z
     *
     * In total, each scanner could be in any of 24 different orientations:
     * facing positive or negative x, y, or z, and considering any of four directions "up" from that facing
     *
     * Assemble the full map of beacons. How many beacons are there?
     */

    val translations = mutableListOf<Point3d>()

    override fun solvePartOne(lines: List<String>) {
        val startTime = System.currentTimeMillis()
        val scanners = parseScanners(lines).toMutableList()

        var scannerX = scanners.removeFirst()
        val threshold = 12
        val scannersSize = scanners.size

        while (scanners.isNotEmpty()) {
            val (foundScanner, rotationCode) = findTranslatedScannerFor(scannerX, scanners, threshold)!!
            translations.add(foundScanner.cubeGrid.translation)

            val foundWithRemovedRotations = foundScanner.withOnlyRotation(rotationCode)
            scannerX = scannerX.mergeWith(foundWithRemovedRotations)

            scanners.removeIf { it.id == foundScanner.id }
            println("progress: ${scannersSize - scanners.size}/$scannersSize (${100.0 * (scannersSize - scanners.size) / scannersSize}[%])")
        }

        val allBeacons = scannerX.cubeGrid.rotations.values.first()

        println("done: ${allBeacons.size}")
        println("Execution time: ${(System.currentTimeMillis() - startTime) / 1000.0} seconds")
    }

    @Suppress("SameParameterValue")
    private fun findTranslatedScannerFor(
        scannerX: Scanner,
        scanners: List<Scanner>,
        threshold: Int,
    ): Pair<Scanner, List<String>>? {

        for (scanner in scanners.filter { it.id != scannerX.id }) {
            val scannerXPoints = scannerX.cubeGrid.rotations[INITIAL_ROTATION]!!
            val translationFrequency = mutableMapOf<Point3d, Int>()

            for ((rotationCode, otherScannerRotatedPoints) in scanner.cubeGrid.rotations) {
                translationFrequency.clear()

                for (scannerXPoint in scannerXPoints) {
                    for (otherPoint in otherScannerRotatedPoints) {
                        val translation = scannerXPoint - otherPoint
                        translationFrequency[translation] = translationFrequency.getOrDefault(translation, 0) + 1
                    }
                }

                val mostCommonTranslation = translationFrequency.entries
                    .filter { it.value >= threshold }
                    .maxByOrNull { it.value }

                if (mostCommonTranslation != null) {
                    val translation = mostCommonTranslation.key
                    val translatedScanner = scanner.translate(translation)

                    // Verify the match
                    val commonPoints = scannerXPoints.intersect(translatedScanner.cubeGrid.rotations[rotationCode]!!)
                    if (commonPoints.size >= threshold) {
                        // Reduce logging to improve performance
                        println("Found scanner ${scanner.id} with ${commonPoints.size} common points")
                        return Pair(translatedScanner, rotationCode)
                    }
                }
            }
        }
        return null
    }

    fun parseScanners(lines: List<String>) =
        lines.fold(mutableListOf<MutableList<String>>()) { lineGroups, line ->
            val updatedGroups = if (line.startsWith("--- scanner ")) {
                lineGroups.add(mutableListOf())
                lineGroups
            } else {
                lineGroups
            }
            val group = updatedGroups.last()
            group.add(line)
            updatedGroups
        }.map(Scanner::parse)

    data class Scanner(val id: Int, val cubeGrid: CubeGrid) {
        private val translationCache = mutableMapOf<Point3d, Scanner>()
        private val rotationCache = mutableMapOf<List<String>, Scanner>()

        fun translate(translation: Point3d): Scanner {
            return translationCache.getOrPut(translation) {
                val newCubeGrid = cubeGrid.translate(translation)
                Scanner(id, newCubeGrid)
            }
        }

        fun withOnlyRotation(defaultRotationKey: List<String> = INITIAL_ROTATION): Scanner {
            return rotationCache.getOrPut(defaultRotationKey) {
                val initialRotation = cubeGrid.rotations[defaultRotationKey]!!
                val newGrid = CubeGrid(initialRotation,
                    cubeGrid.translation,
                    rotations = mapOf(defaultRotationKey to initialRotation))
                Scanner(id, newGrid)
            }
        }

        override fun toString(): String {
            return "Scanner(id=$id, $cubeGrid)"
        }

        fun mergeWith(foundScanner: Scanner): Scanner {
            val myPoints = cubeGrid.rotations.values.first()
            val otherPoints = foundScanner.cubeGrid.rotations.values.first()

            val mergedPoints = HashSet<Point3d>(myPoints.size + otherPoints.size)
            mergedPoints.addAll(myPoints)
            mergedPoints.addAll(otherPoints)

            val newGrid = CubeGrid(mergedPoints,
                cubeGrid.translation,
                rotations = mapOf(INITIAL_ROTATION to mergedPoints))

            return Scanner(id, cubeGrid = newGrid)
        }

        companion object {
            fun parse(lines: List<String>): Scanner {
                val id = lines[0].replace("--- scanner ", "").replace(" ---", "").toInt()
                val points3d = lines.subList(1, lines.size)
                    .filter(String::isNotBlank)
                    .map(Point3d::parse)
                    .toSet()
                val cubeGrid = CubeGrid(points3d, Point3d(0, 0, 0))
                return Scanner(id, cubeGrid = cubeGrid)
            }
        }
    }

    data class Point3d(val x: Int, val y: Int, val z: Int) {
        private val cachedHashCode: Int = Objects.hash(x, y, z)
        private val cachedVector by lazy { listOf(x, y, z) }

        fun toVector(): List<Int> = cachedVector

        override fun toString(): String {
            return "($x,$y,$z)"
        }

        operator fun plus(point: Point3d) = Point3d(x + point.x, y + point.y, z + point.z)
        operator fun minus(point: Point3d) = Point3d(x - point.x, y - point.y, z - point.z)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Point3d) return false
            return x == other.x && y == other.y && z == other.z
        }

        override fun hashCode(): Int = cachedHashCode

        companion object {
            private val parseCache = mutableMapOf<String, Point3d>()
            fun parse(line: String): Point3d {
                return parseCache.getOrPut(line) {
                    val (x, y, z) = line.split(",").filter(String::isNotBlank).map(String::toInt)
                    Point3d(x, y, z)
                }
            }
        }

        fun manhattan(): Int = abs(x) + abs(y) + abs(z)
    }

    class CubeGrid(
        points: Set<Point3d>,
        val translation: Point3d,
        val rotations: Map<List<String>, Set<Point3d>> = createRotations(points, translation),
    ) {
        private val translationCache = mutableMapOf<Point3d, CubeGrid>()

        fun translate(translation: Point3d): CubeGrid {
            return translationCache.getOrPut(translation) {
                CubeGrid(rotations[INITIAL_ROTATION]!!, translation)
            }
        }

        override fun toString(): String {
            return "GRID(T=$translation, rotations=$rotations)"
        }

        companion object {
            private val rotationCache = mutableMapOf<Set<Point3d>, Map<List<String>, Set<Point3d>>>()
            private val RIGHT_HAND_ROTATIONS = listOf(
                listOf("x", "y", "z"), listOf("x", "-y", "-z"), listOf("-x", "y", "-z"), listOf("-x", "-y", "z"),
                listOf("y", "x", "-z"), listOf("-y", "x", "z"), listOf("y", "-x", "z"), listOf("-y", "-x", "-z"),
                listOf("x", "z", "-y"), listOf("x", "-z", "y"), listOf("-x", "z", "y"), listOf("-x", "-z", "-y"),
                listOf("z", "x", "y"), listOf("-z", "x", "-y"), listOf("z", "-x", "-y"), listOf("-z", "-x", "y"),
                listOf("y", "z", "x"), listOf("y", "-z", "-x"), listOf("-y", "z", "-x"), listOf("-y", "-z", "x"),
                listOf("z", "y", "-x"), listOf("-z", "y", "x"), listOf("z", "-y", "x"), listOf("-z", "-y", "-x"),
            ).map(Rotation::create)

            fun createRotations(points: Set<Point3d>, translation: Point3d): Map<List<String>, Set<Point3d>> {
                if (translation == Point3d(0, 0, 0)) {
                    points
                } else {
                    points.map { it + translation }.toSet()
                }

                return rotationCache.getOrPut(points) {
                    val rotationsMap = mutableMapOf<List<String>, MutableSet<Point3d>>()
                    for (point in points) {
                        for (rotation in RIGHT_HAND_ROTATIONS) {
                            val rotatedPoint = rotation.rotate(point)
                            rotationsMap.getOrPut(rotation.code) { mutableSetOf() }.add(rotatedPoint)
                        }
                    }

                    rotationsMap
                }.let { rotationsMap ->
                    if (translation == Point3d(0, 0, 0)) {
                        rotationsMap
                    } else {
                        rotationsMap.mapValues { (_, points) -> 
                            points.map { it + translation }.toSet() 
                        }
                    }
                }
            }
        }

        data class Rotation(
            val coordinatePositions: List<Int>,
            val coordinateSigns: List<Int>,
            val code: List<String>,
        ) {
            fun rotate(point: Point3d): Point3d {
                val asVector = point.toVector()
                val signX = coordinateSigns[AXIS_TO_IDX["x"]!!]
                val signY = coordinateSigns[AXIS_TO_IDX["y"]!!]
                val signZ = coordinateSigns[AXIS_TO_IDX["z"]!!]
                val newX = signX * asVector[coordinatePositions[AXIS_TO_IDX["x"]!!]]
                val newY = signY * asVector[coordinatePositions[AXIS_TO_IDX["y"]!!]]
                val newZ = signZ * asVector[coordinatePositions[AXIS_TO_IDX["z"]!!]]

                return Point3d(newX, newY, newZ)
            }

            override fun toString(): String = "rot: ${code.map { String.format("%2s", it) }}"

            companion object {
                private val AXIS_TO_IDX = mapOf("x" to 0, "y" to 1, "z" to 2)
                fun create(rotationCode: List<String>): Rotation {
                    val positions = rotationCode
                        .map { it.replace("-", "") } // "-y, z, x" --> "y, z, x"

                    val coordinatePositions = AXIS_TO_IDX.keys
                        .map { positions.indexOf(it) } // 2, 0, 1

                    val coordinateSigns = AXIS_TO_IDX.values
                        .map { idx -> rotationCode[coordinatePositions[idx]] }  // 1, -1, 1
                        .map { if (it.startsWith("-")) -1 else 1 }

                    return Rotation(coordinatePositions, coordinateSigns, rotationCode)
                }
            }
        }


    }


    override fun solvePartTwo(lines: List<String>) {
        val distance = findLargestManhattanDistance(translations)
        println(distance)
    }

    fun findLargestManhattanDistance(translations: MutableList<Point3d>): Int {
        var maxDistance = 0

        for (i in 0 until translations.size - 1) {
            val point = translations[i]
            for (j in i + 1 until translations.size) {
                val other = translations[j]
                val distance = (point - other).manhattan()
                if (distance > maxDistance) {
                    maxDistance = distance
                }
            }
        }

        return maxDistance
    }
}
