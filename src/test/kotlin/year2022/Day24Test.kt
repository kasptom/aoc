package year2022

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Timeout
import utils.InputReader
import java.util.concurrent.TimeUnit

class Day24Test {

    @Test
    fun `should solve test case 1`() {
        // given
        val lines = InputReader("aoc2022/input_24_test2.txt").readLines()

        // when
        val (timeToValley, start, end) = Day24.ValleyMap.parse(lines)
        val minutes = Day24().findShortestPath(0, start, end, timeToValley).count() - 1

        // then
        assertEquals(18, minutes)
    }

    @Test
    fun `should solve test case 2`() {
        // given
        val lines = InputReader("aoc2022/input_24_test2.txt").readLines()

        // when
        val (timeToValley, start, end) = Day24.ValleyMap.parse(lines)
        val minutes1 = Day24().findShortestPath(0, start, end, timeToValley).count() - 1
        val minutes2 = Day24().findShortestPath(minutes1, end, start, timeToValley).count() - 1
        val minutes3 = Day24().findShortestPath(minutes1 + minutes2, start, end, timeToValley).count() - 1

        // then
        assertEquals(18, minutes1)
        assertEquals(23, minutes2)
        assertEquals(13, minutes3)
        assertEquals(54, minutes1 + minutes2 + minutes3)
    }

    @Test
    @Disabled // FIXME: time > 300s, OOM: heap space
    @Timeout(value = 1L, unit = TimeUnit.MINUTES)
    fun `should solve part 1`() {
        // given
        val lines = InputReader("aoc2022/input_24.txt").readLines()
        val expected = InputReader("aoc2022/answer_24.txt").readLines().first().toInt()

        // when
        val (timeToValley, start, end) = Day24.ValleyMap.parse(lines)
        val time = Day24().findShortestPath(0, start, end, timeToValley).count() - 1

        // then
        assertEquals(expected, time)
    }

    @Test
    @Disabled // FIXME: time > 1200s, OOM: heap space
    @Timeout(value = 1L, unit = TimeUnit.MINUTES)
    fun `should solve part 2`() {
        // given
        val lines = InputReader("aoc2022/input_24.txt").readLines()
        val expected = InputReader("aoc2022/answer_24.txt").readLines()
            .last().split(" ").map { it.toInt() }

        // when
        val (timeToValley, start, end) = Day24.ValleyMap.parse(lines)
        val minutes1 = Day24().findShortestPath(0, start, end, timeToValley).count() - 1
        val minutes2 = Day24().findShortestPath(minutes1, end, start, timeToValley).count() - 1
        val minutes3 = Day24().findShortestPath(minutes1 + minutes2, start, end, timeToValley).count() - 1

        // then
        assertEquals(expected[0], minutes1)
        assertEquals(expected[1], minutes2)
        assertEquals(expected[2], minutes3)
        assertEquals(expected[3], minutes1 + minutes2 + minutes3)
    }
}