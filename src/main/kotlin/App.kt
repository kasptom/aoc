import aoc.IAocTaskKt
import year2021.Day04
import java.io.BufferedReader
import java.io.FileReader
import java.util.stream.Collectors

fun main() {
    val task: IAocTaskKt = Day04()

    val classLoader: ClassLoader = task.javaClass.classLoader
    val inputFile: java.io.File = java.io.File(
        java.util.Objects.requireNonNull(classLoader.getResource(task.getFileName())).file
    )
    val fileReader = FileReader(inputFile)
    val reader = BufferedReader(fileReader)
    val lines: List<String> = reader.lines().collect(Collectors.toUnmodifiableList())

    task.solvePartOne(lines)
    task.solvePartTwo(lines)
}
