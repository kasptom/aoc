package year2022

import aoc.IAocTaskKt

class Day20 : IAocTaskKt {

    override fun getFileName(): String = "aoc2022/input_20.txt"

    override fun solvePartOne(lines: List<String>) {
        val (numbers, idxToNumber) = createNumbers(lines)
        moveValues(numbers, idxToNumber)
        val values = getValues(numbers)
        println(values.sum())
    }

    private fun moveValues(numbers: List<Number>, idxToNumber: Map<Int, Number>) {
        for (idx in numbers.indices) {
            val number = idxToNumber[idx]!!
            val value = number.value
            if (value > 0) {
                val times = value % (numbers.size.toLong() - 1)
                repeat(times.toInt()) {
                    swap(number, number.next!!)
                }
            } else {
                val times = (-value) % (numbers.size.toLong() - 1)
                repeat(times.toInt()) {
                    swap(number.prev!!, number)
                }
            }
        }
    }

    private fun getValues(numbers: List<Number>): MutableList<Long> {
        var cursor = numbers.first { it.value == 0L }
        val values = mutableListOf<Long>()
        for (repeat in 1..3000) {
            cursor = cursor.next!!
            if (repeat % 1000 == 0) {
                values.add(cursor.value)
            }
        }
        return values
    }

    private fun createNumbers(lines: List<String>, multiplier: Int = 1): Pair<List<Number>, Map<Int, Number>> {
        val numbers = lines.mapIndexed { idx, line -> Number(line.toLong() * multiplier, idx) }
        val idxToNumber = numbers.groupBy { it.originalPosition }.mapValues { (_, v) -> v.single() }
        //        println(numbers.map { it.value })
        for (number in numbers) {
            var nextIdx = number.originalPosition + 1
            if (nextIdx == numbers.size) nextIdx = 0
            number.next = idxToNumber[nextIdx]

            var prevIdx = number.originalPosition - 1
            if (prevIdx < 0) prevIdx = numbers.size - 1
            number.prev = idxToNumber[prevIdx]
        }
        return Pair(numbers, idxToNumber)
    }

    private fun swap(left: Number, right: Number) { // prev left right next --> // prev right left next
//        println("left: ${left.value}, right: ${right.value}")
        val prev = left.prev!!
        val next = right.next!!

        prev.next = right
        right.prev = prev

        right.next = left
        left.prev = right

        left.next = next
        next.prev = left
    }

    override fun solvePartTwo(lines: List<String>) {
        val multiplier = 811589153
        val (numbers, idxToNumber) = createNumbers(lines, multiplier)
        repeat(10) { moveValues(numbers, idxToNumber) }
        val values = getValues(numbers)
        println(values.sum())
    }

    data class Number(val value: Long, val originalPosition: Int, var next: Number? = null, var prev: Number? = null) {
        override fun toString(): String = "(v=$value, org=$originalPosition, next=${next?.value}, prev=${prev?.value})"
    }
}