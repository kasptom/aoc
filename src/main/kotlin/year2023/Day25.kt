package year2023

import aoc.IAocTaskKt

class Day25 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_25_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val compToConnectionsIncomplete = lines.map {
            val (component, others) = it.split(": ").filter(String::isNotEmpty)
            Pair(component, others.split(" ").filter(String::isNotEmpty))
        }.groupBy { it.first }
            .mapValues { (_, v) -> v.first() }
            .mapValues {(_, pair) ->
                val (_, others) = pair
                others
            }
        val compToConnections = mutableMapOf<String, Set<String>>()
        for ((k, _) in compToConnectionsIncomplete) {
            compToConnections.putIfAbsent(k, setOf())
            compToConnections[k] = compToConnections[k]!! + compToConnectionsIncomplete[k]!!
            for (comp in compToConnections[k]!!) {
                compToConnections.putIfAbsent(comp, setOf())
                compToConnections[comp] = compToConnections[comp]!! + k
            }
        }

        println("comp to connections")
        compToConnections.onEach {
            println(it)
        }
        println("edges")
        val edges = compToConnections.flatMap { (k, v) -> v.map { Pair(k, it) } }
        println(edges)
    }

    override fun solvePartTwo(lines: List<String>) {
        println("Not yet implemented")
    }
}
