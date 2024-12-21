package utils

fun <T> assertEquals(expected: T, actual: T) {
    check(actual == expected) { "Check failed. Expected '$expected' but got '$actual'" }
}

fun pow2(n: Int): Int = 1.shl(n)

fun pow2L(n: Int): Long = 1L.shl(n)

fun pow2L(n: Long): Long = pow2L(n.toInt())

fun Boolean?.orFalse(): Boolean = this == true
