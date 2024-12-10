package year2024

import utils.assertEquals
import utils.readInputSeq

private val mulRegex = """mul\(([0-9]{1,3}),([0-9]{1,3})\)""".toRegex()

private val withConditionRegex = """do\(\)|don't\(\)|mul\(([0-9]{1,3}),([0-9]{1,3})\)""".toRegex()

private fun part1(input: Sequence<String>): Int =
    input.flatMap { mulRegex.findAll(it) }
        .sumOf { it.groupValues[1].toInt() * it.groupValues[2].toInt() }

private fun part2(input: Sequence<String>): Int =
    input.flatMap { withConditionRegex.findAll(it) }
        .fold(EnabledCalculator(0) as Calculator) { accum, it ->
            if (it.groupValues[0] == "do()") {
                accum.enable()
            } else if (it.groupValues[0] == "don't()") {
                accum.disable()
            } else {
                accum.add(it.groupValues[1].toInt() * it.groupValues[2].toInt())
            }
        }.memory

sealed interface Calculator {
    val memory: Int
    fun add(value: Int): Calculator
    fun enable(): Calculator
    fun disable(): Calculator
}

data class EnabledCalculator(override val memory: Int) : Calculator {
    override fun add(value: Int): Calculator = EnabledCalculator(memory + value)

    override fun enable(): Calculator = this

    override fun disable(): Calculator = DisabledCalculator(memory)
}

data class DisabledCalculator(override val memory: Int) : Calculator {
    override fun add(value: Int): Calculator = this

    override fun enable(): Calculator = EnabledCalculator(memory)

    override fun disable(): Calculator = this
}

fun main() {
    assertEquals(161, part1(readInputSeq(2024, 3, true)))
    assertEquals(174561379, part1(readInputSeq(2024, 3)))
    assertEquals(48, part2(readInputSeq(2024, 3, true)))
    assertEquals(106921067, part2(readInputSeq(2024, 3)))
}
