package utils

import java.util.*

class Maze<T>(private val grid: Grid<T>, private val wallPredicate: (Cell<T>) -> Boolean) {

    fun findShortestDistance(start: Coordinate, end: Coordinate): Int? {
        var distances: Grid<Int> = GridByLists(List(grid.height) { List(grid.width) { Int.MAX_VALUE } })
        val queue = LinkedList<Pair<Coordinate, Int>>().apply { offer(start to 0) }
        while (queue.isNotEmpty()) {
            val (coord, weight) = queue.poll()
            if (weight < distances.getOrDefault(coord, Int.MAX_VALUE).value) {
                distances = distances.with(coord, weight)
                grid[coord]?.nesw()
                    .orEmpty()
                    .filter { !wallPredicate(it) }
                    .forEach {
                        queue.offer(it.coordinate to weight + 1)
                    }
            }
        }
        return distances[end]?.value
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
                    .filter { !wallPredicate(it) }
                    .map { it.coordinate }
                    .filter { it !in visited && it !in queue }
                    .forEach {
                        queue.offer(it)
                    }
            }
        }
        return false
    }
}
