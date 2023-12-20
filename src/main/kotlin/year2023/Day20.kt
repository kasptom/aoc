package year2023

import aoc.IAocTaskKt
import year2023.Day20.Module.Signal.HIGH
import year2023.Day20.Module.Signal.IDLE
import year2023.Day20.Module.Signal.LOW

class Day20 : IAocTaskKt {
    val nameToModule = mutableMapOf<String, Module>()
    override fun getFileName(): String = "aoc2023/input_20.txt"

    override fun solvePartOne(lines: List<String>) {
        val modules: List<Module> = lines.map(Module::parse)
        nameToModule.clear()
        val conjunctions = mutableSetOf<Module>()
        modules.forEach {
            nameToModule[it.id] = it
            if (it.type == Module.Type.CONJUNCTION) {
                conjunctions += it
            }
        }
        nameToModule.onEach {
            println(it)
        }
        conjunctions.forEach { conn -> conn
            .initializeInputs(modules.filter { it.destinations.contains(conn.id) }.map { m -> m.id }) }
        println("map")
        println(nameToModule)

        modules.onEach { println(it) }
        var lowPulsesCount = 0
        var highPulsesCount = 0
        val repeats = 1000
        for (repeat in 1..repeats) {
            println("--- button press #$repeat ---")
            println("button -low-> broadcaster")
            val srcDestSignals = mutableListOf<Triple<String, String, Module.Signal>>()
            lowPulsesCount += 1
            srcDestSignals += Triple("button", "broadcaster", LOW)

            while (srcDestSignals.any { (_, _, v) -> v != IDLE }) {
                val newDestinationSignals = mutableListOf<Triple<String, String, Module.Signal>>()
                for (entry in srcDestSignals) {
                    val (src, dest, signal) = entry
//                    println("module name: $moduleName")
                    val module = nameToModule.getOrElse(dest) {
                        println("could not find: $dest")
                        null
                    }
                    if (module == null) {
                        continue
                    }

                    if (signal == IDLE) {
                        continue
                    }

                    val newSignal = module.process(src, signal)
                    if (newSignal == LOW) lowPulsesCount += module.destinations.count()
                    else if (newSignal == HIGH) highPulsesCount += module.destinations.count()
                    else if (newSignal == IDLE) continue

                    for (newDest in module.destinations) {
                        newDestinationSignals += Triple(dest, newDest, newSignal)
                        println("$dest -${newSignal.name.lowercase()}-> $newDest")
                    }
                }
                srcDestSignals.clear()
                srcDestSignals.addAll(newDestinationSignals)
            }
//            fipFlops.forEach {
//                it.state = LOW
//            }
        }
        println("low: $lowPulsesCount, high: $highPulsesCount")
        println(lowPulsesCount * highPulsesCount)
    }

    override fun solvePartTwo(lines: List<String>) {
        println("Not yet implemented")
    }

    data class Module(
        val id: String,
        val type: Type,
        var state: Signal = if (type == Type.CONJUNCTION) IDLE else LOW,
        val destinations: List<String>,
        val inputToSignal: MutableMap<String, Signal> = mutableMapOf(),
    ) {
        fun process(src: String, signal: Signal): Signal {
            return when (type) {
                Type.BUTTON -> signal
                Type.FLIP_FLOP -> {
                    when (signal) {
                        LOW -> {
                            state = state.opposite()
                            state
                        }

                        HIGH -> IDLE
                        IDLE -> throw IllegalStateException()
                    }
                }

                Type.CONJUNCTION -> {
                    inputToSignal[src] = signal
                    if (inputToSignal.values.all { it == HIGH }) LOW else HIGH
                }
            }
        }

        companion object {
            fun parse(line: String): Module {
                return if (line.startsWith(Type.BUTTON.value)) {
                    val (name, destsRaw) = line.split(" -> ").filter(String::isNotEmpty)
                    val dests = destsRaw.split(", ").filter(String::isNotEmpty)
                    Module(
                        id = name,
                        type = Type.BUTTON,
                        destinations = dests
                    )
                } else {
                    val (typeName, destsRaw) = line.split(" -> ").filter(String::isNotEmpty)
                    val type = typeName.substring(0, 1).let { letter -> Type.values().first { it.value == letter } }
                    val id = typeName.substring(1)
                    val dests = destsRaw.split(", ").filter(String::isNotEmpty)
                    Module(
                        id = id,
                        type = type,
                        destinations = dests
                    )
                }
            }
        }

        enum class Type(val value: String) {
            BUTTON("broadcaster"),
            FLIP_FLOP("%"),
            CONJUNCTION("&")
        }

        enum class Signal {
            LOW, HIGH, IDLE;

            fun opposite(): Signal {
                return when (this) {
                    LOW -> HIGH
                    HIGH -> LOW
                    IDLE -> throw IllegalStateException()
                }
            }
        }

        override fun toString(): String {
            return "{$id(${type}) -> $destinations}"
        }

        fun initializeInputs(inputs: List<String>) {
            inputs.forEach {
                inputToSignal[it] = LOW
            }
        }
    }
}
