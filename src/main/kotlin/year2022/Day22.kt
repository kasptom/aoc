package year2022

import aoc.IAocTaskKt
import year2022.Day22.*
import year2022.Day22.MonkeyMove.Companion.NEXT_DIR_ANTICLOCKWISE
import year2022.Day22.MonkeyMove.Companion.NEXT_DIR_CLOCKWISE
import year2022.Day22.MonkeyMove.FacingDirection
import year2022.Day22.MonkeyMove.FacingDirection.DOWN
import year2022.Day22.MonkeyMove.FacingDirection.LEFT
import year2022.Day22.MonkeyMove.FacingDirection.RIGHT
import year2022.Day22.MonkeyMove.FacingDirection.UP
import year2022.Day22.MonkeyPosition.Companion.ANSI_GREEN
import year2022.Day22.MonkeyPosition.Companion.ANSI_PURPLE
import year2022.Day22.MonkeyPosition.Companion.ANSI_RED
import year2022.Day22.MonkeyPosition.Companion.ANSI_RESET
import kotlin.math.abs

typealias MonkeyGrid = List<List<MonkeyCell>>

private const val PADDING_CELL = " "
private const val WALL_CELL = "#"
private const val EMPTY_CELL = "."
private const val ROTATE_RIGHT_CODE: String = "R"
private const val ROTATE_LEFT_CODE: String = "L"
private const val PROBLEM_SIZE_TEST = 4
private const val PROBLEM_SIZE_PART2 = 50

val HARDCODED_EDGE_CONNECTIONS = mapOf(
    /**
    E: id=1,  ends=[(  9,   1), ( 12,   1)])
    E: id=2,  ends=[( 12,   1), ( 12,   4)])
    E: id=3,  ends=[( 12,   5), ( 12,   8)])
    E: id=4,  ends=[( 13,   9), ( 16,   9)])
    E: id=5,  ends=[( 16,   9), ( 16,  12)])
    E: id=6,  ends=[( 16,  12), ( 13,  12)])
    E: id=7,  ends=[( 12,  12), (  9,  12)])
    E: id=8,  ends=[(  9,  12), (  9,   9)])
    E: id=9,  ends=[(  8,   8), (  5,   8)])
    E: id=10,  ends=[(  4,   8), (  1,   8)])
    E: id=11,  ends=[(  1,   8), (  1,   5)])
    E: id=12,  ends=[(  1,   5), (  4,   5)])
    E: id=13,  ends=[(  5,   5), (  8,   5)])
    E: id=14,  ends=[(  9,   4), (  9,   1)])
     */
    PROBLEM_SIZE_TEST to listOf(
        EdgePair(from = 1, to = 12, fromPos = Point(9, 1), toPos = Point(4, 5), outward = UP, inward = DOWN),
        EdgePair(from = 2, to = 5, fromPos = Point(12, 1), toPos = Point(16, 9), outward = RIGHT, inward = LEFT),
        EdgePair(from = 3, to = 4, fromPos = Point(12, 8), toPos = Point(13, 9), outward = RIGHT, inward = DOWN),
        EdgePair(from = 6, to = 11, fromPos = Point(16, 12), toPos = Point(1, 8), outward = DOWN, inward = RIGHT),
        EdgePair(from = 7, to = 10, fromPos = Point(9, 12), toPos = Point(4, 8), outward = DOWN, inward = UP),
        EdgePair(from = 8, to = 9, fromPos = Point(9, 9), toPos = Point(8, 8), outward = LEFT, inward = UP),
        EdgePair(from = 13, to = 14, fromPos = Point(8, 5), toPos = Point(9, 4), outward = UP, inward = RIGHT),
    ).flatMap { listOf(it) + listOf(it.complementary()) },

    /**
    E: id=1,  ends=[( 51,   1), (100,   1)])
    E: id=2,  ends=[(101,   1), (150,   1)])
    E: id=3,  ends=[(150,   1), (150,  50)])
    E: id=4,  ends=[(150,  50), (101,  50)])
    E: id=5,  ends=[(100,  51), (100, 100)])
    E: id=6,  ends=[(100, 101), (100, 150)])
    E: id=7,  ends=[(100, 150), ( 51, 150)])
    E: id=8,  ends=[( 50, 151), ( 50, 200)])
    E: id=9,  ends=[( 50, 200), (  1, 200)])
    E: id=10,  ends=[(  1, 200), (  1, 151)])
    E: id=11,  ends=[(  1, 150), (  1, 101)])
    E: id=12,  ends=[(  1, 101), ( 50, 101)])
    E: id=13,  ends=[( 51, 100), ( 51,  51)])
    E: id=14,  ends=[( 51,  50), ( 51,   1)])
     */
    PROBLEM_SIZE_PART2 to listOf(
        EdgePair(from = 1, to = 10, fromPos = Point(51, 1), toPos = Point(1, 151), outward = UP, inward = RIGHT),
        EdgePair(from = 2, to = 9, fromPos = Point(150, 1), toPos = Point(50, 200), outward = UP, inward = UP),
        EdgePair(from = 3, to = 6, fromPos = Point(150, 50), toPos = Point(100, 101), outward = RIGHT, inward = LEFT),
        EdgePair(from = 4, to = 5, fromPos = Point(101, 50), toPos = Point(100, 51), outward = DOWN, inward = LEFT),
        EdgePair(from = 7, to = 8, fromPos = Point(51, 150), toPos = Point(50, 151), outward = DOWN, inward = LEFT),
        EdgePair(from = 11, to = 14, fromPos = Point(1, 101), toPos = Point(51, 50), outward = LEFT, inward = RIGHT),
        EdgePair(from = 12, to = 13, fromPos = Point(50, 101), toPos = Point(51, 100), outward = UP, inward = RIGHT),
    ).flatMap { listOf(it) + listOf(it.complementary()) },
)


class Day22 : IAocTaskKt {

    private val cubeSideSize = if (getFileName().contains("test")) PROBLEM_SIZE_TEST else PROBLEM_SIZE_PART2

    override fun getFileName(): String = "aoc2022/input_22.txt"

    override fun solvePartOne(lines: List<String>) {
        val (map, moves) = getMapAndMoves(lines)
        val password = getPassword(map, moves)
        println(password)
    }

    fun getPassword(map: MonkeyMap, moves: List<MonkeyMove>): Int = map.firstAvailableAtRow(row = 1)
        .run { MonkeyPosition(pos = this, facing = RIGHT) }
        .let { initialPosition ->
            moves.fold(initialPosition) { position, move ->
                map.mark(position)
                move.execute(position, map)
            }
        }.password()

    override fun solvePartTwo(lines: List<String>) {
        val (map, moves) = getMapAndMoves(lines)
//        moves.onEach { println(it) }
        map.createCubicConnections(cubeSideSize)
        val password = getCubePassword(map, moves, cubeSideSize)
        println(password)
    }

    fun getMapAndMoves(lines: List<String>): Pair<MonkeyMap, List<MonkeyMove>> {
        val blankLineIdx = lines.indexOfFirst { it.isEmpty() }
        val map = lines.subList(0, blankLineIdx)
            .let { mapLines -> MonkeyMap.parse(mapLines) }
        val moves = parseMoves(lines, blankLineIdx)
        return Pair(map, moves)
    }

    fun getCubePassword(map: MonkeyMap, moves: List<MonkeyMove>, cubeSideSize: Int, debug: Boolean = false): Int =
        map.firstAvailableAtRow(row = 1)
            .run { MonkeyPosition(pos = this, facing = RIGHT) }
            .let { initialPosition ->
//                println("START:")
//                println(map.display(initialPosition))
                moves.mapToCubicMoves(cubeSideSize)
                    .fold(initialPosition) { position, move ->
                        map.mark(position)
                        move.execute(position, map).also {
                            if (cubeSideSize == PROBLEM_SIZE_TEST || debug) {
                                println("after move: $move, position $position --> $it")
                                println(map.display(it))
                            }
                        }
                    }
            }.password()

    private fun parseMoves(
        lines: List<String>,
        blankLineIdx: Int,
    ) = lines.subList(blankLineIdx + 1, lines.size)
        .single()
        .replace(Regex("(\\d+)"), "\$1,")
        .replace(Regex("([RL])"), "\$1,")
        .split(",")
        .filter { it.isNotEmpty() }
        .map(MonkeyMove::parse)

    data class MonkeyMap(val grid: MonkeyGrid, val moveHistory: MutableList<MonkeyPosition> = mutableListOf()) {
        val edges: MutableList<Edge> = mutableListOf()
        val edgePointsChain: MutableList<MonkeyCell> = mutableListOf()

        @Suppress("unused")
        fun display(monkeyPosition: MonkeyPosition): String = grid.display(monkeyPosition, moveHistory)

        fun firstAvailableAtRow(row: Int): Point = grid[row].first { it.state != PADDING_CELL }.pos

        fun mark(position: MonkeyPosition): Boolean = moveHistory.add(position)

        fun minColumnAt(row: Int): Point = grid[row].first { it.state != PADDING_CELL }.pos

        fun maxColumnAt(row: Int): Point = grid[row].last { it.state != PADDING_CELL }.pos

        fun minRowAt(currentCol: Int): Point = grid
            .filter { row -> currentCol < row.size }
            .map { row -> row[currentCol] }.first { it.state != PADDING_CELL }.pos

        fun maxRowAt(currentCol: Int): Point = grid
            .filter { row -> currentCol < row.size }
            .map { row -> row[currentCol] }
            .last { it.state != PADDING_CELL }.pos

        operator fun get(pos: Point): MonkeyCell = grid[pos.y][pos.x]

        fun createCubicConnections(cubeSideSize: Int) {
            val edgeChainFinder = EdgeChainFinder(map = this)
            edgeChainFinder.markEdges()

            edgeChainFinder.getAllEdgesChained()
                .let {
                    edgePointsChain.addAll(it)
                }
            println(edgePointsChain)

            edgePointsChain.toEdgePointPairs(cubeSideSize)
                .mapIndexed { idx, edgeEnds -> Edge.fromEnds(idx, edgeEnds, edgePointsChain, cubeSideSize) }
                .let {
                    edges.addAll(it)
                }
//            edges.onEach { println(it) }
//            println("connections")
//            HARDCODED_EDGE_CONNECTIONS[cubeSideSize]!!.onEach {
//                println(it)
//            }
        }

        companion object {
            fun parse(mapLines: List<String>): MonkeyMap {
                val maxRowLength = mapLines.maxOf { it.length }
                val grid = mapLines
                    .map { line ->
                        line.chunked(1)
                            .let { if (it.size < maxRowLength) it + cellsPadding(maxRowLength - it.size) else it }
                    }

                val paddedGrid: List<List<MonkeyCell>> =
                    (listOf(cellsPadding(maxRowLength)) + grid + listOf(cellsPadding(maxRowLength)))
                        .map { row -> cellsPadding(1) + row + cellsPadding(1) }
                        .mapIndexed { yIdx, row ->
                            row.mapIndexed { xIdx, cell -> MonkeyCell(Point(xIdx, yIdx), cell) }
                        }.toList()

                return MonkeyMap(paddedGrid)
            }

            private fun cellsPadding(size: Int): List<String> = (1..size).map { PADDING_CELL }
        }
    }

    data class MonkeyCell(
        val pos: Point,
        val state: String,
        var isEdge: Boolean = false,
        var isConcave: Boolean = false,
        var cubeConnection: MonkeyCubeConnection = MonkeyCubeConnection(),
    ) {
        fun isPadding(): Boolean = state == PADDING_CELL
        fun isNotPadding(): Boolean = state != PADDING_CELL
        override fun toString(): String = "($pos=$state)"
    }

    sealed interface MonkeyMove {
        enum class FacingDirection(val delta: Point, val value: Int, val arrow: String) {
            UP(Point(0, -1), 3, "^"),
            DOWN(Point(0, 1), 1, "v"),
            LEFT(Point(-1, 0), 2, "<"),
            RIGHT(Point(1, 0), 0, ">");

            override fun toString(): String = arrow
            fun opposite(): FacingDirection = when (this) {
                UP -> DOWN
                LEFT -> RIGHT
                DOWN -> UP
                RIGHT -> LEFT
            }
        }

        companion object {
            fun parse(moveCode: String): MonkeyMove {
//                println("parsing $moveCode")
                return if (moveCode.startsWith(ROTATE_RIGHT_CODE) || moveCode.startsWith(ROTATE_LEFT_CODE)) {
                    MonkeyRotation(moveCode[0].toString())
                } else {
                    MonkeyGridWalk(moveCode.toInt())
                }
            }

            val NEXT_DIR_CLOCKWISE = mapOf(UP to RIGHT, RIGHT to DOWN, DOWN to LEFT, LEFT to UP)
            val NEXT_DIR_ANTICLOCKWISE = mapOf(UP to LEFT, LEFT to DOWN, DOWN to RIGHT, RIGHT to UP)
        }

        fun execute(position: MonkeyPosition, map: MonkeyMap): MonkeyPosition
    }

    sealed interface MonkeyWalk : MonkeyMove {
        val tilesCount: Int

        override fun execute(position: MonkeyPosition, map: MonkeyMap): MonkeyPosition = moveUntilWall(map, position)

        fun getNextPosition(map: MonkeyMap, current: Point, facing: FacingDirection): MonkeyPosition?

        fun moveUntilWall(map: MonkeyMap, position: MonkeyPosition): MonkeyPosition {
            var finalPosition = position
            var stepsCounter = 0
            var next = getNextPosition(map, finalPosition.pos, finalPosition.facing)
            while (next != null && stepsCounter < tilesCount) {
                finalPosition = next
                next = getNextPosition(map, next.pos, next.facing)
                stepsCounter++
            }

            return finalPosition
        }

        fun move(current: Point, facing: FacingDirection, map: MonkeyMap): MonkeyPosition? {
            var next = current + facing.delta
            when (facing) {
                UP, DOWN -> {
                    val minCell = map.minRowAt(current.x)
                    val maxCell = map.maxRowAt(current.x)
                    if (next.y < minCell.y) {
                        next = maxCell
                    } else if (maxCell.y < next.y) {
                        next = minCell
                    }
                }

                LEFT, RIGHT -> {
                    val minCell = map.minColumnAt(current.y)
                    val maxCell = map.maxColumnAt(current.y)
                    if (next.x < minCell.x) {
                        next = maxCell
                    } else if (maxCell.x < next.x) {
                        next = minCell
                    }
                }
            }
            return if (map[next].state == EMPTY_CELL) MonkeyPosition(next, facing) else null
        }
    }

    data class MonkeyGridWalk(override val tilesCount: Int) : MonkeyWalk {
        override fun toString(): String = "️👣$tilesCount"

        override fun getNextPosition(map: MonkeyMap, current: Point, facing: FacingDirection): MonkeyPosition? =
            move(current, facing, map)
    }

    data class MonkeyCubicWalk(override val tilesCount: Int, val cubeSideSize: Int) : MonkeyWalk {

        override fun getNextPosition(map: MonkeyMap, current: Point, facing: FacingDirection): MonkeyPosition? {
            val currentCell = map[current]
            val edgeJump: EdgePair? = findEdgeJump(currentCell, map, facing)
            return if (edgeJump != null) {
                teleport(current, edgeJump, map)
            } else {
                move(current, facing, map)
            }
        }

        private fun findEdgeJump(currentCell: MonkeyCell, map: MonkeyMap, facing: FacingDirection): EdgePair? {
            val connections: List<EdgePair> = HARDCODED_EDGE_CONNECTIONS[cubeSideSize]!!
            if (!currentCell.isEdge || currentCell.isConcave) return null
            val edgesContainingPoint = map.edges.filter {
                it.points.contains(currentCell.pos)
            }
            val currentEdge = edgesContainingPoint.firstOrNull { edge ->
               connections.any { conn -> conn.from == edge.id && conn.outward == facing }
            }
            return connections.firstOrNull { it.from == currentEdge?.id && it.outward == facing }
        }

        private fun teleport(current: Point, jump: EdgePair, map: MonkeyMap): MonkeyPosition? {
            val fromEdge = map.edges.first { it.id == jump.from }
            val toEdge = map.edges.first { it.id == jump.to }
            val inwardDirection = jump.inward
            val fromPoints =
                if (jump.fromPos == fromEdge.points.first()) fromEdge.points else fromEdge.points.reversed()
            val toPoints = if (jump.toPos == toEdge.points.first()) toEdge.points else toEdge.points.reversed()
            val outPoint = toPoints[fromPoints.indexOf(current)]
            return if (map[outPoint].state == EMPTY_CELL) {
//                if (jump.to == 14 || jump.from == 14) {
//                    println("EDGE JUMP $current --$jump--> $outPoint")
//                    println("\t $fromPoints")
//                    println("\t $toPoints")
//                }
                MonkeyPosition(outPoint, inwardDirection)
            } else null
        }

        override fun toString(): String = "️👣$tilesCount"
    }

    data class MonkeyRotation(val direction: String) : MonkeyMove {
        override fun execute(position: MonkeyPosition, map: MonkeyMap): MonkeyPosition {
            return rotateInPlace(position)
        }

        override fun toString(): String = if (direction == ROTATE_LEFT_CODE) "◀⌚" else "⌚▶"

        private fun rotateInPlace(position: MonkeyPosition): MonkeyPosition {
            val rotationMap = if (direction == ROTATE_RIGHT_CODE) NEXT_DIR_CLOCKWISE else NEXT_DIR_ANTICLOCKWISE
            return position.copy(facing = rotationMap[position.facing]!!)
        }
    }

    data class MonkeyPosition(val pos: Point, val facing: FacingDirection) {
        private operator fun plus(tilesCount: Int): MonkeyPosition =
            MonkeyPosition(pos + facing.delta * tilesCount, facing)

        private operator fun minus(delta: Point): MonkeyPosition = copy(pos = pos - delta)

        operator fun plus(delta: Point): MonkeyPosition = copy(pos = pos + delta)

        fun password(): Int {
            println("PASSWORD: 1000 * ${pos.y} + 4 * ${pos.x} + ${facing.value}")
            return 1000 * pos.y + 4 * pos.x + facing.value
        }
        override fun toString(): String {
            return "🐵: $pos $facing"
        }


        companion object {
            const val ANSI_RESET = "\u001B[0m"
            const val ANSI_GREEN = "\u001B[32m"
            const val ANSI_RED = "\u001B[31m"
            const val ANSI_PURPLE = "\u001B[35m"
        }
    }

    data class Point(val x: Int, val y: Int) {
        operator fun times(tilesCount: Int): Point = Point(x * tilesCount, y * tilesCount)
        operator fun plus(point: Point): Point = Point(x + point.x, y + point.y)
        operator fun minus(point: Point): Point = Point(x - point.x, y - point.y)
        override fun toString(): String = "(${String.format("%3d", x)}, ${String.format("%3d", y)})"
    }

    data class MonkeyCubeConnection(
        val up: MonkeyCell? = null,
        val down: MonkeyCell? = null,
        val left: MonkeyCell? = null,
        val right: MonkeyCell? = null,
    )

    data class Edge(val id: Int, val points: List<Point>, val ends: List<Point>) {
        companion object {
            fun fromEnds(idx: Int, edgeEnds: List<Point>, edgePointsChain: List<MonkeyCell>, cubeSideSize: Int): Edge {
                val id = idx + 1
                val (fromIdx, toIdx) = edgeEnds
                    .map { ee -> edgePointsChain.indexOfFirst { it.pos == ee } }
                    .sorted()
                    .zipWithNext()
                    .single()
                val points = (if (id != 14) edgePointsChain.subList(fromIdx, toIdx + 1)
                else edgePointsChain.subList(
                    edgePointsChain.size - cubeSideSize + 1,
                    edgePointsChain.size
                ) + edgePointsChain[0]).map { cell -> cell.pos }
                return Edge(id, points, edgeEnds)
            }
        }

        override fun toString(): String = "E: id=$id,  ends=$ends)"
    }

    data class EdgePair(
        val from: Int,
        val to: Int,
        val fromPos: Point,
        val toPos: Point,
        val outward: FacingDirection,
        val inward: FacingDirection,
    ) {
        fun complementary(): EdgePair = EdgePair(
            from = to,
            to = from,
            fromPos = toPos,
            toPos = fromPos,
            outward = inward.opposite(),
            inward = outward.opposite()
        )

        override fun toString(): String {
            return "$from $fromPos $outward -🦘-> $to $toPos $inward"
        }

        init {
            if (from == to) throw IllegalStateException("firstId ($from) different than secondId {$to")
        }


    }

    data class EdgeChainFinder(val map: MonkeyMap) {
        private val surroundingCells = listOf(
            Point(0, -1), Point(1, -1), Point(1, 0),
            Point(1, 1), Point(0, 1), Point(-1, 1),
            Point(-1, 0), Point(-1, -1)
        )

        private fun findNeighbours(cell: Point): List<MonkeyCell> {
            return surroundingCells.map { offset -> map[cell + offset] }
        }

        private fun otherEdgeClockwise(edgeCell: MonkeyCell, direction: FacingDirection): Pair<FacingDirection, MonkeyCell> {
//            println("searching the other edge than $direction for $edgeCell")
            if (!edgeCell.isEdge) throw IllegalStateException("not an edge: $edgeCell")
            return (FacingDirection.values().toList() - direction).map { dir -> Pair(dir, edgeCell.pos + dir.delta) }
                .map { (dir, point) -> Pair(dir, map[point]) }
                .first { (_, cell) -> cell.isEdge }
        }

        fun getAllEdgesChained(): List<MonkeyCell> {
            val edgesChain = mutableListOf<MonkeyCell>()
            val startPoint = map.firstAvailableAtRow(row = 1)
            val startCell = map[startPoint]

            var (direction, nextEdge) = otherEdgeClockwise(startCell, UP.opposite())
            edgesChain += startCell
            while (nextEdge != startCell) {
                edgesChain += nextEdge
                val (newDirection, newNextEdge) = otherEdgeClockwise(nextEdge, direction.opposite())
                nextEdge = newNextEdge
                direction = newDirection
            }
            return edgesChain.distinct()
        }

        fun markEdges() {
            for (row in map.grid) {
                for (cell in row) {
                    if (cell.isPadding()) continue
                    val neighbours = findNeighbours(cell.pos)
                    val neighboursCount = neighbours.count { it.isNotPadding() }
                    //                    println("cell: $cell: neighbours count: $neighboursCount")
                    //                    println(neighbours.map { "'${it.state}'" })
                    cell.isEdge = neighboursCount != 8
                    cell.isConcave = neighboursCount == 7
                }
            }
        }
    }
}

private fun List<MonkeyCell>.toEdgePointPairs(cubeSideSize: Int): List<List<Point>> {
    val edgeChain = this
    val pairs = mutableListOf<List<Point>>()
    var counter = 0
    while (counter < edgeChain.size) {
        val first = edgeChain[counter]
        if (first.isConcave) {
//            println("$first is is concave")
            counter++
            continue
        }

        val second = if (counter + cubeSideSize - 1 < edgeChain.size - 1)
            edgeChain[counter + cubeSideSize - 1] else edgeChain[0]

        val edgePointsPair = listOf(first.pos, second.pos)
//        println(edgePointsPair)
        pairs.add(edgePointsPair)

        if (counter + cubeSideSize - 1 >= edgeChain.size - 1) {
            counter += cubeSideSize
            continue
        }

        val nextFirst = edgeChain[counter + cubeSideSize]
        counter += if (nextFirst.pos.x == first.pos.x || nextFirst.pos.y == first.pos.y) cubeSideSize
        else (cubeSideSize - 1)
    }
    return pairs
}

private fun List<MonkeyMove>.mapToCubicMoves(cubeSideSize: Int) = map { move ->
    when (move) {
        is MonkeyCubicWalk, is MonkeyRotation -> move
        is MonkeyGridWalk -> MonkeyCubicWalk(move.tilesCount, cubeSideSize)
    }
}

fun MonkeyGrid.displayCoordinates(): String {
    return this.joinToString("\n") { row ->
        row.joinToString("") {
            val toDisplay = if (it.state == WALL_CELL) "    ${it.state}     " else it.pos.toString()
            if (it.state == PADDING_CELL) "$ANSI_RED${it.pos}$ANSI_RESET" else {
                if (it.isConcave) {
                    "$ANSI_PURPLE${toDisplay}$ANSI_RESET"
                } else if (it.isEdge) {
                    "$ANSI_GREEN${toDisplay}$ANSI_RESET"
                } else toDisplay
            }
        }
    }
}

private fun MonkeyGrid.display(monkeyPosition: MonkeyPosition, moveHistory: MutableList<MonkeyPosition>): String {
    var result = ""
    val bufferVision = 50
    for (rowIdx in this.indices) {
        if (abs(monkeyPosition.pos.y - rowIdx) > bufferVision) continue
        for (colIdx in this[rowIdx].indices ) {
//            if (abs(monkeyPosition.pos.x - colIdx) > bufferVision) continue
            val it = this[rowIdx][colIdx]
            val foundHistoryEntry = moveHistory.firstOrNull { hist -> hist.pos == it.pos }
            result += if (it.pos == monkeyPosition.pos) "$ANSI_GREEN${monkeyPosition.facing.arrow}$ANSI_RESET"
            else foundHistoryEntry?.facing?.arrow ?: it.state
        }
        result += "\n"
    }
    return result
}

