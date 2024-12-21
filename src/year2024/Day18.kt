package year2024

import utils.*

private fun parse(test: Boolean = false): List<String> =
    readInput(2024, 18, test)

private fun buildGrid(size: Int, corruptedCoordinates: Set<Coordinate>): Grid<Char> =
    GridByLists(
        List(size) { y ->
            List(size) { x ->
                if (Coordinate(x, y) in corruptedCoordinates) '#' else '.'
            }
        }
    )

private fun part1(input: List<String>, size: Int, count: Int): Int? {
    val corruptedCoordinates = input.take(count)
        .map { it.asInts().toList() }
        .map { (x, y) -> Coordinate(x, y) }
        .toSet()
    val grid = buildGrid(size, corruptedCoordinates)
    val maze = Maze(grid) { value == '#'}
    return maze.findShortestDistance(grid.first().coordinate, grid.last().coordinate)
}

private fun part2(input: List<String>, size: Int, min: Int): Coordinate {
    val corruptedCoordinates = input.map { it.asInts().toList() }
        .map { (x, y) -> Coordinate(x, y) }
    (min + 1..corruptedCoordinates.size).forEach {
        val grid = buildGrid(size, corruptedCoordinates.take(it).toSet())
        val maze = Maze(grid) { value == '#' }
        if (!maze.existsPath(grid.first().coordinate, grid.last().coordinate)) {
            return corruptedCoordinates[it - 1]
        }
    }
    error("No solution found")
}

fun main() {
    assertEquals(22, part1(parse(true), 7, 12))
    assertEquals(288, part1(parse(), 71, 1024))
    assertEquals(Coordinate(6, 1), part2(parse(true), 7, 12))
    assertEquals(Coordinate(52, 5), part2(parse(), 71, 1024))
}
