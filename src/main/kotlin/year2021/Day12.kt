package year2021

import aoc.IAocTaskKt
import java.util.regex.Pattern

class Day12 : IAocTaskKt {
    override fun getFileName() = "aoc2021/input_12.txt"

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
        validPaths.clear()

        for (smallCave in fromCaveToCaves.keys.filter { it.matches(smallCavePattern) && it != "end" && it != "start" }) {
            caveVisitsCount["start"] = 1
            traverseCaves2("start", listOf("start"), smallCave)
        }

//        validPaths.forEach(::println)
        println(validPaths.distinct().size)
    }

    private fun traverseCaves2(cave: String, path: List<String>, smallCaveYouCanVisitTwice: String) {
//        println(cave)
        if (path.size > 40) {
            return
        }
        for (nextCave in fromCaveToCaves[cave]!!) {
            if (nextCave == "end") {
                validPaths.add(path + nextCave)
                continue
            }

            if (nextCave == "start" ||
                smallCaveYouCanVisitTwice != nextCave && caveVisitsCount.getOrDefault(nextCave, 0) >= 1 ||
                smallCaveYouCanVisitTwice == nextCave && caveVisitsCount.getOrDefault(nextCave, 0) >= 2
            ) {
                continue
            }

            // next
            if (nextCave.matches(smallCavePattern)) {
                caveVisitsCount.putIfAbsent(nextCave, 0)
                caveVisitsCount[nextCave] = caveVisitsCount[nextCave]!! + 1
            }

            traverseCaves2(nextCave, path + nextCave, smallCaveYouCanVisitTwice)

            if (caveVisitsCount.containsKey(nextCave)) {
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