package year2024

import aoc.IAocTaskKt

class Day05 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_05.txt"

    override fun solvePartOne(lines: List<String>) {
        val orderingRulesEnd = lines.indexOfFirst { it.trim().isEmpty() }
        val orderingRules = lines.subList(0, orderingRulesEnd)
            .map { rule -> rule.split("|").map { it.toInt() }
                .let { xy -> Pair(xy[0], xy[1]) }
            }

        val graph: Map<Int, Set<Int>> = toGraph(orderingRules)

        val updates = lines.subList(orderingRulesEnd + 1, lines.size)
            .map { update -> update.split(",").map { it.toInt()} }

        updates.filter { it.isCorrectlyOrdered(graph) }
            .map { update -> update.get(update.size / 2) }
            .onEach { println(it) }
            .sum()
            .let { println(it) }
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

    override fun solvePartTwo(lines: List<String>) {
        if (lines.isEmpty()) println("empty lines") else println(lines.size)
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
