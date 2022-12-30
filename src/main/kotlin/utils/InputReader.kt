package utils

import org.jetbrains.annotations.NotNull
import java.io.File

class InputReader(@NotNull val resourcePath: String) {
    fun readLines(): List<String> = File(this::class.java.getResource("/$resourcePath")!!.file)
        .readLines()
}