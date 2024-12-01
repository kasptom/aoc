package utils

/**
 * e.g.            [1 5]
 *   [1 2 3 4]     [2 6]
 *   [5 6 7 8] --> [3 7]
 *                 [4 8]
 */
fun <T> List<List<T>>.transpose(): List<List<T>> {
    val rowSize = first().size
    assert(all { it.size == rowSize }) { "All of the inner lists have to be the same size" }
    return (0 until rowSize).map { idx -> map { row -> row[idx] } }
}

fun <T> List<T>.permutations(): List<List<T>> {
    if (this.isEmpty()) return emptyList()

    fun allPermutations(list: List<T>): List<List<T>> {
        if (list.isEmpty()) return listOf(emptyList())

        val result: MutableList<List<T>> = mutableListOf()
        for (i in list.indices) {
            allPermutations(list - list[i]).forEach{
                    item -> result.add(item + list[i])
            }
        }
        return result
    }

    return allPermutations(this)
}

fun <T> List<T>.except(vararg elem: T): List<T> = filter { it !in elem }
fun <T> List<T>.except(elems: Set<T>): List<T> = filter { it !in elems }
