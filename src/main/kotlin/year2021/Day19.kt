package year2021

import aoc.IAocTaskKt
import kotlin.math.abs

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
        val scanners = parseScanners(lines).toMutableList()
        scanners.forEach(::println)

        var scannerX = scanners.removeFirst()

        val threshold = 12

        val scannersSize = scanners.size

        while (scanners.isNotEmpty()) {
            val (foundScanner, rotationCode) = findTranslatedScannerFor(scannerX, scanners, threshold)!!
            translations.add(foundScanner.cubeGrid.translation)

            val foundWithRemovedRotations = foundScanner.withOnlyRotation(rotationCode)
            scannerX = scannerX.mergeWith(foundWithRemovedRotations)
            println(scannerX)
            println(scannerX.cubeGrid.rotations[INITIAL_ROTATION])
            scanners.removeIf { it.id == foundScanner.id }
            println("progress: ${scannersSize - scanners.size}/$scannersSize (${100.0 * (scannersSize - scanners.size) / scannersSize}[%])")
        }

        val allBeacons = scannerX.cubeGrid.rotations.values.first()

        allBeacons.map { it.toString().replace("(", "").replace(")", "") }
            .sorted().forEach(::println)

        println("done: ${allBeacons.size}")
    }

    @Suppress("SameParameterValue")
    private fun findTranslatedScannerFor(
        scannerX: Scanner,
        scanners: List<Scanner>,
        threshold: Int,
    ): Pair<Scanner, List<String>>? {

        for (scanner in scanners.filter { it.id != scannerX.id }) {


            val scannerXPoints = scannerX.cubeGrid.rotations[INITIAL_ROTATION]!!
            val otherScannerPoints = scanner.cubeGrid.rotations.values.flatten()
//            val translations = otherScannerPoints
//                .map { point -> scannerXPoints.map { it - point } }
//                .flatten()
//            for (translation in translations) {

            println("possible translations count: ${scannerXPoints.size * otherScannerPoints.size}")
            for (scannerXPoint in scannerXPoints) {
                for (otherScannerPoint in otherScannerPoints) {
                    val translation = scannerXPoint - otherScannerPoint
                    val translatedScanner = scanner.translate(translation)
                    val commonPointsRotationCode = scannerX.commonPoints(translatedScanner, threshold)
                    val commonPointsCount: Int = translatedScanner.getRotationByCode(commonPointsRotationCode).count()
                    if (commonPointsCount >= threshold) {
    //                    println("for $scannerX")
                        println("found scanner $translatedScanner ")
                        println("with $commonPointsCount")
                        println("with code $commonPointsRotationCode")
                        return Pair(translatedScanner, commonPointsRotationCode)
                }
            }
//                }
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
        fun translate(translation: Point3d): Scanner {
            val newCubeGrid = cubeGrid.translate(translation)
            return Scanner(id, newCubeGrid)
        }

        fun withOnlyRotation(defaultRotationKey: List<String> = INITIAL_ROTATION): Scanner {
            val initialRotation = cubeGrid.rotations[defaultRotationKey]!!
            val newGrid = CubeGrid(initialRotation,
                cubeGrid.translation,
                rotations = mapOf(defaultRotationKey to initialRotation))
            return Scanner(id, newGrid)
        }

        fun commonPoints(other: Scanner, threshold: Int): List<String> {
            return cubeGrid.commonPointsRotationKey(other.cubeGrid, threshold)
        }

        override fun toString(): String {
            return "Scanner(id=$id, $cubeGrid)"
        }

        fun getRotationByCode(code: List<String>): Set<Point3d> {
            return cubeGrid.rotations[code] ?: emptySet()
        }

        fun mergeWith(foundScanner: Scanner): Scanner {
            val mergedSingleRotation =
                cubeGrid.rotations.values.first() + foundScanner.cubeGrid.rotations.values.first()
            val newGrid = CubeGrid(mergedSingleRotation,
                cubeGrid.translation,
                rotations = mapOf(INITIAL_ROTATION to mergedSingleRotation))

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
        fun toVector(): List<Int> = listOf(x, y, z)
        override fun toString(): String {
            return "($x,$y,$z)"
        }

        operator fun plus(point: Point3d) = Point3d(x + point.x, y + point.y, z + point.z)
        operator fun minus(point: Point3d) = Point3d(x - point.x, y - point.y, z - point.z)

        companion object {
            fun parse(line: String): Point3d {
                val (x, y, z) = line.split(",").filter(String::isNotBlank).map(String::toInt)
                return Point3d(x, y, z)
            }
        }

        fun manhattan(): Int = abs(x) + abs(y) + abs(z)
    }

    class CubeGrid(
        points: Set<Point3d>,
        val translation: Point3d,
        val rotations: Map<List<String>, Set<Point3d>> = createRotations(points, translation),
    ) {
        fun translate(translation: Point3d): CubeGrid {
            return CubeGrid(rotations[INITIAL_ROTATION]!!, translation)
        }

        fun commonPointsRotationKey(other: CubeGrid, threshold: Int): List<String> {
            val firstRotation = rotations.values.first()

            val fittingRotation = other.rotations
                .keys
                .firstOrNull { key ->
                    other.rotations[key]!!
                        .intersect(firstRotation)
                        .count() >= threshold
                }

            return fittingRotation ?: emptyList()
        }

        override fun toString(): String {
            return "GRID(T=$translation, rotations=$rotations)"
        }

        companion object {
            private val RIGHT_HAND_ROTATIONS = listOf(
                listOf("x", "y", "z"), listOf("x", "-y", "-z"), listOf("-x", "y", "-z"), listOf("-x", "-y", "z"),
                listOf("y", "x", "-z"), listOf("-y", "x", "z"), listOf("y", "-x", "z"), listOf("-y", "-x", "-z"),
                listOf("x", "z", "-y"), listOf("x", "-z", "y"), listOf("-x", "z", "y"), listOf("-x", "-z", "-y"),
                listOf("z", "x", "y"), listOf("-z", "x", "-y"), listOf("z", "-x", "-y"), listOf("-z", "-x", "y"),
                listOf("y", "z", "x"), listOf("y", "-z", "-x"), listOf("-y", "z", "-x"), listOf("-y", "-z", "x"),
                listOf("z", "y", "-x"), listOf("-z", "y", "x"), listOf("z", "-y", "x"), listOf("-z", "-y", "-x"),
            ).map(Rotation::create)

            fun createRotations(points: Set<Point3d>, translation: Point3d): Map<List<String>, Set<Point3d>> {
                return RIGHT_HAND_ROTATIONS.map { rotation -> rotate(points, rotation) }
                    .map { Pair(it.first.code, it.second.map { point -> point + translation }) }
                    .groupBy({ it.first }) { it.second }
                    .mapValues { it.value.flatten().toSet() }
            }

            private fun rotate(points: Set<Point3d>, rotation: Rotation): Pair<Rotation, Set<Point3d>> =
                Pair(rotation, points.map { point -> rotation.rotate(point) }.toSet())
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
        println("translations: $translations")
        println(distance)
    }

    fun findLargestManhattanDistance(translations: MutableList<Point3d>): Int {
        return (translations)
            .maxOf { point -> translations.maxOf { other -> (point - other).manhattan() } }
    }
}