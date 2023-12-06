package year2023

import aoc.IAocTaskKt

class Day06 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_06.txt"

    override fun solvePartOne(lines: List<String>) {
        val times = lines[0].replace(Regex("Time:\\s"), "")
            .trim()
            .split(Regex("\\s"))
            .filter { it.isNotEmpty() }
            .map { it.toLong() }

        val distances = lines[1].replace(Regex("Distance:\\s"), "")
            .trim()
            .split(Regex("\\s"))
            .filter { it.isNotEmpty() }
            .map { it.toLong() }

        val result = multiplyWays(times, distances)
        println(result)
    }

    override fun solvePartTwo(lines: List<String>) {
        val times = lines[0].replace(Regex("Time:\\s"), "")
            .trim()
            .split(Regex("\\s"))
            .filter { it.isNotEmpty() }
            .joinToString("")
            .toLong()
            .let { listOf(it) }

        val distances = lines[1].replace(Regex("Distance:\\s"), "")
            .trim()
            .split(Regex("\\s"))
            .filter { it.isNotEmpty() }
            .joinToString("")
            .toLong()
            .let { listOf(it) }

        val result = multiplyWays(times, distances)
        println(result)
    }

    private fun multiplyWays(
        times: List<Long>,
        distances: List<Long>,
    ): Int {
        val races = times.zip(distances).map { (time, distance) -> Race(time, distance) }
        return races.map { it.possibleWays() }
            .reduce { x, y -> x * y }
    }

    data class Race(val time: Long, val distance: Long) {
        fun possibleWays(): Int {
            val velocities = (1 until time)
            val rideTime = ((time - 1) downTo 1).toList()
            return velocities.mapIndexed { idx, v -> rideTime[idx] * v }
                .count { it > distance }
        }
    }
}
