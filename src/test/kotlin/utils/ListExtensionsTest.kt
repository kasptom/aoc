package utils

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.lang.AssertionError

internal class ListExtensionsTest {

    @Test
    fun transpose() {
        // given
        val integersGrid: List<List<Int>> = listOf(
            listOf(1, 2, 3, 4),
            listOf(5, 6, 7, 8)
        )
        val expected = listOf(
            listOf(1, 5),
            listOf(2, 6),
            listOf(3, 7),
            listOf(4, 8)
        )

        // when
        val transposed = integersGrid.transpose()

        // then
        assertEquals(expected, transposed)
    }

    @Test
    fun transpose_differentSized_shouldThrow() {
        // given
        val integersGrid: List<List<Int>> = listOf(
            listOf(1, 2, 3, 4),
            listOf(5, 6, 7)
        )

        // when then
        val thrown = assertThrows(AssertionError::class.java) { integersGrid.transpose() }
        assertEquals("All of the inner lists have to be the same size", thrown.message)
    }

    @Test
    fun testPermutations() {
        val list = listOf(1, 2, 3)

        val result = list.permutations()

        assertEquals(6, result.size)
    }
}