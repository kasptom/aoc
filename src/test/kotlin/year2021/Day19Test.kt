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

    @Test
    fun manhatanCounter() {
        val input = "[(-1384,-51,-28), (-2459,20,-28), (-2423,-82,1293), (1068,-28,46), (1079,1050,25), (1057,-1345,-11), (2256,-1265,80), (2379,-1302,-1231), (2225,-113,122), (-2423,1137,148), (-3670,1047,5), (2229,1227,-1), (-1324,-1197,69), (-1369,-2427,131), (-1203,-1282,-1192), (-6,1158,-3), (-115,2420,57), (-130,2317,1215), (-54,1154,-1091), (-1247,2338,-35), (-1348,3620,92), (-1211,4725,-29), (-1208,3526,-1199), (-2498,4808,99), (-1211,2280,1298), (-86,1126,1316), (1070,-154,-1168)]"
            .replace("[", "")
            .replace("]", "")
            .split(", ")
            .map { it.replace("(","")
                .replace(")", "")
                .split(",")
                .map(String::toInt)
            }
            .map { it -> Point3d(it[0], it[1], it[2]) }

        val result = Day19().findLargestManhattanDistance(input.toMutableList())
        println(result)
    }
}