package year2025

import aoc.IAocTaskKt

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
        val visited = mutableSetOf<String>()
        visited.add("svr")
        val targetToCode = mapOf("dac" to 1, "fft" to 2)
//        println(nodeToNeigh)
        val pathsCount = dfs2(nodeToNeigh, "svr", "out", 3, targetToCode, visited)
        println(pathsCount)
    }

    private fun dfs2(
        nodeToNeigh: MutableMap<String, Set<String>>,
        src: String,
        dest: String,
        target: Int,
        targetToCode: Map<String, Int>,
        visited: MutableSet<String>,
    ): Int {
        if (src == dest) {
            return if (target == 3) 1 else 0
        }
        val neighs = nodeToNeigh[src] ?: emptySet()
        var count = 0
        for (next in neighs) {
            if (next == dest) {
                return if (target == 3) 1 else 0
            }
            if (visited.contains(next)) {
                continue
            }
            visited.add(next)
            val newTarget = target + (targetToCode[next] ?: 0)
            count += dfs2(nodeToNeigh, next, dest, newTarget, targetToCode, visited)
            visited.remove(next)
        }
        return count
    }
}