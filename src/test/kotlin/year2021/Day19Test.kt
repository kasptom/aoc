package year2021

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import year2021.Day19.Point3d

internal class Day19Test {

    @Test
    fun testCubeGridRotations0() {
        val initialPoints = setOf(
            Point3d(1, 2, 3),
        )

        val cubeGrid = Day19.CubeGrid(initialPoints, Point3d(0, 0, 0))
        cubeGrid.rotations.forEach(::println)

        assertEquals(24, cubeGrid.rotations.count())
    }

    @Test
    fun testCubeGridRotations() {
        val initialPoints = setOf(
            Point3d(-1, -1, 1),
            Point3d(-2, -2, 2),
            Point3d(-3, -3, 3),
            Point3d(-2, -3, 1),
            Point3d(5, 6, -4),
            Point3d(8, 0, 7),
        )

        val cubeGrid = Day19.CubeGrid(initialPoints, translation = Point3d(0, 0, 0))
        cubeGrid.rotations.forEach(::println)

        assertEquals(24, cubeGrid.rotations.count())
    }

    @Test
    fun test() {

    }
}