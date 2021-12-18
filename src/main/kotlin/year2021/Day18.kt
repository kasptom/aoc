package year2021

import aoc.IAocTaskKt
import utils.ANSI_GREEN
import utils.ANSI_RESET
import utils.ANSI_YELLOW
import year2021.Day18.SnailFish.Companion.numberRegex
import java.util.regex.Pattern
import kotlin.math.ceil
import kotlin.math.floor

class Day18 : IAocTaskKt {
    override fun getFileName(): String = "aoc2021/input_18.txt"


    override fun solvePartOne(lines: List<String>) {
        val snailFishes = lines.map(SnailFish::parse)
        val result = sumFishes(snailFishes)
        println("final sum: $result")
        println(result.getMagnitude())
    }

    fun sumFishes(snailFishes: List<SnailFish>) =
        snailFishes.reduce { snailFish, other ->
            snailFish + other
        }

    data class SnailFish(var left: Either, var right: Either) {
        var depth = 0
        var parent: SnailFish? = null

        companion object {
            val numberRegex = Pattern.compile("[0-9]+").toRegex()

            fun parse(line: String): SnailFish {
                var input: List<Input> = line.chunked(1)
                    .fold(mutableListOf<String>()) { acc, str ->
                        val last = acc.lastOrNull() ?: "X"
                        if (numberRegex.matches(last) && numberRegex.matches(str)) {
                            acc.removeLast()
                            acc += last + str
                        } else {
                            acc += str
                        }
                        acc
                    }
                    .map { NotParsed(it) }

                while (input.size != 1) {
//                    println("parsing: ${input.joinToString("", transform = Input::painted)}")
                    val idx = findCommaToProcess(input)
                    val left = input[idx - 1]
                    val right = input[idx + 1]
                    val leftSnailFish = parseOrExtract(left)
                    val rightSnailFish = parseOrExtract(right)
                    val parsed = Parsed(SnailFish(leftSnailFish, rightSnailFish))
                    input = input.subList(0, idx - 2) + parsed + input.subList(idx + 3, input.size)
                }
                return (input[0] as Parsed).snailFish
//                    .also(::println)
                    .also { it.updateDepth(0, null) }
            }

            private fun findCommaToProcess(input: List<Input>): Int {
                for (idx in input.indices) {
                    if (input[idx] != NotParsed(",")) continue
                    val left = input[idx - 1]
                    val right = input[idx + 1]
                    if (left.isReadyToMerge() && right.isReadyToMerge()) {
                        return idx
                    }
                }
                return input.indexOfFirst { it == NotParsed(",") }
            }


            private fun parseOrExtract(pairElement: Input): Either {
                return when (pairElement) {
                    is NotParsed -> Either(pairElement.symbol.toInt())
                    is Parsed -> Either(pairElement.snailFish)
                }
            }
        }

        fun isComplex() = (left.nested ?: right.nested) != null

        fun isLeftChild() = parent?.left?.nested == this
        fun isRightChild() = parent?.right?.nested == this

        fun updateDepth(depth: Int, parent: SnailFish?) {
            if (depth > 4) throw IllegalStateException("too deep: $depth > 4")
            this.depth = depth
            this.parent = parent
            if (isComplex()) {
                left.nested?.updateDepth(depth + 1, this)
                right.nested?.updateDepth(depth + 1, this)
            }
        }

        fun reduce() {
            getRoot().updateDepth(0, null)
            val toExplode = findToExplode()
            val toSplit = findToSplit()
            if (toExplode != null) {
                toExplode.explode()
                println("after explode:$this")
            } else if (toSplit != null) {
                toSplit.split()
                println("after split:  $this")
            }

            if (toExplode != null || toSplit != null) {
                reduce()
            }
            getRoot().updateDepth(0, null)
        }

        /**
         * To explode a pair, the pair's left value is added to the first regular number
         * to the left of the exploding pair (if any), and the pair's right value is added
         * to the first regular number to the right of the exploding pair (if any).
         * Exploding pairs will always consist of two regular numbers. Then, the entire exploding pair
         * is replaced with the regular number 0.
         */
        fun explode() {
            if (isComplex()) throw IllegalStateException("cannot explode complex SnailFish")

            val root = this.getRoot()
            val leaves: List<Pair<Either, SnailFish>> = root.createLeaves()
//            println("exploding: $this, idxs = [${this.left.idx},${this.right.idx}, leaves: ${leaves.count()}]")
//            println(leaves)
//            println("cleaned up:\n[${getRoot().toString().replace("[","").replace("]", "").replace(",", ", ")}]")

            val leftValue = left.value!!
            val rightValue = right.value!!

            // VANISH
            if (isLeftChild()) {
                parent?.left?.nested = null
                parent?.left?.value = 0
            } else if (isRightChild()) {
                parent?.right?.nested = null
                parent?.right?.value = 0
            } else throw IllegalStateException("cannot vanish")

            if (left.idx - 1 >= 0) {
                leaves[left.idx - 1].first.value = leaves[left.idx - 1].first.value!! + leftValue
            }
            if (right.idx + 1 < leaves.size) {
                leaves[right.idx + 1].first.value = leaves[right.idx + 1].first.value!! + rightValue
            }

        }

        /**
         * To split a regular number, replace it with a pair; the left element of the pair should be
         * the regular number divided by two and rounded down, while the right element of the pair
         * should be the regular number divided by two and rounded up. For example,
         * 10 becomes [5,5], 11 becomes [5,6], 12 becomes [6,6], and so on.
         *
         * If any regular number is 10 or greater, the leftmost such regular number splits.
         */
        fun split() {
            println("splitting: $this")
            val leaves = getRoot().createLeaves()
            println("leaves: $leaves")
//            val checkToSplit = leaves.first { it.first.value!! >= 10 }
//            if (checkToSplit != this.left && checkToSplit != this.right) throw IllegalStateException("splitting $this instead of $checkToSplit")

            val split = if (left.isAtLeastTenNumber()) {
                val leftVal = floor(left.value!! / 2.0).toInt()
                val rightVal = ceil(left.value!! / 2.0).toInt()
                SnailFish(Either(leftVal), Either(rightVal))
            } else {
                val leftVal = floor(right.value!! / 2.0).toInt()
                val rightVal = ceil(right.value!! / 2.0).toInt()
                SnailFish(Either(leftVal), Either(rightVal))
            }

            if (left.isAtLeastTenNumber()) {
                left.nested = split
                left.value = null
            } else {
                right.nested = split
                right.value = null
            }
            split.parent = this
//            println("split $this")
        }

        private fun createLeaves(): List<Pair<Either, SnailFish>> {
            val leaves = mutableListOf<Pair<Either, SnailFish>>()
            if (left.isNumber()) {
                leaves.add(Pair(left, this))
            } else {
                left.nested?.addLeaves(leaves)
            }
            if (right.isNumber()) {
                leaves.add(Pair(right, this))
            } else {
                right.nested?.addLeaves(leaves)
            }
            leaves.indices.forEach { idx -> leaves[idx].first.idx = idx }
            return leaves
        }

        private fun addLeaves(leaves: MutableList<Pair<Either, SnailFish>>) {
            if (left.isNumber()) {
                leaves.add(Pair(left, this))
            } else {
                left.nested?.addLeaves(leaves)
            }
            if (right.isNumber()) {
                leaves.add(Pair(right, this))
            } else {
                right.nested?.addLeaves(leaves)
            }
        }

        private fun getRoot(): SnailFish {
            var root = this
            while (root.parent != null) root = root.parent!!
            return root
        }

        operator fun plus(summand: SnailFish): SnailFish {
            println("    $this")
            println("+   $summand")
            val result = SnailFish(Either(this), Either(summand))
            result.updateDepth(0, null)
            println("=   $result (before reduce)")
            result.reduce()
            result.updateDepth(0, null)
            println("=   $result")
            println()
            return result
        }

        override fun toString(): String = "[$left,$right]"

        fun findToExplode(): SnailFish? {
            if (depth == 4) return this
            return left.nested?.findToExplode() ?: right.nested?.findToExplode()
        }

        fun findToSplit(): SnailFish? {
            val leaves = createLeaves()
            val toSplit = leaves.firstOrNull { it.first.value!! >= 10 }
            return toSplit?.second
//            if (left.isAtLeastTenNumber() || right.isAtLeastTenNumber()) return this
//            return left.nested?.findToSplit() ?: right.nested?.findToSplit()
        }

        /**
         * The magnitude of a pair is 3 times the magnitude
         * of its left element plus 2 times the magnitude
         * of its right element.
         * The magnitude of a regular number is just that number.
         */
        fun getMagnitude(): Int {
            val leftMagnitude = if (left.isNumber()) left.value!! else left.nested!!.getMagnitude()
            val rightMagnitude = if (right.isNumber()) right.value!! else right.nested!!.getMagnitude()
            return 3 * leftMagnitude + 2 * rightMagnitude
        }
    }

    sealed class Input {
        fun isReadyToMerge() = isParsed() || isNotParsedNumber()
        private fun isParsed() = this is Parsed
        private fun isNotParsedNumber() = this is NotParsed && numberRegex.matches(this.symbol)
        abstract fun painted(): String
    }

    data class Parsed(val snailFish: SnailFish) : Input() {
        override fun painted(): String = "$ANSI_GREEN$snailFish$ANSI_RESET"
        override fun toString(): String = "$snailFish"
    }

    data class NotParsed(val symbol: String) : Input() {
        override fun painted(): String = "$ANSI_YELLOW$symbol$ANSI_RESET"
        override fun toString(): String = symbol
    }

    data class Either(var value: Int? = null, var nested: SnailFish? = null) {
        var idx = -1

        constructor(value: Int) : this(value, nested = null)
        constructor(nested: SnailFish) : this(value = null, nested)

        override fun toString(): String {
            if (value != null && nested != null) throw IllegalStateException("value and nested cannot exists together")
            return if (value != null) "$value"
            else "$nested"
        }

        fun isNumber() = value != null
        fun isAtLeastTenNumber(): Boolean = nested == null && (value ?: 0) >= 10
    }

    override fun solvePartTwo(lines: List<String>) {

    }
}