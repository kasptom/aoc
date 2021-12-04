package year2021

import aoc.IAocTaskKt

class Day04 : IAocTaskKt {
    override fun getFileName(): String = "aoc2021/input_04.txt"

    override fun solvePartOne(lines: List<String>) {
        val (numbers, boards) = parseBoards(lines)
        numbers.map { number -> boards
            .map { board -> board.mark(number) }
            .firstOrNull(Board::hasBingo)
            ?.score(number)
        }.first { it != null }
            .let(::println)
    }

    class Board(val lines: List<String>, val size: Int) {
        private val rows = lines
            .map { it.split(" ")
                .map(String::trim)
                .filter(String::isNotBlank)
                .map(Integer::parseInt)
                .map(::Field)
            }

        private val columns = rows.indices
            .map { idx -> rows.map { it[idx] } }

        fun mark(number: Int): Board {
            rows.flatten()
                .filter { it.value == number }
                .forEach { it.marked = true }
            return this
        }

        fun hasBingo(): Boolean = rows.any { it.all(Field::marked) }
                    || columns.any { it.all(Field::marked) }

        fun hasNotBingo() = !hasBingo()

        fun score(lastNum: Int): Int = rows.flatten()
            .filter(Field::notMarked)
            .sumOf(Field::value) * lastNum

        override fun toString() = "Board(size=$size, fields=$rows)"
    }

    class Field(var value: Int, var marked: Boolean = false) {
        fun notMarked() = !marked
        override fun toString() = "[$value, $marked]"
    }

    override fun solvePartTwo(lines: List<String>) {
        val (numbers, boards) = parseBoards(lines)

        for (number in numbers) {
            val lastWinningBoard = boards.filter(Board::hasNotBingo)
                .map { it.mark(number) }

            if (boards.all(Board::hasBingo)) {
                lastWinningBoard.last()
                    .score(number)
                    .let(::println)
                break
            }
        }
    }

    private fun parseBoards(lines: List<String>): Pair<List<Int>, List<Board>> {
        val numbers = lines[0].split(",")
            .map(String::trim)
            .map(Integer::parseInt)

        val boardSize = lines[2].split(" ")
            .map(String::trim)
            .count(String::isNotBlank)

        val boards = lines.subList(1, lines.size)
            .filter(String::isNotBlank)
            .windowed(boardSize, boardSize)
            .map { Board(it, boardSize) }

        return Pair(numbers, boards)
    }
}