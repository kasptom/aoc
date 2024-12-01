package year2023

import aoc.IAocTaskKt

class Day15 : IAocTaskKt{
    override fun getFileName() = "aoc2023/input_15.txt"

    override fun solvePartOne(lines: List<String>) {
        val line = lines.first().trim().split(",").filter(String::isNotEmpty)
        println(line
//            .onEach { println("$it --> ${it.hash()}") }
            .sumOf { it.hash() })
    }

    override fun solvePartTwo(lines: List<String>) {
        val operations = lines.first().split(",")
            .filter(String::isNotEmpty)
            .map(Operation::parse)
//            .onEach(::println)
        val boxes = (0..255).map { Box(it.toLong()) }
            .groupBy { it.id }
            .mapValues { (_, v) -> v.first() }

        for (op in operations) {
//            println("After $op")
            op.perform(boxes)
//            boxes
//                .filter { (k, v) -> v.lenses.isNotEmpty() }
//                .onEach { println(it) }
        }
        val result = boxes
            .filter { (k, v) -> v.lenses.isNotEmpty() }
//            .onEach { (_, box) -> println("$box --> ${box.focusingPower()}" ) }
            .values
            .sumOf { it.focusingPower() }
        println(result)
    }

    data class Operation(val lensLabel: String, val targetBoxId: Long, val focalLength: Int?, val operation: String) {
        fun perform(boxes: Map<Long, Box>) {
            if (operation == "-") {
                val box = boxes[targetBoxId]!!
                box.removeLens(lensLabel)
            } else {
                val box = boxes[targetBoxId]!!
                box.addLens(lensLabel, focalLength!!)
            }
        }

        companion object {
            fun parse(input: String): Operation {
                return if (input.contains("=")) {
                    val operation = "="
                    val (lensLabel, focalLenthRaw) = input.split(Regex("[-=]")).filter(String::isNotEmpty)
                    Operation(
                        lensLabel = lensLabel,
                        targetBoxId = lensLabel.hash(),
                        focalLength = focalLenthRaw.toInt(),
                        operation = operation
                    )
                } else {
                    val label = input.replace("-", "")
                    Operation(lensLabel = label, targetBoxId = label.hash(), focalLength = null, operation = "-")
                }
            }
        }
    }

    data class Box(val id: Long, val lenses: MutableList<Pair<String, Int>> = mutableListOf()) {
        fun removeLens(lensLabel: String) {
            val updated = lenses.filter {
                val (label, focalLength) = it
                label != lensLabel
            }
            lenses.clear()
            lenses.addAll(updated)
        }

        fun addLens(lensLabel: String, focalLength: Int) {
            val foundLensIdx = lenses.indexOfFirst {
                val (label, _) = it
                label == lensLabel
            }
            if (foundLensIdx == -1) {
                lenses += Pair(lensLabel, focalLength)
            } else {
                lenses.add(foundLensIdx, Pair(lensLabel, focalLength))
                lenses.removeAt(foundLensIdx + 1)
            }
        }

        fun focusingPower(): Long = lenses.sumOf { (label, focalLength) ->
            (1 + id) * (lenses.indexOfFirst { (l, f) -> label == l } + 1) * focalLength
        }
    }
}

private fun String.hash(): Long {
    var currentValue = 0
    val word = split("").filter(String::isNotEmpty)
    // Determine the ASCII code for the current character of the string.
    // Increase the current value by the ASCII code you just determined.
    // Set the current value to itself multiplied by 17.
    // Set the current value to the remainder of dividing itself by 256.
    return word.fold(0) { acc, letter -> ((acc + letter.first().code) * 17) % 256 }
}


