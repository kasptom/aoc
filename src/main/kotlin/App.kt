import aoc.IAocTaskKt
import utils.InputReader
import year2024.Day25

fun main() {
    val task: IAocTaskKt = Day25()
    val lines: List<String> = InputReader(task.getFileName())
        .readLines()

    val time = System.currentTimeMillis()
    task.solvePartOne(lines)
    val firstPart = System.currentTimeMillis() - time
    println("time 1st part [s]: ${firstPart / 1000.0}")
    task.solvePartTwo(lines)
    val secondPart = System.currentTimeMillis() - time - firstPart
    println("time 2nd part [s]: ${secondPart / 1000.0}")
}
