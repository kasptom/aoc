package year2022

import aoc.IAocTaskKt

class Day07 : IAocTaskKt {
    override fun getFileName(): String = "aoc2022/input_07_test.txt"
    val fileSystem = mutableMapOf<String, Directory>()

    override fun solvePartOne(lines: List<String>) {
        val maxDirSize = 100000

        var idx = 0
        var currentDir: Directory? = null

        while (idx < lines.size) {
            val line = lines[idx]
            if (line.startsWith("$ cd ..")) {
                currentDir = currentDir!!.parent
                idx++
            } else if (line.startsWith("$ cd")) {
                val parent = currentDir
                val currentDirName = (parent?.name ?: "") + line.replace("$ cd ", "")

                currentDir = if (currentDirName.endsWith("/")) Directory(currentDirName, idx, parent, mutableListOf())
                else parent!!.files.first { it.name == currentDirName } as Directory

                fileSystem.putIfAbsent(currentDir.name, currentDir) // TODO necessary?
                idx++
            } else if (line.startsWith("$ ls")) {
                val listingEndIdx = lines.mapIndexed { nextLineIdx, nextLine -> Pair(nextLineIdx, nextLine) }
                    .firstOrNull { (nextLineIdx, nextLine) -> nextLineIdx > idx && nextLine.startsWith("$") }?.first
                    ?: lines.size

                val fileLines = lines.subList(idx + 1, listingEndIdx)
//                println("file lines $fileLines")
//                println("files update")
                for (fileLine in fileLines) {
//                    println(currentDir?.files)
                    currentDir!!.files += parseFile(currentDir!!, fileLine, idx)
//                    println(currentDir.files)
                }
//                println("files update end")
                idx = listingEndIdx
            } else {
                idx++
            }
        }


        fileSystem.keys.forEach {
            println("$it -> ${fileSystem[it]}")
        }

        currentDir = fileSystem["/"]
        updateDirSizes(currentDir!!, fileSystem)

//        fileSystem.keys.forEach {
//            println("$it -> ${fileSystem[it]}")
//        }

        println(fileSystem.values.filter { it.size <= maxDirSize }.sumOf { it.size })
    }

    private fun updateDirSizes(currentDir: Directory, fileSystem: MutableMap<String, Directory>) {
//        println("current dir name:" + currentDir.name)
        if (currentDir.files.isEmpty()) {
            currentDir.size = 0
            return
        } else if (fileSystem[currentDir.name]!!.files.all { it.size != -1 }) {
//            println("no sub dirs: $currentDir")
            fileSystem[currentDir.name]!!.size = fileSystem[currentDir.name]!!.files.sumOf { it.size }
        } else {
            for (subDir in fileSystem[currentDir.name]!!.files.filterIsInstance<Directory>()) {
                updateDirSizes(subDir, fileSystem)
            }
            fileSystem[currentDir.name]!!.size = fileSystem[currentDir.name]!!.files.sumOf { it.size }
        }
//        println(currentDir)
    }

    private fun parseFile(parent: Directory, line: String, idx: Int): AdvFile {
        val file = if (line.startsWith("dir")) {
            Directory(parent.name + line.replace("dir ", ""), idx, parent, mutableListOf())
        } else {
            val (sizeStr, name) = line.split(" ").zipWithNext().single()
            FlatFile(parent.name + name, idx, parent, sizeStr.toInt())
        }
//        println("parsed file $file")
        return file
    }

    override fun solvePartTwo(lines: List<String>) {
        val requiredUnusedSpace = 30000000

    }

    sealed interface AdvFile {
        val name: String
        val idx: Int
        val parent: AdvFile?
        val size: Int
    }

    data class Directory(
        override val name: String,
        override val idx: Int,
        override val parent: Directory?,
        val files: MutableList<AdvFile>,
        override var size: Int = -1,
    ) : AdvFile {
        override fun toString(): String {
            return "DIR('$name', ..=${parent?.name}, ls=$files, size=$size)"
        }
    }

    data class FlatFile(override val name: String, override val idx: Int, override val parent: AdvFile?, override val size: Int) : AdvFile {
        override fun toString(): String {
            return "FILE('$name', ..=${parent?.name}, size=$size)"
        }
    }
}