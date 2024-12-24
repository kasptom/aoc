package year2024

import aoc.IAocTaskKt
import org.jgrapht.Graph
import org.jgrapht.graph.SimpleGraph
import org.jgrapht.nio.Attribute
import org.jgrapht.nio.DefaultAttribute
import org.jgrapht.nio.dot.DOTExporter
import java.io.StringWriter
import java.io.Writer

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
        wirePairToEdges: Map<String, List<Edge>>,
    ) {
        var prevCount = 0

        while (prevCount != wireToValue.size) {
            val wires = wireToValue.keys.toList().sorted()
            prevCount = wireToValue.size
            for (i in 0 until wires.size) {
                for (j in (i + 1) until wires.size) {
                    val wirePair = wires[i] + ";" + wires[j]
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

    private fun findLeastSignificantOne(
        wirePairToEdges: Map<String, List<Edge>>,
    ): Int {
        val wireToValue: MutableMap<String, Int> = TEST_INPUT.toMutableMap()

        var prevCount = 0

        while (prevCount != wireToValue.size) {
            val wires = wireToValue.keys.toList().sorted()
            prevCount = wireToValue.size
            for (i in 0 until wires.size) {
                for (j in (i + 1) until wires.size) {
                    val wirePair = wires[i] + ";" + wires[j]
                    if (wirePairToEdges.containsKey(wirePair)) {
                        val edges = wirePairToEdges[wirePair]!!
                        for (edge in edges) {
                            val (wire, value) = edge.compute(wireToValue)

                            if (wire.startsWith("z") && wire != "z45" && value == 1) {
                                return -1
                            }

                            wireToValue[wire] = value
                        }
                    }
                }
            }
        }
        val zValue = valueOfWires(wireToValue)
        return zValue.reversed().indexOfFirst { it == '1' }
    }

    private fun parse(lines: List<String>): Pair<MutableMap<String, Int>, Map<String, List<Edge>>> {
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
        var (wireToValue, wirePairToEdges) = parse(lines)

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


        val (initialWireToValue, initialWirePairToEdges) = parse(lines)

        // visualization
        val vertices = (initialWirePairToEdges.values.flatten().map { it.left }
                + initialWirePairToEdges.values.flatten().map { it.right }
                + initialWirePairToEdges.values.flatten().map { it.dest }).toSet()
        val edges = initialWirePairToEdges.values.flatten().toSet()
//        viewGraph(vertices, edges)

        val input = testingInput(initialWireToValue)
        compute(input, wirePairToEdges)
        println(" x=${valueOfWires(input, "x")}")
        println(" y=${valueOfWires(input, "y")}")
        println("z=${valueOfWires(input, "z")}")

        val edgesWithoutDirectInputs = wirePairToEdges.values.flatten()
            .filter { it.isDirectInputEdge().not() }
        println("no direct input edges: ${edgesWithoutDirectInputs.size}")

        val selectedEdges = edgesWithoutDirectInputs.groupBy { it.operation }
        selectedEdges.onEach {
            println("${it.key} --> ${it.value.size}")
        }

        val corrected = mutableSetOf<String>()
        var firstIterationBestEdges = mutableListOf<Triple<Edge, Edge, Int>>()
        // first iteration
        var currentBrokenBit = findLeastSignificantOne(wirePairToEdges)
        for (i in edgesWithoutDirectInputs.indices) {
            for (j in i + 1 until edgesWithoutDirectInputs.size) {
                val e1 = edgesWithoutDirectInputs[i]
                val e2 = edgesWithoutDirectInputs[j]

                val wires = e1.wires() + e2.wires()

                if (wires.size != 4) {
                    continue
                }

                val swappedEdges = swapEdges(wirePairToEdges, e1, e2)
                val firstBrokenBitAfterSwap = findLeastSignificantOne(swappedEdges)

                if (currentBrokenBit < firstBrokenBitAfterSwap) {
                    println("broken bit increased ($currentBrokenBit -> $firstBrokenBitAfterSwap) for $e1, $e2")
//                        currentBrokenBit = firstBrokenBitAfterSwap
                    firstIterationBestEdges.add(Triple(e1, e2, firstBrokenBitAfterSwap))
//                        currentBrokenBit = firstBrokenBitAfterSwap
//                        wirePairToEdges = swappedEdges
//                        break
                }
            }
//                if (found) {
//                    break
//                }
        }
        println("SECOND iteration started - candidates")
        firstIterationBestEdges = firstIterationBestEdges.sortedByDescending { it.third }.toMutableList()
        firstIterationBestEdges.onEach {
            println(it)
        }

        assert(wirePairToEdges == initialWirePairToEdges) { "should be initial but are changed"}
        for ((it1E1, it1E2) in firstIterationBestEdges) {
            val secondIterationEdges = swapEdges(wirePairToEdges, it1E1, it1E2)
            for (i in edgesWithoutDirectInputs.indices) {
                for (j in i + 1 until edgesWithoutDirectInputs.size) {
                    val e1 = edgesWithoutDirectInputs[i]
                    val e2 = edgesWithoutDirectInputs[j]

                    val wires = e1.wires() + e2.wires() + it1E1.wires() + it1E2.wires()

                    if (wires.size != 8) {
                        continue
                    }

                    val swappedEdges = swapEdges(secondIterationEdges, e1, e2)
                    val firstBrokenBitAfterSwap = findLeastSignificantOne(swappedEdges)

                    if (currentBrokenBit < firstBrokenBitAfterSwap) {
                        println("broken bit increased ($currentBrokenBit -> $firstBrokenBitAfterSwap) for $e1, $e2")
                        currentBrokenBit = firstBrokenBitAfterSwap
                        corrected.clear()
                        corrected.addAll(wires)
                        println("current corrected: ${corrected.sorted().joinToString(",")}, broken bit: $currentBrokenBit")
//                        currentBrokenBit = firstBrokenBitAfterSwap
//                        wirePairToEdges = swappedEdges
//                        break
                    }
                }
//                if (found) {
//                    break
//                }
            }
        }

        println(corrected.sorted().joinToString(","))
    }

    /**
     * x =   ...111111
     * y =  ...000001
     * expected =
     * z = 1...0000000
     */
    private fun testingInput(initialWireToValue: MutableMap<String, Int>): MutableMap<String, Int> {
        val input = initialWireToValue.map { (key, _) ->
            if (key.startsWith("y")) {
                Pair(key, 1)
            } else if (key == "x00") {
                Pair(key, 1)
            } else if (key.startsWith("x")) {
                Pair(key, 0)
            } else {
                throw IllegalStateException()
            }
        }.toMap()
            .toMutableMap()
        return input
    }

    private fun swapEdges(
        wirePairToEdges: Map<String, List<Edge>>,
        e1: Edge,
        e2: Edge,
    ): MutableMap<String, MutableList<Edge>> {
        var newWirePairToEdges = wirePairToEdges
            .mapValues { (_, v) -> v.toMutableList() }
            .toMutableMap()

        val (f1, f2) = e1.swapOutput(e2)
        newWirePairToEdges[f1.wirePair()]!!.remove(e1)
        newWirePairToEdges[f1.wirePair()]!!.add(f1)

        newWirePairToEdges[f2.wirePair()]!!.remove(e2)
        newWirePairToEdges[f2.wirePair()]!!.add(f2)

        return newWirePairToEdges
    }

    data class Edge(val left: String, val right: String, val operation: Operation, val dest: String) {
        fun wirePair(): String {
            return if (left < right) {
                "$left;$right"
            } else {
                "$right;$left"
            }
        }

        fun wires() = setOf(left, right)

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

        fun operationEdge(): GraphEdge = GraphEdge(left, right, operation.toSymbol())
        fun rightToDestEdge(): GraphEdge = GraphEdge(right, dest, "=")
        fun isDirectInputEdge(): Boolean =
            left.startsWith("x") || left.startsWith("y")
                    || right.startsWith("x") || right.startsWith("y")

        enum class Operation {
            XOR, OR, AND;

            fun compute(leftVal: Int, rightVal: Int): Int = when (this) {
                XOR -> leftVal.xor(rightVal)
                OR -> leftVal.or(rightVal)
                AND -> leftVal.and(rightVal)
            }

            fun toSymbol(): String = when (this) {
                XOR -> "XOR"
                OR -> "OR"
                AND -> "AND"
            }
        }

        data class GraphEdge(val from: String, val to: String, val label: String)

        companion object {
            fun parse(leftRightDest: String): Edge {
                val (leftRight, dest) = leftRightDest.split(" -> ")
                val (left, operationStr, right) = leftRight.split(" ")
                val operation = Operation.valueOf(operationStr)
                return Edge(left, right, operation, dest)
            }
        }
    }

    fun viewGraph(vertices: Set<String>, edges: Set<Edge>) {
        val graph: Graph<String, Edge.GraphEdge> = SimpleGraph(Edge.GraphEdge::class.java)

        vertices.sorted().forEach {
            graph.addVertex(it)
        }
        edges.sortedBy { it.dest }.forEach {
            graph.addEdge(it.left, it.right, it.operationEdge())
            graph.addEdge(it.right, it.dest, it.rightToDestEdge())
        }
        renderGraph(graph)
    }

    private fun renderGraph(graph: Graph<String, Edge.GraphEdge>) {
        val exporter: DOTExporter<String, Edge.GraphEdge> =
            DOTExporter { v -> v }
        exporter.setEdgeAttributeProvider { e ->
            val map: MutableMap<String, Attribute> =
                LinkedHashMap()
            map["label"] = DefaultAttribute.createAttribute(e.label)
            map["orientation"] = DefaultAttribute.createAttribute("portrait")
            map
        }
        exporter.setVertexAttributeProvider { v ->
            val map: MutableMap<String, Attribute> =
                LinkedHashMap()
            map["label"] = DefaultAttribute.createAttribute(v.toString())
            map["orientation"] = DefaultAttribute.createAttribute("portrait")
            map
        }
        exporter.setGraphAttributeProvider {
            val map: MutableMap<String, Attribute> =
                LinkedHashMap()
            map["orientation"] = DefaultAttribute.createAttribute("landscape")
            map["ordering"] = DefaultAttribute.createAttribute("out")
            map
        }

        val writer: Writer = StringWriter()
        exporter.exportGraph(graph, writer)
        println(writer)
    }

    companion object {
        val TEST_INPUT = (listOf(Pair("x00", 1)) +
                (1..44).map {
                    Pair("x" + it.toString().padStart(2, '0'), 0)
                } + (0..44).map {
            Pair("y" + it.toString().padStart(2, '0'), 1)
        }).toMap()
    }
}
