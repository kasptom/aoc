package year2024

import aoc.IAocTaskKt
import utils.except

class Day23 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_23.txt"
    // override fun getFileName(): String = "aoc2024/input_23_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val graph = createGraph(lines)

        val triples = mutableSetOf<Set<String>>()
        for (node in graph.keys.toList()) {
            for (node2 in graph.keys.toList().except(node)) {
                for (node3 in graph.keys.toList().except(node, node2)) {
                    if (node2 in graph[node]!! && node3 in graph[node]!! && node3 in graph[node2]!! && node in graph[node2]!!) {
                        triples.add(setOf(node, node2, node3))
                    }
                }
            }
        }
//        println(triples)
//        println(triples.size)
        println(triples.count { triple -> triple.any { it.startsWith("t") } })
    }

    private fun createGraph(lines: List<String>): MutableMap<String, MutableSet<String>> {
        val nodeToNeighs = mutableMapOf<String, MutableSet<String>>()
        for (line in lines) {
            val (v1, v2) = line.split("-")
            nodeToNeighs.putIfAbsent(v1, mutableSetOf())
            nodeToNeighs.putIfAbsent(v2, mutableSetOf())
            nodeToNeighs[v1]!!.add(v2)
            nodeToNeighs[v2]!!.add(v1)
        }
        return nodeToNeighs
    }

    override fun solvePartTwo(lines: List<String>) {
        val nodeToNeighs = createGraph(lines)
        val cliques = mutableListOf<Set<String>>()

        bronKerbosch(graph = nodeToNeighs, p = nodeToNeighs.keys, cliques = cliques)
//        println(cliques)
        cliques.maxByOrNull { it.size }.also { println(it!!.sorted().joinToString(",")) }
    }

    /**
     * https://en.wikipedia.org/wiki/Bron%E2%80%93Kerbosch_algorithm
     * Finds all the cliques in the graph
     */
    private fun bronKerbosch(
        graph: Map<String, Set<String>>,
        r: Set<String> = mutableSetOf(),
        p: Set<String>,
        x: Set<String> = mutableSetOf(),
        cliques: MutableList<Set<String>>
    ) {
        if (p.isEmpty() && x.isEmpty()) {
            cliques.add(r.toSet())
            return
        }

        val updatedP = p.toMutableSet()
        val updatedX = x.toMutableSet()
        val pivot = (p + x).firstOrNull() ?: return
        val nonNeighbors = p - graph[pivot].orEmpty()

        for (v in nonNeighbors) {
            val neighbors = graph[v].orEmpty()
            bronKerbosch(graph, r + v, p.intersect(neighbors), x.intersect(neighbors), cliques)
            updatedP.remove(v)
            updatedX.add(v)
        }
    }
}
