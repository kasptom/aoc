package year2023

import aoc.IAocTaskKt
import edu.uci.ics.jung.algorithms.flows.EdmondsKarpMaxFlow
import edu.uci.ics.jung.graph.DirectedSparseGraph

typealias UciEdge = edu.uci.ics.jung.graph.util.Pair<String>

class Day25 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_25.txt"

    override fun solvePartOne(lines: List<String>) {
        val compToConnectionsIncomplete = lines.map {
            val (component, others) = it.split(": ").filter(String::isNotEmpty)
            Pair(component, others.split(" ").filter(String::isNotEmpty))
        }.groupBy { it.first }
            .mapValues { (_, v) -> v.first() }
            .mapValues { (_, pair) ->
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
            .toSet()

        val graph = DirectedSparseGraph<String, String>()
        println(edges.size)
        edges.forEach { (from, to) ->
            graph.addEdge("$from->$to", UciEdge(from, to))
        }
        val vertices = edges.map { it.first }.distinct().sorted().toList()
        val edgeFlowMap: MutableMap<String, Number> = mutableMapOf()

        var minCutEdges = setOf<Set<String>>()
        var sinkPart = 0
        var sourcePart = 0
        var count = 0
        while (minCutEdges.size != 3) {
            val ek: EdmondsKarpMaxFlow<String, String> = EdmondsKarpMaxFlow(
                graph,
                vertices.first(),
                vertices[vertices.size - count - 1],
                { 1 },
                edgeFlowMap,
                null
            )
            ek.evaluate()
            minCutEdges = ek.minCutEdges.map { edge -> edge.split("->").toSet() }.toSet()
            sourcePart = ek.nodesInSourcePartition.size
            sinkPart = ek.nodesInSinkPartition.size
            count++
        }
        println(minCutEdges)
        println(sourcePart * sinkPart)
    }

    override fun solvePartTwo(lines: List<String>) {
        println("⭐")
    }
}
