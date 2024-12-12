package year2024

import utils.*

private fun parse(test: Boolean = false): CharGrid =
    CharGrid(readInput(2024, 12, test))

private fun getRegions(grid: CharGrid): List<Set<Cell<Char>>> {
    val seen = mutableSetOf<Cell<Char>>()
    val regions = mutableListOf<Set<Cell<Char>>>()
    while (seen.size < grid.size) {
        val firstCell = grid.first { it !in seen }
        val queue = ArrayDeque(listOf(firstCell))
        val current = mutableSetOf<Cell<Char>>()
        while (queue.isNotEmpty()) {
            val cell = queue.removeLast()
            current.add(cell)
            queue.addAll(cell.nesw().filter { it !in current && it.value == firstCell.value })
        }
        seen.addAll(current)
        regions.add(current.toSet())
    }
    return regions.toList()
}

private fun part1(grid: CharGrid): Int =
    getRegions(grid).sumOf { region ->
        val area = region.size
        val perimeter = region.sumOf { 4 - it.nesw().count { it in region } }
        area * perimeter
    }

private fun part2(grid: CharGrid): Int =
    getRegions(grid).sumOf { region ->
        val area = region.size
        val sides =
            (-1..grid.height).sumOf { y ->
                (-1..grid.width).sumOf { x ->
                    val ul = grid[Coordinate(x, y)] in region
                    val ur = grid[Coordinate(x + 1, y)] in region
                    val dl = grid[Coordinate(x, y + 1)] in region
                    val dr = grid[Coordinate(x + 1, y + 1)] in region
                    val sum = 1.takeIf { ul }.orZero() + 1.takeIf { ur }.orZero() + 1.takeIf { dl }.orZero() + 1.takeIf { dr }.orZero()
                    when {
                        sum == 1 || sum == 3 -> 1
                        (!ul && ur && dl && !dr) || (ul && !ur && !dl && dr) -> 2
                        else -> 0
                    } as Int
                }
            }
        area * sides
    }

fun main() {
    assertEquals(1930, part1(parse(true)))
    assertEquals(140, part1(CharGrid(readInput("year2024/Day12_test1"))))
    assertEquals(772, part1(CharGrid(readInput("year2024/Day12_test2"))))
    assertEquals(1471452, part1(parse()))
    assertEquals(1206, part2(parse(true)))
    assertEquals(80, part2(CharGrid(readInput("year2024/Day12_test1"))))
    assertEquals(436, part2(CharGrid(readInput("year2024/Day12_test2"))))
    assertEquals(236, part2(CharGrid(readInput("year2024/Day12_test3"))))
    assertEquals(368, part2(CharGrid(readInput("year2024/Day12_test4"))))
    assertEquals(863366, part2(parse()))
}
