package year2022

import aoc.IAocTaskKt
import year2022.Day24.*
import year2022.Day24.ValleyCell.CellType.CLEAR
import year2022.Day24.ValleyCell.CellType.WALL
import year2022.Day24.ValleyCell.MovableType.BLIZZ_DOWN
import year2022.Day24.ValleyCell.MovableType.BLIZZ_LEFT
import year2022.Day24.ValleyCell.MovableType.BLIZZ_RIGHT
import year2022.Day24.ValleyCell.MovableType.BLIZZ_UP
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.abs
import kotlin.math.min

typealias ValleyGrid = List<List<ValleyCell>>

class Day24 : IAocTaskKt {
    private val maxStatesStored = 1000
    private val timeLimit = 273

    override fun getFileName(): String = "aoc2022/input_24.txt" // 282, 273 too high

    override fun solvePartOne(lines: List<String>) {
        val valleyMap = ValleyMap.parse(lines)
        println("Initial state")
        println(valleyMap.display())
        val start = valleyMap.entrance
        val end = valleyMap.exit

        val shortestPath = findShortestPaths(start, end, valleyMap)

//        val timeToValley = timeToValleyWeather(valleyMap,shortestPath.size + 10)
//
//        println("found path: ")
//        for ((idx, point) in shortestPath.withIndex()) {
////        for (idx in 0..18) {
//            val map = timeToValley[idx]!!.deepCopy(idx, point)
//            println(map.display())
//        }
        println(shortestPath.size - 1)
    }

    private fun findShortestPaths(start: Point, end: Point, valleyMap: ValleyMap): List<Point> {
        val unvisitedCost = TreeSet<ValleyMap>()
        val maxPathCost = 1000

        val timeToValleyWoExpedition = timeToValleyWeather(valleyMap, maxPathCost)

//        valleyMap.map.flatten().forEach { vertex: ValleyCell ->
//            for (time in 0..maxPathCost) {
//                unvisitedCost[TimePoint(vertex.pos, time)] = Int.MAX_VALUE
//            }
//        }

        val childToParent = HashMap<TimePoint, TimePoint>()
//        var currentPosition: Point = start
        var expeditionMap = timeToValleyWoExpedition[0]!!.deepCopy(0, start)
        unvisitedCost.add(expeditionMap)
        var pathCost: Int

        println(expeditionMap.display())


        while (unvisitedCost.isNotEmpty() && expeditionMap.expedition != end) {
            println("unvisited size: ${unvisitedCost.size}")
            val prevMap = unvisitedCost.first()
            pathCost = prevMap.minute + 1
            unvisitedCost.remove(prevMap)

            val nextValleyMap = timeToValleyWoExpedition[pathCost]!!

            for (move in ValleyMove.values()) {
                //  val currentCell = expeditionMap[expeditionMap.expedition!!]

                val nextExpeditionPosition = expeditionMap.expedition!! + move.diff
                if (!nextExpeditionPosition.isSaveForExpedition(start, end, nextValleyMap)) {
                    continue
                }
//                println("found save position: $nextPosition")
                val nextExpeditionMap = nextValleyMap.deepCopy(pathCost, nextExpeditionPosition)

                childToParent[TimePoint(pathCost, nextExpeditionPosition)] =
                    TimePoint(pathCost - 1, expeditionMap.expedition!!)

                if (pathCost < timeLimit) {
                    unvisitedCost.add(nextExpeditionMap)
                }


                val limited = unvisitedCost.toList().subList(0, min(unvisitedCost.size, maxStatesStored))
                unvisitedCost.clear()
                unvisitedCost.addAll(limited)
            }

            expeditionMap = unvisitedCost.first()
//            println(expeditionMap.display())
        }
        return mapToPath(childToParent, start, end, unvisitedCost)
    }

    private fun timeToValleyWeather(
        valleyMap: ValleyMap,
        maxPathCost: Int,
    ): MutableMap<Int, ValleyMap> {
        val timeToValleyWoExpedition = mutableMapOf<Int, ValleyMap>()
        timeToValleyWoExpedition[0] = valleyMap
        for (pathCost in 1..maxPathCost) {
            if (timeToValleyWoExpedition.containsKey(pathCost)) timeToValleyWoExpedition[pathCost]!! else {
                timeToValleyWoExpedition[pathCost] =
                    timeToValleyWoExpedition[pathCost - 1]!!.deepCopy(pathCost - 1, null).moveBlizzards()
                timeToValleyWoExpedition[pathCost]!!
            }
        }
        return timeToValleyWoExpedition
    }

    private fun mapToPath(
        childToParent: HashMap<TimePoint, TimePoint>,
        start: Point,
        endPoint: Point,
        unvisitedCost: TreeSet<ValleyMap>,
    ): List<Point> {
        val path = mutableListOf<Point>()
        val endCell = unvisitedCost.filter { it.expedition == endPoint }
            .sortedBy { it.minute }
            .first { it.expedition == endPoint }

        path.add(endCell.expedition!!)
        val startCell = TimePoint(0, start)
        var currentCell = TimePoint(endCell.minute, endCell.expedition)

        println(endCell.minute)
        while (currentCell != startCell) {
            val currentCellPosition: TimePoint = childToParent[currentCell]!!
            currentCell = currentCellPosition
            path.add(currentCellPosition.pos)
        }


        return path.reversed()
    }

    override fun solvePartTwo(lines: List<String>) {
        TODO("Not yet implemented")
    }

    data class ValleyMap(
        val minute: Int,
        val entrance: Point,
        val exit: Point,
        val expedition: Point?,
        val map: ValleyGrid,
        val blizzardToPosition: MutableMap<Blizzard, Point>,
    ) : Comparable<ValleyMap> {
        val width: Int
        val height: Int

        init {
            width = map[0].size
            height = map.size
        }

        fun display(color: Boolean = true): String {
            return map.map { row ->
                row.map { col: ValleyCell ->
                    val movableCount = col.movables.size
                    if (col.pos == expedition) {
                        if (color) {
                            if (movableCount > 0) "${ANSI_RED}$movableCount$ANSI_RESET" else "${ANSI_GREEN}E$ANSI_RESET"
                        } else {
                            if (movableCount > 0) "X" else "E"
                        }
                    } else if (movableCount > 1) "$movableCount"
                    else if (movableCount == 1) col.movables.first().type.code
                    else "${col.type}"
                }.joinToString("")
            }.joinToString("\n") + "\n"
        }

        companion object {
            fun parse(lines: List<String>): ValleyMap {
                var movableId = 2
                val blizzardToPosition = mutableMapOf<Blizzard, Point>()
                val map = lines.mapIndexed { yIdx, row ->
                    row.chunked(1).mapIndexed { xIdx, col ->
                        val state = ValleyCell.CellType.ofCode(col)
                        val position = Point(xIdx, yIdx)

                        val movableType = ValleyCell.MovableType.from(col)
                        val movable: Movable? = Movable.from(movableId, movableType)
                        val movables = if (movable != null) {
                            movableId++
                            blizzardToPosition[movable as Blizzard] = position
                            mutableSetOf<Movable>(movable)
                        } else mutableSetOf()

                        ValleyCell(position, state, movables)
                    }
                }
                val entrance = map[0].first { it.type == CLEAR }.pos
                val exit = map.last().first { it.type == CLEAR }.pos

                return ValleyMap(
                    minute = 0,
                    entrance = entrance,
                    exit = exit,
                    expedition = null,
                    map = map,
                    blizzardToPosition = blizzardToPosition
                )
            }

            const val ANSI_RESET = "\u001B[0m"
            const val ANSI_GREEN = "\u001B[32m"
            const val ANSI_RED = "\u001B[31m"
            const val ANSI_PURPLE = "\u001B[35m"
        }

        operator fun get(pos: Point): ValleyCell {
            return map[pos.y][pos.x]
        }

        fun moveBlizzards(): ValleyMap {
            val copy: ValleyMap = this.deepCopy(minute + 1, expedition = null)
            for (blizzard in copy.blizzardToPosition.keys) {
                val oldPosition = copy.blizzardToPosition[blizzard]!!
                val diffPosition = oldPosition + blizzard.direction.diff
                val newPosition = if (copy[diffPosition].type == WALL) {
//                        println("$blizzard reached wall $oldPosition -> $diffPosition")
                    diffPosition.toOpposite(width, height)
                } else diffPosition
                if (newPosition == oldPosition) throw IllegalStateException()

                copy.blizzardToPosition[blizzard] = newPosition
                val prevCell = copy[oldPosition]
                val currCell = copy[newPosition]
                prevCell.movables.remove(blizzard)
                currCell.movables.add(blizzard)
            }
            return copy
        }

        fun deepCopy(time: Int, expedition: Point?): ValleyMap {
            val entranceCopy: Point = entrance.copy()
            val exitCopy: Point = exit.copy()
            val mapCopy: ValleyGrid = map.map { row ->
                row.map { cell: ValleyCell ->
                    val movablesCopy = cell.movables.map { mov -> mov.clone() }.toMutableSet()
                    cell.copy(movables = movablesCopy)
                }
            }
            if (expedition != null && blizzardToPosition.values.any { it == expedition }) {
                throw IllegalStateException("$expedition \n $this")
            }
            val blizzardToPositionCopy: MutableMap<Blizzard, Point> = blizzardToPosition
                .mapValues { (_, v) -> v.copy() }
                .toMutableMap()

            return ValleyMap(
                minute = time, entrance = entranceCopy,
                exit = exitCopy,
                expedition = expedition,
                map = mapCopy,
                blizzardToPosition = blizzardToPositionCopy
            )
        }

        override fun compareTo(other: ValleyMap): Int {
            if (minute != other.minute) return minute.compareTo(other.minute)

            val distanceToEnd: Int = expedition?.manhattan(exit) ?: Integer.MAX_VALUE
            val otherDistanceToEnd = other.expedition?.manhattan(exit) ?: Integer.MAX_VALUE

            if (distanceToEnd != otherDistanceToEnd) return distanceToEnd.compareTo(otherDistanceToEnd)

            if (expedition != null && other.expedition != null && expedition != other.expedition) {
                return other.expedition.compareTo(expedition)
            }
            if (expedition == null && other.expedition != null) return -1
            if (other.expedition == null && expedition != null) return 1

            val blizzards = blizzardToPosition.values.sorted()
            val otherBlizzards = other.blizzardToPosition.values.sorted()
            for (bIdx in blizzards.indices) {
                val blizzard = blizzards[bIdx]
                val otherBlizzard = otherBlizzards[bIdx]
                if (blizzard != otherBlizzard) return blizzard.compareTo(otherBlizzard)
            }

            return 0
        }

        override fun toString(): String {
            return "Minute $minute:\n${
                blizzardToPosition.keys.joinToString("\n") {
                    "$it -> ${blizzardToPosition[it]!!}"
                }
            } \n${display(false)}"
        }


    }

    data class ValleyCell(val pos: Point, val type: CellType, val movables: MutableSet<Movable>) {

        enum class MovableType(val code: String) {
            BLIZZ_UP("^"), BLIZZ_DOWN("v"), BLIZZ_LEFT("<"), BLIZZ_RIGHT(">");

            companion object {
                fun from(col: String): MovableType? {
                    return values().firstOrNull { it.code == col }
                }
            }
        }

        enum class CellType(val code: String) {
            WALL("#"), CLEAR(".");

            override fun toString(): String = code

            companion object {
                private val CODE_TO_CELL_STATE = mutableMapOf<String, CellType>()

                init {
                    for (movableType in MovableType.values()) CODE_TO_CELL_STATE[movableType.code] = CLEAR
                    for (cellType in CellType.values()) CODE_TO_CELL_STATE[cellType.code] = cellType
                }

                fun ofCode(code: String): CellType = CODE_TO_CELL_STATE[code]!!
            }
        }

        override fun toString(): String {
            return "$pos, $type, movables=$movables)"
        }
    }

    sealed interface Movable {
        val id: Int
        val type: ValleyCell.MovableType

        fun clone(): Movable

        companion object {
            fun from(id: Int, state: ValleyCell.MovableType?): Movable? {
                return when (state) {
                    null -> null
                    BLIZZ_UP, BLIZZ_DOWN, BLIZZ_LEFT, BLIZZ_RIGHT -> Blizzard(
                        id, direction = ValleyMove.from(state), state
                    )
                }
            }
        }
    }

    data class Blizzard(override val id: Int, val direction: ValleyMove, override val type: ValleyCell.MovableType) :
        Movable {
        override fun clone(): Blizzard = this.copy()
        override fun toString(): String {
            return "$id: $direction"
        }
    }

    enum class ValleyMove(val diff: Point) {
        UP(Point(0, -1)),
        DOWN(Point(0, 1)),
        LEFT(Point(-1, 0)),
        RIGHT(Point(1, 0)),
        WAIT(Point(0, 0));

        companion object {
            val BLIZZARD_STATE_TO_DIRECTION = mapOf(
                BLIZZ_UP to UP,
                BLIZZ_DOWN to DOWN,
                BLIZZ_LEFT to LEFT,
                BLIZZ_RIGHT to RIGHT
            )

            fun from(state: ValleyCell.MovableType): ValleyMove = BLIZZARD_STATE_TO_DIRECTION[state]!!
        }

        override fun toString(): String = when (this) {
            UP -> "🔼"
            DOWN -> "🔻"
            LEFT -> "👈"
            RIGHT -> "👉"
            WAIT -> "⏳"
        }
    }

    data class Point(val x: Int, val y: Int) : Comparable<Point> {
        operator fun plus(diff: Point): Point {
            return Point(x + diff.x, y + diff.y)
        }

        fun toOpposite(width: Int, height: Int): Point {
            return if (x == 0) Point(width - 2, y)
            else if (x == width - 1) Point(1, y)
            else if (y == height - 1) Point(x, 1)
            else if (y == 0) Point(x, height - 2)
            else throw IllegalStateException("$this")
        }

        fun isSaveForExpedition(start: Point, end: Point, valleyMap: ValleyMap): Boolean {
            if (this == start || this == end) return true
            if (y <= 0 || y >= valleyMap.height - 1) return false
            if (x <= 0 || x >= valleyMap.width - 1) return false
            val cell = valleyMap[this]
            if (valleyMap.blizzardToPosition.values.any { it == this }) return false
            if (cell.type == WALL) return false
            return cell.movables.isEmpty()
        }

        override fun compareTo(other: Point): Int {
            if (x != other.x) return x.compareTo(other.x)
            return y.compareTo(other.y)
        }

        fun manhattan(other: Point): Int {
            return abs(x - other.x) + abs(y - other.y)
        }

        override fun toString(): String {
            return "($x, $y)"
        }
    }

    data class TimePoint(val time: Int, val pos: Point)
}

