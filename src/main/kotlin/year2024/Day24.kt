package year2024

import aoc.IAocTaskKt
import utils.except

class Day24 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_24.txt"
    // override fun getFileName(): String = "aoc2024/input_24_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val (wireToValue, wirePairToEdges) = parse(lines)
        compute(wireToValue, wirePairToEdges)
        valueOfWires(wireToValue)
            .let { it.toLong(2) }
            .let { println(it) }
    }

    private fun compute(
        wireToValue: MutableMap<String, Int>,
        wirePairToEdges: Map<Set<String>, List<Edge>>,
    ) {
        var prevCount = 0

        while (prevCount != wireToValue.size) {
            val wires = wireToValue.keys.toList()
            prevCount = wireToValue.size
            for (a in wires) {
                for (b in wires) {
                    val wirePair = setOf(a, b)
                    if (wirePairToEdges.containsKey(wirePair)) {
                        val edges = wirePairToEdges[wirePair]!!
                        for (edge in edges) {
                            val (wire, value) = edge.compute(wireToValue)
                            wireToValue[wire] = value
                        }
                    }
                }
            }
        }
    }

    private fun parse(lines: List<String>): Pair<MutableMap<String, Int>, Map<Set<String>, List<Edge>>> {
        val wireToValue = mutableMapOf<String, Int>()
        val values = lines.subList(0, lines.indexOfFirst { it.isBlank() })
            .associate { wireValue ->
                val (wire, value) = wireValue.split(": ")
                Pair(wire, value.toInt())
            }
        wireToValue.putAll(values)

        val edges = lines.subList(lines.indexOfFirst { it.isBlank() } + 1, lines.size)
            .map { Edge.parse(it) }

        val wirePairToEdges = edges.groupBy { it.wirePair() }
        return Pair(wireToValue, wirePairToEdges)
    }

    private fun valueOfWires(wireToValue: MutableMap<String, Int>, prefix: String = "z"): String {
        return wireToValue.filter { (k, _) -> k.startsWith(prefix) }
            .keys
            .sortedDescending()
            .map { k -> wireToValue[k]!! }
            .joinToString("")
    }

    override fun solvePartTwo(lines: List<String>) {
        /** Based on forensic analysis of scuff marks
         * and scratches on the device,
         * you can tell that there are exactly four
         * pairs of gates whose output wires have been swapped
         *
         * A gate can only be in at most one such pair
         *
         * */
        val (wireToValue, wirePairToEdges) = parse(lines)

        compute(wireToValue, wirePairToEdges)

        val x = valueOfWires(wireToValue, "x")
        val y = valueOfWires(wireToValue, "y")
        val z = valueOfWires(wireToValue, "z")

        println("x =  $x")
        println("y =  $y")
        println("z = $z")

        val expectedZ = (x.toLong(2) + y.toLong(2)).toString(2)
        println("z = $expectedZ (expected)")

        println("edges count")
        println(wirePairToEdges.values.flatten().size)

        val edges = wirePairToEdges.values.flatten()

        val (initialWireToValue, initialWirePairToEdges) = parse(lines)
        for (firstEdge in edges) {
            for (secondEdge in edges.except(firstEdge)) {
                for (thirdEdge in edges.except(firstEdge, secondEdge)) {
                    for (fourthEdge in edges.except(firstEdge, secondEdge, thirdEdge)) {

                        val firstWirePair = firstEdge.wirePair()
                        val secondWirePair = secondEdge.wirePair()
                        val thirdWirePair = thirdEdge.wirePair()
                        val fourthWirePair = fourthEdge.wirePair()
                        val wirePairs = (firstWirePair + secondWirePair + thirdWirePair + fourthWirePair)
                            .sorted()
                            .toList()
                        if (wirePairs.size != 8) {
                            continue
                        }

                        val updatedWirePairToEdges = initialWirePairToEdges.toMutableMap()
                            .mapValues { (_, v) -> v.toMutableSet() }

                        val (swappedFirstEdge, swappedSecondEdge) = firstEdge.swapOutput(secondEdge)
                        updatedWirePairToEdges[firstWirePair]!!.remove(firstEdge)
                        updatedWirePairToEdges[firstWirePair]!!.add(swappedFirstEdge)
                        updatedWirePairToEdges[secondWirePair]!!.remove(secondEdge)
                        updatedWirePairToEdges[secondWirePair]!!.add(swappedSecondEdge)

                        val (swappedThirdEdge, swappedFourthEdge) = thirdEdge.swapOutput(fourthEdge)
                        updatedWirePairToEdges[thirdWirePair]!!.remove(thirdEdge)
                        updatedWirePairToEdges[thirdWirePair]!!.add(swappedThirdEdge)
                        updatedWirePairToEdges[fourthWirePair]!!.remove(fourthEdge)
                        updatedWirePairToEdges[fourthWirePair]!!.add(swappedFourthEdge)

                        val swappedWirePairToEdges = updatedWirePairToEdges
                            .mapValues { (_, v) -> v.toList() }
                        compute(initialWireToValue.toMutableMap(), swappedWirePairToEdges)

                        val zAfterSwap = valueOfWires(wireToValue, "z")
                        if (zAfterSwap == expectedZ) {
                            println(wirePairs.joinToString(","))
                            return
                        }
                    }
                }
            }
        }
    }

    data class Edge(val left: String, val right: String, val operation: Operation, val dest: String) {
        fun wirePair(): Set<String> {
            return setOf(left, right)
        }

        fun compute(wireToValue: MutableMap<String, Int>): Pair<String, Int> {
            val leftVal = wireToValue[left]!!
            val rightVal = wireToValue[right]!!
            val result = operation.compute(leftVal, rightVal)
            return Pair(dest, result)
        }

        fun swapOutput(otherEdge: Edge): Pair<Edge, Edge> {
            val a = copy(dest = otherEdge.dest)
            val b = otherEdge.copy(dest = dest)
            return Pair(a, b)
        }

        enum class Operation {
            XOR, OR, AND;

            fun compute(leftVal: Int, rightVal: Int): Int = when (this) {
                XOR -> leftVal.xor(rightVal)
                OR -> leftVal.or(rightVal)
                AND -> leftVal.and(rightVal)
            }
        }

        companion object {
            fun parse(leftRightDest: String): Edge {
                val (leftRight, dest) = leftRightDest.split(" -> ")
                val (left, operationStr, right) = leftRight.split(" ")
                val operation = Operation.valueOf(operationStr)
                return Edge(left, right, operation, dest)
            }
        }
    }
}
