package year2024

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class Day21Test {

    val sut = Day21.DirectionalPad()

    @Test
    fun testOutput() {
        // given
        val input = "v<<A>>^A<A>AvA<^AA>A<vAAA>^A"
        val expected = "<vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A"

        // when
        val result = sut.getMovementPaths(input)

        // then
        assertTrue(result.any { it == expected })
        assertTrue(result.first { it == expected}.length == result.minOf { it.length })
    }
}