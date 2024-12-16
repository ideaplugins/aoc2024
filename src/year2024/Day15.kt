package year2024

import utils.*

private data class Problem(val grid: Grid<Char>, val instructions: List<Direction>)

private fun Char.isRobot(): Boolean = this == '@'

private fun Char.isBox(): Boolean = this == 'O' || this == '[' || this == ']'

private fun Char.isWall(): Boolean = this == '#'

private fun Char.isFree(): Boolean = this == '.'

private fun Char.toDirection(): Direction =
    when (this) {
        '^' -> Direction.UP
        'v' -> Direction.DOWN
        '<' -> Direction.LEFT
        '>' -> Direction.RIGHT
        else -> error("Invalid direction: $this")
    }

private fun Coordinate.gps(): Int = y * 100 + x

private fun parse(test: Boolean = false): Problem =
    readInput(2024, 15, test)
        .let {
            Problem(
                CharGrid(it.takeWhile { it.isNotBlank() }),
                it.takeLastWhile { it.isNotBlank() }.joinToString("").map { it.toDirection() },
            )
        }

private fun expandIt(problem: Problem): Problem {
    val list = problem.grid.yRange.map { y ->
        problem.grid.xRange.map { x ->
            when (problem.grid[Coordinate(x, y)]?.value) {
                '#' -> "##"
                '.' -> ".."
                'O' -> "[]"
                '@' -> "@."
                else -> error("Invalid character: ${problem.grid[Coordinate(x, y)]}")
            }
        }.joinToString("")
    }
    return problem.copy(grid = CharGrid(list))
}

private fun part1(problem: Problem): Int =
    problem.instructions
        .fold(problem.grid) { grid, direction ->
            val robot = grid.first { it.value.isRobot() }
            val next = robot.next(direction)
            when {
                next == null || next.value.isWall() -> grid
                next.value.isFree() -> grid.with(next.coordinate, '@').with(robot.coordinate, '.')
                else -> {
                    val boxes = generateSequence(next) { it.next(direction) }
                        .takeWhile { it.value.isBox() }
                        .toList()
                    val nextAfterBoxes = boxes.last().next(direction)
                    when {
                        nextAfterBoxes?.value?.isFree() == true -> {
                            grid.with(nextAfterBoxes.coordinate, 'O')
                                .with(boxes.first().coordinate, '@')
                                .with(robot.coordinate, '.')
                        }
                        else -> grid
                    }
                }
            }
        }
        .filter { it.value.isBox() }
        .sumOf { it.coordinate.gps() }

private fun influencedBy(cell: Cell<Char>, direction: Direction): Set<Cell<Char>> {
    val queue = ArrayDeque(listOf(cell))
    val influenced = mutableSetOf<Cell<Char>>()
    while (queue.isNotEmpty()) {
        val cell = queue.removeFirst()
        val next = cell.next(direction)
        if (next?.value?.isBox() == true) {
            if (next.value == '[') {
                influenced.add(next)
                queue.add(next)
                if (next.next(Direction.RIGHT) !in queue) {
                    influenced.add(next.next(Direction.RIGHT)!!)
                    queue.add(next.next(Direction.RIGHT)!!)
                }
            }
            if (next.value == ']' && next.next(Direction.LEFT) !in influenced) {
                influenced.add(next.next(Direction.LEFT)!!)
                queue.add(next.next(Direction.LEFT)!!)
                influenced.add(next)
                queue.add(next)
            }
        }
    }
    return influenced.toSet()
}

private fun part2(problem: Problem): Int {
    val newProblem = expandIt(problem)
    return newProblem.instructions
        .fold(newProblem.grid) { grid, direction ->
            val robot = grid.first { it.value.isRobot() }
            val next = robot.next(direction)
            when {
                next == null || next.value.isWall() -> grid
                next.value.isFree() -> grid.with(next.coordinate, '@').with(robot.coordinate, '.')
                direction.isHorizontal -> {
                    val boxes = generateSequence(next) { it.next(direction) }
                        .takeWhile { it.value.isBox() }
                        .toList()
                    val nextAfterBoxes = boxes.last().next(direction)
                    when {
                        nextAfterBoxes?.value?.isFree() == true -> {
                            boxes.reversed()
                                .fold(grid) { g, c ->
                                    g.with(c.coordinate.next(direction), c.value)
                                }
                                .with(boxes.first().coordinate, '@')
                                .with(robot.coordinate, '.')
                        }
                        else -> grid
                    }
                }
                else -> {
                    val influenced = influencedBy(robot, direction)
                    if (influenced.isNotEmpty() && influenced.all { it.next(direction) in influenced || it.next(direction)?.value?.isFree() == true }) {
                        influenced.fold(grid) { g, c ->
                            g.with(c.coordinate.next(direction), c.value).with(c.coordinate, '.')
                        }.with(robot.coordinate, '.').with(robot.coordinate.next(direction), '@')
                    } else {
                        grid
                    }
                }
            }
        }
        .filter { it.value == '[' }
        .sumOf { it.coordinate.gps() }
}

fun main() {
    assertEquals(10092, part1(parse(true)))
    assertEquals(1412971, part1(parse()))
    assertEquals(9021, part2(parse(true)))
    assertEquals(1429299, part2(parse()))
}
