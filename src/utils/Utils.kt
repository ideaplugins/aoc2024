package utils

fun <T> assertEquals(expected: T, actual: T) {
    check(actual == expected) { "Check failed. Expected '$expected' but got '$actual'" }
}
