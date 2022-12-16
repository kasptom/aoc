package year2022

import aoc.IAocTaskKt

private var inToOutValve: Map<String, List<String>> = emptyMap()
private var codeToFlowRate: Map<String, Int> = emptyMap()

class Day16 : IAocTaskKt {

    override fun getFileName(): String = "aoc2022/input_16.txt"

    override fun solvePartOne(lines: List<String>) {
        val valves = lines.map { Valve.parse(it) }
//        valves.onEach { println(it) }
        inToOutValve = valves.groupBy { valve -> valve.code }
            .mapValuesTo(mutableMapOf()) { entry -> entry.value.single().leadsTo }
            .toMap()
        codeToFlowRate = valves.groupBy { it.code }
            .mapValues { entry -> entry.value.single().flowRate }

        val valveSystem = ValveSystem(workerPosition = "AA", currentTime = 0, emptySet(), pressureSum = 0)
        val timeLimitMinutes = 30
        val possibleStates = mutableListOf(valveSystem)
        for (time in 1..timeLimitMinutes) {
            val nextPossibleStates = mutableSetOf<ValveSystem>()
            for (state in possibleStates) {
                nextPossibleStates += state.generateNextPossibleStates()
            }
            possibleStates.clear()
            val minimalPressure = nextPossibleStates
                .map { it.pressureSum }
                .sortedDescending().subList(0, maxOf(minOf(100, nextPossibleStates.size), nextPossibleStates.size / 10))
                .last()

            possibleStates.addAll(nextPossibleStates.filter { it.pressureSum >= minimalPressure })
//            println("states size: ${possibleStates.size}")
        }
        val max = possibleStates.maxOf { it.pressureSum }
        println(max)
    }


    override fun solvePartTwo(lines: List<String>) {
        
    }

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
        fun pressurePerMinute(): Int = openedValves.sumOf { codeToFlowRate[it]!! }
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

        override fun toString(): String {
            return "{'$workerPosition', T: $currentTime, OPEN: $openedValves, PRESSURE: $pressureSum}"
        }
    }
}