package year2022

import aoc.IAocTaskKt

class Day13 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_13.txt"

    override fun solvePartOne(lines: List<String>) {
        val packetPairs = lines.chunked(3)
            .map { group -> group.filter { it.isNotEmpty() } }
//            .onEach { println("chunk: $it") }
            .mapIndexed { idx, pair -> PacketPair.parse(idx + 1, pair) }

        packetPairs.onEach { println(it) }
        val result = packetPairs
            .map { (id, left, right) -> if (left <= right) id else 0 }
            .sumOf { it }

        println(result)
    }

    override fun solvePartTwo(lines: List<String>) {
        println("todo")
    }

    data class PacketPair(val id: Int, val left: ParsedPacketValue, val right: ParsedPacketValue) {
        companion object {
            fun parse(id: Int, twoLines: List<String>): PacketPair {
//                println("parsing pair: $twoLines")
                val (left, right) = twoLines.map { PacketValue.parse(it) }.zipWithNext().single()
//                println("--------")
                return PacketPair(id, left as ParsedPacketValue, right as ParsedPacketValue)
            }
        }
    }


    sealed interface PacketValue {
        companion object {
            const val ANSI_RESET = "\u001B[0m"
            const val ANSI_GREEN = "\u001B[32m"
            const val ANSI_YELLOW = "\u001B[33m"
            fun parse(line: String): PacketValue {
//                println("parsing value: $line")
                var packetValue: ListPacketValue = line.replace("[", "[,")
                    .replace("]", ",]")
                    .split(",")
                    .map { NotParsedPacketValue(it).toSimpleParsedPacketValue() }
                    .let { ListPacketValue(it) }

                while (packetValue.value.any { it is NotParsedPacketValue }) {
//                    println("\t$packetValue")
                    val oldValue = packetValue.value
                    val lastNotParsedLeftBraceIdx = oldValue
                        .indexOfLast { it is NotParsedPacketValue && it.isLeftBrace() }
                    val nextNotParsedRightBraceIdx = oldValue
                        .mapIndexed { idx, value -> Pair(idx, value) }
                        .indexOfFirst { (idx, it) -> idx > lastNotParsedLeftBraceIdx && it is NotParsedPacketValue && it.isRightBrace() }
                    if (lastNotParsedLeftBraceIdx > nextNotParsedRightBraceIdx) {
//                        println("$lastNotParsedLeftBraceIdx > $nextNotParsedRightBraceIdx")
                        throw IllegalStateException("parse failed for: $line current state: $packetValue")
                    } /*else {
                        println("$lastNotParsedLeftBraceIdx <= $nextNotParsedRightBraceIdx")
                    }*/

                    val listPacket =
                        packetValue.value.subList(lastNotParsedLeftBraceIdx + 1, nextNotParsedRightBraceIdx)
                            .let { ListPacketValue(value = it) }
                    val newValue = oldValue
                        .subList(0, lastNotParsedLeftBraceIdx) + listPacket + oldValue
                        .subList(nextNotParsedRightBraceIdx + 1, oldValue.size)

                    packetValue = packetValue.copy(value = newValue)
                }

                return packetValue
            }
            // [1,[2,[3,[4,[5,6,7]]]],8,9]
        }
    }

    sealed interface ParsedPacketValue : PacketValue {
        operator fun compareTo(other: ParsedPacketValue): Int =
            if (this is IntPacketValue && other is IntPacketValue) this.value.compareTo(other.value)
            else if (this is IntPacketValue && other is ListPacketValue) this.compareIntToList(other)
            else if (this is ListPacketValue && other is ListPacketValue) this.compareLists(other)
            else if (this is ListPacketValue && other is IntPacketValue) this.compareListToInt(other)
            else throw IllegalStateException()
    }

    data class IntPacketValue(val value: Int) : ParsedPacketValue {
        override fun toString() = "$value"
        fun compareIntToList(other: ListPacketValue): Int {
            val firstRight = other.value.firstOrNull() as ParsedPacketValue? ?: return 1
            val compare = this.compareTo(firstRight)
            if (compare != 0) return compare
            return 1 - other.value.size
        }
    }

    data class ListPacketValue(val value: List<PacketValue>) : ParsedPacketValue {
        override fun toString(): String = "$value"
        fun compareLists(other: ListPacketValue): Int {
            for (idx in this.value.indices) {
                val left = this.value[idx] as ParsedPacketValue
                if (idx >= other.value.size) return 1
                val right = other.value[idx] as ParsedPacketValue
                val comparisonResult = left.compareTo(right)
                if (comparisonResult != 0) return comparisonResult
            }
            return this.value.size - other.value.size
        }

        fun compareListToInt(other: IntPacketValue): Int {
            val firstLeft = this.value.firstOrNull() as ParsedPacketValue?
            val compare = firstLeft?.compareTo(other) ?: -1
            if (compare != 0) return compare
            return this.value.size - 1
        }
    }

    data class NotParsedPacketValue(val value: String) : ParsedPacketValue {
        fun isBlank() = value.isBlank()
        fun isLeftBrace() = value == "["
        fun isRightBrace() = value == "]"
        fun toIntPacketValue() = IntPacketValue(value.toInt())
        fun toSimpleParsedPacketValue(): PacketValue {
            return if (isBlank()) {
                ListPacketValue(emptyList())
            } else if (!isLeftBrace() && !isRightBrace()) {
                toIntPacketValue()
            } else this
        }

        override fun toString(): String = "NP($value)"
    }
}

// 6025 too high