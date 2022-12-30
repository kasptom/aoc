package year2022

import aoc.IAocTaskKt
import year2022.Day19.Blueprint.Companion.zeroRobotsMap
import year2022.Day19.Money.Mineral
import year2022.Day19.Money.Mineral.*
import java.util.*
import kotlin.math.min

const val MAX_STATES_STORED = 2000
val MOST_TO_LEAST_VALUABLE = listOf(GEODE, OBSIDIAN, CLAY, ORE)

class Day19 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_19.txt"

    override fun solvePartOne(lines: List<String>) {
        val maxTime = 24
        val blueprints = lines.map { Blueprint.parse(it, maxTime) }
//        blueprints.onEach { blueprint ->
//            println(blueprint.id)
//            blueprint.robotTypeToCost.onEach { println("\t$it") }
//        }
        val qualityLevels = getBlueprintToMaxGeods(blueprints, maxTime)
        val qualityLevelsSum = qualityLevels
            .sumOf { (id, maxGeodeMined) -> id * maxGeodeMined }
        println(qualityLevelsSum)
    }

    fun getBlueprintToMaxGeods(
        blueprints: List<Blueprint>,
        maxTime: Int,
    ) = blueprints
        .map { blueprint ->
            val productionState = ProductionState(
                blueprint = blueprint,
                earnedMinerals = MOST_TO_LEAST_VALUABLE.groupBy { it }
                    .mapValues { (k, _) -> Money(0, k) }
                    .toMutableMap(),
                robotsAvailable = ProductionState.createInitialRobotsAvailability(),
                notReadyRobots = zeroRobotsMap(),
                timeElapsed = 0,
                maxTime = maxTime,
                history = emptyList(),
            )

            val productionStates = mutableListOf(productionState)
            val bestToWorst: TreeSet<ProductionState> = TreeSet<ProductionState>(ProductionState::compareTo)

            while (productionStates.all { it.timeElapsed < maxTime }) {
//                val selectedNextStates = mutableListOf<ProductionState>()
                for (state in productionStates) {
                    val nextStates = state.generateNextStates().flatMap { it.produce() }
                    for (nextState in nextStates) {
                        bestToWorst.add(nextState)
                    }
                }
                productionStates.clear()
                productionStates.addAll(bestToWorst.toList().subList(0, min(bestToWorst.size, MAX_STATES_STORED)))
//                println(productionStates.size)
//                println("best: ${productionStates.first()}")
            }
            productionStates.sortByDescending { it.getCollectedMineralCount(GEODE) }

            val bestState = productionStates.first()
//            println("history: ")
            val maxGeodeMined = bestState.getCollectedMineralCount(GEODE)
//            bestState.history.onEach {
//                println("$it")
//            }
//            println("best state found")
//            println(bestState)
//            println("mined:$maxGeodeMined * id:${blueprint.id} = ${maxGeodeMined * blueprint.id}")
            Pair(blueprint.id, maxGeodeMined)
        }

    override fun solvePartTwo(lines: List<String>) {
        val maxTime = 32
        val notEatenBlueprints = lines.map { Blueprint.parse(it, maxTime) }.subList(0, min(3, lines.size))
        val qualityLevels = getBlueprintToMaxGeods(notEatenBlueprints, maxTime)
        val qualityLevelsSum = qualityLevels
            .onEach { println(it) }
            .map { (_, maxGeod) -> maxGeod }
            .fold(1) { a, b -> a * b }
        println(qualityLevelsSum)
    }

    data class Blueprint(val id: Int, val robotTypeToCost: Map<Mineral, Cost>, val maxTime: Int) {
        val oreCalculator = ScoreCalculator(robotTypeToCost, maxTime)

        fun buy(
            howManyPerTypeCanBuy: Map<Mineral, Int>,
            moneyAvailable: Map<Mineral, Money>,
            robotTypeToCost: Map<Mineral, Cost>,
        ): List<Pair<Map<Mineral, Robot>, Map<Mineral, Money>>> {
            val buys = mutableListOf<Pair<Map<Mineral, Robot>, Map<Mineral, Money>>>()

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
                                val newMoneyAvailable = mutableMapOf<Mineral, Money>()
                                moneyAvailable.forEach { (currency, money) -> newMoneyAvailable[currency] = money }

                                val producedRobots = zeroRobotsMap()

                                robotToBuyCount.filter { (_, buyCount) -> buyCount > 0 }.forEach { (type, buyCount) ->
                                    val robotCost = robotTypeToCost[type]!!
                                    robotCost.cost.forEach { (_, money) ->
                                        newMoneyAvailable[money.currency] =
                                            newMoneyAvailable[money.currency]!! - money * buyCount
                                    }
                                    val newRobot = Robot(type, buyCount)
                                    producedRobots[type] = producedRobots[type]!! + newRobot
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
            fun parse(line: String, maxTime: Int): Blueprint {
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
                        }.map { (value, cost) -> Money(value.toInt(), Mineral.valueOf(cost.uppercase())) }
                            .groupBy { it.currency }
                            .mapValues { (_, value) -> value.single() }
                        Pair(robotType, Cost(moneys))
                    }.groupBy { (robotType, _) -> Mineral.valueOf(robotType.uppercase()) }
                    .mapValues { entry -> entry.value.map { (_, costs) -> costs }.first() }

                return Blueprint(id, robotTypeToCost, maxTime)
            }

            fun zeroRobotsMap() = MOST_TO_LEAST_VALUABLE.groupBy { it }
                .mapValues { (k, _) -> Robot(k, 0) }
                .toMutableMap()
        }
    }

    data class Cost(val cost: Map<Mineral, Money>) {
        fun howManyCanBuy(moneyAvailable: Map<Mineral, Money>): Int {
            val howManyPerCurrency = cost.map { (currency, money) ->
                val available = moneyAvailable.getOrDefault(currency, Money(0, currency))
                Pair(currency, if (available.amount == 0) return 0 else available.amount / money.amount)
            }
            return howManyPerCurrency.minOf { (_, times) -> times }
        }
    }

    data class Money(val amount: Int, val currency: Mineral) {
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

        enum class Mineral {
            GEODE, OBSIDIAN, CLAY, ORE;

            override fun toString(): String
//                = when(this) { GEODE -> "💎"; OBSIDIAN -> "🟣"; CLAY -> "🧱"; ORE -> "💩" }
                    = when (this) {
                GEODE -> "GEO"; OBSIDIAN -> "OBS"; CLAY -> "CLA"; ORE -> "ORE"
            }
        }
    }

    data class Robot(val type: Mineral, val resourcePerMinute: Int) {
        operator fun plus(newRobot: Robot): Robot {
            if (type != newRobot.type) throw IllegalStateException("robot types must be the same")
            return Robot(type, resourcePerMinute + newRobot.resourcePerMinute)
        }

        operator fun minus(robotToBuild: Robot): Robot {
            if (type != robotToBuild.type) throw IllegalStateException("robot types must be the same")
            return Robot(type, resourcePerMinute - robotToBuild.resourcePerMinute)
        }

        override fun toString(): String = "🤖($resourcePerMinute $type/min)"
    }

    data class ScoreCalculator(
        val robotTypeToCost: Map<Mineral, Cost>,
        val maxTime: Int,
    ) {
        private val mineralToOreAmount: Map<Mineral, Int> = convert(robotTypeToCost)

        private fun convert(robotTypeToCost: Map<Mineral, Cost>): Map<Mineral, Int> {
            val mineralToOre = mutableMapOf<Mineral, Int>()
            mineralToOre[ORE] = 1
            for (mineral in MOST_TO_LEAST_VALUABLE.reversed()) {
                mineralToOre[mineral] =
                    robotTypeToCost[mineral]!!.cost.values.sumOf { money -> toOreAmount(mineralToOre, money) }
            }
            return mineralToOre
        }

        private fun toOreAmount(mineralToOre: MutableMap<Mineral, Int>, money: Money): Int {
            if (money.currency == ORE) return money.amount
            val mineralIdx = MOST_TO_LEAST_VALUABLE.indexOf(money.currency)
            return money.amount * toOreAmount(mineralToOre, Money(1, MOST_TO_LEAST_VALUABLE[mineralIdx + 1]))
        }

        fun estimateInOre(
            estimatedMineral: Mineral,
            timeElapsed: Int,
            earnedMinerals: Map<Mineral, Money>,
            activeRobots: Map<Mineral, Robot>,
            idleRobots: Map<Mineral, Robot>,
        ): Money {
            val remainingTime = maxTime - timeElapsed
            val earnedMineral =
                earnedMinerals[estimatedMineral]!!.let { (amount, mineral) -> mineralToOreAmount[mineral]!! * amount }
            val activeMineralRobots =
                activeRobots[estimatedMineral]!!.let { (mineral, rpm) -> mineralToOreAmount[mineral]!! * rpm } * remainingTime
            val idleRobotsValue =
                idleRobots[estimatedMineral]!!.let { (mineral, rpm) -> mineralToOreAmount[mineral]!! * rpm }
            return Money(earnedMineral + activeMineralRobots + idleRobotsValue, ORE)
        }
    }

    data class ProductionState(
        val blueprint: Blueprint,
        val earnedMinerals: Map<Mineral, Money>,
        val robotsAvailable: Map<Mineral, Robot>,
        val notReadyRobots: Map<Mineral, Robot>,
        val timeElapsed: Int, val maxTime: Int,
        val history: List<ProductionState>,
    ) : Comparable<ProductionState> {
        init {
            if (earnedMinerals.size != MOST_TO_LEAST_VALUABLE.size) throw IllegalStateException("invalid size")
        }

        fun getCollectedMineralCount(type: Mineral): Int {
            return earnedMinerals.getOrDefault(type, Money(0, type)).amount
        }

        fun generateNextStates(): List<ProductionState> {
            val bestToWorst: TreeSet<ProductionState> = TreeSet<ProductionState>(ProductionState::compareTo)
            bestToWorst.add(this.copy())
            val howManyPerTypeCanBuy = blueprint.robotTypeToCost.mapValues { (_, cost) ->
                cost.howManyCanBuy(earnedMinerals)
            }
            val possibleBuys = blueprint.buy(
                howManyPerTypeCanBuy,
                earnedMinerals,
                blueprint.robotTypeToCost,
            )
            possibleBuys.forEach { (notReadyRobots, newMoneyAvailable) ->
                val newState = ProductionState(
                    blueprint,
                    newMoneyAvailable,
                    robotsAvailable,
                    notReadyRobots = notReadyRobots,
                    timeElapsed,
                    maxTime,
                    history,
                )
//                nextStates += ...
                bestToWorst.add(newState)
            }
            return bestToWorst.toList().subList(0, min(bestToWorst.size, MAX_STATES_STORED)) // TODO test
//            return bestToWorst.toList()
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
            if (this == other) return 0
            for (mineral in MOST_TO_LEAST_VALUABLE) {
                val oreValue = blueprint.oreCalculator.estimateInOre(
                    mineral,
                    timeElapsed,
                    earnedMinerals,
                    robotsAvailable,
                    notReadyRobots
                )
                val otherOreValue = blueprint.oreCalculator.estimateInOre(
                    mineral,
                    other.timeElapsed,
                    other.earnedMinerals,
                    other.robotsAvailable,
                    other.notReadyRobots
                )
                if (oreValue != otherOreValue) return otherOreValue.compareTo(oreValue)
            }
            return timeElapsed.compareTo(other.timeElapsed)
        }

        fun produce(): List<ProductionState> {
            if (timeElapsed == maxTime) return listOf(this)
            val newMoneyAvailable = newMoneyAvailable()

            if (notReadyRobots.any { it.value.resourcePerMinute > 0 }) {
                val newRobotChoices = notReadyRobots
                    .values
                    .filter { it.resourcePerMinute > 0 }
                    .sortedBy { MOST_TO_LEAST_VALUABLE.indexOf(it.type) }
                    .map { Robot(it.type, resourcePerMinute = 1) }

                val newStates = mutableListOf<ProductionState>()

                for (robotToBuild in newRobotChoices) {
                    newStates.add(
                        ProductionState(
                            blueprint,
                            newMoneyAvailable,
                            robotsAvailable.mapValues { (mineral, robot) -> if (mineral == robotToBuild.type) robot + robotToBuild else robot },
                            notReadyRobots.mapValues { (mineral, robot) -> if (mineral == robotToBuild.type) robot - robotToBuild else robot },
                            timeElapsed + 1,
                            maxTime,
                            history + this,
                        )
                    )
                }
                return newStates
            } else {
                return listOf(
                    ProductionState(
                        blueprint,
                        newMoneyAvailable,
                        robotsAvailable,
                        notReadyRobots,
                        timeElapsed + 1,
                        maxTime,
                        history + this,
                    )
                )
            }
        }

        private fun newMoneyAvailable(): MutableMap<Mineral, Money> {
            val newMoneyAvailable = mutableMapOf<Mineral, Money>()
            for ((type, robot) in robotsAvailable) {
                val oldMoneyAvailable = earnedMinerals.getOrDefault(type, Money(0, type))
                newMoneyAvailable.putIfAbsent(type, oldMoneyAvailable)
                newMoneyAvailable[type] = newMoneyAvailable[type]!! + Money(robot.resourcePerMinute, type)
            }
            return newMoneyAvailable
        }

        override fun toString(): String {
            return "State(⌚=$timeElapsed/$maxTime, ${blueprint.id}, 🏦: ${earnedMinerals.values}, 🏭: =${robotsAvailable.values})"
        }
    }
}