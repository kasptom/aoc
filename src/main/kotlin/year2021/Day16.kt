package year2021

import aoc.IAocTaskKt
import java.lang.IllegalArgumentException
import java.lang.IndexOutOfBoundsException

val HEX_TO_BIN = mapOf(
    "0" to "0000",
    "1" to "0001",
    "2" to "0010",
    "3" to "0011",
    "4" to "0100",
    "5" to "0101",
    "6" to "0110",
    "7" to "0111",
    "8" to "1000",
    "9" to "1001",
    "A" to "1010",
    "B" to "1011",
    "C" to "1100",
    "D" to "1101",
    "E" to "1110",
    "F" to "1111",
)

const val LITERAL_VALUE_TYPE_ID = 4

class Day16 : IAocTaskKt {
    override fun getFileName() = "aoc2021/input_16.txt"
    override fun solvePartOne(lines: List<String>) {
        val binInput = BinaryInput.fromHex(lines[0])
        val rootPacket = PacketNode.parse(binInput)
        val versionsCount: Int = sumVersions(rootPacket)
        println(versionsCount)
    }

    fun sumVersions(packet: PacketNode): Int {
        if (packet.children.isEmpty()) return packet.version
        return packet.version + packet.children.sumOf { sumVersions(it) }
    }

    override fun solvePartTwo(lines: List<String>) {
        val binInput = BinaryInput.fromHex(lines[0])
        val rootPacket = PacketNode.parse(binInput)
        val result = rootPacket.compute()
        println(result)
    }

    class PacketNode(private val type: Type, val version: Int, private val opType: OpType, val value: Long? = null, val children: List<PacketNode>) {
        companion object {
            fun parse(binaryInput: BinaryInput): PacketNode {
                val version = binaryInput.fetchAsString(3).toInt(2)
                val typeId = binaryInput.fetchAsString(3).toInt(2)
                val opType = OpType.fromId(typeId)
                return if (opType == OpType.VALUE) createLiteralPacketNode(version, binaryInput)
                else createOperatorPacketNode(version, opType, binaryInput)
            }

            private fun createLiteralPacketNode(version: Int, binaryInput: BinaryInput, trySkipPadding: Boolean = true): PacketNode {
                var usedBits = 6
                var nextFiveBits = binaryInput.fetchAsString(5)
                var binaryNumber = nextFiveBits.substring(1, 5)
                usedBits += 5
                while (nextFiveBits[0] != '0') {
                    nextFiveBits = binaryInput.fetchAsString(5)
                    usedBits += 5
                    binaryNumber += nextFiveBits.substring(1, 5)
                }
                if (trySkipPadding) {
                    skipPadding(usedBits, binaryInput)
                }
                return PacketNode(Type.LITERAL, version, OpType.VALUE, binaryNumber.toLong(2), emptyList())
            }

            private fun skipPadding(usedBits: Int, binaryInput: BinaryInput) {
                val endPaddingSize = usedBits % 4
                if (endPaddingSize == 0) return
                val padding = binaryInput.fetch(endPaddingSize).distinct()
                if (padding != listOf(0)) throw IllegalStateException("padding should contain only zeros")
            }

            private fun createOperatorPacketNode(version: Int, opType: OpType, binaryInput: BinaryInput): PacketNode {
                val operatorMode = binaryInput.fetch(1)[0]
                return if (operatorMode == 0) {
                    val subPacketsLength = binaryInput.fetchAsString(15).toInt(2)
                    val subBinaryInput = BinaryInput(binaryInput.fetch(subPacketsLength))
                    val children: List<PacketNode> = createModeZeroOperatorChildren(subBinaryInput)
                    PacketNode(Type.OPERATOR, version, opType, value = null, children = children)
                } else {
                    val subPacketsCount = binaryInput.fetchAsString(11).toInt(2)
                    val children = createModeOneOperatorChildren(subPacketsCount, binaryInput)
                    PacketNode(Type.OPERATOR, version, opType, value = null, children = children)
                }
            }

            private fun createModeZeroOperatorChildren(subBinaryInput: BinaryInput): List<PacketNode> {
                val children = mutableListOf<PacketNode>()
                while (subBinaryInput.isNotEmpty()) {
                    createChildren(subBinaryInput, children)
                }
                return children
            }

            private fun createModeOneOperatorChildren(subPacketsCount: Int, binaryInput: BinaryInput): List<PacketNode> {
                val children = mutableListOf<PacketNode>()
                var count = 0
                while (count != subPacketsCount) {
                    count++
                    createChildren(binaryInput, children)
                }
                return children
            }

            private fun createChildren(subBinaryInput: BinaryInput, children: MutableList<PacketNode>, ) {
                val version = subBinaryInput.fetchAsString(3).toInt(2)
                val typeId = subBinaryInput.fetchAsString(3).toInt(2)
                val opType = OpType.fromId(typeId)
                val child =
                    if (typeId == LITERAL_VALUE_TYPE_ID) createLiteralPacketNode(version, subBinaryInput, false)
                    else createOperatorPacketNode(version, opType, subBinaryInput)
                children += child
            }
        }

        enum class Type {
            LITERAL, OPERATOR
        }

        enum class OpType(val id: Int) {
            SUM(0), PRODUCT(1), MIN(2), MAX(3), VALUE(4), GR8R_THAN(5), LESS_THAN(6), EQUAL(7);

            companion object {
                fun fromId(id: Int): OpType {
                    return values().first { it.id == id }
                }
            }
        }

        override fun toString(): String {
            val childrenStr = if (children.isNotEmpty()) children.joinToString(", \t\n", "\n[\n", "\n]") else ""
            return "PACKET(T=$type, v=$version, $opType, val=$value${childrenStr})"
        }

        fun compute(): Long {
            val reducedChildrenValues = children.map { if (it.opType != OpType.VALUE) it.compute() else it.value!! }
            return when (opType) {
                OpType.SUM -> reducedChildrenValues.sumOf { it }
                OpType.PRODUCT -> reducedChildrenValues.fold(1L) { a, b -> a * b}
                OpType.MIN -> reducedChildrenValues.minOf { it }
                OpType.MAX -> reducedChildrenValues.maxOf { it }
                OpType.VALUE -> value!!
                OpType.GR8R_THAN -> if (reducedChildrenValues[0] > reducedChildrenValues[1]) 1L else 0L
                OpType.LESS_THAN -> if (reducedChildrenValues[0] < reducedChildrenValues[1]) 1L else 0L
                OpType.EQUAL -> if (reducedChildrenValues[0] == reducedChildrenValues[1]) 1L else 0L
            }
        }
    }
}

class BinaryInput(private val binDigits: List<Int>) {

    private var pointer = 0
    fun fetchAsString(count: Int) = fetch(count).joinToString("")

    fun fetch(count: Int): List<Int> {
        if (count <= 0) throw IllegalArgumentException("count $count")
        if (pointer + count > binDigits.size) throw IndexOutOfBoundsException("$pointer + $count > ${binDigits.size}")
        val fetched = binDigits.subList(pointer, pointer + count)
        pointer += count
        return fetched
    }

    fun isNotEmpty() = binDigits.size != pointer
    override fun toString(): String {
        return "BIN(${binDigits.size}, $pointer)"
    }


    companion object {
        fun fromHex(hexInput: String): BinaryInput {
            val binDigits = hexInput.chunked(1) // A B
                .map { HEX_TO_BIN[it]!! } // 1010 1011
                .map { it.chunked(1) } // [1 0 1 0] [1 0 1 1]
                .flatten()
                .map { it.toInt() }
            return BinaryInput(binDigits)
        }
    }
}