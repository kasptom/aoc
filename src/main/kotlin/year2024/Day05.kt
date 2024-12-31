package year2024

import aoc.IAocTaskKt

class Day05 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_05.txt"

    override fun solvePartOne(lines: List<String>) {
        val (graph: Map<Int, Set<Int>>, updates) = parse(lines)

        updates.filter { it.isCorrectlyOrdered(graph) }
            .sumOf { update -> update[update.size / 2] }
            .let { println(it) }
    }

    override fun solvePartTwo(lines: List<String>) {
        val (graph: Map<Int, Set<Int>>, updates) = parse(lines)

        updates.filter { it.isCorrectlyOrdered(graph).not() }
            .map { update -> update.correct(graph) }
            .sumOf { update -> update[update.size / 2] }
            .let { println(it) }

    }

    private fun parse(lines: List<String>): Pair<Map<Int, Set<Int>>, List<List<Int>>> {
        val orderingRulesEnd = lines.indexOfFirst { it.trim().isEmpty() }
        val orderingRules = lines.subList(0, orderingRulesEnd)
            .map { rule ->
                rule.split("|").map { it.toInt() }
                    .let { xy -> Pair(xy[0], xy[1]) }
            }

        val graph: Map<Int, Set<Int>> = toGraph(orderingRules)

        val updates = lines.subList(orderingRulesEnd + 1, lines.size)
            .map { update -> update.split(",").map { it.toInt() } }
        return Pair(graph, updates)
    }

    private fun toGraph(orderingRules: List<Pair<Int, Int>>): Map<Int, Set<Int>> {
        val result = mutableMapOf<Int, MutableSet<Int>>()
        for (rule in orderingRules) {
            val (prev, next) = rule
            result.putIfAbsent(prev, mutableSetOf())
            result[prev]?.add(next)
        }
        return result
    }
}

private fun List<Int>.isCorrectlyOrdered(graph: Map<Int, Set<Int>>): Boolean {
    return findPath(0, this, graph)
}

fun findPath(idx: Int, nodes: List<Int>, graph: Map<Int, Set<Int>>): Boolean {
    if (idx == nodes.size - 1) {
        return true
    }
    val children = graph[nodes[idx]] ?: return false
    val next = nodes[idx + 1]
    if (children.contains(next)) {
        return findPath(idx + 1, nodes, graph)
    }
    return false
}

private fun List<Int>.correct(graph: Map<Int, Set<Int>>): List<Int> {
    val toVisit = this.toSet()
    val correctedGraph = correctGraph(graph, toVisit)
    val reversedGraph = reverseGraph(correctedGraph)

    val roots = this.filter { node -> reversedGraph[node] == null }

    if (roots.size != 1) {
        throw IllegalArgumentException("No path found")
    }
    val root = roots.first()

    val paths = mutableListOf<List<Int>>()

    dfs(root, correctedGraph, listOf(root), paths, setOf(root))
    return paths.first { it.size == this.size }
}

fun dfs(
    node: Int,
    graph: Map<Int, Set<Int>>,
    path: List<Int>,
    paths: MutableList<List<Int>>,
    visited: Set<Int>
) {
    val children = graph[node]?.filter { visited.contains(it).not() } ?: emptyList()
    if (children.isEmpty()) {
        paths.add(path)
        return
    }

    for (child in children) {
        dfs(child, graph, path + child, paths, visited)
    }
}

fun reverseGraph(correctedGraph: Map<Int, Set<Int>>): Map<Int, Set<Int>> {
    val childToParents = mutableMapOf<Int, MutableSet<Int>>()
    for (parent in correctedGraph.keys) {
        val children = correctedGraph[parent] ?: emptySet()
        for (child in children) {
            childToParents.putIfAbsent(child, mutableSetOf())
            childToParents[child]?.add(parent)
        }
    }
    return childToParents
}

fun correctGraph(graph: Map<Int, Set<Int>>, toVisit: Set<Int>): Map<Int, Set<Int>> {
    val newGraph = mutableMapOf<Int, Set<Int>>()
    for (node in graph.keys) {
        if (!toVisit.contains(node)) {
            continue
        }
        val children = graph[node]?.filter { it in toVisit }?.toSet() ?: emptySet()
        if (children.isNotEmpty()) {
            newGraph[node] = children
        }
    }
    return newGraph
}
