package year2021

import aoc.IAocTaskKt
import kotlin.math.min

const val LITERAL_PACKET_CODE = "100"
const val SUB_PACKET_15_LENGTH_ID = "0"

val BIN_TO_HEX = mapOf(
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

class Day16 : IAocTaskKt {
    override fun getFileName() = "aoc2021/input_16.txt"

    override fun solvePartOne(lines: List<String>) {
        println(sumVersions(lines[0]))
    }

    fun sumVersions(line: String): Int {
        val input = BinaryInput.parse(line)
        println(input.hexFormat)
        println(input.binFormat)
        return input.chunks()
            .filter { it.type == BinaryInput.Chunk.Type.VERSION }
            .sumOf { it.binary.toInt(2) }
    }

    class BinaryInput(val hexFormat: String, val binFormat: String) {
        val chunkedBinary = binFormat.chunked(1)
        var pointer = 0
        var nextChunkType = Chunk.Type.VERSION

        companion object {
            fun parse(line: String): BinaryInput {
                val binFormat = hexToBin(line)
                return BinaryInput(hexFormat = line, binFormat)
            }

            fun hexToBin(hex: String) = hex.chunked(1).joinToString("") { BIN_TO_HEX[it]!! }
        }

        fun chunks(): MutableList<Chunk> {
            val chunks = mutableListOf<Chunk>()
            var nextChunk = nextChunk()
            while (nextChunk.type != Chunk.Type.END) {
                chunks += nextChunk
                nextChunk = nextChunk()
            }
            return chunks
        }

        private fun nextChunk(): Chunk {
            val packets = when (nextChunkType) {
                Chunk.Type.VERSION -> {
                    if (pointer < chunkedBinary.size - 3) {
                        nextChunkType = Chunk.Type.PACK_TYPE
                        Chunk(fetch(3), Chunk.Type.VERSION)
                    } else {
                        nextChunkType = Chunk.Type.END
                        Chunk("", Chunk.Type.END)
                    }
                }
                Chunk.Type.PACK_TYPE -> {
                    nextChunkType = selectPacketType(peek(3))
                    Chunk(fetch(3), Chunk.Type.PACK_TYPE)
                }
                Chunk.Type.LITERAL -> {
                    nextChunkType = literalOrNextPacket(peek(1))
                    Chunk(fetch(5), Chunk.Type.LITERAL)
                }
                Chunk.Type.LENGTH_15_PACKET_SIZE -> {
                    nextChunkType = Chunk.Type.VERSION
                    val fetched = fetch(15)
                    Chunk(fetched, Chunk.Type.LENGTH_15_PACKET_SIZE)
                }
                Chunk.Type.LENGTH_11_PACKET_COUNT -> {
                    nextChunkType = Chunk.Type.VERSION
                    val fetched = fetch(11)
                    Chunk(fetched, Chunk.Type.LENGTH_11_PACKET_COUNT)
                }
                Chunk.Type.OPERATOR -> {
                    val operatorCode = fetch(1)
                    nextChunkType = selectSizeOrCountFetch(operatorCode)
                    Chunk(operatorCode, Chunk.Type.OPERATOR)
                }
                Chunk.Type.END -> Chunk("", Chunk.Type.END)
            }

            return packets
        }

        private fun literalOrNextPacket(firstDigitOfLiteral: String): Chunk.Type =
            if (firstDigitOfLiteral == "1") Chunk.Type.LITERAL else Chunk.Type.VERSION

        private fun selectSizeOrCountFetch(id: String): Chunk.Type {
//            print("selecting size or count fetch ")
//            printCurrentPosition()
            return if (id == SUB_PACKET_15_LENGTH_ID) {
                Chunk.Type.LENGTH_15_PACKET_SIZE
            }
            else {
                Chunk.Type.LENGTH_11_PACKET_COUNT
            }
        }

        private fun selectPacketType(peek: String): Chunk.Type =
            if (peek == LITERAL_PACKET_CODE) Chunk.Type.LITERAL
            else Chunk.Type.OPERATOR

        private fun fetch(count: Int): String {
            val result = peek(count)
            val available = min(chunkedBinary.size - pointer, count)
//            print("fetching $available/$count: ")
//            printCurrentPosition(available)
            pointer += available
            if (available < count) {
                nextChunkType = Chunk.Type.END
            }
            return result
        }

        private fun peek(count: Int): String {
//            print("peeking $count: ")
//            printCurrentPosition()
            return chunkedBinary.subList(min(pointer, chunkedBinary.size), min(pointer + count, chunkedBinary.size)).joinToString("")
        }

        @Suppress("unused")
        fun printCurrentPosition(available: Int = 0) {
            for (idx in chunkedBinary.indices) {
                if (idx == pointer) {
                    print("\u001b[31m${chunkedBinary[idx]}\u001b[0m")
                } else {
                    print(chunkedBinary[idx])
                }
                if (pointer + available - 1 == idx) {
                    print("|")
                }
            }
            println()
        }

        data class Chunk(val binary: String, val type: Type) {
            val value = when(type) {
                Type.LITERAL -> binary.substring(1).toInt(2)
                Type.END -> "end"
                else -> if (binary.isEmpty()) "empty" else binary.toInt(2)
            }

            enum class Type {
                VERSION,
                PACK_TYPE,
                LENGTH_15_PACKET_SIZE,
                LENGTH_11_PACKET_COUNT,
                LITERAL,
                OPERATOR,
                END
            }

            override fun toString(): String {
                return "CHUNK('$binary', $type, $value)"
            }


        }
    }

    override fun solvePartTwo(lines: List<String>) {
        val input = BinaryInput.parse(lines[0])
        val chunks = input.chunks()
        println(chunks.size)
    }

    object Calculator {
        fun calculateExpression(): Int = 0
    }
}