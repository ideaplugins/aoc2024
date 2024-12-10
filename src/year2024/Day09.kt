package year2024

import utils.*

private fun parse(test: Boolean = false): String =
    readInput(2024, 9, test).first()

private fun part1(input: String): Long {
    val values = input.map { it.digitToInt() }
    val size = values.sum()
    return Disk(size)
        .apply {
            val start = values.runningFold(0) { acc, c ->
                acc + c
            }
            values.asSequence()
                .zip(start.asSequence())
                .forEachIndexed { index, (size, start) ->
                    if (index.isEven()) {
                        write(index / 2, start, size)
                    }
                }
            defrag()
        }
        .checksum()
}

private fun part2(input: String): Long =
    BlockDisk()
        .apply {
            val values = input.map { it.digitToInt() }
            val start = values.runningFold(0) { acc, c ->
                acc + c
            }
            values.zip(start)
                .chunked(2)
                .forEachIndexed { index, item ->
                    write(File(item[0].second, item[0].first, index))
                    item.getOrNull(1)?.also { free(FreeSpace(it.second, it.first)) }
                }
            defrag()
        }
        .checksum()

private const val FREE = -1

private sealed interface FileSystemItem {
    val location: Int
    val size: Int
    val checksum: Long
}

private data class File(override val location: Int, override val size: Int, val fileId: Int) : FileSystemItem {
    override val checksum: Long =
        (location..<location + size).sumOf { it * fileId.toLong() }
}

private data class FreeSpace(override val location: Int, override val size: Int) : FileSystemItem {
    override val checksum: Long = 0
}

private class Disk(size: Int) {

    private val data = IntArray(size) { FREE }

    fun checksum(): Long =
        data.mapIndexed { index, fileNr ->
            if (fileNr == FREE) 0 else fileNr.toLong() * index
        }.sum()

    private fun firstFreeBlock(): Int = data.indexOf(FREE)

    private fun lastNonFreeBlock(): Int = data.indexOfLast { it != FREE }

    fun write(fileId: Int, location: Int, size: Int) {
        (location..<location + size).forEach {
            data[it] = fileId
        }
    }

    private fun swap(src: Int, dst: Int) {
        val tmp = data[src]
        data[src] = data[dst]
        data[dst] = tmp
    }

    fun defrag() {
        while (firstFreeBlock() < lastNonFreeBlock()) {
            swap(firstFreeBlock(), lastNonFreeBlock())
        }
    }

    override fun toString(): String =
        data.map { if (it == FREE) "." else it.toString() }.joinToString("")
}

private class BlockDisk {

    private val data = mutableListOf<FileSystemItem>()

    fun checksum(): Long = data.sumOf { it.checksum }

    fun write(file: File) {
        data.add(file)
    }

    fun free(space: FreeSpace) {
        data.add(space)
    }

    fun defrag() {
        data.filterIsInstance<File>().maxOf { it.fileId }.downTo(0).forEach { fileId ->
            val itemIndex = data.indexOfFirst { it is File && it.fileId == fileId }
            val item = data[itemIndex] as File
            val relocationIndex =
                data.indexOfFirst { it is FreeSpace && it.size >= item.size && it.location < item.location }
            if (relocationIndex != -1) {
                val relocation = data[relocationIndex] as FreeSpace
                if (item.size == relocation.size) {
                    data[relocationIndex] = item.copy(location = relocation.location)
                    data[itemIndex] = relocation.copy(location = item.location)
                } else {
                    data[relocationIndex] = item.copy(location = relocation.location)
                    data[itemIndex] = relocation.copy(location = item.location, size = item.size)
                    data.add(
                        relocationIndex + 1,
                        FreeSpace(relocation.location + item.size, relocation.size - item.size)
                    )
                }
            }
        }
    }
}

fun main() {
    assertEquals(1928L, part1(parse(true)))
    assertEquals(6399153661894L, part1(parse()))
    assertEquals(2858L, part2(parse(true)))
    assertEquals(6421724645083L, part2(parse()))
}
