package year2024

import utils.*

private fun parse(test: Boolean = false): List<List<Int>> =
    readInput(2024, 2, test).map { it.asInts().toList() }

private fun List<Int>.isSafeAscending(): Boolean =
    zipWithNext().all { (a, b) -> a < b && (b - a) in 1..3 }

private fun List<Int>.isSafeDescending(): Boolean =
    zipWithNext().all { (a, b) -> a > b && (a - b) in 1..3 }

private fun part1(input: List<List<Int>>): Int =
    input.count { it.isSafeAscending() || it.isSafeDescending() }

private fun part2(input: List<List<Int>>): Int {
    val (safe, unsafe) = input.partition { it.isSafeAscending() || it.isSafeDescending() }
    return safe.size + unsafe.count { list ->
        list.indices
            .map { list.removeAt(it) }
            .any { it.isSafeAscending() || it.isSafeDescending() }
    }
}

fun main() {
    assertEquals(2, part1(parse(true)))
    assertEquals(483, part1(parse()))
    assertEquals(4, part2(parse(true)))
    assertEquals(528, part2(parse()))
}
