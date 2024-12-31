package year2024

import aoc.IAocTaskKt

class Day09 : IAocTaskKt {
    override fun getFileName(): String = "aoc2024/input_09.txt"

    override fun solvePartOne(lines: List<String>) {
        val result = getBlocks(lines)
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

        val maxFileId = result.maxOf { it.value }
        for (fileId in maxFileId downTo 1) {
            val fileIdx = result.indexOfFirst { it.value == fileId }
            val file = result[fileIdx]
            val availableSpaceIdx = result.indexOfFirst { it.value == -1 && it.length >= file.length }
            if (availableSpaceIdx != -1 && availableSpaceIdx < fileIdx) {
                val availableSpace = result[availableSpaceIdx]
                if (availableSpace.length == file.length) {
                    result[fileIdx] = file.copy(value = -1)
                    result[availableSpaceIdx] = availableSpace.copy(value = file.value)
                } else {
                    val (alignedFile, alignedSpace) = file.alignWith(availableSpace)
                    result[fileIdx] = file.copy(value = -1)
                    result[availableSpaceIdx] = alignedSpace
                    result.add(availableSpaceIdx, alignedFile)
                }
            }
        }
        println(result.sumOf { it.checksum() })
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
        val result = (fromIndex..toIndex).map { it * value.toLong() }
        return result.sum()
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
}
