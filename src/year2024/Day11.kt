package year2024

import utils.*

private fun parse(test: Boolean = false): List<Long> =
    readInput(2024, 11, test).first().asLongs().toList()

private fun solve(input: List<Long>, blinks: Int): Long =
    generateSequence(input.map { it to 1L }.toMap()) {
        buildMap {
            it.forEach { (n, count) ->
                if (n == 0L) {
                    compute(1) { _, v -> v.orZero() + count }
                } else {
                    val str = n.toString()
                    val len = str.length
                    if (len.isEven()) {
                        compute(str.take(len / 2).toLong()) { _, v -> v.orZero() + count }
                        compute(str.takeLast(len / 2).toLong()) { _, v -> v.orZero() + count }
                    } else {
                        compute(n * 2024) { _, v -> v.orZero() + count }
                    }
                }
            }
        }
    }.drop(blinks).first().values.sum()

fun main() {
    assertEquals(55312L, solve(parse(true), 25))
    assertEquals(183484L, solve(parse(), 25))
    assertEquals(65601038650482L, solve(parse(true), 75))
    assertEquals(218817038947400L, solve(parse(), 75))
}
