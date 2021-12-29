package year2021

import aoc.IAocTaskKt
import java.util.*
import kotlin.math.max
import kotlin.math.min

class Day23 : IAocTaskKt {
    override fun getFileName(): String = "aoc2021/input_23.txt"

    /**
     * That's not the right answer; your answer is too high. If you're stuck, make sure you're using the full input data;
     * there are also some general tips on the about page, or you can ask for hints on the subreddit.
     * Please wait one minute before trying again. (You guessed 15556.) [Return to Day 23]
     */
    override fun solvePartOne(lines: List<String>) {
        loadGrid(lines)
        fillPadding(grid)
        displayGrid(grid)

        val startVertex = createInitialState()

        findMinEnergy(startVertex)

        var vertex: Set<Amphipod> =
            if (childToParent.isEmpty()) endVertex else childToParent.keys.first { it == endVertex }
        val fromParentToChild = mutableListOf<Set<Amphipod>>()
        while (vertex != startVertex) {
            fromParentToChild.add(vertex)
            vertex = childToParent[vertex]!!
        }
        fromParentToChild.add(startVertex)
        fromParentToChild.reverse()

        for (edge in fromParentToChild.windowed(2)) {
            setGrid(edge[1])
            println(edge[0])
            println("cost delta ${edge[1].energy() - edge[0].energy()}")
            displayGrid(grid)
        }

        println("Smallest cost found: ${vertexToMinEnergy.getOrDefault(endVertex, -1)}")
    }

    private fun createInitialState(): Set<Amphipod> {
        val amphipodPositions = (2..HEIGHT - 2).map { y ->
            listOf(Position(3, y), Position(5, y), Position(7, y), Position(9, y))
        }.flatten()

        val initialState = mutableSetOf<Amphipod>()

        for (pos in amphipodPositions) {
            initialState.add(Amphipod(pos, grid[pos.y][pos.x], 0, 0))
        }
        return initialState
    }

    private fun findMinEnergy(initialVertex: Set<Amphipod>) {
        val notVisited = TreeSet<Set<Amphipod>>(
            Comparator<Set<Amphipod>?> { first, second -> first.energy().compareTo(second.energy()) }
                .thenComparing { first, second -> first!!.depth().compareTo(second!!.depth()) }
                .thenComparing { first, second -> first.hashCode().compareTo(second.hashCode()) }
        )
        notVisited.add(initialVertex)

        while (notVisited.isNotEmpty()) {
            val notVisitedSize = notVisited.size
            if (notVisitedSize % 100 == 0 || notVisitedSize < 1000) {
                println("not visited vertices: $notVisitedSize")
            }

            val toVisit = notVisited.pollFirst()!!

//            println("next visited $toVisit")
//            displayGrid(grid)
            setGrid(toVisit)

            val toVisitList = toVisit.toList()
            val children = mutableListOf<Set<Amphipod>>()
            for (amphipodIdx in toVisitList.indices) {
                var amphipod = toVisitList[amphipodIdx]
                val nextPossibleMoves: List<Position> = amphipod.nextPossiblePositions()

                for (move in nextPossibleMoves) {
                    amphipod = toVisitList[amphipodIdx]

                    if (amphipod.isBlockedInOtherRoom()) continue
                    if (amphipod.shouldStayInTheBottomOfTheRoom()) continue
                    if (amphipod.cheaperLetterHasOpenWay(toVisitList)) continue

                    val movedAmphipod = amphipod.move(move)

                    val childVertex =
                        toVisit.map { it.copy(stepsFromParent = amphipod.stepsFromParent + 1) }.toMutableList()
                    childVertex[amphipodIdx] = movedAmphipod

                    children.add(childVertex.toSet())
                }
            }

            for (child in children) {
                val currentCost = child.energy()
                if (currentCost > vertexToMinEnergy.getOrDefault(endVertex, MAX_COST)
                    || currentCost > MAX_COST || child.first().stepsFromParent > MAX_DEPTH
                ) {
                    continue
                }

                if (child == endVertex) {
                    MAX_COST = child.energy()
                    println("END VERTEX REACHED: $child, ${child.energy()}")
                }

                val previousCost = vertexToMinEnergy.getOrDefault(child, MAX_COST)
                if (previousCost >= MAX_COST || previousCost > currentCost) {
                    vertexToMinEnergy.remove(child)
                    vertexToMinEnergy[child] = currentCost
                    childToParent[child] = toVisit
                    notVisited.add(child)
                } else {
//                    val previous = vertexToMinEnergy.keys.first { it == child }
//                    println("vertex aready has better path cost $previous -> ${vertexToMinEnergy[previous]!!}")
                }
            }
        }
    }

    var emptyGridPattern = listOf(
        "#############",
        "#...........#",
        "###.#.#.#.###",
        "  #.#.#.#.#  ",
        "  #########  ",
    )

    private fun setGrid(toVisit: Set<Amphipod>) {
        loadGrid(emptyGridPattern)
        for (amphoid in toVisit) {
            grid[amphoid.position.y][amphoid.position.x] = amphoid.type
        }
    }

    fun loadGrid(lines: List<String>): Array<CharArray> {
        for (lineIdx in lines.indices) {
            val line = lines[lineIdx]
            for (cellIdx in line.indices) {
                val cell = line[cellIdx]
                grid[lineIdx][cellIdx] = cell
            }
        }
        return grid
    }

    private fun fillPadding(grid: Array<CharArray>) {
        for (y in 0 until HEIGHT) {
            for (x in 0 until WIDTH) {
                val cell = grid[y][x]
                if (!INSIDE_SYMBOLS.contains(cell)) {
                    grid[y][x] = '#'
                }
            }
        }
    }

    private fun displayGrid(grid: Array<CharArray>) {
        for (y in 0 until HEIGHT) {
            for (x in 0 until WIDTH) {
                print(grid[y][x])
            }
            println()
        }
        println()
    }

    private fun Set<Amphipod>.energy(): Int {
        val sum = this.sumOf { it.usedEnergy }
        if (sum < 0) throw IllegalStateException("Energy $sum < 0")
        return sum
    }

    private fun Set<Amphipod>.depth(): Int {
        return first().stepsFromParent
    }

    override fun solvePartTwo(lines: List<String>) {
        val modifiedLines = lines.subList(0, 3) +
                listOf("###D#C#B#A###", "###D#B#A#C###") +
                lines.subList(3, 5)

        HEIGHT = 7
        MAX_DEPTH = 200
        MAX_COST = Int.MAX_VALUE / 2

        emptyGridPattern = emptyGridPattern.subList(0, 4) +
                emptyGridPattern.subList(2, 3) +
                emptyGridPattern.subList(2, 3) +
                emptyGridPattern.subList(4, 5)

        grid = Array(7) { CharArray(13) }

        endVertex = listOf(
            Amphipod(Position(3, 5), 'A', Int.MAX_VALUE / 8, Int.MAX_VALUE),
            Amphipod(Position(3, 4), 'A', Int.MAX_VALUE / 8, Int.MAX_VALUE),
            Amphipod(Position(3, 3), 'A', Int.MAX_VALUE / 8, Int.MAX_VALUE),
            Amphipod(Position(3, 2), 'A', Int.MAX_VALUE / 8, Int.MAX_VALUE),
            Amphipod(Position(5, 5), 'B', Int.MAX_VALUE / 8, Int.MAX_VALUE),
            Amphipod(Position(5, 4), 'B', Int.MAX_VALUE / 8, Int.MAX_VALUE),
            Amphipod(Position(5, 3), 'B', Int.MAX_VALUE / 8, Int.MAX_VALUE),
            Amphipod(Position(5, 2), 'B', Int.MAX_VALUE / 8, Int.MAX_VALUE),
            Amphipod(Position(7, 5), 'C', Int.MAX_VALUE / 8, Int.MAX_VALUE),
            Amphipod(Position(7, 4), 'C', Int.MAX_VALUE / 8, Int.MAX_VALUE),
            Amphipod(Position(7, 3), 'C', Int.MAX_VALUE / 8, Int.MAX_VALUE),
            Amphipod(Position(7, 2), 'C', Int.MAX_VALUE / 8, Int.MAX_VALUE),
            Amphipod(Position(9, 5), 'D', Int.MAX_VALUE / 8, Int.MAX_VALUE),
            Amphipod(Position(9, 4), 'D', Int.MAX_VALUE / 8, Int.MAX_VALUE),
            Amphipod(Position(9, 3), 'D', Int.MAX_VALUE / 8, Int.MAX_VALUE),
            Amphipod(Position(9, 2), 'D', Int.MAX_VALUE / 8, Int.MAX_VALUE),
        ).toSet()


        loadGrid(modifiedLines)
        fillPadding(grid)
        displayGrid(grid)

        val startVertex = createInitialState()

        findMinEnergy(startVertex)

        var vertex: Set<Amphipod> =
            if (childToParent.isEmpty()) endVertex else childToParent.keys.first { it == endVertex }
        val fromParentToChild = mutableListOf<Set<Amphipod>>()
        while (vertex != startVertex) {
            fromParentToChild.add(vertex)
            vertex = childToParent[vertex]!!
        }
        fromParentToChild.add(startVertex)
        fromParentToChild.reverse()

        for (edge in fromParentToChild.windowed(2)) {
            setGrid(edge[1])
            println(edge[0])
            println("cost delta ${edge[1].energy() - edge[0].energy()}")
            displayGrid(grid)
        }

        println("Smallest cost found: ${vertexToMinEnergy.getOrDefault(endVertex, -1)}")
    }

    data class Amphipod(val position: Position, val type: Char, val usedEnergy: Int, val stepsFromParent: Int) {
        fun isFree(move: Position): Boolean {
            return grid[move.y][move.x] == '.'
        }

        fun isHallwayPosition(pos: Position) = HALLWAY_ENTRANCES.contains(pos)

        fun move(destination: Position): Amphipod {
            if (position == destination) throw IllegalStateException("moving to the same position $position")
            val energyToUse =
                if (position.manhattan(destination) > 1) AMPHIPOD_TO_ENERGY_STEP[type]!! * 2 else AMPHIPOD_TO_ENERGY_STEP[type]!!
//            println("moving $this: $position -> $destination")
            if (usedEnergy + energyToUse < 0) throw IllegalStateException("used + toUse < 0")
            val stepIncr = if (position.manhattan(destination) > 1) 2 else 1
            return Amphipod(destination, type, usedEnergy + energyToUse, stepsFromParent + stepIncr)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Amphipod) return false

            if (position != other.position) return false
            if (type != other.type) return false

            return true
        }

        override fun hashCode(): Int {
            var result = position.hashCode()
            result = 31 * result + type.hashCode()
            return result
        }

        fun nextPossiblePositions(): List<Position> {
            val nextPositions = MOVES.map { move -> move + position }
                .filter { pos -> isFree(pos) && isNotCurrent(pos) }
                .map { pos ->
                    if (isHallwayPosition(pos))
                        listOf(pos + Position(-1, 0), pos + Position(1, 0), pos + Position(0, 1))
                            .filter { offset -> isFree(offset) && isNotCurrent(offset) }
                    else if (isNotCurrent(pos)) listOf(pos)
                    else emptyList()
                }
                .flatten()
                .filter { pos -> !isForbiddenMoveIntoOtherRoom(pos) }
                .filter { pos -> !isRoomOccupiedByIntruderMove(pos) }

            return nextPositions
        }

        private fun isRoomOccupiedByIntruderMove(pos: Position): Boolean {
            val roomIdx = LETTER_TO_ROOM[type]!!
            if (pos.x != roomIdx || pos.y == 1) return false
            for (y in 2..HEIGHT - 2) {
                if (grid[y][roomIdx] != '.' && grid[y][roomIdx] != type) return true
            }
            return false
        }

        private fun isRoomOccupiedByIntruder(): Boolean {
            val roomIdx = LETTER_TO_ROOM[type]!!
            for (y in 2..HEIGHT - 2) {
                if (grid[y][roomIdx] != '.' && grid[y][roomIdx] != type) return true
            }
            return false
        }

        private fun isForbiddenMoveIntoOtherRoom(pos: Position): Boolean {
            if (position.y >= 2) return false // already in a room - good or bad
            if (pos.y >= 2 && pos.x != LETTER_TO_ROOM[type]!!) return true
            return false
        }

        private fun isNotCurrent(pos: Position): Boolean {
            return pos != position
        }

        override fun toString(): String {
            return "($position, $type, NRG=$usedEnergy, DPTH=$stepsFromParent)"
        }

        fun shouldStayInTheBottomOfTheRoom(): Boolean {
            val roomIdx = LETTER_TO_ROOM[type]
            if (position.x != roomIdx) return false
            if (position.y == 1) return false // hall position

            for (y in position.y + 1..HEIGHT - 2) {
                if (grid[y][roomIdx] != type) return false
            }
            return true
        }

        fun cheaperLetterHasOpenWay(toVisitList: List<Amphipod>): Boolean {
            for (letter in 'A'..type) {
                val toCheck = toVisitList.filter { it.type == letter && it.position.y == 1 && it.position != position }
                if (toCheck.any { !it.isRoomOccupiedByIntruder() && it.hasFreeWayToRoom() }) return true
            }
            return false
        }

        private fun hasFreeWayToRoom(): Boolean {
            val fromPosition = min(position.x, LETTER_TO_ROOM[type]!!)
            val targetPosition = max(position.x, LETTER_TO_ROOM[type]!!)
            for (x in fromPosition..targetPosition) {
                if (x == position.x) continue
                if (grid[1][x] == '.') continue
                return false
            }
            return true
        }

        fun isBlockedInOtherRoom(): Boolean {
            if (position.y == 1 || position.y == 2) return false
            if (position.x == LETTER_TO_ROOM[type]!!) return false
            for (y in position.y - 1 downTo 2) {
                if (grid[y][position.x] != '.') return true
            }
            return false
        }
    }

    companion object {
        var HEIGHT = 5
        val INSIDE_SYMBOLS = listOf('.', 'A', 'B', 'C', 'D')
        val LETTER_TO_ROOM = mapOf('A' to 3, 'B' to 5, 'C' to 7, 'D' to 9)

        var grid = Array(5) { CharArray(13) }

        var MAX_COST = 15556
        var MAX_DEPTH = 50

        val vertexToMinEnergy: MutableMap<Set<Amphipod>, Int> = mutableMapOf()
        val childToParent = mutableMapOf<Set<Amphipod>, Set<Amphipod>>()

        var endVertex = listOf(
            Amphipod(Position(3, 3), 'A', Int.MAX_VALUE / 8, Int.MAX_VALUE),
            Amphipod(Position(3, 2), 'A', Int.MAX_VALUE / 8, Int.MAX_VALUE),
            Amphipod(Position(5, 3), 'B', Int.MAX_VALUE / 8, Int.MAX_VALUE),
            Amphipod(Position(5, 2), 'B', Int.MAX_VALUE / 8, Int.MAX_VALUE),
            Amphipod(Position(7, 3), 'C', Int.MAX_VALUE / 8, Int.MAX_VALUE),
            Amphipod(Position(7, 2), 'C', Int.MAX_VALUE / 8, Int.MAX_VALUE),
            Amphipod(Position(9, 3), 'D', Int.MAX_VALUE / 8, Int.MAX_VALUE),
            Amphipod(Position(9, 2), 'D', Int.MAX_VALUE / 8, Int.MAX_VALUE),
        ).toSet()

        val AMPHIPOD_TO_ENERGY_STEP = mapOf(
            'A' to 1,
            'B' to 10,
            'C' to 100,
            'D' to 1000
        )

        val HALLWAY_ENTRANCES = listOf(
            Position(3, 1),
            Position(5, 1),
            Position(7, 1),
            Position(9, 1),
        )

        const val WIDTH = 13

        val MOVES = listOf(Position(0, 1), Position(1, 0), Position(0, -1), Position(-1, 0))
    }


    data class Position(val x: Int, val y: Int) {
        operator fun plus(move: Position): Position = Position(x + move.x, y + move.y)
        fun manhattan(other: Position) = kotlin.math.abs(x - other.x) + kotlin.math.abs(y - other.y)
        override fun toString(): String = "($x, $y)"
    }
}
