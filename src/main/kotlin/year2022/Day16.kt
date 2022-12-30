package year2022

import aoc.IAocTaskKt

private var inToOutValve: Map<String, List<String>> = emptyMap()
private var codeToFlowRate: Map<String, Int> = emptyMap()

class Day16 : IAocTaskKt {

    override fun getFileName(): String = "aoc2022/input_16.txt"

    override fun solvePartOne(lines: List<String>) {
        parseValves(lines)

        val valveSystem = ValveSystem(workerPosition = "AA", currentTime = 0, emptySet(), pressureSum = 0)
        val possibleStates = mutableListOf(valveSystem)

        repeat(times = 30) {
            val nextPossibleStates = mutableSetOf<ValveSystem>()
            for (state in possibleStates) {
                nextPossibleStates += state.generateNextPossibleStates()
            }
            possibleStates.clear()

            val minimalPressure = getMinimalPressure(nextPossibleStates, ValveSystem::pressureSum, 100, 90)

            possibleStates.addAll(nextPossibleStates.filter { it.pressureSum >= minimalPressure })
//            println("states size: ${possibleStates.size}")
        }
        val max = possibleStates.maxOf { it.pressureSum }
        println("found: ${possibleStates.count { it.pressureSum == max }}, e.g. ${possibleStates.first { it.pressureSum == max }}")
        println(max)
    }

    override fun solvePartTwo(lines: List<String>) {
        parseValves(lines)
        val valveSystem =
            ValveSystem2(workerPosition = "AA", elephantPosition = "AA", currentTime = 0, emptySet(), pressureSum = 0)

        val possibleStates = mutableListOf(valveSystem)

        repeat (26) {
            val nextPossibleStates = mutableSetOf<ValveSystem2>()
            for (state in possibleStates) {
                nextPossibleStates += state.generateNextPossibleStates()
            }
            possibleStates.clear()
            val minimalPressure = getMinimalPressure(nextPossibleStates, ValveSystem2::pressureSum, 1000, 70)

            possibleStates.addAll(nextPossibleStates.filter { it.pressureSum >= minimalPressure })
//            println("states size: ${possibleStates.size}")
        }
        val max = possibleStates.maxOf { it.pressureSum }
        println("found: ${possibleStates.count { it.pressureSum == max }}, e.g. ${possibleStates.first { it.pressureSum == max }}")
        println(max)
    }

    private fun parseValves(lines: List<String>) {
        if (inToOutValve.isNotEmpty()) return
        val valves = lines.map { Valve.parse(it) }
        inToOutValve = valves.groupBy { valve -> valve.code }
            .mapValuesTo(mutableMapOf()) { entry -> entry.value.single().leadsTo }
            .toMap()
        codeToFlowRate = valves.groupBy { it.code }
            .mapValues { entry -> entry.value.single().flowRate }
    }

    private fun <T> getMinimalPressure(
        nextPossibleStates: Set<T>,
        sortedFieldExtractor: (T) -> Int,
        minSubListSize: Int,
        percentile: Int,
    ) = nextPossibleStates
        .map(sortedFieldExtractor)
        .sortedDescending()
        .subList(0, maxOf(minOf(minSubListSize, nextPossibleStates.size), nextPossibleStates.size * (100 - percentile) / 100))
        .last()

    data class Valve(val code: String, val flowRate: Int, val leadsTo: List<String>) {
        companion object {
            fun parse(caveLine: String): Valve {
                val nameFlowRateLeadsTo = caveLine.replace("Valve ", "")
                    .replace(" has flow rate=", ";")
                    .replace("; tunnels lead to valves ", ";")
                    .replace("; tunnel leads to valve ", ";")

                val nameFlowRateLeadsToSplit = nameFlowRateLeadsTo.split(";")
                val (code, flowRate, leadsTo) = Triple(
                    nameFlowRateLeadsToSplit[0],
                    nameFlowRateLeadsToSplit[1].toInt(), nameFlowRateLeadsToSplit[2].split(", ")
                )
                return Valve(code, flowRate, leadsTo)
            }
        }

        override fun toString() = "'$code': $flowRate, $leadsTo"
    }

    data class ValveSystem(
        val workerPosition: String,
        val currentTime: Int = 0,
        val openedValves: Set<String> = emptySet(),
        val pressureSum: Int,
    ) {
        private fun pressurePerMinute(): Int = openedValves.sumOf { codeToFlowRate[it]!! }
        fun generateNextPossibleStates(): Set<ValveSystem> {
            val possibleStates = mutableSetOf<ValveSystem>()

            if (!openedValves.contains(workerPosition) && codeToFlowRate[workerPosition]!! != 0) {
                possibleStates += ValveSystem(
                    workerPosition,
                    currentTime = currentTime + 1,
                    openedValves = openedValves + workerPosition,
                    pressureSum + pressurePerMinute()
                )
            }

            for (move in inToOutValve[workerPosition]!!) {
                possibleStates += ValveSystem(
                    move,
                    currentTime = currentTime + 1,
                    openedValves = openedValves,
                    pressureSum + pressurePerMinute()
                )
            }

            return possibleStates
        }

        override fun toString(): String = "👷‍♂️: $workerPosition', ⌚: $currentTime, 🔼: $openedValves, ⏩: $pressureSum}"
    }

    data class ValveSystem2(
        val workerPosition: String,
        val elephantPosition: String,
        val currentTime: Int = 0,
        val openedValves: Set<String> = emptySet(),
        val pressureSum: Int,
    ) {
        private fun pressurePerMinute(): Int = openedValves.sumOf { codeToFlowRate[it]!! }
        fun generateNextPossibleStates(): Set<ValveSystem2> {
            val possibleStates = mutableSetOf<ValveSystem2>()

            if (canOpenValve(workerPosition) && canOpenValve(elephantPosition)) {
                possibleStates += ValveSystem2(
                    workerPosition,
                    elephantPosition,
                    currentTime = currentTime + 1,
                    openedValves = openedValves + workerPosition + elephantPosition,
                    pressureSum + pressurePerMinute()
                )
            } else if (canOpenValve(workerPosition)) {
                for (move in inToOutValve[elephantPosition]!!) {
                    possibleStates += ValveSystem2(
                        workerPosition,
                        move,
                        currentTime = currentTime + 1,
                        openedValves = openedValves + workerPosition,
                        pressureSum + pressurePerMinute()
                    )
                }
            } else if (canOpenValve(elephantPosition)) {
                for (move in inToOutValve[workerPosition]!!) {
                    possibleStates += ValveSystem2(
                        move,
                        elephantPosition,
                        currentTime = currentTime + 1,
                        openedValves = openedValves + elephantPosition,
                        pressureSum + pressurePerMinute()
                    )
                }
            }
            for (move in inToOutValve[workerPosition]!!) {
                for (elephantMove in inToOutValve[elephantPosition]!!) {
                    possibleStates += ValveSystem2(
                        move,
                        elephantMove,
                        currentTime = currentTime + 1,
                        openedValves = openedValves,
                        pressureSum + pressurePerMinute()
                    )
                }
            }

            return possibleStates
        }

        private fun canOpenValve(code: String) = !openedValves.contains(code) && codeToFlowRate[code]!! != 0

        override fun toString(): String =
            "{'👷‍♂️: $workerPosition', 🐘: $elephantPosition, ⌚: $currentTime, 🔼: $openedValves, ⏩: $pressureSum}"
    }
}