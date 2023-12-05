package year2023

import aoc.IAocTaskKt

class Day05 : IAocTaskKt {
    override fun getFileName(): String = "aoc2023/input_05_test.txt"

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
            val soilIdx = seedToSoil.findBySourceRange(listOf(seed)).mapToNewRange(listOf(seed))
            val fertilizerIdx = soilToFertilizer.findBySourceRange(soilIdx).mapToNewRange(soilIdx)
            val waterIdx = fertilizerToWater.findBySourceRange(fertilizerIdx).mapToNewRange(fertilizerIdx)
            val lightIdx = waterToLight.findBySourceRange(waterIdx).mapToNewRange(waterIdx)
            val temperatureIdx = lightToTemperature.findBySourceRange(lightIdx).mapToNewRange(lightIdx)
            val humidityIdx = temperatureToHumidity.findBySourceRange(temperatureIdx).mapToNewRange(temperatureIdx)
            val location = humidityToLocation.findBySourceRange(humidityIdx).mapToNewRange(humidityIdx)
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
                Range(start, start + rangeLength)
            }

        println(lowestLocationNumber(lines, seeds))
    }

    data class Range(val from: Long, val to: Long)

    private fun List<Conversion>.mapToNewRange(ranges: List<Range>): List<Range> {
        return ranges.flatMap {range ->
            this.map { it.mapToNewRange(range) }.ifEmpty { listOf(range) }
        }
    }

    data class Conversion(val source: Long, val destination: Long, val rangeSize: Long) {
        private val minSource = source
        private val maxSource = source + rangeSize - 1
        private val minDest = destination
        private val maxDest = destination + rangeSize - 1

        fun intersectsWith(range: Range): Boolean {
            return minSource <= range.from && maxSource >= range.to
        }

        override fun toString(): String {
            return "(src=$minSource..$maxSource, dst=$minDest..$maxDest)"
        }

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

    private fun List<Conversion>.findBySourceRange(ranges: List<Range>): List<Conversion> =
        ranges.flatMap { range -> this.filter { it.intersectsWith(range) } }

}
