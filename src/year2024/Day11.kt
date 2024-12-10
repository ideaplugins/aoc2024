package year2024

import utils.*

private fun parse(test: Boolean = false): List<String> =
    readInput(2024, 11, test)

private fun part1(input: List<String>): Int =
    input.size + 1

private fun part2(input: List<String>): Int =
    input.size + 2

fun main() {
    check(part1(parse(true)) == 1)
    check(part1(parse()) == 1)
    check(part2(parse(true)) == 2)
    check(part2(parse()) == 2)
}
