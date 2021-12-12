package year2021

import aoc.IAocTaskKt
import java.util.regex.Pattern

class Day12 : IAocTaskKt {
    override fun getFileName() = "aoc2021/input_12_test.txt"

    val validPaths: MutableList<List<String>> = mutableListOf()
    val visitedSmallCaves: MutableSet<String> = mutableSetOf()
    val caveVisitsCount = mutableMapOf<String, Int>()
    val fromCaveToCaves = mutableMapOf<String, List<String>>()
    val smallCavePattern = Pattern.compile("[a-z]+").toRegex()

    override fun solvePartOne(lines: List<String>) {
        val connections = lines.map(CaveConnection::parse)
        for (connection in connections) {
            for (cave in connection.connection) {
                fromCaveToCaves.putIfAbsent(cave, emptyList())
                fromCaveToCaves[cave] = (fromCaveToCaves[cave]!! + connection.other(cave)).sorted()
            }
        }
        fromCaveToCaves.forEach(::println)

        visitedSmallCaves.add("start")
        traverseCaves("start", listOf("start"))
        visitedSmallCaves.remove("start")

//        validPaths.forEach(::println)
        println(validPaths.size)
    }

    private fun traverseCaves(cave: String, path: List<String>) {
//        println(cave)
        if (path.size > 100) {
            return
        }
        for (nextCave in fromCaveToCaves[cave]!!) {
            if (nextCave == "end") {
                validPaths.add(path + nextCave)
                continue
            }

            if (visitedSmallCaves.contains(nextCave)) {
                continue
            }

            // next
            if (nextCave.matches(smallCavePattern)) {
                visitedSmallCaves.add(nextCave)
            }

            traverseCaves(nextCave, path + nextCave)

            if (visitedSmallCaves.contains(nextCave)) {
                visitedSmallCaves.remove(nextCave)
            }
        }
    }

    override fun solvePartTwo(lines: List<String>) {
        val connections = lines.map(CaveConnection::parse)
        for (connection in connections) {
            for (cave in connection.connection) {
                fromCaveToCaves.putIfAbsent(cave, emptyList())
                fromCaveToCaves[cave] = (fromCaveToCaves[cave]!! + connection.other(cave)).sorted()
            }
        }
        fromCaveToCaves.forEach(::println)
        validPaths.clear()

        for (smallCave in fromCaveToCaves.keys.filter { it.matches(smallCavePattern) && it != "end" && it != "start" }) {
            visitedSmallCaves.clear()

            visitedSmallCaves.add("start")
            traverseCaves2("start", listOf("start"), smallCave)
            visitedSmallCaves.remove("start")
        }

        validPaths.forEach(::println)
        println(validPaths.distinct().size)
    }

    private fun traverseCaves2(cave: String, path: List<String>, smallCaveYouCanVisitTwice: String) {
//        println(cave)
        if (path.size > 20) {
            return
        }
        for (nextCave in fromCaveToCaves[cave]!!) {
            if (nextCave == "end") {
                val completePath = path + nextCave
                if (completePath.filter { it.matches(smallCavePattern) }
                        .groupingBy { it }
                        .eachCount().values
                        .any { it == 2 }) {
                    validPaths.add(path + nextCave)
                }
                continue
            }

            if (nextCave == "start" ||
                visitedSmallCaves.contains(nextCave) &&
                (caveVisitsCount.getOrDefault(nextCave, 0) == 1 ||
                        smallCaveYouCanVisitTwice == nextCave && caveVisitsCount[nextCave]!! == 2)) {
                continue
            }

            // next
            if (nextCave.matches(smallCavePattern)) {
                visitedSmallCaves.add(nextCave)
                caveVisitsCount.putIfAbsent(nextCave, 0)
                caveVisitsCount[nextCave] = caveVisitsCount[nextCave]!! + 1
            }

            traverseCaves2(nextCave, path + nextCave, smallCaveYouCanVisitTwice)

            if (visitedSmallCaves.contains(nextCave)) {
                visitedSmallCaves.remove(nextCave)
                caveVisitsCount[nextCave] = caveVisitsCount[nextCave]!! - 1
            }
        }
    }

    class CaveConnection(from: String, to: String) {
        val connection: Set<String>

        init {
            connection = setOf(from, to)
        }

        fun other(cave: String): String {
            return (connection - cave).first()
        }

        companion object {
            fun parse(input: String): CaveConnection {
                val (from, to) = input.split("-")
                    .map(String::trim)
                    .filter(String::isNotBlank)
                return CaveConnection(from, to)
            }
        }
    }
}