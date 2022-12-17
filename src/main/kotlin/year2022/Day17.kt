package year2022

import aoc.IAocTaskKt

const val VERTICAL_CHAMBER_WIDTH: Int = 7

const val LEFT_ROCK_EDGE_OFFSET = 2
const val BOTTOM_EDGE_OFFSET = 3 // highest rock or the floor, if there isn't one


class Day17 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_17_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val moves = lines.first().chunked(1)
            .let(::TetrisMove)

        val rocksGenerator = TetrisRockGenerator()
        val board = TetrisBoard(VERTICAL_CHAMBER_WIDTH)
//        println(board.display() + "\n")

        val game = TetrisGame(moves, rocksGenerator, board)

        repeat(2022) {
            game.processNextRock()
        }
        println(game.towerHeight())
    }

    override fun solvePartTwo(lines: List<String>) {
        val moves = lines.first().chunked(1)
            .let(::TetrisMove)

        val rocksGenerator = TetrisRockGenerator()
        val board = TetrisBoard(VERTICAL_CHAMBER_WIDTH)
//        println(board.display() + "\n")

        val game = TetrisGame(moves, rocksGenerator, board)

//        repeat(2022) {
        while(game.patternRepeats()) {
            game.processNextRock()
        }
        println(game.towerHeight())
        val rocksCount = 1000000000000
        val remaining = rocksCount % game.usedRocksCounter
        val newMoves = lines.first().chunked(1).let { TetrisMove(it, game.usedRocksCounter % it.size) }
        val newRocksGenerator = TetrisRockGenerator(startFrom = game.usedRocksCounter % newMoves.getMovesCount())
        val newBoard = TetrisBoard(VERTICAL_CHAMBER_WIDTH)
        val remainingGame = TetrisGame(newMoves, newRocksGenerator, newBoard)

        println("remaining game size: $remaining")
        repeat(remaining.toInt()) {
            game.processNextRock()
        }

        val ultraTowerHeight = (rocksCount / game.usedRocksCounter) * game.towerHeight() + remainingGame.towerHeight()
        println(ultraTowerHeight)
    }

    data class TetrisMove(val moves: List<String>, val startFrom: Int = 0) {
        private var currentIdx = startFrom
        private var movesSize = moves.size

        fun getNextMove(): String = moves[currentIdx++ % movesSize]
        fun getMovesCount() = currentIdx
    }

    data class TetrisRockGenerator(val startFrom: Int = 0) {
        private val rockTypes: List<TetrisRock> = listOf(
            TetrisRock(rockLines = listOf("####")),
            TetrisRock(rockLines = listOf(".#.", "###", ".#.")),
            TetrisRock(rockLines = listOf("..#", "..#", "###")),
            TetrisRock(rockLines = listOf("#", "#", "#", "#")),
            TetrisRock(rockLines = listOf("##", "##"))
        )
        private var currentIdx = startFrom
        fun getNextRock(): TetrisRock = rockTypes[currentIdx++ % rockTypes.size]
    }

    data class TetrisRock(
        val rockLines: List<String>,
        val height: Int = rockLines.size,
        val width: Int = rockLines.maxOf { it.length },
    ) {
        operator fun get(rowIdx: Int): List<String> {
            return rockLines[rowIdx].chunked(1)
        }
    }

    data class TetrisGame(val moves: TetrisMove, val generator: TetrisRockGenerator, val board: TetrisBoard) {
        var usedRocksCounter = 0
        fun processNextRock() {
            usedRocksCounter++
            val nextRock = generator.getNextRock()
            board.placeNewRock(nextRock)
//            board.printBoard()
//            board.clearBoard()
            while(true) {
                board.moveFallingRock(moves.getNextMove())
                if (!board.isMovingRockStuck(0, 1, "if")) {
                    board.moveFallingRockDown()
                } else break
            }

            board.freezeMovingRock()
//            board.printBoard()
        }

        fun towerHeight(): Int = board.getTowerHeight()
        fun patternRepeats(): Boolean {
            return usedRocksCounter != 1324
//            return false // TODO detect when the pattern repeats
        }
    }

    data class TetrisBoard(val width: Int, val initialHeight: Int = 8) {
        private val BOTTOM_ROW: List<String> = listOf("+") + (1..width).map { "-" } + "+"
        private val EMPTY_ROW: List<String> = listOf("|") + (1..width).map { "." } + "|"
        private val topToBottomRows: MutableList<MutableList<String>> = initializeTopToBottomRows()

        private fun initializeTopToBottomRows() = (initialHeight downTo 1)
            .map { lvl ->
                if (lvl != 1) EMPTY_ROW.toMutableList() else BOTTOM_ROW.toMutableList()
            }
            .toMutableList()

        @Suppress("unused")
        fun printBoard() {
            println(display())
            println()
        }

        fun display(): String {
            return topToBottomRows.joinToString("\n") { it.joinToString("") }
        }

        fun getTopmostRowWithRockIdx(): Int {
            return topToBottomRows.indexOfFirst { it.contains("#") || it.contains("-") }
        }

        fun getEmptySpaceAvailable(): Int {
            return topToBottomRows.indexOfLast { it == EMPTY_ROW } + 1
        }

        fun placeNewRock(nextRock: TetrisRock) {
            val emptySpaceAvailable = getEmptySpaceAvailable()
            if (emptySpaceAvailable < 8) {
                topToBottomRows.addAll(
                    0,
                    (1..(8 - emptySpaceAvailable)).map { EMPTY_ROW.toMutableList() }.toMutableList()
                )
            }
            val drawStartIdx = getTopmostRowWithRockIdx() - BOTTOM_EDGE_OFFSET - nextRock.height
            for (rowIdx in 0 until nextRock.height) {
                for (drawIdx in 0 until nextRock.width) {
                    val pixel = nextRock[rowIdx][drawIdx]
                    topToBottomRows[drawStartIdx + rowIdx][LEFT_ROCK_EDGE_OFFSET + 1 + drawIdx] =
                        if (pixel == "#") "@" else pixel
                }
            }
        }

        @Suppress("unused")
        fun clearBoard() {
            topToBottomRows.clear()
            topToBottomRows.addAll(initializeTopToBottomRows())
        }

        @Suppress("UNUSED_PARAMETER")
        fun isMovingRockStuck(dx: Int = 0, dy: Int = 1, debug: String): Boolean {
//            println("is moving rock stuck? ($dx, $dy) $debug")
            val firstIdxWithMovingRock = topToBottomRows.indexOfFirst { it.contains("@") }
            val lastIdxWithMovingRock = topToBottomRows.indexOfLast { it.contains("@") }
            for (rowIdx in firstIdxWithMovingRock..lastIdxWithMovingRock) {
                for (colIdx in topToBottomRows[0].indices) {
                    val cell = topToBottomRows[rowIdx][colIdx]
                    if (cell == "@") {
                        val nextCell = topToBottomRows[rowIdx + dy][colIdx + dx]
                        if (nextCell != "@" && nextCell != ".") {
//                            println("YES")
                            return true
                        }
                    }
                }
            }
//            println("NO")
            return false
        }

        fun moveFallingRock(nextMove: String) {
            val dx = if (nextMove == "<") -1 else 1
            val dy = 0
            if (isMovingRockStuck(dx, dy, nextMove)) return

            for (row in topToBottomRows) {
                for (idx in if (dx == 1) row.indices.reversed() else row.indices) {
                    if (row[idx] == "@") {
                        row[idx] = "."
                        row[idx + dx] = "@"
                    }
                }
            }
//            println("falling rock moved: $nextMove")
//            printBoard()
        }

        fun moveFallingRockDown() {
//            println("moving the rock down")
            val lastIdxWithMovingRock = topToBottomRows.indexOfLast { it.contains("@") }
            val firstIdxWithMovingRock = topToBottomRows.indexOfFirst { it.contains("@") }
            for (rowIdx in lastIdxWithMovingRock downTo firstIdxWithMovingRock) {
                for (colIdx in topToBottomRows[0].indices) {
                    val cell = topToBottomRows[rowIdx][colIdx]
                    if (cell == "@") {
                        topToBottomRows[rowIdx + 1][colIdx] = cell
                        topToBottomRows[rowIdx][colIdx] = "."
                    }
                }
            }
//            printBoard()
        }

        fun freezeMovingRock() {
            topToBottomRows.forEachIndexed { rowIdx, _ ->
                topToBottomRows[rowIdx].forEachIndexed { colIdx, _ ->
                    if (topToBottomRows[rowIdx][colIdx] == "@") topToBottomRows[rowIdx][colIdx] = "#"
                }
            }
        }

        fun getTowerHeight(): Int {
            return topToBottomRows.count { it.contains("#") }
        }
    }
}
