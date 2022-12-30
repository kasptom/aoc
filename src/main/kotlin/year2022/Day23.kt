package year2022

import aoc.IAocTaskKt
import year2022.Day23.Direction.*
import year2022.Day23.GridCell

typealias ElfMap = MutableList<MutableList<GridCell>>

class Day23 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_23.txt"

    override fun solvePartOne(lines: List<String>) {
        val grid: ElfGrid = lines.mapIndexed { yIdx, line ->
            line
                .chunked(1)
                .mapIndexed { xIdx, cell -> GridCell(Point(xIdx, yIdx), cell) }
                .toMutableList()
        }.toMutableList()
            .let { ElfGrid(it) }

        val roundsLimit = 10

//        println("== Initial State ==")
//        println(grid.elfMap.display())
//        println()

        for (round in 1 .. roundsLimit) {
            val roundDirections =
                Direction.values().toList().subList((round - 1) % 4, 4) + Direction.values().toList().subList(0, (round - 1) % 4)
            if (grid.edgeIsOccupied()) {
                grid.grow()
            }
            val newElfMap = grid.elfMap
                .copy()
                .reset()

//            println("backup")
//            println(backup.display())
//            println()

//            println("elf map")
//            println(grid.elfMap.display())
//            println()

            for (yIdx in newElfMap.indices) {
                for (xIdx in newElfMap[yIdx].indices) {
                    val cell = grid[Point(xIdx, yIdx)]
//                    print(cell)
                    if (cell.state == ".") continue
                    if (grid.noElvesAround(cell)) {
                        newElfMap[cell.pos.y][cell.pos.x] = cell.copy()
                        continue
                    }
                    var canMove = false
                    for (direction in roundDirections) {
                        if (grid.noElvesInDirection(cell, direction)) {
//                            println("$cell proposes $direction")
                            val proposedPosition = cell.pos + DIRECTION_TO_DIFF[direction]!!
                            // grid[proposedPosition].state = cell.state
                            grid.addToInterested(cell, proposedPosition)
                            canMove = true
                            break
                        }
                    }
                    if (!canMove) {
                        newElfMap[cell.pos.y][cell.pos.x] = cell.copy()
                    }
                }
//                println()
            }

            grid.updateCells(newElfMap)
//            println("== End of Round $round ==")
//            println(grid.elfMap.display())
//            println()
        }
        val emptyTilesWithinRegion: Int = grid.countEmptyTilesInElvesRectangle()
        println(emptyTilesWithinRegion)
    }

    override fun solvePartTwo(lines: List<String>) {
        val grid: ElfGrid = lines.mapIndexed { yIdx, line ->
            line
                .chunked(1)
                .mapIndexed { xIdx, cell -> GridCell(Point(xIdx, yIdx), cell) }
                .toMutableList()
        }.toMutableList()
            .let { ElfGrid(it) }

        var roundsCounter = 1

//        println("== Initial State ==")
//        println(grid.elfMap.display())
//        println()

        var previousStateChanged = true
        while (previousStateChanged) {
            val roundDirections =
                Direction.values().toList().subList((roundsCounter - 1) % 4, 4) + Direction.values().toList().subList(0, (roundsCounter - 1) % 4)
            if (grid.edgeIsOccupied()) {
                grid.grow()
            }
            val newElfMap = grid.elfMap
                .copy()
                .reset()

            for (yIdx in newElfMap.indices) {
                for (xIdx in newElfMap[yIdx].indices) {
                    val cell = grid[Point(xIdx, yIdx)]
//                    print(cell)
                    if (cell.state == ".") continue
                    if (grid.noElvesAround(cell)) {
                        newElfMap[cell.pos.y][cell.pos.x] = cell.copy()
                        continue
                    }
                    var canMove = false
                    for (direction in roundDirections) {
                        if (grid.noElvesInDirection(cell, direction)) {
//                            println("$cell proposes $direction")
                            val proposedPosition = cell.pos + DIRECTION_TO_DIFF[direction]!!
                            // grid[proposedPosition].state = cell.state
                            grid.addToInterested(cell, proposedPosition)
                            canMove = true
                            break
                        }
                    }
                    if (!canMove) {
                        newElfMap[cell.pos.y][cell.pos.x] = cell.copy()
                    }
                }
//                println()
            }

            previousStateChanged = grid.updateCells(newElfMap)
            roundsCounter++
        }
        println(roundsCounter - 1)
    }

    data class GridCell(val pos: Point, var state: String) {
        override fun toString(): String {
            return "('$state')"
        }
    }

    data class Point(val x: Int, val y: Int) {
        operator fun plus(diff: Point) = Point(x + diff.x, y + diff.y)
        override fun toString(): String = "($x, $y)"

    }

    data class ElfGrid(var elfMap: ElfMap) {
        private val pointToInterestedElves: MutableMap<Point, List<Point>> = mutableMapOf()

        fun edgeIsOccupied(): Boolean {
            if (elfMap[0].any { it.state == "#" }) return true
            if (elfMap[elfMap.size - 1].any { it.state == "#" }) return true
            if (elfMap.map { row -> row[0] }.any { it.state == "#" }) return true
            if (elfMap.map { row -> row[row.size - 1] }.any { it.state == "#" }) return true
            return false
        }

        fun grow() {
            for (row in elfMap) {
                row.add(0, GridCell(Point(0, 0), "."))
                row.add(GridCell(Point(0, 0), "."))
            }
            elfMap.add(0, newRow())
            elfMap.add(newRow())
            for (yIdx in elfMap.indices) {
                for (xIdx in elfMap[yIdx].indices) {
                    elfMap[yIdx][xIdx] = GridCell(Point(xIdx, yIdx), elfMap[yIdx][xIdx].state)
                }
            }
        }

        private fun newRow() = (1..(elfMap[0].size)).map { GridCell(Point(0, 0), ".") }.toMutableList()

        operator fun set(pos: Point, value: GridCell) {
            elfMap[pos.y][pos.x] = value
        }

        fun noElvesAround(cell: GridCell): Boolean {
            return SURROUNDING_POSITIONS.map { diff -> cell.pos + diff }.all { elfMap[it.y][it.x].state == "." }
        }

        fun noElvesInDirection(
            cell: GridCell,
            direction: Direction,
        ): Boolean {
            return DIRECTION_TO_CHECKED_FIELDS[direction]!!.map { diff -> cell.pos + diff }
                .all { elfMap[it.y][it.x].state == "." }
        }


        operator fun get(pos: Point): GridCell {
            return elfMap[pos.y][pos.x]
        }

        fun addToInterested(cell: GridCell, proposedPosition: Point) {
            pointToInterestedElves.putIfAbsent(proposedPosition, mutableListOf())
            pointToInterestedElves[proposedPosition] = pointToInterestedElves[proposedPosition]!! + cell.pos
        }

        fun updateCells(newElfMap: ElfMap): Boolean {
            var atLeastOneMoved = false
            this.elfMap = newElfMap
            for ((point, interestedElves) in pointToInterestedElves) {
                if (interestedElves.size == 1) {
                    this[point].state = "#"
                    atLeastOneMoved = true
                }
                else {
                    for (elf in interestedElves) {
                        this[elf].state = "#"
                    }
                }
            }
            this.pointToInterestedElves.clear()
            return atLeastOneMoved
        }

        fun countEmptyTilesInElvesRectangle(): Int {
            val elfPoints = elfMap.flatten().filter { it.state == "#" }.map { it.pos }

            val minColIdx = elfPoints.minOf { it.x }
            val maxColIdx = elfPoints.maxOf { it.x }
            val minRowIdx = elfPoints.minOf { it.y }
            val maxRowIdx = elfPoints.maxOf { it.y }

//            println("counting in range limited by:\nx: $minColIdx to $maxColIdx\ny: $minRowIdx to $maxRowIdx")
            return elfMap.flatten()
                .count { it.pos.x in minColIdx..maxColIdx && it.pos.y in minRowIdx..maxRowIdx && it.state == "."}
        }

    }

    enum class Direction {
        NORTH, SOUTH, WEST, EAST
    }

    companion object {
        val SURROUNDING_POSITIONS = listOf(
            Point(0, -1), Point(1, -1), Point(1, 0),
            Point(1, 1), Point(0, 1), Point(-1, 1),
            Point(-1, 0), Point(-1, -1)
        )
        val DIRECTION_TO_CHECKED_FIELDS = mapOf(
            NORTH to listOf(Point(-1, -1), Point(0, -1), Point(1, -1)),
            SOUTH to listOf(Point(1, 1), Point(0, 1), Point(-1, 1)),
            EAST to listOf(Point(1, -1), Point(1, 0), Point(1, 1)),
            WEST to listOf(Point(-1, 1), Point(-1, 0), Point(-1, -1)),
        )
        val DIRECTION_TO_DIFF = mapOf(
            NORTH to Point(0, -1),
            SOUTH to Point(0, 1),
            EAST to Point(1, 0),
            WEST to Point(-1, 0),
        )
    }
}

@Suppress("unused")
private fun ElfMap.display(): String {
    return this.joinToString("\n") { row -> row.joinToString("") { it.state } }
}

private fun ElfMap.copy(): ElfMap {
    val newMap = mutableListOf<MutableList<GridCell>>()
    val old = this
    for (row in old) {
        newMap += row.map { it.copy() }.toMutableList()
    }
    return newMap.toMutableList()
}

private fun ElfMap.reset(): ElfMap {
    for (yIdx in this.indices) {
        for (xIdx in this[yIdx].indices) {
            this[yIdx][xIdx] = GridCell(Day23.Point(xIdx, yIdx), ".")
        }
    }
    return this
}
