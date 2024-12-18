package year2024

import utils.*
import java.util.*

private fun parse(test: Boolean = false): List<String> =
    readInput(2024, 18, test)

private fun buildMaze(size: Int, corruptedCoordinates: Set<Coordinate>): Grid<Char> =
    GridByLists(
        List(size) { y ->
            List(size) { x ->
                if (Coordinate(x, y) in corruptedCoordinates) '#' else '.'
            }
        }
    )

private fun Cell<Char>.isCorrupted(): Boolean = value == '#'

private fun Grid<Char>.findShortestDistance(start: Coordinate, end: Coordinate): Int? {
    var distances: Grid<Int> = GridByLists(List(height) { List(width) { Int.MAX_VALUE } })
    val queue = LinkedList<Pair<Coordinate, Int>>().apply { offer(start to 0) }
    while (queue.isNotEmpty()) {
        val (coord, weight) = queue.poll()
        if (weight < distances.getOrDefault(coord, Int.MAX_VALUE).value) {
            distances = distances.with(coord, weight)
            this[coord]?.nesw()
                .orEmpty()
                .filter { !it.isCorrupted() }
                .forEach {
                    queue.offer(it.coordinate to weight + 1)
                }
        }
    }
    return distances[end]?.value
}

private fun part1(input: List<String>, size: Int, count: Int): Int? {
    val corruptedCoordinates = input.take(count)
        .map { it.asInts().toList() }
        .map { (x, y) -> Coordinate(x, y) }
        .toSet()
    val grid = buildMaze(size, corruptedCoordinates)
    return grid.findShortestDistance(grid.first().coordinate, grid.last().coordinate)
}

private fun Grid<Char>.existsPath(start: Coordinate, end: Coordinate): Boolean {
    val visited = mutableSetOf<Coordinate>()
    val queue = LinkedList<Coordinate>().apply { offer(start) }
    while (queue.isNotEmpty()) {
        val coord = queue.poll()
        if (coord == end) {
            return true
        } else {
            visited.add(coord)
            this[coord]?.nesw()
                .orEmpty()
                .filter { !it.isCorrupted() }
                .map { it.coordinate }
                .filter { it !in visited && it !in queue }
                .forEach {
                    queue.offer(it)
                }
        }
    }
    return false
}

private fun part2(input: List<String>, size: Int, min: Int): Coordinate {
    val corruptedCoordinates = input.map { it.asInts().toList() }
        .map { (x, y) -> Coordinate(x, y) }
    (min + 1..corruptedCoordinates.size).forEach {
        val grid = buildMaze(size, corruptedCoordinates.take(it).toSet())
        if (!grid.existsPath(grid.first().coordinate, grid.last().coordinate)) {
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
