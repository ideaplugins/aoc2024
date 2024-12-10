package year2024

import utils.*

private fun check1(cell: Cell<Char>, dir: CardinalDirection): Boolean =
    cell.toString(dir, 4) == "XMAS"

private fun check2(cell: Cell<Char>): Boolean {
    val diag1 = cell.next(CardinalDirection.NORTH_WEST)?.toString(CardinalDirection.SOUTH_EAST, 3)
    val diag2 = cell.next(CardinalDirection.SOUTH_WEST)?.toString(CardinalDirection.NORTH_EAST, 3)
    return (diag1 == "MAS" || diag1 == "SAM") && (diag2 == "MAS" || diag2 == "SAM")
}

private fun part1(input: List<String>): Int =
    CharGrid(input).sumOf { cell ->
        CardinalDirection.entries.count { check1(cell, it) }
    }

private fun part2(input: List<String>): Int =
    CharGrid(input).count {
        check2(it)
    }

fun main() {
    assertEquals(18, part1(readInput(2024, 4, true)))
    assertEquals(2534, part1(readInput(2024, 4)))
    assertEquals(9, part2(readInput(2024, 4, true)))
    assertEquals(1866, part2(readInput(2024, 4)))
}
