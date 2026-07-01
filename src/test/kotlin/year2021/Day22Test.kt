package year2021

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import year2021.Day22.Cuboid
import year2021.Day22.Operation
import year2021.Day22.Point3d
import kotlin.random.Random

internal class Day22Test {

    private val day22 = Day22()

    @Test
    fun `volume counts inclusive cubes`() {
        assertEquals(8, Cuboid(Point3d(0, 0, 0), Point3d(1, 1, 1)).volume())
        assertEquals(1, Cuboid(Point3d(5, 5, 5), Point3d(5, 5, 5)).volume())
        assertEquals(64, Cuboid(Point3d(0, 0, 0), Point3d(3, 3, 3)).volume())
    }

    @Test
    fun `intersect returns the shared region`() {
        val first = Cuboid(Point3d(0, 0, 0), Point3d(3, 3, 3))
        val second = Cuboid(Point3d(2, 2, 2), Point3d(5, 5, 5))

        val overlap = first.intersect(second)

        assertEquals(Cuboid(Point3d(2, 2, 2), Point3d(3, 3, 3)), overlap)
        assertEquals(8, overlap!!.volume())
    }

    @Test
    fun `intersect at a shared corner has volume 1`() {
        val first = Cuboid(Point3d(0, 0, 0), Point3d(2, 2, 2))
        val second = Cuboid(Point3d(2, 2, 2), Point3d(4, 4, 4))

        assertEquals(1, first.intersect(second)!!.volume())
    }

    @Test
    fun `intersect returns null when cuboids are disjoint`() {
        val first = Cuboid(Point3d(0, 0, 0), Point3d(2, 2, 2))
        val second = Cuboid(Point3d(3, 3, 3), Point3d(5, 5, 5))

        assertNull(first.intersect(second))
    }

    @Test
    fun `small example with re-on yields 39`() {
        val lines = listOf(
            "on x=10..12,y=10..12,z=10..12",
            "on x=11..13,y=11..13,z=11..13",
            "off x=9..11,y=9..11,z=9..11",
            "on x=10..10,y=10..10,z=10..10",
        )
        val operations = lines.map(Operation::parse)

        assertEquals(39, day22.countOn(operations))
    }

    @Test
    fun `part one larger example within init range yields 590784`() {
        val operations = LARGER_EXAMPLE.map(Operation::parse).filter(Operation::inInitRange)

        assertEquals(590784, day22.countOn(operations))
    }

    @Test
    fun `inclusion-exclusion matches brute-force grid for random inputs`() {
        val random = Random(42)
        repeat(40) {
            val operations = (0..random.nextInt(4, 12)).map { randomOperation(random) }

            assertEquals(bruteForceOn(operations), day22.countOn(operations))
        }
    }

    private fun randomOperation(random: Random): Operation {
        val state = if (random.nextBoolean()) "on" else "off"
        fun axis(): String {
            val a = random.nextInt(-6, 7)
            val b = random.nextInt(-6, 7)
            return "${minOf(a, b)}..${maxOf(a, b)}"
        }
        return Operation.parse("$state x=${axis()},y=${axis()},z=${axis()}")
    }

    /** Ground truth: literally toggle a bounded grid of cubes. */
    private fun bruteForceOn(operations: List<Operation>): Long {
        val on = HashSet<Triple<Int, Int, Int>>()
        for (op in operations) {
            for (x in op.x) for (y in op.y) for (z in op.z) {
                val cell = Triple(x, y, z)
                if (op.state == Operation.State.ON) on.add(cell) else on.remove(cell)
            }
        }
        return on.size.toLong()
    }

    companion object {
        private val LARGER_EXAMPLE = listOf(
            "on x=-20..26,y=-36..17,z=-47..7",
            "on x=-20..33,y=-21..23,z=-26..28",
            "on x=-22..28,y=-29..23,z=-38..16",
            "on x=-46..7,y=-6..46,z=-50..-1",
            "on x=-49..1,y=-3..46,z=-24..28",
            "on x=2..47,y=-22..22,z=-23..27",
            "on x=-27..23,y=-28..26,z=-21..29",
            "on x=-39..5,y=-6..47,z=-3..44",
            "on x=-30..21,y=-8..43,z=-13..34",
            "on x=-22..26,y=-27..20,z=-29..19",
            "off x=-48..-32,y=26..41,z=-47..-37",
            "on x=-12..35,y=6..50,z=-50..-2",
            "off x=-48..-32,y=-32..-16,z=-15..-5",
            "on x=-18..26,y=-33..15,z=-7..46",
            "off x=-40..-22,y=-38..-28,z=23..41",
            "on x=-16..35,y=-41..10,z=-47..6",
            "off x=-32..-23,y=11..30,z=-14..3",
            "on x=-49..-5,y=-3..45,z=-29..18",
            "off x=18..30,y=-20..-8,z=-3..13",
            "on x=-41..9,y=-7..43,z=-33..15",
            "on x=-54112..-39298,y=-85059..-49293,z=-27449..7877",
            "on x=967..23432,y=45373..81175,z=27513..53682",
        )
    }
}
