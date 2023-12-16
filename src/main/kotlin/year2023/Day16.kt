package year2023

import aoc.IAocTaskKt
import year2023.Day16.Direction.DOWN
import year2023.Day16.Direction.LEFT
import year2023.Day16.Direction.RIGHT
import year2023.Day16.Direction.UP

class Day16 : IAocTaskKt{
    override fun getFileName(): String  = "aoc2023/input_16.txt"

    override fun solvePartOne(lines: List<String>) {
        val grid = Grid.parse(lines)
        println(grid.print())
        grid.energizeUntilPossible()
        println()
        println(grid.printWithBeams())
        println(grid.beams.distinctBy { it.pos }.count())
    }

    override fun solvePartTwo(lines: List<String>) {
        println("Not yet implemented")
    }
    /*
    If the beam encounters empty space (.), it continues in the same direction.
    If the beam encounters a mirror (/ or \), the beam is reflected 90 degrees depending
        on the angle of the mirror. For instance, a rightward-moving beam that encounters
        a / mirror would continue upward in the mirror's column, while a rightward-moving
        beam that encounters a \ mirror would continue downward from the mirror's column.
    If the beam encounters the pointy end of a splitter (| or -), the beam passes through
        the splitter as if the splitter were empty space. For instance, a rightward-moving
        beam that encounters a - splitter would continue in the same direction.
    If the beam encounters the flat side of a splitter (| or -), the beam
        is split into two beams going in each of the two directions the splitter's
        pointy ends are pointing. For instance, a rightward-moving beam that encounters
        a | splitter would split into two beams: one that continues upward from the splitter's
        column and one that continues downward from the splitter's column.

     Beams do not interact with other beams; a tile can have many beams passing through it at the same time.
     A tile is energized if that tile has at least one beam pass through it, reflect in it, or split in it.
     */
    data class Grid(val grid: List<List<String>>, val beams: MutableSet<Beam> = mutableSetOf(Beam(Point(0,0), RIGHT))) {
        fun print(): String = grid.joinToString("\n") { it.joinToString("") }
        fun printWithBeams() = grid
            .mapIndexed { yIdx, row -> row
                .mapIndexed { xIdx, col -> if (beams.any { beam -> beam.pos == Point(xIdx, yIdx) }) "#" else col }
            }
            .joinToString("\n") { it.joinToString("") }

        fun energize(): Boolean {
            val newBeams = mutableSetOf<Beam>()
            for (beam in beams) {
                val cellValue = runCatching { grid.valueAt(beam.pos).let { TileType.from(it) } }
                    .onFailure {
                        println("failure for value: ${grid.valueAt(beam.pos)} $it")
                    }.getOrNull()
                val partNewBeams: Set<Beam> = beam.generateBeams(cellValue!!, grid)
                newBeams += partNewBeams
            }
            val prevBeamsSize = beams.size
            beams += newBeams
            return prevBeamsSize != beams.size
        }

        companion object {
            fun parse(lines: List<String>): Grid {
                val grid = lines.map { it.split("").filter(String::isNotEmpty) }
                return Grid(grid)
            }
        }

        private fun List<List<String>>.valueAt(pos: Point): String = this[pos.y][pos.x]
        fun energizeUntilPossible() {
            var energized = true
            while (energized) {
                energized = energize()
            }
        }
    }

    data class Beam(val pos: Point, val dir: Direction) {
        fun generateBeams(cellValue: TileType, grid: List<List<String>>): Set<Beam> {
            val beams = when (cellValue) {
                TileType.HOR_SPLIT -> if (dir.isHorizontal()) {
                    setOf(Beam(pos + dir, dir))
                } else {
                    setOf(
                        Beam(pos + LEFT, LEFT),
                        Beam(pos + RIGHT, RIGHT),
                    )
                }
                TileType.VER_SPLIT -> if (dir.isVertical()) {
                    setOf(Beam(pos + dir, dir))
                } else {
                    setOf(
                        Beam(pos + UP, UP),
                        Beam(pos + DOWN, DOWN),
                    )
                }
                TileType.EMPTY -> setOf(Beam(pos + dir, dir))
                TileType.SLASH_MIR -> when (dir) {
                    UP -> setOf(Beam(pos + RIGHT, RIGHT))
                    DOWN -> setOf(Beam(pos + LEFT, LEFT))
                    LEFT -> setOf(Beam(pos + DOWN, DOWN))
                    RIGHT -> setOf(Beam(pos + UP, UP))
                }
                TileType.BACK_SL_MIR -> when(dir) {
                    UP -> setOf(Beam(pos + LEFT, LEFT))
                    DOWN -> setOf(Beam(pos + RIGHT, RIGHT))
                    LEFT -> setOf(Beam(pos + UP, UP))
                    RIGHT -> setOf(Beam(pos + DOWN, DOWN))
                }
            }
            return beams.filter { it.isInRange(grid) }
                .toSet()
        }

        private fun isInRange(grid: List<List<String>>): Boolean = pos.isInRange(grid)

    }

    data class Point(val x: Int, val y: Int) {
        operator fun plus(dir: Direction): Point {
            return when (dir) {
                UP -> this + Point(0, -1)
                DOWN -> this + Point(0, 1)
                LEFT -> this + Point(-1, 0)
                RIGHT -> this + Point(1, 0)
            }
        }

        operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)
        fun isInRange(grid: List<List<String>>): Boolean = x >= 0 && y >= 0 && x < grid[0].size && y < grid.size
    }

    enum class Direction(val dir: String) {
        UP("^"), DOWN("v"), LEFT("<"), RIGHT(">");

        fun isHorizontal(): Boolean = this == LEFT || this == RIGHT
        fun isVertical(): Boolean = this == UP || this == DOWN
    }

    enum class TileType(val value: String) {
        HOR_SPLIT("-"), VER_SPLIT("|"), EMPTY("."), SLASH_MIR("/"), BACK_SL_MIR("\\");

        companion object {
            fun from(value: String): TileType = values().first { it.value == value }
        }
    }
}
