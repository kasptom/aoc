package year2025

import aoc.IAocTaskKt
import kotlin.collections.orEmpty

class Day11 : IAocTaskKt{
    override fun getFileName(): String = "aoc2025/input_11.txt"

    override fun solvePartOne(lines: List<String>) {
        val nodeToNeigh = mutableMapOf<String, Set<String>>()
        for (line in lines) {
            val (node, neighStr) = line.trim().split(": ")
            val neighs = neighStr.split(" ")
            nodeToNeigh[node] = neighs.toSet()
        }
        val visited = mutableSetOf<String>()
        visited.add("you")
//        println(nodeToNeigh)
        val pathsCount = dfs(nodeToNeigh, "you", "out", visited)
        println(pathsCount)
    }

    private fun dfs(
        nodeToNeigh: MutableMap<String, Set<String>>,
        src: String,
        dest: String,
        visited: MutableSet<String>,
    ): Int {
        if (src == dest) {
            return 1
        }
        val neighs = nodeToNeigh[src] ?: emptySet()
        var count = 0
        for (next in neighs) {
            if (next == dest) {
                return 1
            }
            if (visited.contains(next)) {
                continue
            }
            visited.add(next)
            count += dfs(nodeToNeigh, next, dest, visited)
            visited.remove(next)
        }
        return count
    }

    override fun solvePartTwo(lines: List<String>) {
        val nodeToNeigh = mutableMapOf<String, Set<String>>()
        for (line in lines) {
            val (node, neighStr) = line.trim().split(": ")
            val neighs = neighStr.split(" ")
            nodeToNeigh[node] = neighs.toSet()
        }

        val targetToMask = mapOf("dac" to 1, "fft" to 2)
        val memo = HashMap<Pair<String, Int>, Long>()

        val visited = mutableSetOf("svr")
        val pathsCount = dfs2("svr", 0, visited, memo, nodeToNeigh, targetToMask)
        println(pathsCount)
    }

    fun dfs2(
        node: String,
        mask: Int,
        visited: MutableSet<String>,
        memo: HashMap<Pair<String, Int>, Long>,
        nodeToNeigh: MutableMap<String, Set<String>>,
        targetToMask: Map<String, Int>
    ): Long {
        if (node == "out") {
            return if (mask == 3) 1L else 0L
        }

        val key = node to mask
        memo[key]?.let { return it }

        var count = 0L
        for (next in nodeToNeigh[node].orEmpty()) {
            if (next in visited) {
                continue
            }
            visited.add(next)
            val nextMask = mask or (targetToMask[next] ?: 0)
            count += dfs2(next, nextMask, visited, memo, nodeToNeigh, targetToMask)
            visited.remove(next)
        }

        memo[key] = count
        return count
    }
}