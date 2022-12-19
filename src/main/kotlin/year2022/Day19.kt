package year2022

import aoc.IAocTaskKt

const val ORE = "ore"
const val CLAY = "clay"
const val OBSIDIAN = "obsidian"
const val GEODE = "geode"
val MINERAL_TO_WEIGHT = mapOf(
    ORE to 1,
    CLAY to 100,
    OBSIDIAN to 10000,
    GEODE to 100000000
)

class Day19 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_19.txt"
    // 1881 is too high

    override fun solvePartOne(lines: List<String>) {
        val blueprints = lines.map { Blueprint.parse(it) }
        blueprints.onEach { blueprint ->
            println(blueprint.id)
            blueprint.robotTypeToCost.onEach { println("\t$it") }
        }
//        val maxTime = 24 // TODO
        val maxTime = 24
        val qualityLevels = blueprints
//            .subList(0, 1) // TODO
            .map { blueprint ->
                val productionState = ProductionState(
                    blueprint = blueprint,
                    moneyAvailable = mutableMapOf(),
                    robotsAvailable = ProductionState.createInitialRobotsAvailability(),
                    notReadyRobots = emptyList(),
                    timeElapsed = 0,
                    maxTime = maxTime,
                    history = emptyList(),
                )

                val productionStates = mutableListOf(productionState)
                val maxStatesStored = 1000

                while (productionStates.all { it.timeElapsed < maxTime }) {
                    val newProductStates = mutableListOf<ProductionState>()
                    for (state in productionStates) {
                        if (newProductStates.size < maxStatesStored) {
                            newProductStates.addAll(state.generateNextStates().sorted())
                        }
                    }
                    productionStates.addAll(newProductStates)
                    val incremented = productionStates.flatMap(ProductionState::produce).sorted()
                    productionStates.clear()
                    productionStates.addAll(incremented)
//                    println(productionStates.first())
//                    productionStates.onEach { println("\t$it") }
                }
                productionStates.sortByDescending { it.getCollectedMineralCount(GEODE) }

                val bestState = productionStates.first()
                println("history: ")
                val maxGeodeMined = bestState.getCollectedMineralCount(GEODE)
                bestState.history.onEach {
                    println("$it")
                }
                println("best state found")
                println(bestState)
//                println(bestState.geodePerRound)
                println("mined:$maxGeodeMined * id:${blueprint.id} = ${maxGeodeMined * blueprint.id}")
                maxGeodeMined * blueprint.id
            }
        val qualityLevelsSum = qualityLevels
            .onEach { println(it) }
            .sumOf { it }
        println(qualityLevelsSum)
    }

    override fun solvePartTwo(lines: List<String>) {
        TODO("Not yet implemented")
    }

    data class Blueprint(val id: Int, val robotTypeToCost: Map<String, Cost>) {
        fun buy(
            howManyPerTypeCanBuy: Map<String, Int>,
            moneyAvailable: Map<String, Money>,
            robotTypeToCost: Map<String, Cost>,
        ): List<Pair<List<Robot>, MutableMap<String, Money>>> {
            val buys = mutableListOf<Pair<List<Robot>, MutableMap<String, Money>>>()

            repeat(howManyPerTypeCanBuy[ORE]!! + 1) { oreBuyCount ->
                repeat(howManyPerTypeCanBuy[CLAY]!! + 1) { clayBuyCount ->
                    repeat(howManyPerTypeCanBuy[OBSIDIAN]!! + 1) { obsidianBuyCount ->
                        repeat(howManyPerTypeCanBuy[GEODE]!! + 1) { geodeBuyCount ->
                            val robotToBuyCount = mapOf(
                                ORE to oreBuyCount,
                                CLAY to clayBuyCount,
                                OBSIDIAN to obsidianBuyCount,
                                GEODE to geodeBuyCount,
                            )
                            if (robotToBuyCount.any { it.value > 0 }) {
                                val newMoneyAvailable = mutableMapOf<String, Money>()
                                moneyAvailable.forEach { (currency, money) -> newMoneyAvailable[currency] = money }

                                val producedRobots = mutableListOf<Robot>()

                                robotToBuyCount.filter { (_, buyCount) -> buyCount > 0 }.forEach { (type, buyCount) ->
                                    val robotCost = robotTypeToCost[type]!!
                                    robotCost.cost.forEach { amountCurrency ->
                                        newMoneyAvailable[amountCurrency.currency] =
                                            newMoneyAvailable[amountCurrency.currency]!! - amountCurrency * buyCount
                                    }
                                    val newRobot = Robot(type, buyCount)
                                    producedRobots += newRobot.let { (1..buyCount).map { Robot(newRobot.type, 1) } }
                                }

                                val validBudget = newMoneyAvailable.all { (_, money) -> money.amount >= 0 }
                                if (validBudget) {
                                    buys.add(Pair(producedRobots, newMoneyAvailable))
                                }
                            }
                        }
                    }
                }
            }

//            println("bought robot: $newRobot, transaction: $moneyAvailable --> $newMoneyAvailable")
            return buys
        }

        companion object {
            fun parse(line: String): Blueprint {
                val (blueprintStr, eachRobotsStrings) = line.split(": ")
                val id = blueprintStr.replace("Blueprint ", "").toInt()
                val robotTypeToCost = eachRobotsStrings.split(". ")
                    .map { eachRobotString ->
                        eachRobotString.replace("Each ", "")
                            .replace(" robot ", " ")
                            .replace("costs ", "")
                            .replace(" and ", " ")
                            .replace(".", "")
                            .split(" ")
                    }.map { robotTypeCostsStr ->
                        val robotType = robotTypeCostsStr[0]
                        val costsString = robotTypeCostsStr.subList(1, robotTypeCostsStr.size)
                        Pair(robotType, costsString)
                    }.map { (robotType, costsStr) ->
                        val moneys = costsStr.windowed(2, 2).map { valueCost ->
                            valueCost.zipWithNext().single()
                        }.map { (value, cost) -> Money(value.toInt(), cost) }
                        Pair(robotType, Cost(moneys))
                    }.groupBy { (robotType, _) -> robotType }
                    .mapValues { entry -> entry.value.map { (_, costs) -> costs }.first() }

                return Blueprint(id, robotTypeToCost)
            }
        }
    }

    data class Cost(val cost: List<Money>) {
        fun howManyCanBuy(moneyAvailable: Map<String, Money>): Int {
            val howManyPerCurrency = cost.map { (amount, currency) ->
                val available = moneyAvailable.getOrDefault(currency, Money(0, currency))
                Pair(currency, if (available.amount == 0) return 0 else available.amount / amount)
            }
            return howManyPerCurrency.minOf { (_, times) -> times }
        }
    }

    data class Money(val amount: Int, val currency: String) {
        operator fun plus(money: Money): Money {
            if (currency != money.currency) throw IllegalStateException("wrong currency: ${money.currency}, expected $currency")
            return Money(amount + money.amount, currency)
        }

        operator fun minus(money: Money): Money {
            return Money(amount - money.amount, currency)
        }

        operator fun compareTo(money: Money): Int = amount.compareTo(money.amount)
        override fun toString(): String {
            return "$amount 💲$currency"
        }

        operator fun times(multiplier: Int): Money = Money(amount * multiplier, currency)
    }

    data class Robot(val type: String, val resourcePerMinute: Int) {
        operator fun plus(newRobot: Robot): Robot {
            if (type != newRobot.type) throw IllegalStateException("robot types must be the same")
            return Robot(type, resourcePerMinute + newRobot.resourcePerMinute)
        }

        override fun toString(): String = "🤖($resourcePerMinute $type/min)"
    }

    data class ProductionState(
        val blueprint: Blueprint,
        val moneyAvailable: Map<String, Money>,
        val robotsAvailable: Map<String, Robot>,
        val notReadyRobots: List<Robot>,
        val timeElapsed: Int, val maxTime: Int,
        val history: List<ProductionState>,
    ) : Comparable<ProductionState> {
        fun getCollectedMineralCount(type: String): Int {
            return moneyAvailable.getOrDefault(type, Money(0, type)).amount
        }

        fun generateNextStates(): List<ProductionState> {
            val nextStates = mutableListOf<ProductionState>()

            val howManyPerTypeCanBuy = blueprint.robotTypeToCost.mapValues { (_, cost) ->
                cost.howManyCanBuy(moneyAvailable)
            }
            val possibleBuys = blueprint.buy(
                howManyPerTypeCanBuy,
                moneyAvailable,
                blueprint.robotTypeToCost,
            )
            possibleBuys.forEach { (notReadyRobots, newMoneyAvailable) ->
                nextStates += ProductionState(
                    blueprint,
                    newMoneyAvailable,
                    robotsAvailable,
                    notReadyRobots = notReadyRobots,
                    timeElapsed,
                    maxTime,
                    history,
                )
            }
            return nextStates
        }

        companion object {
            fun createInitialRobotsAvailability() = mutableMapOf(
                ORE to Robot(ORE, resourcePerMinute = 1),
                CLAY to Robot(CLAY, resourcePerMinute = 0),
                OBSIDIAN to Robot(OBSIDIAN, resourcePerMinute = 0),
                GEODE to Robot(GEODE, resourcePerMinute = 0)
            )
        }

        override fun compareTo(other: ProductionState): Int {
            if (timeElapsed != other.timeElapsed) throw IllegalStateException("cannot compare different times")

            val worth = moneyAvailable.entries.sumOf { (type, money) -> MINERAL_TO_WEIGHT[type]!! * money.amount } +
                    robotsAvailable.entries.sumOf { (type, robot) -> MINERAL_TO_WEIGHT[type]!! * robot.resourcePerMinute }
            val otherWorth =
                other.moneyAvailable.entries.sumOf { (type, money) -> MINERAL_TO_WEIGHT[type]!! * money.amount } +
                        other.robotsAvailable.entries.sumOf { (type, robot) -> MINERAL_TO_WEIGHT[type]!! * robot.resourcePerMinute }
            return otherWorth.compareTo(worth)
        }


        fun produce(): List<ProductionState> {
            if (timeElapsed == 24) return listOf(this)
            val newMoneyAvailable = mutableMapOf<String, Money>()

            val newNotReadyRobots = notReadyRobots
                .sortedByDescending { MINERAL_TO_WEIGHT[it.type]!! }
                .toMutableList()
            val robotToBuild = newNotReadyRobots.removeFirstOrNull()

            for ((type, robot) in robotsAvailable) {
                if (robot.resourcePerMinute != 0) {
                    val oldMoneyAvailable = moneyAvailable.getOrDefault(type, Money(0, type))
                    newMoneyAvailable.putIfAbsent(type, oldMoneyAvailable)
                    newMoneyAvailable[type] = newMoneyAvailable[type]!! + Money(robot.resourcePerMinute, type)
                }
            }
            val newAvailableRobots = robotsAvailable
                .mapValues { (_, value) -> value.copy() }
                .toMutableMap()

            if (robotToBuild != null) {
                newAvailableRobots[robotToBuild.type] = robotsAvailable[robotToBuild.type]!! + robotToBuild
            }

            return listOf(ProductionState(
                blueprint,
                newMoneyAvailable,
                newAvailableRobots,
                newNotReadyRobots,
                timeElapsed + 1,
                maxTime,
                history + this,
            )) // TODO more states according to the selected robots to produce the other robots
        }

        override fun toString(): String {
            return "State(⌚=$timeElapsed/$maxTime, ${blueprint.id}, 🏦: ${moneyAvailable.values}, 🏭: =${robotsAvailable.values})"
        }
    }
}