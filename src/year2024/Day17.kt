package year2024

import utils.*

private enum class OpCode {
    ADV,
    BXL,
    BST,
    JNZ,
    BXC,
    OUT,
    BDV,
    CDV,
}

class Computer(
    var regA: Long,
    var regB: Long,
    var regC: Long,
    val instructions: List<Int>,
) {
    private var ip: Int = 0

    fun run(): List<Int> {
        val output = mutableListOf<Int>()
        while (ip < instructions.size) {
            val opcode = OpCode.entries[instructions[ip++]]
            val operand = instructions[ip++]
            when (opcode) {
                OpCode.ADV -> regA /= pow2L(comboOperand(operand))
                OpCode.BXL -> regB = regB.xor(operand.toLong())
                OpCode.BST -> regB = comboOperand(operand) % 8
                OpCode.JNZ -> if (regA != 0L) ip = operand
                OpCode.BXC -> regB = regB.xor(regC)
                OpCode.OUT -> output.add((comboOperand(operand) % 8).toInt())
                OpCode.BDV -> regB = regA / pow2L(comboOperand(operand))
                OpCode.CDV -> regC = regA / pow2L(comboOperand(operand))
            }
        }
        return output.toList()
    }

    private fun comboOperand(n: Int): Long =
        when (n) {
            in 0..3 -> n.toLong()
            4 -> regA
            5 -> regB
            6 -> regC
            else -> error("Invalid combo operand")
        }
}

private fun parse(test: Boolean = false): Computer =
    readInput(2024, 17, test).let {
        Computer(
            regA = it.find("Register A:").toLong(),
            regB = it.find("Register B:").toLong(),
            regC = it.find("Register C:").toLong(),
            instructions = it.find("Program:").asInts().toList(),
        )
    }

private fun part1(computer: Computer): String = computer.run().joinToString(",")

private fun calculateB(a: Long): Long {
    val b = (a % 8).xor(5)
    val c = a / pow2L(b)
    return b.xor(6).xor(c)
}

private fun part2a(computer: Computer): Long =
    generateSequence(0L) { it + 1 }
        .first {
            Computer(it, computer.regB, computer.regC, computer.instructions).run() == computer.instructions
        }

private fun calculateA(bMod8: Int, aMul: Long): List<Long> =
    (0..7)
        .filter {
            it.xor(5)
                .xor(6).toLong()
                .xor((8 * aMul + it) / 1.shl(it.xor(5)))
                .and(7)
                .toInt() == bMod8
        }
        .map { 8 * aMul + it }

private fun part2b(computer: Computer): Long =
    computer.instructions
        .reversed()
        .asSequence()
        .runningFold(listOf(0L)) { acc, n ->
            acc.flatMap { calculateA(n, it) }
        }
        .last()
        .first()

fun main() {
    assertEquals("4,6,3,5,6,3,5,2,1,0", part1(parse(true)))
    assertEquals("2,7,6,5,6,0,2,3,1", part1(parse()))
    assertEquals(117440, part2a(Computer(2024, 0, 0, listOf(0, 3, 5, 4, 3, 0))))
    assertEquals(107416870455451, part2b(parse()))
}
