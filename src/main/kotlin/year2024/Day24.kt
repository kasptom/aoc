package year2024

import aoc.IAocTaskKt

class Day24 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_24.txt"
//    override fun getFileName(): String = "aoc2024/input_24_test.txt"

    override fun solvePartOne(lines: List<String>) {
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

        val zRegisterMap = mutableMapOf<String, Int>()
        val nodeToValue = registerBitToValue.toMutableMap()
        for (zReg in zRegisters) {
            zRegisterMap[zReg] = evaluate(zReg, idToNode, nodeToValue)
        }
        zRegisterMap.entries.sortedByDescending { it.key }.map { it.value }.joinToString("").toLong(2)
            .let { println(it) }
    }

    private fun evaluate(
        id: String,
        idToNode: MutableMap<String, DeviceNode>,
        nodeToValue: MutableMap<String, Int>,
    ): Int {
        val node = idToNode[id]!!
        try {
            val value = when (node) {
                is AndNode -> evaluate(node.v1, idToNode, nodeToValue)
                    .and(evaluate(node.v2, idToNode, nodeToValue))

                is OrNode -> evaluate(node.v1, idToNode, nodeToValue)
                    .or(evaluate(node.v2, idToNode, nodeToValue))

                is XorNode -> evaluate(node.v1, idToNode, nodeToValue)
                    .xor(evaluate(node.v2, idToNode, nodeToValue))

                is OutputNode -> evaluate(node.input, idToNode, nodeToValue)
                is RegisterNode -> nodeToValue[node.id]!!
                is LazyInputNode -> idToNode.values
                    .filterIsInstance<OperationNode>().first { it.output == node.id }
                    .let { evaluate(it.id, idToNode, nodeToValue) }
            }
            nodeToValue[id] = value
            return value
        } catch (exc: Exception) {
            throw RuntimeException("exception for node $node", exc)
        }
    }

    override fun solvePartTwo(lines: List<String>) {
        TODO("Not yet implemented")
    }

    sealed interface DeviceNode {
        val id: String
    }

    data class Connection(
        val a: InputNode,
        val b: InputNode,
        val operation: OperationNode,
        val output: OutputNode,
    ) {
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

    sealed interface OperationNode : DeviceNode {
        val v1: String
        val v2: String
        val output: String

        companion object {
            fun parse(va: String, op: String, vb: String, rhs: String): OperationNode {
                val (v1, v2) = if (va < vb) Pair(va, vb) else Pair(vb, va)
                return when (op) {
                    "OR" -> OrNode("$v1;$op;$v2;$rhs", v1, v2, rhs)
                    "XOR" -> XorNode("$v1;$op;$v2;$rhs", v1, v2, rhs)
                    "AND" -> AndNode("$v1;$op;$v2;$rhs", v1, v2, rhs)
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
    ) : OperationNode

    data class XorNode(
        override val id: String,
        override val v1: String,
        override val v2: String,
        override val output: String,
    ) : OperationNode

    data class OrNode(
        override val id: String,
        override val v1: String,
        override val v2: String,
        override val output: String,
    ) : OperationNode

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
        companion object {
            fun parseValue(input: String): Pair<String, Int> {
                val (id, valueStr) = input.split(": ")
                return Pair(id, valueStr.toInt())
            }
        }
    }

    data class LazyInputNode(override val id: String) : InputNode

    data class OutputNode(override val id: String, val input: String) : DeviceNode {
        companion object {
            fun of(id: String, input: String): OutputNode {
                return OutputNode(id, input)
            }
        }
    }
}
