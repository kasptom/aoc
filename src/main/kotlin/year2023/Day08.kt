package year2023

import aoc.IAocTaskKt

class Day08 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_08.txt"

    override fun solvePartOne(lines: List<String>) {
        val stepsLoop = lines.first().split("").filter { it.isNotEmpty() }
        val nodes = lines.subList(2, lines.size).map(Node::parse)
        val map = nodes.groupBy { it.id }
            .mapValues { (_, v) -> v.first() }
        var steps = 0;
        var nodeId = nodes.first { it.id == "AAA" }.id
        var stepId = 0

        while (nodeId != "ZZZ") {
            val node = map[nodeId]!!
            nodeId = if (stepsLoop[stepId] == "L") {
                node.left
            } else node.right
            steps++
            stepId = if (stepId == stepsLoop.size - 1) 0 else stepId + 1
        }
        println(steps)
    }

    override fun solvePartTwo(lines: List<String>) {
        val stepsLoop = lines.first().split("").filter { it.isNotEmpty() }
        val nodes = lines.subList(2, lines.size).map(Node::parse)
        val map = nodes.groupBy { it.id }
            .mapValues { (_, v) -> v.first() }
        val nodeIds = nodes.filter { it.id.endsWith("A") }
            .map { it.id }
            .toMutableList()

        val neededSteps = nodeIds.map {
            var stepId = 0
            var steps = 0
            var nodeId = it
            while (nodeId.endsWith("Z").not()) {
                    val node = map[nodeId]!!
                    nodeId = if (stepsLoop[stepId] == "L") {
                        node.left
                    } else node.right

                steps++
                stepId = if (stepId == stepsLoop.size - 1) 0 else stepId + 1
            }
            steps
        }
        val primeFactors = neededSteps.flatMap { stepCount -> getPrimeFactors(stepCount) }.toSet()
        println(primeFactors)
        val result = primeFactors.fold(1L) { x, y -> x * y}
        println(result)
    }

    private fun getPrimeFactors(number: Int): Set<Long> {
        val factors = mutableSetOf<Long>()
        var factor = 1L
        factors.add(factor)
        factor = 2L
        var remaining: Long =  number.toLong()
        while (remaining > 1) {
            if (remaining % factor == 0L) {
                factors += factor
                remaining /= factor
            }
            factor = if (factor == 2L) 3L else factor + 2L
        }
        return factors
    }

    data class Node(val id: String, val left: String, val right: String) {
        companion object {
            fun parse(line: String): Node {
                val (id, leftRight) = line.split(" = ")
                val (left, right) = leftRight.replace("(", "")
                    .replace(")", "")
                    .split(", ")
                return Node(id, left, right)
            }
        }
    }
}
