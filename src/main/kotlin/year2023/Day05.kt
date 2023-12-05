package year2023

import aoc.IAocTaskKt

class Day05 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_05.txt"

    override fun solvePartOne(lines: List<String>) {
        val seeds = lines.first().replace("seeds: ", "")
            .split(" ")
            .map { Range(it.toLong(), it.toLong()) }

        println(lowestLocationNumber(lines, seeds))
    }

    private fun lowestLocationNumber(lines: List<String>, seeds: List<Range>): Long {
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
            val soilIdx = seedToSoil.findAffectingConversions(listOf(seed)).mapToNewRange(listOf(seed))
            val fertilizerIdx = soilToFertilizer.findAffectingConversions(soilIdx).mapToNewRange(soilIdx)
            val waterIdx = fertilizerToWater.findAffectingConversions(fertilizerIdx).mapToNewRange(fertilizerIdx)
            val lightIdx = waterToLight.findAffectingConversions(waterIdx).mapToNewRange(waterIdx)
            val temperatureIdx = lightToTemperature.findAffectingConversions(lightIdx).mapToNewRange(lightIdx)
            val humidityIdx =
                temperatureToHumidity.findAffectingConversions(temperatureIdx).mapToNewRange(temperatureIdx)
            val location = humidityToLocation.findAffectingConversions(humidityIdx).mapToNewRange(humidityIdx)
            location
        }
        return seedToLocation.minOf { it.minOf(Range::from) }
    }

    override fun solvePartTwo(lines: List<String>) {
        val seeds = lines.first().replace("seeds: ", "")
            .split(" ")
            .map { it.toLong() }
            .windowed(2, 2)
            .map {
                val (start, rangeLength) = it
                Range(start, start + rangeLength - 1)
            }

        println(lowestLocationNumber(lines, seeds))
    }

    data class Range(val from: Long, val to: Long) {
        fun divideBy(conv: Conversion): List<Range> {
            val min = conv.minSource
            val max = conv.maxSource
            // from, min = to, max
            if (to == min) {
                return listOf(Range(from, to - 1), conv.mapToNewRange(Range(to, to)))
            }
            // from < min < to < max
            if (from < min && to < max) {
                return listOf(Range(from, min - 1), conv.mapToNewRange(Range(min, to)))
            }
            // min <= from < to <= max
            if (min <= from && to < max) {
                return listOf(conv.mapToNewRange(this))
            }
            // min <= from <= max < to
            if (min <= from && max < to) {
                return listOf(conv.mapToNewRange(Range(from, max)), Range(max + 1, to))
            }

            // max == from == to
            if (from == max && from == to) {
                return listOf(conv.mapToNewRange(Range(from, from)))
            }

            // max == from < to
            if (from == max) {
                return listOf(conv.mapToNewRange(this))
            }

            return listOf(conv.mapToNewRange(Range(from, max)), Range(max + 1, to))

//            return TODO("$conv $this")
        }
    }

    private fun List<Conversion>.mapToNewRange(ranges: List<Range>): List<Range> {
        val intersectingConversions = this
        return ranges.flatMap { range ->
            val rangeConversions = intersectingConversions.filter { conv -> conv.intersectsWith(range) }
            if (rangeConversions.isEmpty()) {
                listOf(range)
            } else { // cut the range
                rangeConversions.flatMap { conv ->
                    val dividedRange = range.divideBy(conv)
                    dividedRange
                }
            }
        }
    }

    data class Conversion(val source: Long, val destination: Long, val rangeSize: Long) {
        val minSource = source
        val maxSource = source + rangeSize - 1
        private val minDest = destination
        private val maxDest = destination + rangeSize - 1

        fun intersectsWith(range: Range): Boolean = minSource <= range.to && range.from <= maxSource

        override fun toString(): String = "(src=$minSource..$maxSource, dst=$minDest..$maxDest)"

        fun mapToNewRange(range: Range): Range {
            val (from, to) = range
            return Range(minDest + (from - minSource), minDest + (to - minSource))
        }

        companion object {
            fun parse(line: String): Conversion {
                val (destination, source, rangeSize) = line.split(" ").map { it.toLong() }
                return Conversion(source, destination, rangeSize)
            }
        }
    }

    private fun List<String>.createConversions(thisIdx: Int, nextIdx: Int) = this.subList(thisIdx + 1, nextIdx - 1)
        .map(Conversion::parse)

    private fun List<Conversion>.findAffectingConversions(ranges: List<Range>): List<Conversion> =
        ranges.flatMap { range -> this.filter { it.intersectsWith(range) } }
}
