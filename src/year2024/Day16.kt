package year2024

import utils.*
import java.util.*

private fun Char.isStart(): Boolean = this == 'S'

private fun Char.isEnd(): Boolean = this == 'E'

private fun Char.isWall(): Boolean = this == '#'

private fun parse(test: Boolean = false): CharGrid = CharGrid(readInput(2024, 16, test))

private fun solve(grid: CharGrid, onEndReached: (List<Cell<Char>>, Boolean) -> Unit = { _, _ -> }): Int {
    var distances: Grid<Int> = GridByLists(List(grid.height) { List(grid.width) { Int.MAX_VALUE } })
    val queue = LinkedList<Triple<List<Cell<Char>>, Direction, Int>>()
    grid.first { it.value.isStart() }.also {
        distances = distances.with(it.coordinate, 0)
        queue.offer(Triple(listOf(it), Direction.RIGHT, 0))
    }
    val end = grid.first { it.value.isEnd() }
    var minCost = Int.MAX_VALUE
    while (queue.isNotEmpty()) {
        val (path, dir, cost) = queue.poll()
        val cell = path.last()
        if (cell == end) {
            if (cost <= minCost) {
                onEndReached(path, cost < minCost)
                minCost = cost
            }
        } else {
            listOf(MovementDirection.FRONT to 1, MovementDirection.LEFT to 1001, MovementDirection.RIGHT to 1001)
                .asSequence()
                .map { dir.headTo(it.first) to it.second }
                .mapNotNull { (newDir, newCost) -> cell.next(newDir)?.let { Triple(it, newDir, newCost) } }
                .filter { (newCell, _, _) -> newCell !in path }
                .filter { (newCell, _, _) -> !newCell.value.isWall() }
                .map { (newCell, newDir, newCost) -> Triple(newCell, newDir, cost + newCost) }
                .filter { (_, _, newCost) -> newCost <= minCost }
                .forEach { (newCell, newDir, newCost) ->
                    if (distances[newCell.coordinate]!!.value >= newCost - 1001) {
                        queue.offer(Triple(path + newCell, newDir, newCost))
                        distances = distances.with(newCell.coordinate, newCost)
                    }
                }
        }
    }
    return minCost
}

private fun solve2(grid: CharGrid): Int =
    buildSet {
        solve(grid) { path, isNewMin ->
            if (isNewMin) {
                clear()
            }
            addAll(path)
        }
    }.size

fun main() {
    assertEquals(7036, solve(parse(true)))
    assertEquals(79404, solve(parse()))
    assertEquals(45, solve2(parse(true)))
    assertEquals(451, solve2(parse()))
}
