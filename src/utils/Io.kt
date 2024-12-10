package utils

import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.readLines

fun readInput(year: Int, day: Int, test: Boolean = false): List<String> =
    readInput("year$year/Day${day.toString().padStart(2, '0')}${if (test) "_test" else ""}")

fun readInput(name: String): List<String> =
    Path("src/$name.txt").readLines()

fun readInputSeq(year: Int, day: Int, test: Boolean = false): Sequence<String> =
    readInputSeq("year$year/Day${day.toString().padStart(2, '0')}${if (test) "_test" else ""}")

fun readInputSeq(name: String): Sequence<String> =
    File("src/$name.txt").bufferedReader().lineSequence()
