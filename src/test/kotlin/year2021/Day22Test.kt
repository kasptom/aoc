package year2021

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import year2021.Day22.Cuboid
import year2021.Day22.Operation.State.OFF
import year2021.Day22.Operation.State.ON
import year2021.Day22.Point3d

internal class Day22Test {

    @Test
    fun testCollide01() {
        val first = Cuboid(Point3d(0, 0, 0), Point3d(1, 1, 1), ON)
        val second = Cuboid(Point3d(0, 0, 0), Point3d(1, 1, 1), ON)

        val (firstLeftovers, common, secondLeftovers) = first.collide(second)

        assertEquals(8, (firstLeftovers + secondLeftovers + common).sumOf { it.volume() })
    }

    @Test
    fun testCollide02() {
        val first = Cuboid(Point3d(0, 0, 0), Point3d(1, 1, 1), ON)
        val second = Cuboid(Point3d(0, 0, 0), Point3d(1, 1, 1), OFF)

        val (firstLeftovers, common, secondLeftovers) = first.collide(second)
        val collisionResult = firstLeftovers + secondLeftovers + common

        assertTrue(collisionResult.all { it.isOff() })
        assertEquals(0, collisionResult.filter { it.isOn() }.sumOf { it.volume() })
    }

    @Test
    // 4x4x4 cube with removed 2x2x2 middle
    fun testCollide1() {
        val first = Cuboid(Point3d(0, 0, 0), Point3d(3, 3, 3), ON) // 64
        val second = Cuboid(Point3d(1, 1, 1), Point3d(2, 2, 2), OFF) // 8

        val (firstLeftovers, common, secondLeftovers) = first.collide(second)

        assertTrue(firstLeftovers.all { it.isOn() })
        assertEquals(26, firstLeftovers.size)
        assertEquals(56, firstLeftovers.filter { it.isOn() }.sumOf { it.volume() })
    }

    @Test
    // 4x4x4 cube with all elements because second is on
    fun testCollide2() {
        val first = Cuboid(Point3d(0, 0, 0), Point3d(3, 3, 3), ON)
        val second = Cuboid(Point3d(1, 1, 1), Point3d(2, 2, 2), ON)

        val (firstLeftovers, common, secondLeftovers) = first.collide(second)
        val collisionResult = firstLeftovers + secondLeftovers + common

        assertTrue(collisionResult.all { it.isOn() })
        assertEquals(64, collisionResult.filter { it.isOn() }.sumOf { it.volume() })
    }

    @Test
    // rubiks intersecting at one corner
    fun testCollide3() {
        val first = Cuboid(Point3d(0, 0, 0), Point3d(2, 2, 2), ON)
        val second = Cuboid(Point3d(2, 2, 2), Point3d(4, 4, 4), ON)

        val (firstLeftovers, common, secondLeftovers) = first.collide(second)
        val collisionResult = firstLeftovers + secondLeftovers + common

        assertTrue(collisionResult.all { it.isOn() })
//        assertEquals(53, collisionResult.size)
        assertEquals(53, collisionResult.filter { it.isOn() }.sumOf { it.volume() })
    }

    @Test
    // rubiks intersecting at one corner one off
    fun testCollide4() {
        val first = Cuboid(Point3d(0, 0, 0), Point3d(2, 2, 2), ON)
        val second = Cuboid(Point3d(2, 2, 2), Point3d(4, 4, 4), OFF)

        val (firstLeftovers, common, secondLeftovers) = first.collide(second)

        assertEquals(26, secondLeftovers.sumOf { it.volume() })
        assertEquals(26, firstLeftovers.sumOf { it.volume() })
        assertEquals(1, common.sumOf { it.volume() })
        assertTrue(firstLeftovers.all { it.isOn() })
        assertTrue(secondLeftovers.all { it.isOff() })
    }

    @Test
    @Disabled
    // separate
    fun `collide should throw when cuboids are separate`() {
        val first = Cuboid(Point3d(0, 0, 0), Point3d(2, 2, 2), ON)
        val second = Cuboid(Point3d(3, 3, 3), Point3d(5, 5, 5), OFF)

        assertThrows<IllegalStateException> { first.collide(second) }
    }

    @Test
    fun testCollide6() {
        val first = Cuboid(Point3d(0, 0, 0), Point3d(2, 2, 2), ON)
        val second = Cuboid(Point3d(2, 0, 0), Point3d(3, 2, 2), OFF)

        val (firstLeftovers, common, secondLeftovers) = first.collide(second)

        assertTrue(firstLeftovers.all { it.isOn() })
        assertTrue(secondLeftovers.all { it.isOff() })
        assertEquals(18, firstLeftovers.sumOf { it.volume() })
    }

    @Test
    fun testIsInRange() {
        val first = Cuboid(Point3d(0, 0, 0), Point3d(3, 3, 3), ON)
        val second = Cuboid(Point3d(2, 0, 0), Point3d(5, 3, 3), OFF)

        assertTrue(first.isInRange(second))
        assertTrue(second.isInRange(first))
    }

    @Test
    fun testIsInRange2() {
        val first = Cuboid(Point3d(0, 0, 0), Point3d(3, 3, 3), ON)
        val second = Cuboid(Point3d(3, 3, 3), Point3d(5, 5, 5), OFF)

        assertTrue(first.isInRange(second))
        assertTrue(second.isInRange(first))
    }

    @Test
    /**
    overlapping cuboids detected
    ((-20, -29, -38) - (26, -22, -27), ON) vs
    ((-22, -29, -38) - (26, 17, 7), ON)
    history: [((-20, -36, -47) - (26, 17, 7), ON), ((-20, -21, -26) - (33, 23, 28), ON)]
     */
    fun cornerCase() {

    }

    @Test
    fun testCollideExample4Cuboids() {
        val first = Cuboid(Point3d(10, 10, 10), Point3d(12, 12, 12), ON)
        val second = Cuboid(Point3d(11, 11, 11), Point3d(13, 13, 13), ON)

        val (firstLeftovers, common, secondLeftovers) = first.collide(second)
        val collisionResult = firstLeftovers + secondLeftovers + common

        assertEquals(46, collisionResult.sumOf { it.volume() })
    }
}