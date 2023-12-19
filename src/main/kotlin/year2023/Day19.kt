package year2023

import aoc.IAocTaskKt
import java.util.function.Predicate

class Day19 : IAocTaskKt {
    private val labelToWorkflow: MutableMap<String, Workflow> = mutableMapOf()

    override fun getFileName(): String = "aoc2023/input_19.txt"

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
        println("pt 2")
    }

    data class Workflow(val name: String, val rules: List<Rule>) {
        fun getNextWorkflowName(part: Part): String {
            return rules.first { rule -> rule.predicate.test(part) }.nextWorkflow
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

    data class Rule(val predicate: Predicate<Part>, val nextWorkflow: String) {
        companion object {
            fun parse(input: String): Rule {
                println("parsing: $input")
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
                    Rule({ true }, input)
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
                return Rule(predicate, dest)
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

    enum class Category {
        x, m, a, s
    }
}
