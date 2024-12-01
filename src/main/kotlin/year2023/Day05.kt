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
            val water = fertilizerToWater.findAffectingConversions(fertilizer).mapToNewRange(fertilizer)
            val light = waterToLight.findAffectingConversions(water).mapToNewRange(water)
            val temperature = lightToTemperature.findAffectingConversions(light).mapToNewRange(light)
            val humidity = temperatureToHumidity.findAffectingConversions(temperature).mapToNewRange(temperature)
            val location = humidityToLocation.findAffectingConversions(humidity).mapToNewRange(humidity)
            println("-------")
            println("🌱: $seed")
            println("🟫: $soil")
            println("💩: $fertilizer")
            println("💧: $water")
            println("🌞: $light")
            println("🔥: $temperature")
            println("💦: $humidity")
            println("📍: $location")
            location
        }
        return seedToLocation.minOf {
            it.map(Range::from)
//            .filter { it > 0} // 17963910 is too low
//            .filter { it > 24425756} // 24425756 is too low // 33942462 is not right
                .minOf { it }
        }
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

        //        fun intersectsWith(range: Range): Boolean = from <= range.to && range.from <= to
        fun size(): Long = to - from + 1

        private fun mapToNewRange(conv: Conversion): Range = conv.mapToNewRange(this)

        fun divideBy(conv: Conversion): Pair<List<Range>, List<Range>> {
            // mapped, leftovers
//            println("conv: $conv, range: $this")
            val min = conv.minSource
            val max = conv.maxSource
            return when {
                from == to && min <= from && from <= max -> Pair(
                    listOf(mapToNewRange(conv)),
                    emptyList()
                )

                from == to -> Pair(
                    emptyList(),
                    listOf(this)
                )

                min <= from && to <= max -> Pair(
                    listOf(mapToNewRange(conv)),
                    emptyList()
                )

                min <= from && from < max -> Pair(
                    listOf(Range(from, max).mapToNewRange(conv)),
                    listOf(Range(max + 1, to))
                )

                to < min -> Pair(
                    emptyList(),
                    listOf(this)
                )

                to == min -> Pair(
                    listOf(Range(to, to).mapToNewRange(conv)),
                    listOf(Range(from, to - 1))
                )

                from < min && to <= max -> Pair(
                    listOf(Range(min, to).mapToNewRange(conv)),
                    listOf(Range(from, min - 1))
                )

                from < min -> Pair(
                    listOf(conv.mapToNewRange(Range(min, max))),
                    listOf(Range(from, min - 1), Range(max + 1, to))
                )

                max < from -> Pair(
                    emptyList(),
                    listOf(this)
                )

                else -> throw IllegalStateException(
                    "not supported case min..max: $min..$max vs from..to: $from..$to ${
                        listOf(
                            min,
                            max,
                            from,
                            to
                        ).sorted()
                    }"
                )
            }
        }

        override fun toString(): String {
            return "$from..$to"
        }


    }

    private fun List<Conversion>.mapToNewRange(ranges: List<Range>): List<Range> {
//        println("-------------")
//        println("mapping $ranges to new range")
        val intersectingConversions = this.sortedBy { it.minSource }
        val mappedRanges = mutableListOf<Range>()
        val notMappedRanges = ranges.toMutableList()
        while (notMappedRanges.any { range -> intersectingConversions.any { conv -> conv.intersectsWith(range) } }) {
            val range =
                notMappedRanges.first { rng -> intersectingConversions.any { conv -> conv.intersectsWith(rng) } }
            notMappedRanges.remove(range)

            val conv = intersectingConversions.first { cnv -> cnv.intersectsWith(range) }

            val (mapped, updatedLeftovers) = range.divideBy(conv)
            println("range division: $range (${range.size()}) -> $mapped, $updatedLeftovers (${mapped.sumOf(Range::size)})")
            mappedRanges += mapped
            notMappedRanges += updatedLeftovers
        }
        mappedRanges += notMappedRanges
        return mappedRanges.also { checkRangeConsistency(ranges, it) }
            .distinct()
//            .also { println("range size: ${it.size}") }
    }

    private fun checkRangeConsistency(one: List<Range>, another: List<Range>) {
        val oneSum = one.sumOf { it.size() }
        val otherSum = another.sumOf { it.size() }
        if (oneSum != otherSum) throw IllegalStateException(
            "different sum of ranges $oneSum vs $otherSum" +
                    "\n prev: $one, next: $another"
        )
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
