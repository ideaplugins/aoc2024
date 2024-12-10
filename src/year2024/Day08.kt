package year2024

import utils.*

private fun Char.isAntenna(): Boolean = this != '.'

private fun parse(test: Boolean = false): CharGrid =
    CharGrid(readInput(2024, 8, test))

private fun part1(grid: CharGrid): Int =
    grid.filter { it.value.isAntenna() }
        .flatMap { firstCell ->
            grid.dropWhile { it.coordinate != firstCell.coordinate }
                .drop(1)
                .filter { it.value == firstCell.value }
                .flatMap {
                    val delta = it.coordinate - firstCell.coordinate
                    listOf(firstCell.coordinate - delta, it.coordinate + delta)
                        .filter { it in grid.indices }
                }
        }
        .distinct()
        .size

private fun part2(grid: CharGrid): Int =
    grid.filter { it.value.isAntenna() }
        .flatMap { firstCell ->
            grid.dropWhile { it.coordinate != firstCell.coordinate }
                .drop(1)
                .filter { it.value == firstCell.value }
                .flatMap {
                    val delta = it.coordinate - firstCell.coordinate
                    val firstSeq = generateSequence(firstCell.coordinate) { it + delta }
                        .takeWhile { it in grid.indices }
                    val secondSeq = generateSequence(firstCell.coordinate) { it - delta }
                        .takeWhile { it in grid.indices }
                    firstSeq + secondSeq
                }
        }
        .distinct()
        .size

fun main() {
    assertEquals(14, part1(parse(true)))
    assertEquals(303, part1(parse()))
    assertEquals(34, part2(parse(true)))
    assertEquals(1045, part2(parse()))
}
