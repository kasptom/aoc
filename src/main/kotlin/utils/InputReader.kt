package utils

import org.jetbrains.annotations.NotNull

class InputReader(@NotNull val resourcePath: String) {
    fun readLines(): List<String> = this::class.java.getResource("/$resourcePath")!!
        .readText()
        .split("\n","\r")
        .filter(String::isNotBlank)
}