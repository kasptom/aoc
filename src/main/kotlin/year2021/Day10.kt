package year2021

import aoc.IAocTaskKt
import java.util.*

class Day10 : IAocTaskKt {
    override fun getFileName() = "aoc2021/input_10.txt"

    val SCORES = mapOf(")" to 3, "]" to 57, "}" to 1197, ">" to 25137)
    val SCORES_2 = mapOf(")" to 1, "]" to 2, "}" to 3, ">" to 4)
    val OPENING = setOf("(", "[", "{", "<")
    val OPENING_TO_CLOSING = mapOf("(" to ")", "[" to "]", "{" to "}", "<" to ">")
    val remainingBracketsToClose = mutableListOf<List<String>>()

    override fun solvePartOne(lines: List<String>) {
        var errorScore = 0
        val bracketPairsList = lines.map { it.chunked(1) }
        val stack = Stack<String>()
        for (bracketList in bracketPairsList) {
            var lineOk = true
            stack.clear()
            for (bracket in bracketList) {
                if (OPENING.contains(bracket)) {
                    stack.push(bracket)
                } else {
                    val closing = stack.pop()
                    if (OPENING_TO_CLOSING[closing] != bracket) {
                        errorScore += SCORES[bracket]!!
                        lineOk = false
                        break
                    }
                }
            }
            if (lineOk) {
                remainingBracketsToClose.add(stack.elements().toList().reversed())
            }
        }

        println(errorScore)
    }

    override fun solvePartTwo(lines: List<String>) {
//        remainingBracketsToClose.forEach(::println)
        val results = mutableListOf<Long>()
        for (openingBrackets in remainingBracketsToClose) {
            var partialResult = 0L
            for (bracket in openingBrackets) {
                partialResult *= 5
                partialResult += SCORES_2[OPENING_TO_CLOSING[bracket]]!!
            }
//            println(partialResult)
            results.add(partialResult)
        }
        results.sort()
        println(results[results.size / 2])
    }
}