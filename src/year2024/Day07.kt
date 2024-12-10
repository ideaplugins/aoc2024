package year2024

import utils.*

private data class Line(val value: Long, val operands: List<Long>)

private fun parse(test: Boolean = false): Sequence<Line> = readInputSeq(2024, 7, test)
    .map { it.asLongs().headAndTail }
    .map { (head, tail) -> Line(head, tail.toList()) }

private fun part1(input: Sequence<Line>): Long =
    input.filter { line ->
        cartesianProduct(line.operands.size - 1, OperatorPart1.entries).any {
            val (firstOperand, otherOperands) = line.operands.headAndTail
            otherOperands.zip(it).fold(firstOperand) { acc, (operand, operator) ->
                when (operator) {
                    OperatorPart1.ADD -> acc + operand
                    OperatorPart1.MULTIPLY -> acc * operand
                }
            } == line.value
        }
    }.sumOf { it.value }

private fun part2(input: Sequence<Line>): Long =
    input.filter { line ->
        cartesianProduct(line.operands.size - 1, OperatorPart2.entries).any {
            val (firstOperand, otherOperands) = line.operands.headAndTail
            otherOperands.zip(it).fold(firstOperand) { acc, (operand, operator) ->
                when (operator) {
                    OperatorPart2.ADD -> acc + operand
                    OperatorPart2.MULTIPLY -> acc * operand
                    OperatorPart2.CONCATENATE -> (acc.toString() + operand.toString()).toLong()
                }
            } == line.value
        }
    }.sumOf { it.value }

private enum class OperatorPart1 { ADD, MULTIPLY }

private enum class OperatorPart2 { ADD, MULTIPLY, CONCATENATE }

fun main() {
    assertEquals(3749L, part1(parse(true)))
    assertEquals(66343330034722L, part1(parse()))
    assertEquals(11387L, part2(parse(true)))
    assertEquals(637696070419031L, part2(parse()))
}
