package year2024

import utils.*
import java.util.*

private data class Problem19(
    val available: List<String>,
    val desired: List<String>,
)

private fun parse(test: Boolean = false): Problem19 =
    readInput(2024, 19, test).let {
        Problem19(
            available = it.first().split(",").map { it.trim() },
            desired = it.drop(2),
        )
    }

private fun part1(problem: Problem19): Int =
    problem.desired.count { desired ->
        val queue = PriorityQueue<String> { a, b -> a.length.compareTo(b.length) }.apply { offer("") }
        while (queue.isNotEmpty() && desired !in queue) {
            val head  = queue.poll()
            val tail = desired.substringAfter(head)
            problem.available
                .asSequence()
                .filter { tail.startsWith(it) }
                .map { head + it }
                .filter { it !in queue }
                .forEach { queue.offer(it) }
        }
        desired in queue
    }

private fun part2(problem: Problem19): Long =
    problem.desired.sumOf { desired ->
        val seen = mutableMapOf<String, Long>()
        val queue = PriorityQueue<String> { a, b -> a.length.compareTo(b.length) }.apply { offer("") }
        while (queue.isNotEmpty()) {
            val head = queue.poll()
            val tail = desired.substringAfter(head)
            problem.available
                .asSequence()
                .filter { tail.startsWith(it) }
                .map { head + it }
                .onEach {
                    seen.compute(it) { _, v ->
                        v?.let { it + seen[head].orZero() } ?: seen[head] ?: 1
                    }
                }
                .filter { it !in queue }
                .forEach { queue.offer(it) }
        }
        seen[desired].orZero()
    }

fun main() {
    assertEquals(6, part1(parse(true)))
    assertEquals(322, part1(parse()))
    assertEquals(16L, part2(parse(true)))
    assertEquals(715514563508258L, part2(parse()))
}
