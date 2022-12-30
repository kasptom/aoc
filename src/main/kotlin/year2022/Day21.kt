package year2022

import aoc.IAocTaskKt
import year2022.Day21.Monkey.Operation.*
import java.math.BigDecimal

const val HUMAN = "humn"
class Day21 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_21.txt"

    override fun solvePartOne(lines: List<String>) {
        val (nameToMonkey, root) = getMonkeys(lines)
        root.calculate(nameToMonkey)
        println(root.result)
    }

    override fun solvePartTwo(lines: List<String>) {
        val (nameToMonkey, root) = getMonkeys(lines)
        root.calculate(nameToMonkey)
        root.calculate2(nameToMonkey)

        val x = root.findX(nameToMonkey)
        checkIfCorrect(lines, x)
        println(x)
    }

    private fun getMonkeys(lines: List<String>): Pair<Map<String, Monkey>, Monkey> {
        val monkeys = lines.map { Monkey.parse(it) }
        val nameToMonkey = monkeys.groupBy { it.name }.mapValues { (_, v) -> v.single() }
        val root = nameToMonkey["root"]!!
        return Pair(nameToMonkey, root)
    }

    private fun checkIfCorrect(lines: List<String>, x: Long) {
        val (nameToMonkey, root) = getMonkeys(lines)
        nameToMonkey[HUMAN]!!.result = x

        val (left, right) = root.getChildrenMonkeys(nameToMonkey)
        left.calculate(nameToMonkey)
        right.calculate(nameToMonkey)
        println("left: ${left.result}, right: ${right.result} ${if (left.result == right.result) "OK!" else "NOT OK!"}")
    }

    data class Monkey(
        val name: String,
        var result: Long?,
        val operation: Operation,
        val children: List<String>,
        var result2: Result? = null,
    ) {
        fun calculate(nameToMonkey: Map<String, Monkey>) {
            if (operation == YELL) return
            val childrenMonkeys = getChildrenMonkeys(nameToMonkey)
            for (child in childrenMonkeys) {
                if (child.result == null) child.calculate(nameToMonkey)
            }
            val childrenResults = childrenMonkeys.map { it.result!! }
            result = when (operation) {
                MULTIPLY -> childrenResults.reduce { a, b -> a * b }
                DIVIDE -> childrenResults.reduce { a, b -> a / b }
                ADD -> childrenResults.sum()
                SUBTRACT -> childrenResults.reduce { a, b -> a - b }
                else -> return
            }
        }

        fun calculate2(nameToMonkey: Map<String, Monkey>) {
            if (operation == YELL) {
                result2 = Result(
                    YELL,
                    value = result!!.toBigDecimal(),
                    variable = name == HUMAN,
                    hasVariable = name == HUMAN,
                    simple = result!!.toBigDecimal()
                )
                return
            }
            val (left, right) = getChildrenMonkeys(nameToMonkey)

            if (left.result2 == null) left.calculate2(nameToMonkey)
            if (right.result2 == null) right.calculate2(nameToMonkey)

            result2 = when (operation) {
                MULTIPLY -> left.result2!! * right.result2!!
                DIVIDE -> left.result2!! / right.result2!!
                ADD -> left.result2!! + right.result2!!
                SUBTRACT -> left.result2!! - right.result2!!
                else -> return
            }
            result2!!.hasVariable = left.result2!!.hasVariable || right.result2!!.hasVariable
            result2!!.simple = result!!.toBigDecimal()
        }


        fun getChildrenMonkeys(nameToMonkey: Map<String, Monkey>): List<Monkey> {
            return children.map { nameToMonkey[it]!! }
        }

        fun findX(nameToMonkey: Map<String, Monkey>): Long {
            val (left, right) = getChildren(nameToMonkey)
            if (!left.hasSuccessor(HUMAN, nameToMonkey)) {
                left.result2 = Result(YELL, value = left.result!!.toBigDecimal())
            } else {
                right.result2 = Result(YELL, value = right.result!!.toBigDecimal())
            }

            var (lhs, rhs) = this.result2!!.getOperationSides()
            if (lhs.isSimple()) { // root is always sum
                lhs = Result(YELL, left.result!!.toBigDecimal())
            } else {
                rhs = Result(YELL, right.result!!.toBigDecimal())
            }
//            println("$lhs == $rhs")

            while ((lhs.isCompound() || rhs.isCompound()) && !rhs.variable && !lhs.variable) {
                if (lhs.isCompound()) {
                    val (innerLhs, innerRhs) = lhs.getOperationSides()
                    if (innerRhs.isSimple()) {
                        rhs = lhs.inverseOperation(innerRhs, rhs)
                        lhs = innerLhs
                    } else if (innerLhs.isSimple() && listOf(DIVIDE, SUBTRACT).contains(lhs.operation)) {
                        lhs = lhs.operation(innerLhs, rhs)
                        rhs = innerRhs
                    } else if (innerLhs.isSimple() && !listOf(DIVIDE, SUBTRACT).contains(lhs.operation)) {
                        lhs = lhs.inverseOperation(innerLhs, rhs)
                        rhs = innerRhs
                    } else throw IllegalStateException()
                    val pair = Pair(lhs, rhs)
                    lhs = pair.first
                    rhs = pair.second
                } else if (rhs.isCompound()) {
                    val (innerLhs, innerRhs) = rhs.getOperationSides()
                    if (innerRhs.isSimple()) {
                        lhs = rhs.inverseOperation(innerRhs, lhs)
                        rhs = innerLhs
                    } else if (innerLhs.isSimple() && listOf(DIVIDE, SUBTRACT).contains(rhs.operation)) {
                        val newRhs = rhs.operation(innerLhs, lhs)
                        lhs = innerRhs
                        rhs = newRhs
                    } else if (innerLhs.isSimple() && !listOf(DIVIDE, SUBTRACT).contains(rhs.operation)) {
                        rhs = rhs.inverseOperation(innerLhs, lhs)
                        lhs = innerRhs
                    } else throw IllegalStateException()
                    val pair = Pair(rhs, lhs)
                    lhs = pair.first
                    rhs = pair.second
                } else throw IllegalStateException()
//                println("$lhs == $rhs")
            }

            return if (lhs.variable) rhs.value!!.toLong() else lhs.value!!.toLong()
        }

        private fun hasSuccessor(nameToFind: String, nameToMonkey: Map<String, Monkey>): Boolean {
            if (name == nameToFind) return true
            if (children.isEmpty()) return false
            val (left, right) = getChildren(nameToMonkey)
            return left.hasSuccessor(nameToFind, nameToMonkey) || right.hasSuccessor(nameToFind, nameToMonkey)
        }

        private fun getChildren(nameToMonkey: Map<String, Monkey>) =
            children.map { nameToMonkey[it]!! }.zipWithNext().single()

        companion object {
            fun parse(line: String): Monkey {
                val (name, operation) = line.split(": ")
                val operationElements = operation.split(" ")
                if (operationElements.size == 1) return Monkey(
                    name,
                    result = operationElements[0].toLong(),
                    YELL,
                    emptyList()
                )
                val operationCode = operationElements[1].let { code -> Operation.values().first { it.code == code } }
                val childrenNames = listOf(operationElements[0], operationElements[2])
                return Monkey(name, null, operationCode, childrenNames)
            }
        }

        enum class Operation(val code: String? = null) {
            YELL, MULTIPLY("*"), DIVIDE("/"), ADD("+"), SUBTRACT("-")
        }
    }

    data class Result(
        var operation: Monkey.Operation,
        val value: BigDecimal? = null,
        val left: Result? = null,
        val right: Result? = null,
        val variable: Boolean = false,
        var hasVariable: Boolean = false,
        var simple: BigDecimal? = null,
    ) {
        operator fun times(b: Result): Result = Result(MULTIPLY, null, this, b)
        operator fun div(b: Result): Result = Result(DIVIDE, null, this, b)
        operator fun plus(b: Result): Result = Result(ADD, null, this, b)
        operator fun minus(b: Result): Result = Result(SUBTRACT, null, this, b)
        override fun toString(): String = when (operation) {
            YELL -> if (variable) "x=$value"
            else "$value"
            else -> "($left ${operation.code} $right)"
        }

        fun isSimple(): Boolean = operation == YELL && !variable && !hasVariable
        fun isCompound(): Boolean = operation != YELL || variable || hasVariable
        fun getOperationSides(): Pair<Result, Result> {
            val left = if (left!!.hasVariable || left.variable) left else Result(
                YELL,
                value = left.simple,
                simple = left.simple
            )
            val right = if (right!!.hasVariable || right.variable) right else Result(
                YELL,
                value = right.simple,
                simple = right.simple
            )
            return Pair(left, right)
        }

        fun inverseOperation(lhs: Result, rhs: Result): Result {
            if (this.isSimple()) throw IllegalStateException()
            if (lhs.value == null || rhs.value == null) throw IllegalStateException()
            val result = when (operation) {
                YELL -> throw IllegalStateException()
                MULTIPLY -> rhs.value / lhs.value
                DIVIDE -> rhs.value * lhs.value
                ADD -> rhs.value - lhs.value
                SUBTRACT -> rhs.value + lhs.value
            }
            return Result(YELL, result, simple = result)
        }

        fun operation(lhs: Result, rhs: Result): Result {
            if (this.isSimple()) throw IllegalStateException()
            if (lhs.value == null || rhs.value == null) throw IllegalStateException("Cannot divide ${lhs.value} by ${rhs.value}")
            val result = when (operation) {
                YELL -> throw IllegalStateException()
                MULTIPLY -> lhs.value * rhs.value
                DIVIDE -> {
                    // if (lhs.value % rhs.value != 0L) throw IllegalStateException()
                    lhs.value / rhs.value
                }

                ADD -> lhs.value + rhs.value
                SUBTRACT -> lhs.value - rhs.value
            }
            return Result(YELL, result, simple = result)
        }
    }
}