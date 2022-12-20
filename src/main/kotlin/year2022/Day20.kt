package year2022

import aoc.IAocTaskKt

class Day20 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_20.txt"

    override fun solvePartOne(lines: List<String>) {
        val numbers = lines.mapIndexed{ idx, line -> Number(line.toInt(), idx) }
        val idxToNumber = numbers.groupBy { it.originalPosition }.mapValues { (k, v) -> v.single() }
//        println(numbers.map { it.value })
        for (number in numbers) {
            var nextIdx = number.originalPosition + 1
            if (nextIdx == numbers.size) nextIdx = 0
            number.next = idxToNumber[nextIdx]

            var prevIdx = number.originalPosition - 1
            if (prevIdx < 0) prevIdx = numbers.size - 1
            number.prev = idxToNumber[prevIdx]
        }
        for (idx in numbers.indices) {
            val number = idxToNumber[idx]!!
            val value = number.value
            if (value > 0) repeat(value) {
                swap(number, number.next!!)
            } else repeat(-value){
                swap(number.prev!!, number)
            }

//            println("moved: ${number.value}")
//            var cursor = numbers[0]
//            repeat(numbers.size) {
//                print(" ${cursor.value} ")
//                cursor = cursor.next!!
//            }
//            println("\n----")
        }

        var cursor = numbers.first { it.value == 0 }
        val values = mutableListOf<Int>()
        for (repeat in 1..3000) {
            cursor = cursor.next!!
            if (repeat % 1000 == 0) {
                values.add(cursor.value)
            }
        }
        println(values)
        println(values.sum())
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
        println("Not yet implemented")
    }

    data class Number(val value: Int, val originalPosition: Int) {
        var next: Number? = null
        var prev: Number? = null
        override fun toString(): String {
            return "Number(value=$value, originalPosition=$originalPosition, next=${next?.value}, prev=${prev?.value})"
        }
    }
}