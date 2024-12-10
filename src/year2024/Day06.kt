package year2024

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import utils.*
import java.util.concurrent.atomic.AtomicInteger

private fun Cell<Char>.isGuard(): Boolean = value == '^'

private fun Cell<Char>?.isObstruction(): Boolean = this?.value == '#'

private fun CharGrid.startingStep(): PathStep<Char> =
    PathStep(first { it.isGuard() }, Direction.UP)

private fun CharGrid.patrol(): Sequence<PathStep<Char>> =
    generateSequence(startingStep()) { step ->
        generateSequence(step) { it.turn(MovementDirection.RIGHT) }
            .first { it.next()?.cell?.isObstruction() != true }
            .next()
    }

private fun part1(input: List<String>): Int =
    CharGrid(input).patrol().map { it.cell.coordinate }.distinct().count()

private fun part2(input: List<String>): Int {
    val grid = CharGrid(input)
    val obstructions = grid.filter { it.isObstruction() }.map { it.coordinate }.toSet()
    val startingStep = grid.startingStep()
    val candidates = grid.patrol().map { it.cell.coordinate }.toSet() - startingStep.cell.coordinate
    val loops = AtomicInteger()
    runBlocking(Dispatchers.Default) {
        candidates.forEach { candidate ->
            launch {
                val visited = mutableSetOf<PathStep<Char>>()
                var step: PathStep<Char>? = startingStep
                while (step != null && step !in visited) {
                    visited.add(step)
                    step = generateSequence(step) { it.turn(MovementDirection.RIGHT) }
                        .first { it.next()?.cell?.coordinate != candidate && it.next()?.cell?.coordinate !in obstructions }
                        .next()
                }
                if (step in visited) {
                    loops.incrementAndGet()
                }
            }
        }
    }
    return loops.get()
}

fun main() {
    assertEquals(41, part1(readInput(2024, 6, true)))
    assertEquals(5551, part1(readInput(2024, 6)))
    assertEquals(6, part2(readInput(2024, 6, true)))
    assertEquals(1939, part2(readInput(2024, 6)))
}
