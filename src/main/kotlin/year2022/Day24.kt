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
    override fun getFileName(): String = "aoc2022/input_24.txt"

    override fun solvePartOne(lines: List<String>) {
        val (timeToValley, start, end) = ValleyMap.parse(lines)
        val shortestPath = findShortestPath(0, start, end, timeToValley)
        println(shortestPath.size - 1)
    }

    fun findShortestPath(initialTime: Int, start: Point, end: Point, timeToValley: Map<Int, ValleyMap>): List<Point> {
        val unvisitedCost = TreeSet(MapsComparator(end))
        val childToParent = HashMap<TimePoint, TimePoint>()
//        var currentPosition: Point = start
        var expeditionMap = timeToValley[initialTime]!!.deepCopy(time = 0, expedition = start)
        unvisitedCost.add(expeditionMap)
        var pathCost: Int

//        println(expeditionMap.display())


        while (unvisitedCost.isNotEmpty() && expeditionMap.expedition != end) {
//            println("unvisited size: ${unvisitedCost.size}")
            val prevMap = unvisitedCost.first()
            pathCost = prevMap.minute + 1
            unvisitedCost.remove(prevMap)

            val nextValleyMap = timeToValley[initialTime + pathCost]!!

            for (move in ValleyMove.values()) {
                //  val currentCell = expeditionMap[expeditionMap.expedition!!]

                val nextExpeditionPosition = expeditionMap.expedition!! + move.diff
                if (!nextExpeditionPosition.isSaveForExpedition(nextValleyMap, start, end)) {
                    continue
                }
//                println("found save position: $nextPosition")
                val nextExpeditionMap = nextValleyMap.deepCopy(pathCost, nextExpeditionPosition)

                childToParent[TimePoint(pathCost, nextExpeditionPosition)] =
                    TimePoint(pathCost - 1, expeditionMap.expedition!!)

                if (pathCost < TIME_LIMIT_MINUTES) {
                    unvisitedCost.add(nextExpeditionMap)
                }


                val limited = unvisitedCost.toList().subList(0, min(unvisitedCost.size, MAX_STATES_STORED))
                unvisitedCost.clear()
                unvisitedCost.addAll(limited)
            }

            expeditionMap = unvisitedCost.first()
//            println(expeditionMap.display())
        }
        return mapToPath(childToParent, unvisitedCost, start)
    }

    private fun mapToPath(
        childToParent: HashMap<TimePoint, TimePoint>,
        unvisitedCost: TreeSet<ValleyMap>,
        start: Point,
    ): List<Point> {
        val path = mutableListOf<Point>()
        val endCell = unvisitedCost.first()

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
        val (timeToValley, start, end) = ValleyMap.parse(lines)

        val  firstTripLength = findShortestPath(0, start, end, timeToValley).size - 1
        println("I trip length: $firstTripLength")

        val secondTripLength = findShortestPath(firstTripLength, end, start, timeToValley).size - 1
        println("II trip length: $secondTripLength")

        val thirdTripLength = findShortestPath(firstTripLength + secondTripLength, start, end, timeToValley).size - 1
        println("I + II + II: $firstTripLength + $secondTripLength + $thirdTripLength")

        val result = firstTripLength + secondTripLength + thirdTripLength
        println(result)
    }

    data class ValleyMap(
        val minute: Int,
        val expedition: Point?,
        val map: ValleyGrid,
        val blizzardToPosition: MutableMap<Blizzard, Point>,
    ) {
        val width: Int = map[0].size
        val height: Int = map.size

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
            fun parse(lines: List<String>): Triple<Map<Int, ValleyMap>, Point, Point> {
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

                val valleyMap = ValleyMap(
                    minute = 0,
                    expedition = null,
                    map = map,
                    blizzardToPosition = blizzardToPosition
                )
                val start = map.first().first { it.type == CLEAR }.pos
                val end = map.last().first { it.type == CLEAR }.pos

                val timeToValley: MutableMap<Int, ValleyMap> = timeToValleyWeather(valleyMap)

                return Triple(timeToValley, start, end)
            }

            const val ANSI_RESET = "\u001B[0m"
            const val ANSI_GREEN = "\u001B[32m"
            const val ANSI_RED = "\u001B[31m"

            private fun timeToValleyWeather(valleyMap: ValleyMap): MutableMap<Int, ValleyMap> {
                val timeToValleyWoExpedition = mutableMapOf<Int, ValleyMap>()
                timeToValleyWoExpedition[0] = valleyMap
                for (pathCost in 1..MAX_STATES_STORED) {
                    if (timeToValleyWoExpedition.containsKey(pathCost)) timeToValleyWoExpedition[pathCost]!! else {
                        timeToValleyWoExpedition[pathCost] =
                            timeToValleyWoExpedition[pathCost - 1]!!.deepCopy(pathCost - 1, null).moveBlizzards()
                        timeToValleyWoExpedition[pathCost]!!
                    }
                }
                return timeToValleyWoExpedition
            }
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
                minute = time,
                expedition = expedition,
                map = mapCopy,
                blizzardToPosition = blizzardToPositionCopy
            )
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
            private val BLIZZARD_STATE_TO_DIRECTION = mapOf(
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

        fun isSaveForExpedition(valleyMap: ValleyMap, start: Point, end: Point): Boolean {
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

    class MapsComparator(val end: Point): Comparator<ValleyMap> {

        override fun compare(one: ValleyMap, other: ValleyMap): Int {
            if (one.minute != other.minute) return one.minute.compareTo(other.minute)

            val distanceToEnd: Int = one.expedition?.manhattan(end) ?: Integer.MAX_VALUE
            val otherDistanceToEnd = other.expedition?.manhattan(end) ?: Integer.MAX_VALUE

            if (distanceToEnd != otherDistanceToEnd) return distanceToEnd.compareTo(otherDistanceToEnd)

            if (one.expedition != null && other.expedition != null && one.expedition != other.expedition) {
                return other.expedition.compareTo(one.expedition)
            }
            if (one.expedition == null && other.expedition != null) return -1
            if (other.expedition == null && one.expedition != null) return 1

            val blizzards = one.blizzardToPosition
            val otherBlizzards = other.blizzardToPosition
            for (blizzardKey in blizzards.keys) {
                val blizzard = blizzards[blizzardKey]!!
                val otherBlizzard = otherBlizzards[blizzardKey]!!
                if (blizzard != otherBlizzard) return blizzard.compareTo(otherBlizzard)
            }

            return 0
        }
    }

    companion object {
        const val MAX_STATES_STORED = 1000
        const val TIME_LIMIT_MINUTES = 300
    }
}

