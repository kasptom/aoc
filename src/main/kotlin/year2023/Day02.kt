package year2023

import aoc.IAocTaskKt

class Day02 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_02.txt"

    override fun solvePartOne(lines: List<String>) {
        val games: List<CubeGame> = lines.map { CubeGame.parse(it) }
        games.filter { it.isWithinLimits(red = 12, green = 13, blue = 14) }
            .sumOf { it.id }
            .let { println(it) }
    }

    override fun solvePartTwo(lines: List<String>) {
        val games: List<CubeGame> = lines.map(CubeGame.Companion::parse)
        println(games.sumOf(CubeGame::power))
    }

    data class CubeGame(val id: Int, val steps: List<Map<String, Int>>) {
        fun isWithinLimits(red: Int, green: Int, blue: Int): Boolean {
            val (maxRed, maxGreen, maxBlue) = getMaxColorsCounts()
            return maxRed <= red && maxBlue <= blue && maxGreen <= green
        }

        fun power(): Int {
            val (maxRed, maxGreen, maxBlue) = getMaxColorsCounts()
            return maxRed * maxGreen * maxBlue
        }

        private fun getMaxColorsCounts(): Triple<Int, Int, Int> {
            val maxRed = findMaxOfColor("red")
            val maxGreen = findMaxOfColor("green")
            val maxBlue = findMaxOfColor("blue")
            return Triple(maxRed, maxGreen, maxBlue)
        }

        private fun findMaxOfColor(color: String) = steps.map { it[color] ?: 0 }.maxOf { it }

        companion object {
            fun parse(line: String): CubeGame {
                val (levelRaw, stepsRaw) = line.replace("Game ", "")
                    .split(": ")

                val level = levelRaw.toInt()
                val steps = stepsRaw.split("; ")
                    .map { stepRaw ->
                        val colors = stepRaw.split(", ")
                            .map { countColor ->
                                val (countRaw, colorName) = countColor.split(" ")
                                val count = countRaw.toInt()
                                Pair(colorName, count)
                            }
                        val step = colors.toMap()
                        step
                    }

                return CubeGame(level, steps)
            }
        }
    }
}