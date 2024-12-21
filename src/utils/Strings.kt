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

fun List<String>.find(prefix: String): String =
    first { it.startsWith(prefix) }.substringAfter(prefix).trim()

fun CharSequence.countWhile(predicate: (Char) -> Boolean): Int {
    var count = 0
    var i = 0
    while (i < length && predicate(this[i++])) {
        count++
    }
    return count
}

fun CharSequence.countLastWhile(predicate: (Char) -> Boolean): Int {
    var count = 0
    var i = length
    while (i > 0 && predicate(this[--i])) {
        count++
    }
    return count
}
