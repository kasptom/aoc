package year2023

import aoc.IAocTaskKt
import kotlin.math.sign

class Day24 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_24.txt"

    override fun solvePartOne(lines: List<String>) {
        val hailstones = lines.map(Hailstone::parse)
        hailstones.onEach {
            println(it)
        }
        val (min, max) = if (getFileName().endsWith("test.txt")) Pair(7L, 27L) else Pair(
            200000000000000L,
            400000000000000L
        )
        val result = checkCollisions(min, max, hailstones)
        println(result)
    }

    private fun checkCollisions(min: Long, max: Long, hailstones: List<Hailstone>): Long {
        var sum = 0L
        for (first in hailstones) {
            for (second in hailstones) {
                if (first != second) {
                    val cross = first.cross(second) ?: continue

                    if (first.crossInTheFuture(cross) && second.crossInTheFuture(cross) && first.crossesInTheArea(
                            cross,
                            min.toDouble(),
                            max.toDouble()
                        )
                    ) {
                        println("CROSSING in area $min < ${first.cross(second)} $max")
                        sum++
                    }

                }
            }
        }
        return sum / 2
    }

    override fun solvePartTwo(lines: List<String>) {
        println("Not yet implemented")
    }

    data class Hailstone(val pos: Point3D, val velocity: Point3D) {
        operator fun compareTo(second: Hailstone): Int {
            return if (pos != second.pos) return pos.compareTo(second.pos)
            else velocity.compareTo(second.velocity)
        }

        fun crosses(second: Hailstone, min: Double, max: Double): Boolean {
            val cross = cross(second)
            if (cross == null) return false
            return crossesInTheArea(cross, min, max)
        }

        fun crossesInTheArea(cross: Point2D, min: Double, max: Double): Boolean {
            val (x, y) = cross
            //            println("\t\t$this crosses with $second in $cross")
            return x in min..max && (y in min..max)
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

        override fun toString(): String {
            return "(P=$pos, v=$velocity)"
        }

        fun crossInTheFuture(crossPoint: Point2D): Boolean {
            val diff = crossPoint - pos.xy()
            val velXy = velocity.xy()
            return sign(diff.x) == sign(velXy.x) && sign(diff.y) == sign(velXy.y)
        }

        companion object {
            fun parse(line: String): Hailstone {
                val (rawPos, rawVelo) = line
                    .replace(" ", "")
                    .split("@")
                val (px, py, pz) = rawPos.split(",").filter(String::isNotEmpty)
                    .map { it.toLong() }

                val (vx, vy, vz) = rawVelo.split(",").filter(String::isNotEmpty)
                    .map { it.toLong() }

                return Hailstone(Point3D(px, py, pz), Point3D(vx, vy, vz))
            }
        }
    }

    data class Point3D(val x: Long, val y: Long, val z: Long) {
        operator fun compareTo(other: Point3D): Int {
            if (x == other.x) return x.compareTo(other.x)
            if (y == other.y) return y.compareTo(other.y)
            return z.compareTo(other.z)
        }

        fun xy(): Point2D {
            return Point2D(x.toDouble(), y.toDouble())
        }

        override fun toString(): String {
            return "($x,$y,$z)"
        }
    }

    data class Point2D(val x: Double, val y: Double) {
        operator fun compareTo(other: Point2D): Int {
            if (x == other.x) return x.compareTo(other.x)
            return y.compareTo(other.y)
        }

        fun parallelTo(other: Point2D): Boolean {
            return x * other.y == y * other.x
        }

        override fun toString(): String {
            return "($x,$y)"
        }

        operator fun minus(xy: Point2D): Point2D {
            return Point2D(x - xy.x, y - xy.y)
        }
    }

    data class Line2D(val a: Double, val b: Double, val c: Double) {
        fun crossPoint(other: Line2D): Point2D? {
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