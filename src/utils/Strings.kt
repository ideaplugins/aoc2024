package utils

import java.math.BigInteger
import java.security.MessageDigest

fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

fun String.blankToNull() = trim().ifBlank { null }

private val numRegex = Regex("""(-?\d+)""")

fun String.asInts(): Sequence<Int> =
    numRegex.findAll(this)
        .map { it.groupValues[1].toInt() }

fun String.asLongs(): Sequence<Long> =
    numRegex.findAll(this)
        .map { it.groupValues[1].toLong() }
