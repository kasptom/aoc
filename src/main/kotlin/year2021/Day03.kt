package year2021

import aoc.IAocTaskKt

class Day03 : IAocTaskKt{
    override fun getFileName(): String = "aoc2021/input_03.txt"
    var size = 0

    override fun solvePartOne(lines: List<String>) {
        size = lines[0].length
        val onesCounter = IntArray(size)
        val zerosCounter = IntArray(size)
        for (binary in lines) {
            for (idx in 0 until size) {
                if (binary[idx] == '0') zerosCounter[idx]++
                else onesCounter[idx]++
            }
        }
        val gamma = IntArray(size)
        val epsilon = IntArray(size)
        for (idx in 0 until size) {
            if (onesCounter[idx] > zerosCounter[idx]) {
                gamma[idx] = 1
                epsilon[idx] = 0
            } else {
                gamma[idx] = 0
                epsilon[idx] = 1
            }
        }
        val gammaDec = Integer.parseInt(gamma.joinToString(""), 2)
        val epsilonDec = Integer.parseInt(epsilon.joinToString(""), 2)
        val powerConsumption = gammaDec * epsilonDec
        println("$gammaDec * $epsilonDec = $powerConsumption")
    }

    override fun solvePartTwo(lines: List<String>) {
        size = lines[0].length

        val oxLines = lines.toMutableList()
        val coLines = lines.toMutableList()
        var oxIdx = 0
        var coIdx = 0

//        println("OXS")
        while (oxLines.size != 1) {
            val (onesCount, zerosCount) = countZerosAndOnes(oxLines, oxIdx)
            val oxBit = if (onesCount >= zerosCount) 1 else 0
            oxLines.removeIf { Integer.parseInt("${it[oxIdx]}") != oxBit }
            oxIdx++
//            println(oxLines)
        }
//        println("COS")
        while (coLines.size != 1) {
            val (onesCount, zerosCount) = countZerosAndOnes(coLines, coIdx)
            val coBit = if (onesCount >= zerosCount) 0 else 1
            coLines.removeIf { Integer.parseInt("${it[coIdx]}") != coBit }
            coIdx++
//            println(coLines)
        }

        val oxyDec = Integer.parseInt(oxLines[0], 2)
        val carDec = Integer.parseInt(coLines[0], 2)
        val lifeSupportRating = oxyDec * carDec
        println("$oxyDec * $carDec = $lifeSupportRating")
    }

    private fun countZerosAndOnes(lines: List<String>, pos: Int): Pair<Int, Int> {
        var onesCounter = 0
        var zerosCounter = 0

        for (binary in lines) {
            for (line in lines) {
                if (binary[pos] == '0') zerosCounter++
                else onesCounter++
            }
        }

//        println("zeros: $zerosCounter, ones $onesCounter")
        return Pair(onesCounter, zerosCounter)
    }

}
