package year2024

import utils.*
import java.lang.Math.floorMod

private data class Robot(val position: Coordinate, val velocity: Vector2D) {
    fun move(grid: Grid<*>, times: Int = 1): Robot = copy(
        position = (position + velocity * times)
            .let { Coordinate(floorMod(it.x, grid.width), floorMod(it.y, grid.height)) }
    )
}

private fun parse(width: Int, height: Int, test: Boolean = false): SparseGrid<List<Robot>> =
    readInput(2024, 14, test)
        .map { it.split(" ") }
        .map { (p, v) ->
            val (px, py) = p.asInts().toList()
            val (vx, vy) = v.asInts().toList()
            Robot(Coordinate(px, py), Vector2D(vx, vy))
        }
        .let { SparseGrid(it.groupBy { it.position }, width, height) }

private fun part1(grid: SparseGrid<List<Robot>>, times: Int = 100): Int {
    val newGrid = SparseGrid(
        grid.flatMap { it.value.map { it.move(grid, times) } }.groupBy { it.position },
        grid.width,
        grid.height,
    )
    return newGrid.quadrants
        .map { q -> newGrid.filter { it.coordinate in q }.sumOf { it.value.size } }
        .fold(1) { acc, i -> acc * i }
}

private fun SparseGrid<List<Robot>>.print() {
    (0..<height).forEach { y ->
        (0..<width).forEach { x ->
            print(this[Coordinate(x, y)]?.value?.size ?: ".")
        }
        println()
    }
}

private fun part2(grid: SparseGrid<List<Robot>>): Int {
    var count = 0
    var newGrid = grid
    val n = 30
    var found = false
    while (!found) {
        newGrid = SparseGrid(
            newGrid.flatMap { it.value.map { it.move(newGrid) } }.groupBy { it.position },
            newGrid.width,
            newGrid.height
        )
        count++
        found = (0..<newGrid.height).any { y ->
            (0..<newGrid.width - n).any { x ->
                (0..n).map { Coordinate(x + it, y) }.all { it in newGrid.values }
            }
        }
    }
    // newGrid.print()
    return count
}

fun main() {
    assertEquals(12, part1(parse(11, 7, true)))
    assertEquals(208437768, part1(parse(101, 103)))
    assertEquals(7492, part2(parse(101, 103)))
}
