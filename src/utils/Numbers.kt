package utils

fun Long.lcm(other: Long): Long =
    (this * other) / gcd(other)

tailrec fun Long.gcd(other: Long): Long =
    if (other == 0L) {
        this
    } else {
        other.gcd(this % other)
    }

fun Int.isEven(): Boolean = this % 2 == 0

fun Int.isOdd(): Boolean = this % 2 == 1

fun Int?.orZero(): Int = this ?: 0
