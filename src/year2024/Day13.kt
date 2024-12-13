package year2024

import utils.*

private data class LongCoord(val x: Long, val y: Long)

private data class ClawMachine(
    val buttonA: LongCoord,
    val buttonB: LongCoord,
    val prize: LongCoord,
) {
    fun isValid(timesA: Long, timesB: Long): Boolean =
        prize.x == buttonA.x * timesA + buttonB.x * timesB && prize.y == buttonA.y * timesA + buttonB.y * timesB
}

private fun List<String>.find(prefix: String): LongCoord =
    first { it.startsWith(prefix) }
        .let {
            val components = it.substringAfter(prefix)
                .split(",")
                .map { it.trim() }
            LongCoord(
                components.first { it.startsWith("X") }.drop(2).toLong(),
                components.first { it.startsWith("Y") }.drop(2).toLong(),
            )
        }

private fun parse(test: Boolean = false): List<ClawMachine> =
    readInput(2024, 13, test)
        .chunked(4)
        .map {
            ClawMachine(
                buttonA = it.find("Button A: "),
                buttonB = it.find("Button B: "),
                prize = it.find("Prize: "),
            )
        }

private fun solve(machines: List<ClawMachine>): Long =
    machines.sumOf {
        val timesB = (it.buttonA.x * it.prize.y - it.buttonA.y * it.prize.x) / (it.buttonA.x * it.buttonB.y - it.buttonA.y * it.buttonB.x)
        val timesA = (it.prize.x - it.buttonB.x * timesB) / it.buttonA.x
        if (it.isValid(timesA, timesB)) {
            timesA * 3 + timesB
        } else {
            0
        }
    }

private fun part1(machines: List<ClawMachine>): Long =
    solve(machines)

private fun part2(machines: List<ClawMachine>): Long =
    solve(machines.map { it.copy(prize = it.prize.copy(x = it.prize.x + 10000000000000L, y = it.prize.y + 10000000000000L)) })

fun main() {
    assertEquals(480L, part1(parse(true)))
    assertEquals(35255L, part1(parse()))
    assertEquals(875318608908L, part2(parse(true)))
    assertEquals(87582154060429L, part2(parse()))
}
