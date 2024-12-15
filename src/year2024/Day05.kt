package year2024

import utils.*

private fun getOrdering(input: List<String>): Map<Int, Set<Int>> = input.takeWhile { it.isNotBlank() }
    .map { it.split("|") }
    .map { (a, b) -> a.toInt() to b.toInt() }
    .groupBy({ it.first }) { it.second }
    .mapValues { (_, v) -> v.toSet() }

private fun getUpdates(input: List<String>): List<List<Int>> = input.dropWhile { it.isNotBlank() }.drop(1)
    .map { it.asInts().toList() }

private fun isValid(pages: List<Int>, ordering: Map<Int, Set<Int>>): Boolean =
    (1..pages.size).all {
        val (init, last) = pages.take(it).initAndLast
        init.intersect(ordering[last].orEmpty()).isEmpty()
    }

private fun part1(input: List<String>): Int {
    val ordering = getOrdering(input)
    return getUpdates(input).filter { isValid(it, ordering) }
        .sumOf { it[it.size / 2] }
}

private fun part2(input: List<String>): Int {
    val ordering = getOrdering(input)
    val requirements = (ordering.keys + ordering.values.flatten())
        .map { n -> n to ordering.filter { n in it.value }.keys }
        .toMap()
    return getUpdates(input)
        .filterNot { isValid(it, ordering) }
        .map { invalidUpdate ->
            invalidUpdate.fold(emptyList<Int>()) { acc, n ->
                acc + invalidUpdate.first {
                    it !in acc && requirements[it].orEmpty().all { it in acc || it !in invalidUpdate }
                }
            }
        }
        .sumOf { it[it.size / 2] }
}

fun main() {
    assertEquals(143, part1(readInput(2024, 5, true)))
    assertEquals(5087, part1(readInput(2024, 5)))
    assertEquals(123, part2(readInput(2024, 5, true)))
    assertEquals(4971, part2(readInput(2024, 5)))
}
