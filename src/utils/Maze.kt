package utils

import java.util.*

class Maze<T>(private val grid: Grid<T>, private val isWall: Cell<T>.() -> Boolean) {

    fun findShortestDistance(start: Coordinate, end: Coordinate): Int? =
        calculateDistances(start)[end]?.value

    fun findShortestPaths(start: Coordinate, end: Coordinate): List<Path> =
        buildList<Path> {
            val distances = calculateDistances(start)
            val queue = LinkedList<List<Cell<Int>>>().apply { offer(listOfNotNull(distances[start])) }
            while (queue.isNotEmpty()) {
                val path = queue.poll()
                if (path.last().coordinate == end) {
                    add(path.map { it.coordinate })
                } else {
                    path.last()
                        .nesw()
                        .filter { it.value == path.last().value + 1 }
                        .forEach { queue.offer(path + it) }
                }
            }
        }

    private fun calculateDistances(start: Coordinate): Grid<Int> =
        buildGrid<Int>(grid.width, grid.height) {
            val queue = LinkedList<Pair<Coordinate, Int>>().apply { offer(start to 0) }
            while (queue.isNotEmpty()) {
                val (coord, weight) = queue.poll()
                if (weight < getOrDefault(coord, Int.MAX_VALUE).value) {
                    this[coord] = weight
                    grid[coord]?.nesw()
                        .orEmpty()
                        .filter { !it.isWall() }
                        .forEach { queue.offer(it.coordinate to weight + 1) }
                }
            }
        }

    fun existsPath(start: Coordinate, end: Coordinate): Boolean {
        val visited = mutableSetOf<Coordinate>()
        val queue = LinkedList<Coordinate>().apply { offer(start) }
        while (queue.isNotEmpty()) {
            val coord = queue.poll()
            if (coord == end) {
                return true
            } else {
                visited.add(coord)
                grid[coord]?.nesw()
                    .orEmpty()
                    .filter { !it.isWall() }
                    .map { it.coordinate }
                    .filter { it !in visited && it !in queue }
                    .forEach { queue.offer(it) }
            }
        }
        return false
    }

    fun findPath(start: Coordinate, end: Coordinate): Path? {
        val visited = mutableSetOf<Coordinate>()
        val queue = LinkedList<Path>().apply { offer(listOf(start)) }
        while (queue.isNotEmpty()) {
            val path = queue.poll()
            val coord = path.last()
            visited.add(coord)
            if (coord == end) {
                return path
            } else {
                grid[coord]?.nesw()
                    .orEmpty()
                    .filter { !it.isWall() }
                    .map { it.coordinate }
                    .filter { it !in visited && it !in path }
                    .forEach { queue.offer(path + it) }
            }
        }
        return null
    }

    fun findUniquePath(start: Coordinate, end: Coordinate): Path =
        buildList {
            add(start)
            while (last() != end) {
                add(
                    grid[last()]?.nesw()
                        .orEmpty()
                        .filter { !isWall(it) }
                        .filter { it.coordinate !in this }
                        .first()
                        .coordinate
                )
            }
        }
}
