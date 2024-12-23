package year2024

import aoc.IAocTaskKt
import utils.except

class Day23 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_23.txt"
    // override fun getFileName(): String = "aoc2024/input_23_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val nodeToNeighs = mutableMapOf<String, MutableSet<String>>()
        for (line in lines) {
            val (v1, v2) = line.split("-")
            nodeToNeighs.putIfAbsent(v1, mutableSetOf())
            nodeToNeighs.putIfAbsent(v2, mutableSetOf())
            nodeToNeighs[v1]!!.add(v2)
            nodeToNeighs[v2]!!.add(v1)
        }

        val triples = mutableSetOf<Set<String>>()
        for (node in nodeToNeighs.keys.toList()) {
            for (node2 in nodeToNeighs.keys.toList().except(node)) {
                for (node3 in nodeToNeighs.keys.toList().except(node, node2)) {
                    if (node2 in nodeToNeighs[node]!! && node3 in nodeToNeighs[node]!! && node3 in nodeToNeighs[node2]!! && node in nodeToNeighs[node2]!!) {
                        triples.add(setOf(node, node2, node3))
                    }
                }
            }
        }
        println(triples)
        println(triples.size)
        println(triples.count { triple -> triple.count { it.startsWith("t") } >= 1 })
    }

    override fun solvePartTwo(lines: List<String>) {
        if (lines.isEmpty()) println("empty lines") else println(lines.size)
    }
}
