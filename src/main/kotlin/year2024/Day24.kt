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


        val (initialWireToValue, initialWirePairToEdges) = parse(lines)

        // visualization
        val vertices = (initialWirePairToEdges.values.flatten().map { it.left }
                        + initialWirePairToEdges.values.flatten().map { it.right }
                        + initialWirePairToEdges.values.flatten().map { it.dest }).toSet()
        val edges = initialWirePairToEdges.values.flatten().toSet()
        // visualisation
//        viewGraph(vertices, edges)
        // x =   ...111111
        // y =  ...000001
        // expected =
        // z = 1...0000000
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
        compute(input, wirePairToEdges)
        println(" x=${valueOfWires(input, "x")}")
        println(" y=${valueOfWires(input, "y")}")
        println("z=${valueOfWires(input, "z")}")
        // z06 and z023
        val edgesWith06End = wirePairToEdges.values.flatten().filter { it.dest == "z06" }
        val edgesWith23End = wirePairToEdges.values.flatten().filter { it.dest == "z23" }

        assert(edgesWith06End.size == 1)
        assert(edgesWith23End.size == 1)

        println(edgesWith06End)
        println(edgesWith23End)
        var newWirePairToEdges = wirePairToEdges
            .mapValues { (_, v) -> v.toMutableList() }
            .toMutableMap()

        val edgeWithZ06 = edgesWith06End[0]
        val edgeWithZ23 = edgesWith23End[0]

        val (swappedA, swappedB) = edgeWithZ06.swapOutput(edgeWithZ23)
        newWirePairToEdges[swappedA.wirePair()]!!.remove(edgeWithZ06)
        newWirePairToEdges[swappedA.wirePair()]!!.add(swappedA)

        newWirePairToEdges[swappedB.wirePair()]!!.remove(edgeWithZ23)
        newWirePairToEdges[swappedB.wirePair()]!!.add(swappedB)

        compute(input, newWirePairToEdges)
        println(" x=${valueOfWires(input, "x")}")
        println(" y=${valueOfWires(input, "y")}")
        println("z=${valueOfWires(input, "z")}")
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

        fun operationEdge(): GraphEdge = GraphEdge(left, right, operation.toSymbol())
        fun rightToDestEdge(): GraphEdge = GraphEdge(right, dest, "=")

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
}
