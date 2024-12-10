package year2024

import utils.*

private fun parse(test: Boolean = false): CharGrid =
    CharGrid(readInput(2024, 10, test))

private fun CharGrid.traverse(onPathCompleted: (List<Cell<Char>>) -> Unit) {
    val queue = ArrayDeque(filter { it.value == '0' }.map { listOf(it) })
    while (queue.isNotEmpty()) {
        val path = queue.removeLast()
        val cell = path.last()
        if (cell.value == '9') {
            onPathCompleted(path)
        } else {
            cell.nesw()
                .filter { it.value == cell.value + 1 }
                .forEach { queue.add(path + it) }
        }
    }
}

private fun part1(grid: CharGrid): Int {
    val paths = mutableSetOf<Pair<Cell<Char>, Cell<Char>>>()
    grid.traverse { paths.add(it.first() to it.last()) }
    return paths.size
}

private fun part2(grid: CharGrid): Int {
    val paths = mutableSetOf<List<Cell<Char>>>()
    grid.traverse { paths.add(it) }
    return paths.size
}

fun main() {
    assertEquals(36, part1(parse(true)))
    assertEquals(607, part1(parse()))
    assertEquals(81, part2(parse(true)))
    assertEquals(1384, part2(parse()))
}
