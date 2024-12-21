package year2024

import utils.*

private fun parse(test: Boolean = false): CharGrid =
    CharGrid(readInput(2024, 20, test))

private fun Char.isStart(): Boolean = this == 'S'

private fun Char.isEnd(): Boolean = this == 'E'

private fun Char.isWall(): Boolean = this == '#'

private fun solve(grid: CharGrid, length: Int, threshold: Int): Int {
    val maze = Maze(grid) { value.isWall() }
    val pathIndexed = maze.findUniquePath(grid.first { it.value.isStart() }.coordinate, grid.first { it.value.isEnd() }.coordinate)
        .mapIndexed { index, coordinate -> index to coordinate }
    return pathIndexed.sumOf { (i0, c0) ->
        pathIndexed.drop(i0 + 1)
            .count { (i1, c1) ->
                val mazeDist = i1 - i0
                val realDist = c0.distanceTo(c1)
                realDist <= mazeDist - threshold && realDist <= length
            }
    }
}

fun main() {
    assertEquals(8, solve(parse(true), 2, 11))
    assertEquals(1317, solve(parse(), 2, 100))
    assertEquals(285, solve(parse(true), 20, 50))
    assertEquals(982474, solve(parse(), 20, 100))
}
