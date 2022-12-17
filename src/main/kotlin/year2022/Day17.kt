package year2022

import aoc.IAocTaskKt
import kotlin.math.min

const val VERTICAL_CHAMBER_WIDTH: Int = 7
const val LEFT_ROCK_EDGE_OFFSET = 2
const val BOTTOM_EDGE_OFFSET = 3

class Day17 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_17.txt"

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
//        board.printBoard()
        println(game.towerHeight())
    }

    override fun solvePartTwo(lines: List<String>) {
        val moves = lines.first().chunked(1)
            .let(::TetrisMove)

        val rocksGenerator = TetrisRockGenerator()
        val board = TetrisBoard(VERTICAL_CHAMBER_WIDTH)
//        println(board.display() + "\n")

        val game = TetrisGame(moves, rocksGenerator, board)

        while (!game.patternRepeats()) {
            game.processNextRock()
        }
//        board.printBoard()
//        println(game.towerHeight())
//        println("non-repeating bottom part: ")
//        println(game.display(game.nonRepeatingBottomPart!!))

        var rocksLeftToProcess = 1000000000000

        val introMoves = lines.first().chunked(1).let(::TetrisMove)
        val introGenerator = TetrisRockGenerator()
        val introBoard = TetrisBoard(VERTICAL_CHAMBER_WIDTH)
        val introGame = TetrisGame(introMoves, introGenerator, introBoard)

        while (introBoard.allRowsWithBlocks() != game.nonRepeatingBottomPart) {
            rocksLeftToProcess--
            introGame.processNextRock()
        }
        val startTowerHeight = introGame.towerHeight()

        var rocksPerPattern = 0
        while (introBoard.allRowsWithBlocks() != (game.pattern!! + game.nonRepeatingBottomPart!!)) {
            rocksLeftToProcess--
            rocksPerPattern++
            introGame.processNextRock()
        }
        val singlePatternTowerHeight = introGame.towerHeight() - startTowerHeight

        println("intro game ended, left: $rocksLeftToProcess")

        val remaining = rocksLeftToProcess % rocksPerPattern

        println("remaining game size: $remaining")
        repeat(remaining.toInt()) {
            introGame.processNextRock()
        }
        val endTowerHeight = introGame.towerHeight() - startTowerHeight - singlePatternTowerHeight

        val ultraTowerHeight = startTowerHeight +
                ((rocksLeftToProcess / rocksPerPattern) * singlePatternTowerHeight) +
                endTowerHeight + singlePatternTowerHeight

        println(ultraTowerHeight)
    }

    data class TetrisMove(val moves: List<String>, val startFrom: Int = 0) {
        private var currentIdx = startFrom
        private var movesSize = moves.size

        fun getNextMove(): String = moves[currentIdx++ % movesSize]
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
        private var usedRocksCounter = 0
        var pattern: List<List<String>>? = null
        var nonRepeatingBottomPart: List<List<String>>? = null

        fun processNextRock() {
            usedRocksCounter++
            val nextRock = generator.getNextRock()
            board.placeNewRock(nextRock)
//            board.printBoard()
//            board.clearBoard()
            while (true) {
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
            val rowsWithBlocks = board.allRowsWithBlocks()

            if (rowsWithBlocks.size < 100) return false

            val topRowGroup = rowsWithBlocks.subList(0, 10)
            val firstTopRowRepeatIdx = List(rowsWithBlocks.size) { idx -> idx }
                .indexOfFirst { idx ->
                    idx > 0 &&
                            rowsWithBlocks.subList(idx, min(rowsWithBlocks.size, idx + topRowGroup.size)) == topRowGroup
                }

            if (firstTopRowRepeatIdx == -1) return false

            if (firstTopRowRepeatIdx < 10) return false

            val rowsDivided = rowsWithBlocks.windowed(
                firstTopRowRepeatIdx,
                step = firstTopRowRepeatIdx,
                partialWindows = true
            )

            if (rowsDivided.size < 4) return false

            return if (rowsDivided[0] == rowsDivided[1] && rowsDivided[1] == rowsDivided[2]) {
//                println("found the pattern")
//                println(display(rowsDivided[0]))
//                println("------")
//                println("all rows size: ${rowsWithBlocks.size}")
//                val rowsDividedSumSize = rowsDivided.sumOf { it.size }
//                println("rows divided sum size: $rowsDividedSumSize")
//                println("non-repeating bottom part size: ${rowsWithBlocks.size - rowsDividedSumSize}")
//                println(display(rowsWithBlocks.subList(rowsDividedSumSize, rowsWithBlocks.size)))
//                println("----------")
                pattern = rowsDivided[0].toList()
                nonRepeatingBottomPart = rowsDivided.last().toList()
                true
            } else false
        }

        @Suppress("unused")
        fun display(rows: List<List<String>>) =
            rows.joinToString("\n") { it.joinToString("") }
    }

    data class TetrisBoard(val width: Int, val initialHeight: Int = 8) {
        private val bottomRow: List<String> = listOf("+") + (1..width).map { "-" } + "+"
        private val emptyRow: List<String> = listOf("|") + (1..width).map { "." } + "|"
        private val topToBottomRows: MutableList<MutableList<String>> = initializeTopToBottomRows()

        private fun initializeTopToBottomRows() = (initialHeight downTo 1)
            .map { lvl ->
                if (lvl != 1) emptyRow.toMutableList() else bottomRow.toMutableList()
            }
            .toMutableList()

        @Suppress("unused")
        fun printBoard() {
            println(display())
            println()
        }

        private fun display(): String = topToBottomRows.joinToString("\n") { it.joinToString("") }

        private fun getTopmostRowWithRockIdx(): Int =
            topToBottomRows.indexOfFirst { it.contains("#") || it.contains("-") }

        private fun getEmptySpaceAvailable(): Int = topToBottomRows.indexOfLast { it == emptyRow } + 1

        fun placeNewRock(nextRock: TetrisRock) {
            val emptySpaceAvailable = getEmptySpaceAvailable()
            if (emptySpaceAvailable < 8) {
                topToBottomRows.addAll(
                    0,
                    (1..(8 - emptySpaceAvailable)).map { emptyRow.toMutableList() }.toMutableList()
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

        fun allRowsWithBlocks(): List<List<String>> {
            val firstNotEmptyIdx = getTopmostRowWithRockIdx()
            return topToBottomRows.subList(firstNotEmptyIdx, topToBottomRows.size - 1).toList()
        }
    }
}
