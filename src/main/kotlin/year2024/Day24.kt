package year2024

import aoc.IAocTaskKt
import utils.GraphVisualisation
import utils.except

class Day24 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_24.txt"
//    override fun getFileName(): String = "aoc2024/input_24_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val (registerBitToValue, zRegisters, idToNode) = parse(lines)


        val nodeToValue = registerBitToValue.toMutableMap()
        val zResult = compute(zRegisters, idToNode, nodeToValue)

        zResult
            .toLong(2)
            .let { println(it) }
    }

    private fun compute(
        zRegisters: List<String>,
        idToNode: MutableMap<String, DeviceNode>,
        nodeToValue: MutableMap<String, Int>,
    ): String {
        val zRegisterMap = mutableMapOf<String, Int>()
        for (zReg in zRegisters) {
            zRegisterMap[zReg] = evaluate(zReg, idToNode, nodeToValue)
        }
        val zResult = zRegisterMap.entries.sortedByDescending { it.key }.map { it.value }.joinToString("")
        return zResult
    }

    private fun parse(lines: List<String>): Triple<Map<String, Int>, List<String>, MutableMap<String, DeviceNode>> {
        val separatorLineIdx = lines.indexOfFirst { it.isBlank() }
        val registerBitToValue = lines.subList(0, separatorLineIdx).associate { RegisterNode.parseValue(it) }

        val connections =
            lines.subList(separatorLineIdx + 1, lines.size).map { Connection.parseConnections(it, registerBitToValue) }

        val zRegisters = connections.map { it.output.id }.filter { it.startsWith("z") }.sorted()

        val idToNode = mutableMapOf<String, DeviceNode>()
        for (connection in connections) {
            val (a, b, operation, output) = connection
            idToNode[a.id] = a
            idToNode[b.id] = b
            idToNode[operation.id] = operation
            idToNode[output.id] = output
        }
        return Triple(registerBitToValue, zRegisters, idToNode)
    }

    private fun evaluate(
        id: String,
        idToNode: MutableMap<String, DeviceNode>,
        nodeToValue: MutableMap<String, Int>,
        prevNodes: Set<String>? = null
    ): Int {
        if (prevNodes.orEmpty().contains(id)) {
            return -1
        }

        val node = idToNode[id]!!
        try {
            val value = when (node) {
                is AndNode -> evaluate(node.v1, idToNode, nodeToValue, prevNodes.orEmpty() + node.id)
                    .and(evaluate(node.v2, idToNode, nodeToValue, prevNodes.orEmpty() + node.id))

                is OrNode -> evaluate(node.v1, idToNode, nodeToValue, prevNodes.orEmpty() + node.id)
                    .or(evaluate(node.v2, idToNode, nodeToValue, prevNodes.orEmpty() + node.id))

                is XorNode -> evaluate(node.v1, idToNode, nodeToValue, prevNodes.orEmpty() + node.id)
                    .xor(evaluate(node.v2, idToNode, nodeToValue, prevNodes.orEmpty() + node.id))

                is OutputNode -> evaluate(node.input, idToNode, nodeToValue, prevNodes.orEmpty() + node.id)
                is RegisterNode -> nodeToValue[node.id]!!
                is LazyInputNode -> idToNode.values
                    .filterIsInstance<OperationNode>().first { it.output == node.id }
                    .let { evaluate(it.id, idToNode, nodeToValue, prevNodes.orEmpty() + node.id) }
            }
            if (value == -1) {
                throw IllegalStateException("loop detected")
            }
            nodeToValue[id] = value
            return value
        } catch (exc: Exception) {
            throw RuntimeException("exception for node $node", exc)
        }
    }

    override fun solvePartTwo(lines: List<String>) {
        val separatorLineIdx = lines.indexOfFirst { it.isBlank() }
        val (registerBitToValue, zRegisters, idToNode) = parse(lines)

        val visualisation = GraphVisualisation()
        val connections =
            lines.subList(separatorLineIdx + 1, lines.size).map { Connection.parseConnections(it, registerBitToValue) }

        val vertices = idToNode.values.toSet()
        val edges = connections.flatMap {
            it.edges(idToNode)
        }.toSet()

        visualisation.viewGraph(vertices, edges)
        // z06,dhg
        // dpd,brk
        // z23,bhd
        // z38,nbf
        println(listOf("z06", "dhg", "dpd", "brk", "z23", "bhd", "z38","nbf").sorted().joinToString(","))

        println("X = $TEST_X")
        println("Y = $TEST_Y")
        val zResult = compute(zRegisters, idToNode, TEST_INPUT.toMutableMap())
        println("expected: $EXPECTED_OUTPUT")
        println("actual:   $zResult")

        val newLines = lines.swapOutput("z06", "dhg")
            .swapOutput("dpd", "brk")
            .swapOutput("z23", "bhd")
            .swapOutput("z38","nbf")
        val (_, newZRegisters, newIdToNode) = parse(newLines)
        val newZResult = compute(newZRegisters, newIdToNode, TEST_INPUT.toMutableMap())
        println("after swaps: $newZResult")
    }

    sealed interface DeviceNode : GraphVisualisation.GraphVertex {
        val id: String
        override val v: String
            get() = id
        override val label: String
            get() = id

        override fun compareTo(other: GraphVisualisation.GraphVertex): Int {
            return (label + v).compareTo(other.label + other.v)
        }
    }

    data class Connection(
        val a: InputNode,
        val b: InputNode,
        val operation: OperationNode,
        val output: OutputNode,
    ) {
        fun edges(idToNode: MutableMap<String, DeviceNode>): Set<ConnectionEdge> {
            return setOf(
                ConnectionEdge(idToNode[a.id]!!, idToNode[operation.id]!!, ""),
                ConnectionEdge(idToNode[b.id]!!, idToNode[operation.id]!!, ""),
                ConnectionEdge(idToNode[operation.id]!!, idToNode[output.id]!!, ""),
            )
        }

        companion object {
            fun parseConnections(input: String, registerBitToValue: Map<String, Int>): Connection {
                val (lhs, rhs) = input.split(" -> ")
                val (v1Str, opStr, v2Str) = lhs.split(" ")
                val bitNodeA = InputNode.of(id = v1Str, registerBitToValue)
                val bitNodeB = InputNode.of(id = v2Str, registerBitToValue)
                val operationNode = OperationNode.parse(v1Str, opStr, v2Str, rhs)
                val output = OutputNode.of(id = rhs, input = operationNode.id)
                return Connection(bitNodeA, bitNodeB, operationNode, output)
            }
        }
    }

    data class ConnectionEdge(
        override val v1: GraphVisualisation.GraphVertex,
        override val v2: GraphVisualisation.GraphVertex,
        override val label: String,
    ) : GraphVisualisation.GraphEdge

    sealed interface OperationNode : DeviceNode {
        val v1: String
        val v2: String
        val output: String

        companion object {
            fun parse(va: String, op: String, vb: String, rhs: String): OperationNode {
                val (v1, v2) = if (va < vb) Pair(va, vb) else Pair(vb, va)
                return when (op) {
                    "OR" -> OrNode("${v1}_${op}_${v2}_$rhs", v1, v2, rhs)
                    "XOR" -> XorNode("${v1}_${op}_${v2}_$rhs", v1, v2, rhs)
                    "AND" -> AndNode("${v1}_${op}_${v2}_$rhs", v1, v2, rhs)
                    else -> throw IllegalStateException("could not parse operation node from $va, $op, $vb, $rhs")
                }
            }
        }
    }

    data class AndNode(
        override val id: String,
        override val v1: String,
        override val v2: String,
        override val output: String,
        override val v: String = id,
    ) : OperationNode {
        override val label: String
            get() = "AND"
    }

    data class XorNode(
        override val id: String,
        override val v1: String,
        override val v2: String,
        override val output: String,
        override val v: String = id,
    ) : OperationNode {
        override val label: String
            get() = "XOR"
    }

    data class OrNode(
        override val id: String,
        override val v1: String,
        override val v2: String,
        override val output: String,
        override val v: String = id,
    ) : OperationNode {
        override val label: String
            get() = "OR"
    }

    sealed interface InputNode : DeviceNode {
        companion object {
            fun of(id: String, registerBitToValue: Map<String, Int>): InputNode {
                return if (id.startsWith("x") || id.startsWith("y")) {
                    RegisterNode(id, registerBitToValue[id]!!)
                } else {
                    LazyInputNode(id)
                }
            }
        }
    }

    data class RegisterNode(override val id: String, var value: Int = 0) : InputNode {
        override val v: String = id

        companion object {
            fun parseValue(input: String): Pair<String, Int> {
                val (id, valueStr) = input.split(": ")
                return Pair(id, valueStr.toInt())
            }
        }
    }

    data class LazyInputNode(override val id: String) : InputNode {
        override val v: String = id
    }

    data class OutputNode(override val id: String, val input: String) : DeviceNode {
        override val v: String = id

        companion object {
            fun of(id: String, input: String): OutputNode {
                return OutputNode(id, input)
            }
        }
    }

    companion object {
        val TEST_X =            "000000000000000000000000000000000000000000001"
        val TEST_Y =            "111111111111111111111111111111111111111111111"
        val EXPECTED_OUTPUT =  "1000000000000000000000000000000000000000000000"
        val TEST_INPUT = ((0..44).map {
                    Pair("x" + it.toString().padStart(2, '0'), TEST_X[44 - it].digitToInt())
                } + (0..44).map {
            Pair("y" + it.toString().padStart(2, '0'), TEST_Y[44 - it].digitToInt())
        }).toMap()
    }
}

private fun List<String>.swapOutput(out1: String, out2: String): List<String> {
    val output1 = filter { it.endsWith(out1) }
    val output2 = filter {it.endsWith(out2) }
    assert(output1.size == 1)
    assert(output2.size == 1)
    val a = output1.first()
    val b = output2.first()
    val replacementA = a.replace(out1, out2)
    val replacementB = b.replace(out2, out1)
    return this.except(a).except(b) + replacementA + replacementB
}
