package utils

fun <T> List<T>.removeAt(position: Int): List<T> =
    filterIndexed { index, _ -> index != position }

fun <T> cartesianProduct(dimensions: Int, a: List<T>): Sequence<List<T>> =
    (1..dimensions)
        .asSequence()
        .fold(emptySequence<List<T>>()) { acc, i -> acc + listOf(a) }
        .fold(sequenceOf(listOf())) { acc, set ->
            acc.flatMap { list -> set.map { element -> list + element } }
        }

val <T> List<T>.head: T
    get() = first()

val <T> List<T>.tail: List<T>
    get() = drop(1)

val <T> List<T>.headAndTail: Pair<T, List<T>>
    get() = head to tail

val <T> List<T>.last: T
    get() = last()

val <T> List<T>.init: List<T>
    get() = dropLast(1)

val <T> List<T>.initAndLast: Pair<List<T>, T>
    get() = init to last

val <T> Sequence<T>.head: T
    get() = first()

val <T> Sequence<T>.tail: Sequence<T>
    get() = drop(1)

val <T> Sequence<T>.headAndTail: Pair<T, Sequence<T>>
    get() = head to tail

val <T> Sequence<T>.last: T
    get() = last()

val <T> Sequence<T>.init: Sequence<T>
    get() = toList().dropLast(1).asSequence()

val <T> Sequence<T>.initAndLast: Pair<Sequence<T>, T>
    get() = init to last

fun <T> List<T>.countWhile(predicate: (T) -> Boolean): Int {
    var count = 0
    var i = 0
    while (i < size && predicate(this[i++])) {
        count++
    }
    return count
}

fun <T> List<T>.countLastWhile(predicate: (T) -> Boolean): Int {
    var count = 0
    var i = size
    while (i > 0 && predicate(this[--i])) {
        count++
    }
    return count
}
