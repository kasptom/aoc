package year2022

import aoc.IAocTaskKt

class Day05 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_05.txt"

    override fun solvePartOne(lines: List<String>) {
        val stackIndicesLineIdx = lines.indexOfFirst { it.trim().startsWith("1") }
        val stackSize = stackIndicesLineIdx
        val containers = lines[stackIndicesLineIdx].trim().split(" ").filter { it.isNotEmpty() }
            .map { it.toInt() }
            .map { Container(it) }

        for (stackLevel in 0 until stackSize) {
            val level = lines[stackLevel]
                .replace("    ", "- ")
                .split(" ")
                .map {
                    it.replace("[", "")
                        .replace("]", "")
                }

            for (container in containers) {
                if (level.size >= container.id && level[container.id - 1] != "-") {
                    container.stack += level[container.id - 1]
                }
            }
        }

        val movesStartIdx = stackSize + 2
        val moves = lines.subList(movesStartIdx, lines.size)
            .map { Move.parse(it) }

//        println(containers)
//        println(moves)

        for (move in moves) {
            val fromContainer = containers.first { it.id == move.fromId }
            val toContainer = containers.first { it.id == move.toId }
            for (step in 1..move.count) {
                val removed = fromContainer.stack.removeFirst()
                toContainer.stack.add(0, removed)
            }
        }
        containers.joinToString("") { it.stack.firstOrNull() ?: "" }
            .let { println(it) }
    }

    override fun solvePartTwo(lines: List<String>) {
        println("??")
    }

    data class Container(val id: Int, val stack: MutableList<String> = mutableListOf())

    data class Move(val count: Int, val fromId: Int, val toId: Int) {
        companion object {
            fun parse(moveLine: String): Move {
                val moveFromTo = moveLine.split(" from ")
                val count = moveFromTo[0].replace("move ", "").toInt()
                val (from, to) = moveFromTo[1].split(" to ").map { it.toInt() }.zipWithNext().single()
                return Move(count, from, to)
            }
        }
    }
}