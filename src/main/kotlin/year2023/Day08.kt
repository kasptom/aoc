package year2023

import aoc.IAocTaskKt

class Day08 : IAocTaskKt {
//    override fun getFileName(): String = "aoc2023/input_08.txt"
    override fun getFileName(): String = "aoc2023/input_08.txt"

    override fun solvePartOne(lines: List<String>) {
        val stepsLoop = lines.first().split("").filter { it.isNotEmpty() }
        val nodes = lines.subList(2, lines.size).map(Node::parse)
        val map = nodes.groupBy { it.id }
            .mapValues { (_, v) -> v.first() }
        var steps = 0;
        var nodeId = "AAA"
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
        println("second")
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
