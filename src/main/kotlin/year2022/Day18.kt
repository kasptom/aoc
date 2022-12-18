package year2022

import aoc.IAocTaskKt

class Day18 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_18.txt"

    override fun solvePartOne(lines: List<String>) {
        val cubes = lines.map(Cube::parse)
        cubes.onEach { println(it) }
        val points = cubes[0].getPoints()
        val surfaceNotConnected = cubes
        val firstCubeSides = Side.getSides(cubes[0])
        println(firstCubeSides.count())
        println(firstCubeSides)
        val sides = cubes.map { Side.getSides(it) }.flatten().toSet()
        println(2 * sides.count() - cubes.count() * 6)
    }

    override fun solvePartTwo(lines: List<String>) {
        TODO("Not yet implemented")
    }

    data class Cube(val x: Int, val y: Int, val z: Int) {
        fun getPoints(): List<Point3D> {
            return (x - 1..x).map { x ->
                (y - 1..y).map { y ->
                    (z - 1..z).map { z ->
                        Point3D(x, y, z)
                    }
                }.flatten()
            }.flatten()
        }

        fun getSidesX(value: Int, points: List<Point3D>) = points.filter { it.x == value }
        fun getSidesY(value: Int, points: List<Point3D>) = points.filter { it.y == value }
        fun getSidesZ(value: Int, points: List<Point3D>) = points.filter { it.z == value }

        companion object {
            fun parse(line: String): Cube {
                val xyz = line.split(",").map { it.toInt() }
                val (x, y, z) = Triple(xyz[0], xyz[1], xyz[2])
                return Cube(x, y, z)
            }
        }
    }

    data class Side(val points: Set<Point3D>) {
        companion object {
            fun getSides(cube: Cube): Set<Side> {
                val upperX = Side(cube.getSidesX(cube.x, cube.getPoints()).toSet())
                val lowerX = Side(cube.getSidesX(cube.x - 1, cube.getPoints()).toSet())

                val upperY = Side(cube.getSidesY(cube.y, cube.getPoints()).toSet())
                val lowerY = Side(cube.getSidesY(cube.y - 1, cube.getPoints()).toSet())

                val upperZ = Side(cube.getSidesZ(cube.z, cube.getPoints()).toSet())
                val lowerZ = Side(cube.getSidesZ(cube.z - 1, cube.getPoints()).toSet())

                return setOf(upperX, upperY, upperZ, lowerX, lowerY, lowerZ)
            }
        }
    }

    data class Point3D(val x: Int, val y: Int, val z: Int)
}