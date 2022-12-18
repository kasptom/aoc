package year2022

import aoc.IAocTaskKt
import java.util.function.Predicate
import kotlin.math.pow
import kotlin.math.sqrt

class Day18 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_18.txt"

    override fun solvePartOne(lines: List<String>) {
        val cubes = lines.map(Cube::parse)
        val sides = cubes.map { it.getFaces() }.flatten().toSet()
        println(2 * sides.count() - cubes.count() * 6)
    }

    override fun solvePartTwo(lines: List<String>) {
        val cubes = lines.map(Cube::parse)
        val cubeToSides = cubes.map { Pair(it, it.getFaces()) }

        val faceToCount = mutableMapOf<Face, Int>()
        for (entry in cubeToSides) {
            for (side in entry.second) {
                faceToCount.putIfAbsent(side, 0)
                faceToCount[side] = faceToCount[side]!! + 1
            }
        }
//        println(sideToCount.filterValues { it == 2 }.count())
        val surfaceAreaFaces = faceToCount.entries
            .filter { sideCount -> sideCount.value == 1 }
            .map { it.key }
            .toSet()

//        surfaceAreaFaces.onEach { println(it.sides) }

        surfaceAreaFaces.first().isWatered = true
        var countChanged = true
        var count = surfaceAreaFaces.count { it.isWatered }

        val sideToFaces = mutableMapOf<Side, List<Face>>()

        surfaceAreaFaces.forEach {face ->
            for (side in face.sides) {
                sideToFaces.putIfAbsent(side, emptyList())
                sideToFaces[side] = sideToFaces[side]!! + face
            }
        }

        while(countChanged) {
            propagateWater(surfaceAreaFaces, sideToFaces)
            val newCount = surfaceAreaFaces.count { it.isWatered }
            countChanged = count != newCount
            count = newCount
        }
        println(count)
    }

    private fun propagateWater(faces: Set<Face>, sideToFaces: MutableMap<Side, List<Face>>) {
        for (wateredFace in faces.filter { it.isWatered && !it.neighboursFound }) {
            wateredFace.findNeighbours(sideToFaces)
            wateredFace.sides.forEach { side -> side.neighbour?.isWatered = true}
        }
    }

    data class Cube(val x: Int, val y: Int, val z: Int) {
        private val midPoint = Point3D(x - 0.5, y - 0.5, z - 0.5)

        private fun getPoints(): List<Point3D> {
            return (x - 1..x).map { x ->
                (y - 1..y).map { y ->
                    (z - 1..z).map { z ->
                        Point3D(x.toDouble(), y.toDouble(), z.toDouble())
                    }
                }.flatten()
            }.flatten()
        }

        private fun getSidesX(value: Int, points: List<Point3D>) = points.filter { it.x == value.toDouble() }
        private fun getSidesY(value: Int, points: List<Point3D>) = points.filter { it.y == value.toDouble() }
        private fun getSidesZ(value: Int, points: List<Point3D>) = points.filter { it.z == value.toDouble() }
        override fun toString(): String {
            return "CUBE($x,$y,$z, mid=$midPoint)"
        }

        fun getFaces(): Set<Face> {
            val cubeMiddlePoint = Point3D(
                x = x - 0.5,
                y = y - 0.5,
                z = z - 0.5
            )
            val upperXPoint = cubeMiddlePoint + Point3D(1.0, 0.0, 0.0)
            val upperX = Face(getSidesX(x, getPoints()).toSet(), cubeMiddlePoint, upperXPoint)
            val lowerXPoint = cubeMiddlePoint + Point3D(-1.0, 0.0, 0.0)
            val lowerX = Face(getSidesX(x - 1, getPoints()).toSet(), cubeMiddlePoint, lowerXPoint)

            val upperYPoint = cubeMiddlePoint + Point3D(0.0, 1.0, 0.0)
            val upperY = Face(getSidesY(y, getPoints()).toSet(), cubeMiddlePoint, upperYPoint)
            val lowerYPoint = cubeMiddlePoint + Point3D(0.0, -1.0, 0.0)
            val lowerY = Face(getSidesY(y - 1, getPoints()).toSet(), cubeMiddlePoint, lowerYPoint)

            val upperZPoint = cubeMiddlePoint + Point3D(0.0, 0.0, 1.0)
            val upperZ = Face(getSidesZ(z, getPoints()).toSet(), cubeMiddlePoint, upperZPoint)
            val lowerZPoint = cubeMiddlePoint + Point3D(0.0, 0.0, -1.0)
            val lowerZ = Face(getSidesZ(z - 1, getPoints()).toSet(), cubeMiddlePoint, lowerZPoint)

            return setOf(upperX, upperY, upperZ, lowerX, lowerY, lowerZ)
        }

        companion object {
            fun parse(line: String): Cube {
                val xyz = line.split(",").map { it.toInt() }
                val (x, y, z) = Triple(xyz[0], xyz[1], xyz[2])
                return Cube(x, y, z)
            }
        }
    }

    data class Face(val points: Set<Point3D>, val cubeMidPoint: Point3D, val pointsToward: Point3D) {
        var neighboursFound: Boolean = false
        val sides: List<Side> = createSides(points)

        private fun createSides(points: Set<Point3D>): List<Side> {
            val predicates: List<Predicate<Pair<Point3D, Point3D>>> = listOf(
                Predicate { (a, b) -> a.x == b.x && a.y == b.y && a.z != b.z },
                Predicate { (a, b) -> a.x == b.x && a.y != b.y && a.z == b.z },
                Predicate { (a, b) -> a.x != b.x && a.y == b.y && a.z == b.z },
            )
            return predicates.map { pred ->
                points.map { first ->
                    points.map { second -> Pair(first, second) }
                }.flatten()
                    .filter { pred.test(it) }
                    .map { Side(setOf(it.first, it.second)) }
            }.flatten()
        }

        var isWatered: Boolean = false

        init {
            if (points.size != 4) throw IllegalStateException("side must have 4 points, got ${points.size}")
        }

        override fun toString(): String = "F($points, $cubeMidPoint -> $pointsToward)"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Face) return false

            if (points != other.points) return false

            return true
        }

        override fun hashCode(): Int {
            return points.hashCode()
        }

        fun findNeighbours(sideToFaces: MutableMap<Side, List<Face>>) {
            if (neighboursFound) {
                return
            }
            for (side in sides) {
                side.neighbour = side.findNeighbour(this, sideToFaces)
            }
            neighboursFound = true
        }
    }

    data class Side(val points: Set<Point3D>) {
        var neighbour: Face? = null

        init {
            if (points.size != 2) throw IllegalStateException("Side must have two points: $points")
        }

        override fun toString(): String {
            return "S($points)"
        }

        fun findNeighbour(face: Face, sideToFaces: MutableMap<Side, List<Face>>): Face? {
            return sideToFaces[this]!!.filter { it != face }
                .minByOrNull { it.pointsToward.distanceTo(face.pointsToward) }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Side) return false

            if (points != other.points) return false

            return true
        }

        override fun hashCode(): Int {
            return points.hashCode()
        }
    }

    data class Point3D(val x: Double, val y: Double, val z: Double) : Comparable<Point3D> {
        override fun compareTo(other: Point3D): Int {
            if (x != other.x) x.compareTo(other.x)
            if (y != other.y) y.compareTo(other.y)
            return z.compareTo(other.z)
        }

        override fun toString(): String {
            return "($x, $y, $z)"
        }

        operator fun plus(point: Point3D): Point3D = Point3D(x + point.x, y + point.y, z + point.z)
        fun distanceTo(other: Point3D): Double {
            return sqrt((x - other.x).pow(2) + (y - other.y).pow(2) + (z - other.z).pow(2))
        }

        operator fun minus(point: Point3D): Point3D = Point3D(x - point.x, y - point.y, z - point.z)
    }
}
