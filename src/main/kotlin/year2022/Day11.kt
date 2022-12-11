package year2022

import aoc.IAocTaskKt

class Day11 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_11_test.txt"

    override fun solvePartOne(lines: List<String>) {
        solve(lines, 20, 3L)
    }

    private fun solve(lines: List<String>, rounds: Long, divisor: Long) {
        val groups = lines.chunked(7)
            .map { it.filter { line -> line.isNotEmpty() } }
            .map { it.map { line -> line.trim() } }
            .also { println(it) }

        val monkeys = groups.map { Monkey.parse(it) }
        monkeys.onEach { println(it) }
        val idToMonkey = monkeys.groupBy { it.id }
            .mapValues { entry -> entry.value.first() }

        for (round in 1..rounds) {
            monkeys.onEach { it.act(idToMonkey, divisor) }
        }

        val result = monkeys.map { it.getInspectionsCount() }
            .sortedDescending()
            .subList(0, 2)
            .onEach { print("$it ") }
            .fold(1L) { acc, x -> acc * x }
        println(result)
    }

    override fun solvePartTwo(lines: List<String>) {
        solve(lines, 10000, 1)
    }

    data class Monkey(val id: Long, val items: MutableList<MonkeyItem>, val operation: MonkeyOperation, val test: MonkeyTest, val yeet: MonkeyYeet) {
        private var inspectionCounter = 0L

        fun act(idToMonkey: Map<Long, Monkey>, divisor: Long) {
            for (item in items) {
                item.worryLevel = if (divisor > 1) operation.execute(item.worryLevel) / divisor else operation.execute(item.worryLevel)
                inspectionCounter++
                val destinationMonkey = when (test.test(item.worryLevel)) {
                    true -> yeet.trueMonkey
                    false -> yeet.falseMonkey
                }.let { idToMonkey[it]!! }
                destinationMonkey.pass(item)
            }
            items.clear()
        }

        private fun pass(item: MonkeyItem) {
            items += item
        }

        fun getInspectionsCount(): Long = inspectionCounter
        companion object {
            fun parse(lines: List<String>): Monkey {
                val id = lines[0].replace("Monkey ", "")
                    .replace(":", "")
                    .toLong()
                val startingItems = lines[1].replace("Starting items: ", "")
                    .split(", ")
                    .map { it.toLong() }
                    .mapIndexed { idx, value -> MonkeyItem(id * 100 + idx, value)}
                    .toMutableList()

                val operation = MonkeyOperation.parse(lines[2])
                val test = MonkeyTest.parse(lines[3])
                val yeet = MonkeyYeet.parse(lines.subList(4, 6))
                return Monkey(id, startingItems, operation, test, yeet)
            }
        }
    }

    data class MonkeyItem(val id: Long, var worryLevel: Long)
    data class MonkeyOperation(val b: Long, val type: String) {
        fun execute(old: Long): Long = when (type) {
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
                    val b = operationRhs.split(" * ")[1].toLong()
                    MonkeyOperation(b, "*")
                } else {
                    val b = operationRhs.split(" + ")[1].toLong()
                    MonkeyOperation(b, "+")
                }
            }
        }
    }

    data class MonkeyTest(val divisor: Long) {
        fun test(value: Long): Boolean = value % divisor == 0L

        companion object {
            fun parse(testLine: String): MonkeyTest {
                val divisor = testLine.replace("Test: divisible by ", "").toLong()
                return MonkeyTest(divisor)
            }
        }
    }
    data class MonkeyYeet(val trueMonkey: Long, val falseMonkey: Long) {
        companion object {
            fun parse(yeetLines: List<String>): MonkeyYeet {
                val trueMonkey = yeetLines[0].replace("If true: throw to monkey ", "").toLong()
                val falseMonkey = yeetLines[1].replace("If false: throw to monkey ", "").toLong()
                return MonkeyYeet(trueMonkey, falseMonkey)
            }
        }
    }
}