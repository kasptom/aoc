package year2023

import aoc.IAocTaskKt
import kotlin.math.max

class Day07 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_07_test.txt"

    override fun solvePartOne(lines: List<String>) {
        val cards = lines.map(Hand::parse)
        val sortedCards = cards.sorted()
        val ranks = sortedCards.mapIndexed { idx, hand -> (idx + 1) * hand.bid }
        println(cards.map { Triple(it.cards, it.bid, it.strength) })
        println(sortedCards.map { Triple(it.cards, it.bid, it.strength) })

        println(ranks)
        println(ranks.sumOf { it })
    }

    override fun solvePartTwo(lines: List<String>) {
        val cards = lines.map(Hand::parse)
        val sortedCards = cards.sortedWith(JokerComparator())
        val ranks = sortedCards.mapIndexed { idx, hand -> (idx + 1) * hand.bid }
        println(cards.map { Triple(it.cards, it.bid, it.jokerStrength) })
        println(sortedCards.map { Triple(it.cards, it.bid, it.jokerStrength) })

        println(ranks)
        println(ranks.sumOf { it })
    }

    companion object {
        val cardValues = listOf(
            "A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2"
        )
        val cardValues2 = listOf(
            "A", "K", "Q", "T", "9", "8", "7", "6", "5", "4", "3", "2", "J",
        )
    }

    data class Hand(val cards: List<String>, val bid: Int): Comparable<Hand> {
        val groupedCards = cards.groupBy { it }
            .mapValues { (_, v) -> v.count() }

        val jokerGroupedCards = computeJokerGrouping()

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
                val toAdd = max(5 - maxCount, jokersCount)
                jokersCount -= toAdd
                val key = grouped.keys.first { k -> grouped[k] == maxCount }
                grouped[key] = grouped[key]!! + toAdd
            }
            return grouped
        }

        val strength: Int = computeStrength()
        val jokerStrength: Int = computeStrengthWithJokers()

        fun computeStrength(): Int {
            return if (groupedCards.size == 1) 7 // five of a kind
            else if (groupedCards.size == 2 && groupedCards.values.maxOf { it } == 4) 6 // four of a kind
            else if (groupedCards.size == 2 && groupedCards.values.maxOf { it } == 3) 5 // full house
            else if (groupedCards.size == 3 && groupedCards.values.maxOf { it } == 3) 4 // three of a kind
            else if (groupedCards.values.maxOf { it } == 2 && groupedCards.values.count { it == 2 } == 2) 3 // two pair
            else if (groupedCards.size == 4 && groupedCards.values.maxOf { it } == 2 && groupedCards.values.count { it == 2} == 1) 2 // one pair
            else if (groupedCards.size == 5) 1 // high card
            else throw IllegalStateException("unknown state $cards")
        }

        fun computeStrengthWithJokers(): Int {
            return if (jokerGroupedCards.values.maxOf { it } == 5) 7 // five of a kind
            else if (jokerGroupedCards.values.maxOf { it } == 4) 6 // four of a kind
            else if (jokerGroupedCards.values.maxOf { it } == 3) 5 // full house
            else if (jokerGroupedCards.values.maxOf { it } == 3) 4 // three of a kind
            else if (jokerGroupedCards.values.maxOf { it } == 2 && jokerGroupedCards.values.count { it == 2 } == 2) 3 // two pair
            else if (jokerGroupedCards.values.maxOf { it } == 2 && jokerGroupedCards.values.count { it == 2} == 1) 2 // one pair
            else if (jokerGroupedCards.values.maxOf { it } == 1) 1 // high card
            else throw IllegalStateException("unknown state $cards")
        }

        companion object {
            fun parse(line: String): Hand {
                val (cardsRaw, bidRaw) = line.split(" ")
                val cards = cardsRaw.split("").filter { it.isNotEmpty() }
                val bid = bidRaw.toInt()
                return Hand(cards, bid)
            }
        }

        override fun compareTo(other: Hand): Int {
            val otherStrength = other.strength

            if (otherStrength != strength) return strength - otherStrength
            for (cardIdx in cards.indices) {
                val index = cardValues.indexOf(cards[cardIdx])
                val otherIndex = cardValues.indexOf(other.cards[cardIdx])
                if (index != otherIndex) return otherIndex - index
                continue
            }
            return 0
        }

        override fun toString(): String {
            return "Hand(cards=$cards, bid=$bid, groupedCards=$groupedCards, strength=$strength)"
        }
    }
    class JokerComparator: Comparator<Hand> {
        override fun compare(o1: Hand, o2: Hand): Int {
            val strength = o1.jokerStrength
            val otherStrength = o2.jokerStrength
            val cards = o1.cards

            if (otherStrength != strength) return strength - otherStrength
            for (cardIdx in cards.indices) {
                val index = cardValues2.indexOf(cards[cardIdx])
                val otherIndex = cardValues2.indexOf(o2.cards[cardIdx])
                if (index != otherIndex) return otherIndex - index
                continue
            }
            return 0
        }

    }
}
