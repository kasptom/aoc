package year2023

import aoc.IAocTaskKt
import kotlin.math.min

class Day07 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_07.txt"

    override fun solvePartOne(lines: List<String>) {
        val cards = lines.map(Hand::parse)
        val sortedCards = cards.sorted()
        val totalWinnings = sortedCards
            .mapIndexed { idx, hand -> (idx + 1) * hand.bid }
            .sumOf { it }

        println(totalWinnings)
    }

    override fun solvePartTwo(lines: List<String>) {
        val cards = lines.map(Hand::parse)
        val sortedCards = cards.sortedWith(JokerComparator())
        val totalWinnings = sortedCards
            .mapIndexed { idx, hand -> (idx + 1) * hand.bid }
            .sumOf { it }

        println(totalWinnings)
    }

    companion object {
        val CARD_VALUES = listOf("A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2")
        val CARD_VALUES_2 = listOf("A", "K", "Q", "T", "9", "8", "7", "6", "5", "4", "3", "2", "J")
    }

    data class Hand(val cards: List<String>, val bid: Int) : Comparable<Hand> {
        private val groupedCards = cards.groupBy { it }
            .mapValues { (_, v) -> v.count() }

        private val jokerGroupedCards = computeJokerGrouping()

        override fun compareTo(other: Hand): Int = compare(
            strength = strength,
            otherStrength = other.strength,
            otherCards = other.cards,
            cardValues = CARD_VALUES
        )

        override fun toString(): String = "Hand(cards=$cards, bid=$bid, groupedCards=$groupedCards, strength=$strength)"

        private fun computeJokerGrouping(): Map<String, Int> {
            if (!cards.contains("J")) return groupedCards
            var jokersCount = cards.count { it == "J" }
            val noJokersCards = cards.filter { it != "J" }

            val grouped = noJokersCards
                .groupBy { it }
                .mapValues { (_, v) -> v.count() }
                .toMutableMap()

            if (grouped.isEmpty()) {
                grouped["J"] = 5
                return grouped
            }

            while (jokersCount > 0) {
                val maxCount = grouped.maxOf { (_, v) -> v }
                val toAdd = min(5 - maxCount, jokersCount)
                jokersCount -= toAdd
                val key = grouped.keys.first { k -> grouped[k] == maxCount }
                grouped[key] = grouped[key]!! + toAdd
            }
            return grouped
        }

        private val strength: Int = computeStrength()
        val jokerStrength: Int = computeStrengthWithJokers()

        private fun computeStrength(cardGrouping: Map<String, Int>): Int = with(cardGrouping) {
            return if (values.maxOf { it } == 5) 7 // five of a kind
            else if (values.maxOf { it } == 4) 6 // four of a kind
            else if (values.maxOf { it } == 3 && values.filter { it != 3 }.maxOf { it } == 2) 5 // full house
            else if (values.maxOf { it } == 3 && values.filter { it != 3 }.maxOf { it } == 1) 4 // three of a kind
            else if (values.maxOf { it } == 2 && values.count { it == 2 } == 2) 3 // two pair
            else if (values.maxOf { it } == 2 && values.count { it == 2 } == 1) 2 // one pair
            else if (values.maxOf { it } == 1) 1 // high card
            else throw IllegalStateException("unknown state $cards")
        }

        private fun computeStrength(): Int = computeStrength(groupedCards)
        private fun computeStrengthWithJokers(): Int = computeStrength(jokerGroupedCards)

        fun compare(strength: Int, otherStrength: Int, otherCards: List<String>, cardValues: List<String>): Int {
            if (otherStrength != strength) return strength - otherStrength
            for (cardIdx in cards.indices) {
                val index = cardValues.indexOf(cards[cardIdx])
                val otherIndex = cardValues.indexOf(otherCards[cardIdx])
                if (index != otherIndex) return otherIndex - index
                continue
            }
            return 0
        }

        companion object {
            fun parse(line: String): Hand {
                val (cardsRaw, bidRaw) = line.split(" ")
                val cards = cardsRaw.split("").filter { it.isNotEmpty() }
                val bid = bidRaw.toInt()
                return Hand(cards, bid)
            }
        }
    }

    class JokerComparator : Comparator<Hand> {
        override fun compare(o1: Hand, o2: Hand): Int = o1.compare(
            strength = o1.jokerStrength,
            otherStrength = o2.jokerStrength,
            otherCards = o2.cards,
            cardValues = CARD_VALUES_2
        )
    }
}
