package year2022

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import year2022.Day25.Snafu

class Day25Test {
    @Test
    fun snafu() {
        val first = Snafu(1747, "1=-0-2")
        val second = Snafu(198, "2=0=")

        val snafuSum = first + second
        val decSum = first.decimal + second.decimal
        val snafuSumToDec = Snafu.parse(snafuSum.snafu).decimal

        assertEquals(decSum, snafuSumToDec)
    }

    @Test
    fun `snafu 2`() {
        val first = Snafu(1747, "1=-0-2")
        val second = Snafu(906, "12111")

        val snafuSum = first + second
        val decSum = first.decimal + second.decimal
        val snafuSumToDec = Snafu.parse(snafuSum.snafu).decimal

        assertEquals(decSum, snafuSumToDec)
    }
}