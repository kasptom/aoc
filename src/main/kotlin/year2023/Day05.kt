package year2023

import aoc.IAocTaskKt

class Day05 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_05.txt"

    override fun solvePartOne(lines: List<String>) {
        val seeds = lines.first().replace("seeds: ", "")
            .split(" ")
            .map { it.toLong() }

        println(lowestLocationNumber(lines, seeds))
    }

    private fun lowestLocationNumber(lines: List<String>, seeds: List<Long>): Long {
        val seedToSoilIdx = lines.indexOfFirst { it.startsWith("seed-to-soil") }
        val soilToFertilizerIdx = lines.indexOfFirst { it.startsWith("soil-to-fertilizer") }
        val fertilizerToWaterIdx = lines.indexOfFirst { it.startsWith("fertilizer-to-water") }
        val waterToLightIdx = lines.indexOfFirst { it.startsWith("water-to-light") }
        val lightToTemperatureIdx = lines.indexOfFirst { it.startsWith("light-to-temperature") }
        val temperatureToHumidityIdx = lines.indexOfFirst { it.startsWith("temperature-to-humidity") }
        val humidityToLocationIdx = lines.indexOfFirst { it.startsWith("humidity-to-location") }

        val seedToSoil = lines.createConversions(seedToSoilIdx, soilToFertilizerIdx)
        val soilToFertilizer = lines.createConversions(soilToFertilizerIdx, fertilizerToWaterIdx)
        val fertilizerToWater = lines.createConversions(fertilizerToWaterIdx, waterToLightIdx)
        val waterToLight = lines.createConversions(waterToLightIdx, lightToTemperatureIdx)
        val lightToTemperature = lines.createConversions(lightToTemperatureIdx, temperatureToHumidityIdx)
        val temperatureToHumidity = lines.createConversions(temperatureToHumidityIdx, humidityToLocationIdx)
        val humidityToLocation = lines.createConversions(humidityToLocationIdx, lines.size + 1)

        val seedToLocation = seeds.map { seed ->
            val soilIdx = seedToSoil.findBySourceRange(seed)?.getDestinationFromSourceIdx(seed) ?: seed
            val fertilizerIdx =
                soilToFertilizer.findBySourceRange(soilIdx)?.getDestinationFromSourceIdx(soilIdx) ?: soilIdx
            val waterIdx =
                fertilizerToWater.findBySourceRange(fertilizerIdx)?.getDestinationFromSourceIdx(fertilizerIdx)
                    ?: fertilizerIdx
            val lightIdx = waterToLight.findBySourceRange(waterIdx)?.getDestinationFromSourceIdx(waterIdx) ?: waterIdx
            val temperatureIdx =
                lightToTemperature.findBySourceRange(lightIdx)?.getDestinationFromSourceIdx(lightIdx) ?: lightIdx
            val humidityIdx =
                temperatureToHumidity.findBySourceRange(temperatureIdx)?.getDestinationFromSourceIdx(temperatureIdx)
                    ?: temperatureIdx
            val location = humidityToLocation.findBySourceRange(humidityIdx)?.getDestinationFromSourceIdx(humidityIdx)
                ?: humidityIdx
            location
        }
        return seedToLocation.minOf { it }
    }

    override fun solvePartTwo(lines: List<String>) {
        val seeds = lines.first().replace("seeds: ", "")
            .split(" ")
            .map { it.toLong() }
            .windowed(2, 2)
            .map {
                val (start, rangeLength) = it
                start until (start + rangeLength)
            }.flatMap { range -> range.map { it } }

        println(seeds.size)
        println(lowestLocationNumber(lines, seeds))
    }

    data class Conversion(val source: Long, val destination: Long, val rangeSize: Long) {
        private val minSource = source
        private val maxSource = source + rangeSize - 1
        private val minDest = destination
        private val maxDest = destination + rangeSize - 1

        fun getOffset(): Long = destination - source

        fun isInSourceRange(resourceIdx: Long): Boolean {
            return resourceIdx in minSource..maxSource
        }

        fun overlaps(prevConversion: Conversion): Boolean {
            return minSource <= prevConversion.maxDest && prevConversion.minDest <= maxSource
        }

        override fun toString(): String {
            return "(src=$minSource..$maxSource, dst=$minDest..$maxDest)"
        }

        fun getDestinationFromSourceIdx(idx: Long): Long = minDest + (idx - minSource)

        fun mergeWith(overlapping: List<Conversion>): List<Conversion> {
            if (overlapping.isEmpty()) return overlapping
            if (overlapping.size == 1) {
                val last = overlapping.last()
                val gapStart = if (this.source < last.source) minSource else last.maxSource + 1
                val gapEnd = if (this.source < last.source) last.minSource - 1 else maxSource
                val gap = Conversion(gapStart, gapStart, gapEnd - gapStart)
                return listOf(last, gap).sortedBy { it.source }
            }
            val current = this
            val toAdd = mutableListOf<Conversion>()
                overlapping.windowed(2).forEach {
                (prev, next) -> val rangeSize = prev.source - next.source
                    if (rangeSize >= 1) {
                    val gap = Conversion(prev.source + 1, prev.source + 1, rangeSize)
                    // This may be buggy
                    if (gap.overlaps(current)) {
                        toAdd += gap
                    }
                }
            }
            return (overlapping + toAdd).sortedBy { it.source }
        }

        fun contains(other: Conversion): Boolean = minSource <= other.minSource && other.maxSource <= maxSource

        companion object {
            fun parse(line: String): Conversion {
                val (destination, source, rangeSize) = line.split(" ").map { it.toLong() }
                return Conversion(source, destination, rangeSize)
            }
        }
    }

    private fun List<String>.createConversions(thisIdx: Int, nextIdx: Int) = this.subList(thisIdx + 1, nextIdx - 1)
        .map(Conversion::parse)

    private fun List<Conversion>.findBySourceRange(resourceIdx: Long): Conversion? =
        this.firstOrNull { it.isInSourceRange(resourceIdx) }

    private fun List<Conversion>.findBySourceRange(conversion: Conversion): Conversion =
        this.first { it.overlaps(conversion) }

    private fun List<Conversion>.mapFrom(previous: List<Conversion>): List<Conversion> {
        val merged = (this + previous).sortedBy { it.source }.fold(emptyList<Conversion>()) { acc, next ->
            if (acc.isEmpty()) acc + next
            else {
                val (overlapping, notOverlapping) = acc.partition { it.overlaps(next) && !it.contains(next) }
                (notOverlapping + next.mergeWith(overlapping)).sortedBy { it.source }
            }
        }
        return merged
    }
}
