package year2022

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import utils.InputReader
import year2022.Day22.EdgeChainFinder

class Day22Test {

    @Test
    fun `should solve test case 1`() {
        // given
        val lines = InputReader("aoc2022/input_22_test.txt").readLines()
        val (map, moves) = Day22().getMapAndMoves(lines)

        // when
        val password = Day22().getPassword(map, moves)

        // then
        assertEquals(password, 6032)
    }

    @Test
    fun `should solve test case 2`() {
        // given
        val cubeSideSize = 4
        val lines = InputReader("aoc2022/input_22_test.txt").readLines()
        val (map, moves) = Day22().getMapAndMoves(lines)

        // when
        map.createCubicConnections(cubeSideSize)
        println(map.grid.displayCoordinates())
        // and
        val password = Day22().getCubePassword(map, moves, cubeSideSize)

        // then
        assertEquals(password, 5031)
    }

    @Test
    fun `should solve part 1`() {
        // given
        val lines = InputReader("aoc2022/input_22.txt").readLines()
        val expected = InputReader("aoc2022/answer_22.txt").readLines()
        val (map, moves) = Day22().getMapAndMoves(lines)

        // when
        val password = Day22().getPassword(map, moves)

        // then
        assertEquals(password, expected[0].toInt())
    }

    @Test
    fun `should solve part 2`() {
        // given
        val lines = InputReader("aoc2022/input_22.txt").readLines()
        val expected = InputReader("aoc2022/answer_22.txt").readLines()
        val cubeSideSize = 50
        val (map, moves) = Day22().getMapAndMoves(lines)

        // when
        map.createCubicConnections(cubeSideSize)
        println(map.grid.displayCoordinates())
        // and
        val password = Day22().getCubePassword(map, moves, cubeSideSize, debug = false)

        // then
        assertEquals(expected[1].toInt(), password)
    }

    // neighbour finder
    @Test
    fun `should return neighbours in desired orders test 2`() {
        // given
        val lines = InputReader("aoc2022/input_22_test.txt").readLines()
        val (map, _) = Day22().getMapAndMoves(lines)
        val sut = EdgeChainFinder(map)
        val cubeSideSize = 4

        // when
        sut.markEdges()
        map.createCubicConnections(cubeSideSize)
        println(map.grid.displayCoordinates())

        // then
        assertEquals(map.edgePointsChain.distinct().count(), map.edgePointsChain.count())
        assertEquals(14, map.edges.size)
        assertTrue(map.edges.all { it.points.size == cubeSideSize })
    }

    @Test
    fun `should return neighbours in desired orders part 2`() {
        // given
        val lines = InputReader("aoc2022/input_22.txt").readLines()
        val (map, _) = Day22().getMapAndMoves(lines)
        val sut = EdgeChainFinder(map)
        val cubeSideSize = 50

        // when
        sut.markEdges()
        map.createCubicConnections(cubeSideSize)
//        println(map.grid.displayCoordinates())

        // then
        assertEquals(map.edgePointsChain.distinct().count(), map.edgePointsChain.count())
        assertEquals(14, map.edges.size)
        assertTrue(map.edges.all { it.points.size == cubeSideSize })
    }
}