package year2021

import org.junit.jupiter.api.Test

internal class Day21Test {

    @Test
    fun createPossibleTosses() {
        val tosses = Day21().createDiracTosses()
        tosses.forEach(::println)
    }
}