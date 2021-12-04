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
