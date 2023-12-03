package year2023

import aoc.IAocTaskKt

class Day03 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_03.txt"

    override fun solvePartOne(lines: List<String>) {
        val schematic: Array<Array<String>> = lines.map { it.trim().split("")
            .filter { x -> x != "" }
            .toTypedArray() }
            .toTypedArray()

        val numbers: List<SchematicNumber> = schematic.findNumbers()
        val partNumbers = numbers
            .filter { it.isPartNumber(schematic) }

        println(numbers.size)
        println(numbers)

        val notPartNumbers = numbers.filter { it.isPartNumber(schematic).not() }
        println(notPartNumbers.size)
        println(notPartNumbers)

        println(partNumbers.size)
        println(partNumbers)
        println(partNumbers.sumOf { it.value })
    }

    override fun solvePartTwo(lines: List<String>) {
        TODO("Not yet implemented")
    }

    data class SchematicNumber(val value: Int, val minX: Int, val maxX: Int, val y: Int) {
        fun isPartNumber(schematic: Array<Array<String>>): Boolean {
            for (x in minX..maxX) {
                for (idx in DX.indices) {
                    val dx = DX[idx]
                    val dy = DY[idx]
                    val xPos = x + dx
                    val yPos = y + dy
                    if (notInRange(xPos, schematic[0].size) || notInRange(yPos, schematic.size)) {
                        continue
                    }
                    val neighbour = schematic[yPos][xPos]
                    if (neighbour.matches(Regex("\\d")).not() && neighbour != ".") {
                        return true
                    }
                }
            }
            return false
        }

        private fun notInRange(pos: Int, size: Int): Boolean = pos < 0 || pos >= size
        override fun toString(): String {
            return "($value, x=[$minX..$maxX], y=$y)"
        }


        companion object {
            fun create(schematic: Array<Array<String>>, xIdx: Int, yIdx: Int): SchematicNumber {
                val row = schematic[yIdx]
                var numberBeginningIdx = xIdx
                while (numberBeginningIdx >= 0 && row[numberBeginningIdx].matches(Regex("\\d"))) {
                    numberBeginningIdx--
                }
                numberBeginningIdx++
                val value = row.toList().subList(numberBeginningIdx, xIdx + 1).joinToString("").toInt()
                return SchematicNumber(value, numberBeginningIdx, xIdx, yIdx)
            }
        }
    }

    private fun Array<Array<String>>.findNumbers(): List<SchematicNumber> {
        val foundNumbers = mutableListOf<SchematicNumber>()
        for (yIdx in indices) {
            for (xIdx in this[yIdx].indices) {
                if (this[yIdx][xIdx].matches(Regex("\\d"))
                    && (xIdx == this[yIdx].size - 1 || this[yIdx][xIdx + 1].matches(Regex("\\d")).not())
                ) {
                    val number = SchematicNumber.create(this, xIdx, yIdx)
                    foundNumbers.add(number)
                }
            }
        }
        return foundNumbers
    }

    companion object {
        val DX: IntArray = intArrayOf(-1,  0,  1, -1, 1, -1, 0, 1)
        val DY: IntArray = intArrayOf(-1, -1, -1,  0, 0,  1, 1, 1)
    }
}

