package year2022

import aoc.IAocTaskKt

class Day11 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_11.txt"

    override fun solvePartOne(lines: List<String>) {
        val monkeys = monkeys(lines)

        solve(monkeys, 20)
    }

    private fun solve(monkeys: List<Monkey>, rounds: Int, manageableWorryLevels: Boolean = true): Map<Int, Monkey> {
        val idToMonkey = monkeys.groupBy { it.id }
            .mapValues { entry -> entry.value.first() }

        for (round: Int in 1..rounds) {
            if (manageableWorryLevels) {
                monkeys.onEach { it.act(idToMonkey) }
            } else {
                monkeys.onEach { it.act2(idToMonkey) }
            }
//            if (setOf<Int>(1, 20).contains(round) || round % 1000 == 0) {
//            if (round == 1000 || round == 1 || round == 20) {
//                println("== After round $round ==")
//                monkeys.onEach { println("Monkey ${it.id} inspected items ${it.getInspectionsCount()} times.") }
//            }
        }

        val result = monkeys.map { it.getInspectionsCount() }
            .sortedDescending()
            .subList(0, 2)
//            .onEach { print("$it ") }
            .fold(1L) { acc, x -> acc * x }
        println(result)
        return idToMonkey
    }

    override fun solvePartTwo(lines: List<String>) {
        val monkeys = monkeys(lines)

        val possibleDivisors = monkeys.map { it.test.divisor }.toList()
        monkeys.forEach { it.setDivisorToReminder(possibleDivisors) }

        solve(monkeys, 10000, manageableWorryLevels = false)
    }

    private fun monkeys(lines: List<String>): List<Monkey> =
        lines.chunked(7)
            .map { it.filter { line -> line.isNotEmpty() } }
            .map { it.map { line -> line.trim() } }
//            .also { println(it) }
            .map { Monkey.parse(it) }

    data class Monkey(
        val id: Int,
        val items: MutableList<MonkeyItem>,
        val operation: MonkeyOperation,
        val test: MonkeyTest,
        val yeet: MonkeyYeet,
    ) {
        private var inspectionCounter = 0

        fun act(idToMonkey: Map<Int, Monkey>) {
            for (item in items) {
                item.worryLevel = operation.execute(item.worryLevel) / 3
                inspectionCounter++
                val destinationMonkey = when (test.test(item.worryLevel)) {
                    true -> yeet.trueMonkey
                    false -> yeet.falseMonkey
                }.let { idToMonkey[it]!! }
                destinationMonkey.pass(item)
            }
            items.clear()
        }

        fun act2(idToMonkey: Map<Int, Monkey>) {
            for (item in items) {
                operation.execute2(item)
                val destinationMonkey = when (test.test2(item)) {
                    true -> yeet.trueMonkey
                    false -> yeet.falseMonkey
                }.let { idToMonkey[it]!! }
                destinationMonkey.pass(item)
                inspectionCounter++
            }
            items.clear()
        }

        private fun pass(item: MonkeyItem) {
            items += item
        }

        fun getInspectionsCount(): Int = inspectionCounter
        fun setDivisorToReminder(possibleDivisors: List<Int>) {
            items.forEach { item -> item.setDivisorToReminder(possibleDivisors) }
        }

        companion object {
            fun parse(lines: List<String>): Monkey {
                val id = lines[0].replace("Monkey ", "")
                    .replace(":", "")
                    .toInt()
                val startingItems = lines[1].replace("Starting items: ", "")
                    .split(", ")
                    .map { it.toInt() }
                    .mapIndexed { idx, value -> MonkeyItem(id * 100 + idx, value) }
                    .toMutableList()

                val operation = MonkeyOperation.parse(lines[2])
                val test = MonkeyTest.parse(lines[3])
                val yeet = MonkeyYeet.parse(lines.subList(4, 6))
                return Monkey(id, startingItems, operation, test, yeet)
            }
        }
    }

    data class MonkeyItem(
        val id: Int, var worryLevel: Int
    ) {
        private val divisorToReminder = mutableMapOf<Int, Int>()
        fun setDivisorToReminder(possibleDivisors: List<Int>) {
            possibleDivisors.forEach { divisor -> divisorToReminder[divisor] = worryLevel % divisor }
        }

        operator fun plus(x: Int) = divisorToReminder.keys.forEach {
            divisorToReminder[it] = (divisorToReminder[it]!! + x) % it
        }

        operator fun times(x: Int) = divisorToReminder.keys.forEach {
            divisorToReminder[it] = (divisorToReminder[it]!! * x) % it
        }

        operator fun times(old: MonkeyItem) = divisorToReminder.keys.forEach {
            divisorToReminder[it] = (divisorToReminder[it]!! * old.divisorToReminder[it]!!) % it
        }

        fun isDivisibleBy(divisor: Int): Boolean = divisorToReminder[divisor]!! % divisor == 0
    }

    data class MonkeyOperation(val b: Int, val type: String) {
        fun execute(old: Int): Int = when (type) {
            "+" -> old + b
            "*" -> old * b
            "* old" -> old * old
            else -> throw IllegalStateException("unknown type: $type")
        }

        fun execute2(old: MonkeyItem) = when (type) {
            "+" -> old + b
            "*" -> old * b
            "* old" -> old * old
            else -> throw IllegalStateException("unknown type: $type")
        }

        companion object {
            fun parse(operationLine: String): MonkeyOperation {
                val operationRhs = operationLine.replace("Operation: new = ", "")
                return if (operationRhs == "old * old") {
                    MonkeyOperation(0, "* old")
                } else if (operationRhs.contains("*")) {
                    val b = operationRhs.split(" * ")[1].toInt()
                    MonkeyOperation(b, "*")
                } else {
                    val b = operationRhs.split(" + ")[1].toInt()
                    MonkeyOperation(b, "+")
                }
            }
        }
    }

    data class MonkeyTest(val divisor: Int) {
        fun test(value: Int): Boolean = value % divisor == 0
        fun test2(item: MonkeyItem): Boolean {
            return item.isDivisibleBy(divisor)
        }

        companion object {
            fun parse(testLine: String): MonkeyTest {
                val divisor = testLine.replace("Test: divisible by ", "").toInt()
                return MonkeyTest(divisor)
            }
        }
    }

    data class MonkeyYeet(val trueMonkey: Int, val falseMonkey: Int) {
        companion object {
            fun parse(yeetLines: List<String>): MonkeyYeet {
                val trueMonkey = yeetLines[0].replace("If true: throw to monkey ", "").toInt()
                val falseMonkey = yeetLines[1].replace("If false: throw to monkey ", "").toInt()
                return MonkeyYeet(trueMonkey, falseMonkey)
            }
        }
    }
}