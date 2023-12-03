package year2023

import aoc.IAocTaskKt

class Day03 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_03.txt"

    override fun solvePartOne(lines: List<String>) {
        val schematic = EngineSchematic(lines.parse2dArray())
        val numbers: List<SchematicNumber> = schematic.findNumbers()

        val partNumbers = numbers
            .onEach { it.findPart(schematic) }
            .filter { it.partPos != null }

        println(partNumbers.sumOf(SchematicNumber::value))
    }

    override fun solvePartTwo(lines: List<String>) {
        val schematic = EngineSchematic(lines.parse2dArray())
        val numbers: List<SchematicNumber> = schematic.findNumbers()

        val ratioNumbersSum = numbers
            .onEach { it.findPart(schematic) }
            .filter { it.isGearPartNumber(schematic) }
            .groupBy { it.partPos }
            .filterValues { it.size == 2 }
            .values
            .sumOf { gearNumbers -> gearNumbers.map { it.value }.reduce { x, y -> x * y } }

        println(ratioNumbersSum)
    }

    private fun List<String>.parse2dArray() = map {
        it.trim()
            .split("")
            .filter(String::isNotEmpty)
    }

    data class Pos(val x: Int, val y: Int) {
        override fun toString(): String = "($x, $y)"
        operator fun plus(other: Pos): Pos = Pos(x + other.x, y + other.y)
        operator fun minus(other: Pos): Pos = Pos(x - other.x, y - other.y)
    }

    data class SchematicNumber(val value: Int, val minPos: Pos, val maxPos: Pos, var partPos: Pos? = null) {
        fun findPart(schematic: EngineSchematic) {
            for (x in minPos.x..maxPos.x) {
                for (idx in DX.indices) {
                    val neighbourPos = Pos(x + DX[idx], minPos.y + DY[idx])
                    if (schematic.notInRange(neighbourPos)) {
                        continue
                    }
                    val neighbour = schematic.cell(neighbourPos)
                    if (neighbour.isDigit().not() && neighbour != ".") {
                        partPos = neighbourPos
                    }
                }
            }
        }

        fun isGearPartNumber(schematic: EngineSchematic): Boolean =
            if (partPos == null) false else schematic.cell(partPos!!) == "*"

        override fun toString(): String = "($value, [$minPos..$maxPos])"

        companion object {
            fun create(schematic: EngineSchematic, lastDigitPos: Pos): SchematicNumber {
                val row = schematic.row(lastDigitPos.y)
                var firstDigitPos = lastDigitPos
                while (schematic.inRange(firstDigitPos) && schematic.cell(firstDigitPos).isDigit()) {
                    firstDigitPos -= Pos(1, 0)
                }
                firstDigitPos += Pos(1, 0)

                val value = row.toList().subList(firstDigitPos.x, lastDigitPos.x + 1).joinToString("").toInt()
                return SchematicNumber(value, firstDigitPos, lastDigitPos)
            }
        }
    }

    class EngineSchematic(private val rawSchematic: List<List<String>>) {
        private val positions = rawSchematic.flatMapIndexed { yIdx, row -> row.indices.map { Pos(it, yIdx) } }

        fun findNumbers(): List<SchematicNumber> {
            val foundNumbers = mutableListOf<SchematicNumber>()
            positions.forEach {
                val (xIdx, yIdx) = it
                val nextPosition = it + Pos(1, 0)
                if (isDigit(it) && (notInRange(nextPosition) || isNotDigit(nextPosition))) {
                    val lastDigitPos = Pos(xIdx, yIdx)
                    val number = SchematicNumber.create(schematic = this, lastDigitPos)
                    foundNumbers += number
                }
            }
            return foundNumbers
        }

        fun cell(pos: Pos): String = rawSchematic[pos.y][pos.x]
        fun row(yIdx: Int): List<String> = rawSchematic[yIdx]
        fun notInRange(pos: Pos): Boolean = inRange(pos).not()
        fun inRange(pos: Pos): Boolean = pos.x in rawSchematic[0].indices && pos.y in rawSchematic.indices

        private fun isDigit(pos: Pos) = cell(pos).isDigit()
        private fun isNotDigit(pos: Pos) = isDigit(pos).not()
    }

    companion object {
        val DX: IntArray = intArrayOf(-1, 0, 1, -1, 1, -1, 0, 1)
        val DY: IntArray = intArrayOf(-1, -1, -1, 0, 0, 1, 1, 1)
    }
}

private fun String.isDigit(): Boolean = matches(Regex("\\d"))
