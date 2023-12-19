package year2023

import aoc.IAocTaskKt
import java.util.function.Predicate
import kotlin.math.max
import kotlin.math.min

class Day19 : IAocTaskKt {
    private val labelToWorkflow: MutableMap<String, Workflow> = mutableMapOf()

    override fun getFileName(): String = "aoc2023/input_19.txt"
    // 126090293754249 --> too low

    override fun solvePartOne(lines: List<String>) {
        val partsStartIdx = lines.indexOfFirst { it.isBlank() } + 1
        val workflows = lines.subList(0, partsStartIdx - 1)
            .map(Workflow::parse)
        val parts = lines.subList(partsStartIdx, lines.size)
            .map(Part::parse)

        for (workflow in workflows) {
            labelToWorkflow[workflow.name] = workflow
        }

        val startWorkflow = "in"
        val acceptedParts: MutableList<Part> = mutableListOf()
        for (part in parts) {
            var currentWorkflowName = startWorkflow
            while (currentWorkflowName !in setOf("A", "R")) {
                val workflow = labelToWorkflow[currentWorkflowName]!!
                val nextWorkflow: String = workflow.getNextWorkflowName(part)
                currentWorkflowName = nextWorkflow
            }
            if (currentWorkflowName == "A") {
                acceptedParts += part
            }
        }

        println(workflows)
        println(parts)
        println(acceptedParts.sumOf { it.score() })
    }

    override fun solvePartTwo(lines: List<String>) {
        val partsStartIdx = lines.indexOfFirst { it.isBlank() } + 1
        val workflows = lines.subList(0, partsStartIdx - 1)
            .map(Workflow::parse)

        for (workflow in workflows) {
            labelToWorkflow[workflow.name] = workflow
        }
        // TODO count combinations
        val start = "in"
        val multiParts = mutableMapOf<List<String>, MutableList<MultiPart>>()
        val path = listOf(start)
        val startPart = MultiPart.start()

        search(start, startPart, multiParts, path)

        // test 167409079868000
        val result = multiParts
            .onEach { println(it) }
            .values
            .map { parts ->
                parts.sumOf { it.partsCount() }
            }
            .sumOf { it }
        println(result)
    }

    private fun search(
        workflowName: String,
        multiPart: MultiPart,
        multiParts: MutableMap<List<String>, MutableList<MultiPart>>,
        path: List<String>,
    ) {
        if (multiPart.ignore) {
            return
        }
        if (workflowName == "A") {
            println(path)
            if (multiParts.containsKey(path)) {
                multiParts[path]!!.add(multiPart)
            } else {
                multiParts[path] = mutableListOf(multiPart)
            }
            return
        } else if (workflowName == "R") {
            return
        }
        val workflow = labelToWorkflow[workflowName]!!
        val nextMoves: List<Pair<String, MultiPart>> = workflow.divide(multiPart)
        println("next moves $nextMoves")
        for ((nextWorkflowName, nextMultiPart) in nextMoves) {
            search(nextWorkflowName, nextMultiPart, multiParts, path + nextWorkflowName)
        }
    }

    data class Workflow(val name: String, val rules: List<Rule>) {
        fun getNextWorkflowName(part: Part): String {
            return rules.first { rule -> rule.predicate.test(part) }.nextWorkflow
        }

        fun divide(multiPart: MultiPart): List<Pair<String, MultiPart>> {
            val divided = mutableListOf<Pair<String, MultiPart>>()
            var currentMultiPart = multiPart.copy()
            for (rule in rules) {
                val (accepted, notAccepted) = rule.divide(currentMultiPart)
                divided.add(Pair(rule.nextWorkflow, accepted))
                currentMultiPart = notAccepted
            }
            return divided
        }

        companion object {
            fun parse(line: String): Workflow {
                val (label, rest1) = line.replace("}", "")
                    .split("{").filter(String::isNotEmpty)
                val rulesRaw = rest1.split(",").filter(String::isNotEmpty)
                val rules = rulesRaw.map(Rule::parse)
                return Workflow(label, rules)
            }
        }
    }

    data class Rule(
        val predicate: Predicate<Part>,
        val nextWorkflow: String,
        val category: Category?,
        val acceptedRange: Range?,
    ) {
        fun divide(toDivide: MultiPart): Pair<MultiPart, MultiPart> {
            if (category == null) {
                return Pair(toDivide, toDivide.copy(ignore = true))
            }
            return toDivide.divide(category, acceptedRange!!)
        }

        companion object {
            fun parse(input: String): Rule {
                return if ("<" in input) {
                    val (category, scoreDest) = input.split("<").filter(String::isNotEmpty)
                    val (scoreRaw, dest) = scoreDest.split(":").filter(String::isNotEmpty)
                    val score = scoreRaw.toInt()
                    createFrom(category, "<", score, dest)
                } else if (">" in input) {
                    val (category, scoreDest) = input.split(">").filter(String::isNotEmpty)
                    val (scoreRaw, dest) = scoreDest.split(":").filter(String::isNotEmpty)
                    val score = scoreRaw.toInt()
                    createFrom(category, ">", score, dest)
                } else {
                    Rule({ true }, input, null, Range.max())
                }
            }

            private fun createFrom(catRaw: String, symbol: String, score: Int, dest: String): Rule {
                val category = Category.valueOf(catRaw)
                val predicate = when (category) {
                    Category.x -> if (symbol == "<") Predicate<Part> { p -> p.x < score } else Predicate<Part> { p -> p.x > score }
                    Category.m -> if (symbol == "<") Predicate<Part> { p -> p.m < score } else Predicate<Part> { p -> p.m > score }
                    Category.a -> if (symbol == "<") Predicate<Part> { p -> p.a < score } else Predicate<Part> { p -> p.a > score }
                    Category.s -> if (symbol == "<") Predicate<Part> { p -> p.s < score } else Predicate<Part> { p -> p.s > score }
                }
                val range = if (symbol == "<") Range(1, score - 1) else Range(score + 1, 4000)
                return Rule(predicate, dest, category, range)
            }
        }
    }

    data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {
        fun score(): Int {
            return x + m + a + s
        }

        companion object {
            fun parse(line: String): Part {
                val (x, m, a, s) = line.replace("{", "")
                    .replace("}", "")
                    .split(",")
                    .filter(String::isNotEmpty)
                    .map { letterEqualsScore ->
                        letterEqualsScore.substring(2)
                            .toInt()
                    }
                return Part(x, m, a, s)
            }
        }
    }

    data class MultiPart(val x: Range, val m: Range, val a: Range, val s: Range, val ignore: Boolean = false) {
        fun partsCount(): Long {
            return x.size() * m.size() * a.size() * s.size()
        }

        override fun toString(): String {
            return "(x=$x, m=$m, a=$a, s=$s)"
        }

        fun divide(category: Category, acceptedRange: Range): Pair<MultiPart, MultiPart> {
            val rangeToUpdate = when (category) {
                Category.x -> x
                Category.m -> m
                Category.a -> a
                Category.s -> s
            }
            val (accepted, rejected) = if(acceptedRange.isGreaterThanRange()) {
                Pair( // 300..4000
                    Range(max(rangeToUpdate.from, acceptedRange.from), rangeToUpdate.to),
                    Range(rangeToUpdate.from, min(rangeToUpdate.to, acceptedRange.from - 1))
                )
            } else if (acceptedRange.isLessThanRange()){
                Pair( // 1..200
                    Range(rangeToUpdate.from, min(rangeToUpdate.to, acceptedRange.to)),
                    Range(max(rangeToUpdate.from, acceptedRange.to + 1), rangeToUpdate.to)
                )
            } else throw IllegalStateException()
            val ignoreAccepted = accepted.isInvalid()
            val ignoreRejected = rejected.isInvalid()

            if (ignoreAccepted) println("ignore accepted $accepted")
            if (ignoreRejected) println("ignore rejected $rejected")

            return when (category) {
                Category.x -> Pair(copy(x = accepted), copy(x = rejected))
                Category.m -> Pair(copy(m = accepted), copy(m = rejected))
                Category.a -> Pair(copy(a = accepted), copy(a = rejected))
                Category.s -> Pair(copy(s = accepted), copy(s = rejected))
            }.let {
                val (acc, rej) = it
                val result = Pair(acc.copy(ignore = ignoreAccepted), rej.copy(ignore = ignoreRejected))
                println("dividing $rangeToUpdate by ($category) $acceptedRange  --> $result")
                result
            }
        }


        companion object {
            fun start(): MultiPart {
                return MultiPart(
                    x = Range.max(),
                    m = Range.max(),
                    a = Range.max(),
                    s = Range.max(),
                )
            }
        }
    }

    data class Range(val from: Int, val to: Int) {
        fun size(): Long = to.toLong() - from + 1
        fun isGreaterThanRange() = to == 4000
        override fun toString(): String {
            return "$from..$to"
        }

        fun isInvalid(): Boolean {
            return from > to || from < 0 || to > 4000
        }

        fun isLessThanRange(): Boolean {
            return from == 1
        }


        companion object {
            fun max(): Range {
                return Range(1, 4000)
            }
        }
    }

    enum class Category {
        x, m, a, s
    }
}
