package year2024

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class Day17Test {

    @Test
    // If register C contains 9, the program 2,6 would set register B to 1.
    fun testCase1() {
        // given
        val sut = Day17.Program(listOf(2,6), registerA = 0, registerB = 0, registerC = 9)

        // when
        sut.execute()

        // then
        assertEquals(1, sut.registerB)
    }

    // If register A contains 10, the program 5,0,5,1,5,4 would output 0,1,2.
    @Test
    fun testCase2() {
        // given
        val sut = Day17.Program(listOf(5,0,5,1,5,4), registerA = 10, registerB = 0, registerC = 0)

        // when
        sut.execute()

        // then
        assertEquals("0,1,2", sut.outputStr())
    }

    // If register A contains 2024, the program 0,1,5,4,3,0 would output 4,2,5,6,7,7,7,7,3,1,0 and leave 0 in register A.
    @Test
    fun testCase3() {
        // given
        val sut = Day17.Program(listOf(0,1,5,4,3,0), registerA = 2024, registerB = 0, registerC = 0)

        // when
        sut.execute()

        // then
        assertEquals("4,2,5,6,7,7,7,7,3,1,0", sut.outputStr())
        assertEquals(0, sut.registerA)
    }

    // If register B contains 29, the program 1,7 would set register B to 26.
    @Test
    fun testCase4() {
        // given
        val sut = Day17.Program(listOf(1,7), registerA = 0, registerB = 29, registerC = 0)

        // when
        sut.execute()

        // then
        assertEquals(26, sut.registerB)
    }

    // If register B contains 2024 and register C contains 43690, the program 4,0 would set register B to 44354
    @Test
    fun testCase5() {
        // given
        val sut = Day17.Program(listOf(4,0), registerA = 0, registerB = 2024, registerC = 43690)

        // when
        sut.execute()

        // then
        assertEquals(44354, sut.registerB)
    }
}