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

    override fun solvePartTwo(lines: List<String>) {
        val seeds = lines.first().replace("seeds: ", "")
            .split(" ")
            .map { it.toLong() }
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


    data class Conversion(val source: Long, val destination: Long, val rangeSize: Long) {
        private val minSource = source
        private val maxSource = source + rangeSize - 1
        private val minDest = destination
        private val maxDest = destination + rangeSize - 1

        fun isInSourceRange(resourceIdx: Long): Boolean {
            return resourceIdx in minSource..maxSource
        }

        fun overlaps(prevConversion: Conversion): Boolean {
            return (minSource <= prevConversion.maxDest) && (prevConversion.minDest <= maxSource)
        }

        override fun toString(): String {
            return "(src=$minSource..$maxSource, dst=$minDest..$maxDest)"
        }

        fun getDestinationFromSourceIdx(idx: Long): Long = minDest + (idx - minSource)


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
}
