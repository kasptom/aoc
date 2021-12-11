import aoc.IAocTaskKt
import utils.InputReader
import year2021.Day11

fun main() {
    val task: IAocTaskKt = Day11()
    val lines: List<String> = InputReader(task.getFileName())
        .readLines()

    task.solvePartOne(lines)
    task.solvePartTwo(lines)
}
