package year2024

import aoc.IAocTaskKt

class Day09 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_09.txt"
//     override fun getFileName(): String = "aoc2024/input_09_test.txt"

    override fun solvePartOne(lines: List<String>) {
        // 2333133121414131402
        // file length free space
        // Each file on disk also has an ID number
            // based on the order of the files as they appear *before they are rearranged* 0-idx
        // 12345
        // --> 0..111....22222
        // 2333133121414131402
        // --> 00...111...2...333.44.5555.6666.777.888899
        // moving blocks
        /* (steps)
            0..111....22222
            02.111....2222.
            022111....222..
            0221112...22...
            02211122..2....
            022111222......
         */
        // then compute checksum:
        // multiply position time idx
        // 2333133121414131402
        // --> 00...111...2...333.44.5555.6666.777.888899
        /*
            00...111...2...333.44.5555.6666.777.888899
            009..111...2...333.44.5555.6666.777.88889.
            0099.111...2...333.44.5555.6666.777.8888..
            00998111...2...333.44.5555.6666.777.888...
            009981118..2...333.44.5555.6666.777.88....
            0099811188.2...333.44.5555.6666.777.8.....
            009981118882...333.44.5555.6666.777.......
            0099811188827..333.44.5555.6666.77........
            00998111888277.333.44.5555.6666.7.........
            009981118882777333.44.5555.6666...........
            009981118882777333644.5555.666............
            00998111888277733364465555.66.............
            0099811188827773336446555566..............
         */
        // 0 * 0 = 0, 1 * 0 = 0, 2 * 9 = 18, 3 * 9 = 27, 4 * 8 = 32, and so on. In this example, the checksum is the sum of these, 1928
        val blockSizes = lines[0].chunked(1)
            .map { it.toInt() }
        val resultSize = blockSizes.sum()
        val result =  IntArray(resultSize)
        result.fill(-1)

        var idx = 0
        var isFile = true
        var idGenerator = 0
        var blockIdx = 0
        while (idx < resultSize) {
            val blockLength = blockSizes[blockIdx++]
            if (isFile) {
                result.fill(element = idGenerator, fromIndex = idx, toIndex = idx + blockLength)
                idGenerator++
            } else {
                result.fill(element = -1, fromIndex = idx, toIndex = idx + blockLength)
            }
            idx += blockLength
            isFile = isFile.not()
        }
        println(result.map { if (it == -1) "." else it.toString() }.joinToString(""))
        var lastDigitIdx = result.size - 1
        var firstSpaceIdx = result.indexOfFirst { it == -1 }
        while (firstSpaceIdx < lastDigitIdx) {
            result.swap(firstSpaceIdx, lastDigitIdx)
            lastDigitIdx--
            firstSpaceIdx = result.indexOfFirst { it == -1 }
        }

        println(result.checksum())
    }

    override fun solvePartTwo(lines: List<String>) {
        if (lines.isEmpty()) println("empty lines") else println(lines.size)
    }
}

private fun IntArray.swap(i: Int, j: Int) {
    val tmp = this[i]
    this[i] = this[j]
    this[j] = tmp
}

private fun IntArray.checksum(): Long = this.mapIndexed { index, value -> index.toLong() * (if (value == -1) 0L else value.toLong()) }
    .sum()
