package year2024

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class Day21Test {
    private val sut = Day21()

    @CsvSource(
        textBlock = """
        029A | 68
        980A | 60
        179A | 68
        456A | 64
        379A | 64"""
            , delimiterString = "|"
    )
    @ParameterizedTest
    fun testOutput(input: String, expectedLength: Int) {
        // when
        val result = sut.getNumCodeLength(input, 0, 2)

        // then
        assertEquals(expectedLength, result)
    }
}
