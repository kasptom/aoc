package year2023

import aoc.IAocTaskKt
import kotlin.math.sign
import kotlin.math.sqrt

class Day24 : IAocTaskKt {
    //    override fun getFileName(): String = "aoc2023/input_24_test.txt"
    override fun getFileName(): String = "aoc2023/input_24.txt"

    override fun solvePartOne(lines: List<String>) {
        val hailstones = lines.map(Hailstone::parse)
        val (min, max) = if (getFileName().endsWith("test.txt")) {
            Pair(7.0, 27.0)
        } else {
            Pair(200000000000000.0, 400000000000000.0)
        }
        val result = countCollisions(min, max, hailstones)
        println(result)
    }

    private fun countCollisions(min: Double, max: Double, hailstones: List<Hailstone>): Long {
        var collisionsCount = 0L
        for (first in hailstones) {
            for (second in hailstones.filter { it != first }) {
                val crossPoint = first.cross(second)
                val crossingInFuture = crossPoint != null &&
                        first.crossingInFuture(crossPoint, min, max) != null &&
                        second.crossingInFuture(crossPoint, min, max) != null
                if (crossingInFuture) collisionsCount++
            }
        }
        return collisionsCount / 2
    }

    override fun solvePartTwo(lines: List<String>) {
        val hailstones = lines.map(Hailstone::parse)
        val delta = -500L..500L
        while (true) {
            val hail = hailstones.shuffled().take(4)
            delta.forEach { dx ->
                delta.forEach { dy ->
                    val hail0 = hail[0].plusVelocity(dx, dy)
                    val crossings = hail.drop(1).mapNotNull {
                        it.plusVelocity(dx, dy).crossing(hail0)
                    }
                    if (crossings.size == 3 &&
                        crossings.all { it.point.x == crossings.first().point.x } &&
                        crossings.all { it.point.y == crossings.first().point.y }
                    ) {
                        val (cross1, cross2, cross3) = crossings
                        delta.forEach { dz ->
                            val z1 = hail[1].zPositionAtTime(cross1.time, dz)
                            val z2 = hail[2].zPositionAtTime(cross2.time, dz)
                            val z3 = hail[3].zPositionAtTime(cross3.time, dz)
                            if (z1 == z2 && z2 == z3) {
                                println((crossings.first().point.x + crossings.first().point.y + z1).toLong())
                                return
                            }

                        }
                    }
                }
            }
        }
    }

    data class Hailstone(val pos: Vector3D, val velocity: Vector3D) : Comparable<Hailstone> {
        private val slope = velocity.y / velocity.x.toDouble()

        override operator fun compareTo(other: Hailstone): Int {
            return if (pos != other.pos) return pos.compareTo(other.pos)
            else velocity.compareTo(other.velocity)
        }

        fun crossing(other: Hailstone): Crossing? {
            if (velocity.x == 0L || other.velocity.x == 0L) {
                return null
            }

            val c = pos.y - slope * pos.x
            val otherC = other.pos.y - other.slope * other.pos.x

            val x = (otherC - c) / (slope - other.slope)
            val t1 = (x - pos.x) / velocity.x
            val t2 = (x - other.pos.x) / other.velocity.x

            if (t1 < 0 || t2 < 0) return null

            val y = slope * (x - pos.x) + pos.y
            return Crossing(Point2D(x, y), t1)
        }

        private fun crossesInTheArea(cross: Point2D, min: Double, max: Double): Boolean {
            val (x, y) = cross
            //            println("\t\t$this crosses with $second in $cross")
            return x in min..max && (y in min..max)
        }

        data class Crossing(val point: Point2D, val time: Double) {
            init {
                require(time > 0)
            }
        }

        fun cross(second: Hailstone): Point2D? {
            if (velocity.xy().parallelTo(second.velocity.xy())) {
//                println("$this parallel to $second")
                return null
            }
            val firstLine = Line2D.from(pos.xy(), velocity.xy())
            val secondLine = Line2D.from(second.pos.xy(), second.velocity.xy())
            return firstLine.crossPoint(secondLine)
        }

        override fun toString(): String = "(P=$pos, v=$velocity, |v|=${velocity.speed})"

        fun crossingInFuture(
            crossPoint: Point2D,
            min: Double = Double.MIN_VALUE,
            max: Double = Double.MAX_VALUE,
        ): Crossing? {
            val diff = crossPoint - pos.xy()
            val velXy = velocity.xy()
            if (crossesInTheArea(crossPoint, min, max) &&
                sign(diff.x) == sign(velXy.x) &&
                sign(diff.y) == sign(velXy.y)
            ) {
                return Crossing(crossPoint, diff.x / velXy.x)
            }
            return null
        }

        fun move(time: Double): Vector3D = pos + velocity * time
        fun plusVelocity(dx: Long, dy: Long): Hailstone =
            copy(velocity = velocity.plus(Vector3D(dx, dy, 0)))

        fun zPositionAtTime(time: Double, dz: Long): Double = (pos.z + time * (velocity.z + dz))

        companion object {
            fun parse(line: String): Hailstone {
                val (rawPos, rawVelo) = line
                    .replace(" ", "")
                    .split("@")
                val (px, py, pz) = rawPos.split(",").filter(String::isNotEmpty)
                    .map { it.toLong() }

                val (vx, vy, vz) = rawVelo.split(",").filter(String::isNotEmpty)
                    .map { it.toLong() }

                return Hailstone(Vector3D(px, py, pz), Vector3D(vx, vy, vz))
            }
        }
    }

    data class Vector3D(val x: Long, val y: Long, val z: Long) : Comparable<Vector3D> {
        val speed: Double = sqrt((x * x + y * y + z * z).toDouble())

        override operator fun compareTo(other: Vector3D): Int {
            if (x != other.x) return x.compareTo(other.x)
            if (y != other.y) return y.compareTo(other.y)
            return z.compareTo(other.z)
        }

        operator fun times(other: Vector3D): Vector3D =
            Vector3D(y * other.z - other.y * z, other.x * z - x * other.z, other.y * x - y * other.x)

        fun xy(): Point2D {
            return Point2D(x.toDouble(), y.toDouble())
        }

        override fun toString(): String = "($x,$y,$z)"

        operator fun times(time: Long): Vector3D = Vector3D(x * time, y * time, z * time)
        operator fun times(time: Double): Vector3D =
            Vector3D((x * time).toLong(), (y * time).toLong(), (z * time).toLong())

        operator fun plus(other: Vector3D): Vector3D = Vector3D(x + other.x, y + other.y, z + other.z)
        operator fun minus(other: Vector3D): Vector3D = Vector3D(x - other.x, y - other.y, z - other.z)

        companion object {
            @JvmStatic
            fun main(args: Array<String>) {
                println(Vector3D(2, 5, -1) * Vector3D(-2, 4, -6))
            }
        }
    }

    data class Point2D(val x: Double, val y: Double) {
        operator fun compareTo(other: Point2D): Int {
            if (x == other.x) return x.compareTo(other.x)
            return y.compareTo(other.y)
        }

        fun parallelTo(other: Point2D): Boolean = x * other.y == y * other.x
        override fun toString(): String = "($x,$y)"
        operator fun minus(xy: Point2D): Point2D = Point2D(x - xy.x, y - xy.y)
    }

    data class Line2D(val a: Double, val b: Double, val c: Double) {
        fun crossPoint(other: Line2D): Point2D {
            // l: Ax + By + C = 0
            // k: Dx + Ey + F = 0
            // point:
            // Ax + By + C = Dx + Ey + F
            // x (A-D) + y (B-E) = F - C
            val (d, e, f) = other
            val x = (c * e - b * f) / (b * d - a * e)
            val y = (a * f - c * d) / (b * d - a * e)
            return Point2D(x, y)
        }

        companion object {
            // l: Ax + By + C = 0
            fun from(point: Point2D, vec: Point2D): Line2D {
                val normVec = Point2D(-vec.y, vec.x)
                val a = normVec.x
                val b = normVec.y
                // point in l so:
                val c = -(a * point.x + b * point.y)
                return Line2D(a, b, c)
            }
        }
    }
}
