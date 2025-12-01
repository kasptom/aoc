package year2021

import aoc.IAocTaskKt

val dxs = listOf(-1, 0, 1, -1, 0, 1, -1, 0, 1)
val dys = listOf(-1, -1, -1, 0, 0, 0, 1, 1, 1)

class Day20 : IAocTaskKt {
    override fun getFileName() = "aoc2021/input_20_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val enhancementAlgorithm = lines[0].chunked(1)
        val inputImage = lines.subList(1, lines.size)
            .map { line -> line.chunked(1).toMutableList() }
            .toMutableList()

        val times = 2
        val result = getLitPixelsCount(times, inputImage, enhancementAlgorithm)
        println(result)
    }

    override fun solvePartTwo(lines: List<String>) {
        val enhancementAlgorithm = lines[0].chunked(1)
        val inputImage = lines.subList(1, lines.size)
            .map { line -> line.chunked(1).toMutableList() }
            .toMutableList()

        val times = 50
        val result = getLitPixelsCount(times, inputImage, enhancementAlgorithm)
        println(result)
    }

    private fun getLitPixelsCount(
        times: Int,
        image: MutableList<MutableList<String>>,
        enhancementAlgorithm: List<String>,
    ): Int {
        var inputImage = image
        repeat(5 * times) { inputImage.grow() }

        repeat(times) {
            val outputImage = inputImage.copy()
            inputImage.update(enhancementAlgorithm, outputImage)
            inputImage = outputImage
        }

        //        display(inputImage)
        inputImage.clearEdge(times)
        return inputImage.flatten().count { it == "#" }
    }

    @Suppress("unused")
    private fun display(groups: List<List<String>>) {
        for (element in groups) {
            for (x in 0 until groups[0].size) {
                print(element[x])
            }
            println()
        }
        println()
    }
}

private fun MutableList<MutableList<String>>.clearEdge(padding: Int) {
    for (y in indices) {
        for (x in this[0].indices) {
            if (y <= padding || y >= size - padding) this[y][x] = "."
            if (x <= padding || x >= size - padding) this[y][x] = "."
        }
    }
}

private fun MutableList<MutableList<String>>.copy(): MutableList<MutableList<String>> {
    val copy = mutableListOf<MutableList<String>>()
    for (row in this) {
        copy += row.toMutableList()
    }
    return copy
}

private fun MutableList<MutableList<String>>.update(
    enhancementAlgorithm: List<String>,
    outputImage: MutableList<MutableList<String>>,
) {
    for (y in 1 until this.size - 1) {
        for (x in 1 until this[0].size - 1) {
            val binCode = mutableListOf<String>()
            for (idx in dxs.indices) {
                val otherX = x + dxs[idx]
                val otherY = y + dys[idx]
                binCode += if (this[otherY][otherX] == "#") "1" else "0"
            }
            val joined = binCode.joinToString("")
            val algoIdx = joined.toInt(2)
//            println("$x, $y -> $joined -> $algoIdx -> ${enhancementAlgorithm[algoIdx]}")
            outputImage[y][x] = enhancementAlgorithm[algoIdx]
        }
    }
}

private fun MutableList<MutableList<String>>.grow() {
    val edgeRowSize = this[0].size + 2
    val topRow = (0 until edgeRowSize).map { "." }.toMutableList()
    val bottomRow = (0 until edgeRowSize).map { "." }.toMutableList()
    this.forEach { row ->
        row.add(0, ".")
        row.add(".")
    }
    this.add(0, topRow)
    this.add(bottomRow)
}
