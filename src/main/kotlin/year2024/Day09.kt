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
        val result = getBlocks(lines)
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
        val result = getBlocks2(lines).toMutableList()
        println(result.joinToString("") {it.toCode()})

        // part 2
        val maxFileId = result.maxOf { it.value }
        for (fileId in maxFileId downTo 1) {
            val fileIdx = result.indexOfFirst { it.value == fileId }
            val file = result[fileIdx]
            val availableSpaceIdx = result.indexOfFirst { it.value == -1 && it.length >= file.length }
            if (availableSpaceIdx != -1 && availableSpaceIdx < fileIdx) {
                val availableSpace = result[availableSpaceIdx]
                if (availableSpace.length == file.length) {
                    result[fileIdx] = file.copy(value = -1)
                    result[availableSpaceIdx] = file
                } else {
                    val (alignedFile, alignedSpace) = file.alignWith(availableSpace)
                    result[fileIdx] = file.copy(value = -1)
                    result[availableSpaceIdx] = alignedSpace
                    result.add(availableSpaceIdx, alignedFile)
                }
            }
        }
        println(result.joinToString(""))
        println(result.joinToString("") { it.toCode() })

        //

        val str = result.joinToString("") { it.toCode() }
        var idx = 0
        var sum = 0L
        for (i in str.indices) {
            val value = str.substring(i, i + 1)
            if (value == ".") {
                continue
            }
            sum += value.toInt() * i
        }
        println(sum)
    }

    private fun getBlocks(lines: List<String>): IntArray {
        val blockSizes = lines[0].chunked(1)
            .map { it.toInt() }
        val resultSize = blockSizes.sum()
        val result = IntArray(resultSize)
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
        return result
    }

    private fun getBlocks2(lines: List<String>): List<Block> {
        val blockSizes = lines[0].chunked(1)
            .map { it.toInt() }
        val resultSize = blockSizes.sum()
        val result = mutableListOf<Block>()

        var idx = 0
        var isFile = true
        var idGenerator = 0
        var blockIdx = 0
        while (idx < resultSize) {
            val blockLength = blockSizes[blockIdx++]
            if (isFile) {
                result.add(Block(value = idGenerator, fromIndex = idx, toIndex = idx + blockLength - 1))
                idGenerator++
            } else if (blockLength != 0){
                result.add(Block(value = -1, fromIndex = idx, toIndex = idx + blockLength - 1))
            }
            idx += blockLength
            isFile = isFile.not()
        }
        return result
    }
}

private fun IntArray.swap(i: Int, j: Int) {
    val tmp = this[i]
    this[i] = this[j]
    this[j] = tmp
}

private fun IntArray.checksum(): Long =
    this.mapIndexed { index, value -> index.toLong() * (if (value == -1) 0L else value.toLong()) }
        .sum()

data class Block(val value: Int, val fromIndex: Int, val toIndex: Int) {
    val length = toIndex - fromIndex + 1

    fun checksum(): Long {
        if (value == -1 || value == 0) {
            return 0
        }
        return (fromIndex..toIndex).sumOf { (fromIndex + it) * value.toLong() }
    }

    fun alignWith(availableSpace: Block): Pair<Block, Block> {
        val fileFromIdx = availableSpace.fromIndex
        val fileToIdx = fileFromIdx + this.length - 1
        val spaceFromIdx = fileToIdx + 1
        return Pair(copy(fromIndex = fileFromIdx, toIndex = fileToIdx), availableSpace.copy(fromIndex = spaceFromIdx))
    }

    override fun toString(): String {
        return if (value != -1) "$fromIndex..$toIndex($value)" else "$fromIndex..$toIndex(❌)"
    }

    fun toCode(): String {
        return (1..length).map { if (value != -1) value else "." }.joinToString("")
    }
}
