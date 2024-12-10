package year2024

import utils.assertEquals
import utils.readInputSeq
import kotlin.math.abs

private fun part1(input: Sequence<String>): Int =
    input.map { it.substringBefore(" ") to it.substringAfterLast(" ") }
        .unzip()
        .let { (l1, l2) -> l1.map { it.toInt() }.sorted() to l2.map { it.toInt() }.sorted() }
        .let { (l1, l2) -> l1.zip(l2) { a, b -> abs(a - b) } }
        .sum()

private fun part2(input: Sequence<String>): Int =
    input.map { it.substringBefore(" ") to it.substringAfterLast(" ") }
        .unzip()
        .let { (l1, l2) -> l1.map { it.toInt() }.sorted() to l2.map { it.toInt() }.sorted() }
        .let { (l1, l2) ->
            l1.fold(0) { acc, i ->
                l2.count { it == i } * i + acc
            }
        }

fun main() {
    assertEquals(11, part1(readInputSeq(2024, 1, true)))
    assertEquals(2166959, part1(readInputSeq(2024, 1)))
    assertEquals(31, part2(readInputSeq(2024, 1, true)))
    assertEquals(23741109, part2(readInputSeq(2024, 1)))
}
