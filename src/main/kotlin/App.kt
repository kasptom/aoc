import aoc.IAocTaskKt
import utils.InputReader
import year2022.Day09

fun main() {
    val task: IAocTaskKt = Day09()
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

