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
            val soil = seedToSoil.findAffectingConversions(listOf(seed)).mapToNewRange(listOf(seed))
            val fertilizer = soilToFertilizer.findAffectingConversions(soil).mapToNewRange(soil)
            val waterIdx = fertilizerToWater.findAffectingConversions(fertilizer).mapToNewRange(fertilizer)
            val light = waterToLight.findAffectingConversions(waterIdx).mapToNewRange(waterIdx)
            val temperature = lightToTemperature.findAffectingConversions(light).mapToNewRange(light)
            val humidity = temperatureToHumidity.findAffectingConversions(temperature).mapToNewRange(temperature)
            val location = humidityToLocation.findAffectingConversions(humidity).mapToNewRange(humidity)
            location
        }
        return seedToLocation.minOf { it.map(Range::from)
//            .filter { it > 0} // 17963910 is too low
//            .filter { it > 24425756} // 24425756 is too low // 33942462 is not right
            .minOf { it } }
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
        init {
            if (from > to) throw IllegalStateException("from > to: $from > $to")
            if (from < 0) throw IllegalStateException("from < 0: $from < 0")
        }

        private fun mapToNewRange(conv: Conversion): Range = conv.mapToNewRange(this)

        fun divideBy(conv: Conversion): List<Range> {
//            println("conv: $conv, range: $this")
            val min = conv.minSource
            val max = conv.maxSource
            return when {
                from == to && min <= from && from <= max -> listOf(
                    mapToNewRange(conv)
                )
                from == to -> listOf(this)
                min <= from && to <= max -> listOf(
                    mapToNewRange(conv)
                )
                min <= from && from < max -> listOf(
                    Range(from, max).mapToNewRange(conv),
                    Range(max + 1, to))
                to < min -> listOf(this)
                to == min -> listOf(
                    Range(from, to - 1),
                    Range(to, to).mapToNewRange(conv)
                )
                from < min && to <= max -> listOf(
                    Range(from, min - 1),
                    Range(min, to).mapToNewRange(conv)
                )
                from < min -> listOf(
                    Range(from, min - 1),
                    conv.mapToNewRange(Range(min, max)),
                    Range(max + 1, to)
                )
                max < from -> listOf(this)
                else -> throw IllegalStateException("not supported case min..max: $min..$max vs from..to: $from..$to ${listOf(min, max, from, to).sorted()}")
            }
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
        }.distinct()
//            .also { println("range size: ${it.size}") }
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
            val diff = minDest - minSource
            return Range(from + diff, to + diff)
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
