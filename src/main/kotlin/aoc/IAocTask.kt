package aoc

interface IAocTaskKt {
    fun getFileName(): String

    fun solvePartOne(lines: List<String>)

    fun solvePartTwo(lines: List<String>)
}
