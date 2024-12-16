package utils

import kotlin.math.abs

enum class MovementDirection {
    RIGHT, LEFT, FRONT;
}

enum class Direction(val x: Int, val y: Int, private val degrees: Int) {
    UP(0, -1, 0),
    DOWN(0, 1, 180),
    RIGHT(1, 0, 90),
    LEFT(-1, 0, 270);

    fun headTo(direction: MovementDirection): Direction =
        when (direction) {
            MovementDirection.RIGHT -> turn(90)
            MovementDirection.LEFT -> turn(-90)
            MovementDirection.FRONT -> this
        }

    private fun turn(n: Int): Direction =
        Direction.entries.first { it.degrees == ((degrees + n + 360) % 360) }
}

val Direction.opposite: Direction
    get() = when (this) {
        Direction.UP -> Direction.DOWN
        Direction.DOWN -> Direction.UP
        Direction.RIGHT -> Direction.LEFT
        Direction.LEFT -> Direction.RIGHT
    }

val Direction.isHorizontal: Boolean
    get() = when (this) {
        Direction.RIGHT, Direction.LEFT -> true
        Direction.UP, Direction.DOWN -> false
    }

val Direction.isVertical: Boolean
    get() = !isHorizontal

enum class CardinalDirection(val x: Int, val y: Int) {
    NORTH(0, -1),
    SOUTH(0, 1),
    EAST(1, 0),
    WEST(-1, 0),
    NORTH_EAST(1, -1),
    NORTH_WEST(-1, -1),
    SOUTH_EAST(1, 1),
    SOUTH_WEST(-1, 1),
}

val CardinalDirection.opposite: CardinalDirection
    get() = when (this) {
        CardinalDirection.NORTH -> CardinalDirection.SOUTH
        CardinalDirection.SOUTH -> CardinalDirection.NORTH
        CardinalDirection.EAST -> CardinalDirection.WEST
        CardinalDirection.WEST -> CardinalDirection.EAST
        CardinalDirection.NORTH_EAST -> CardinalDirection.SOUTH_WEST
        CardinalDirection.NORTH_WEST -> CardinalDirection.SOUTH_EAST
        CardinalDirection.SOUTH_EAST -> CardinalDirection.NORTH_WEST
        CardinalDirection.SOUTH_WEST -> CardinalDirection.NORTH_EAST
    }

data class Coordinate(val x: Int, val y: Int) {

    fun next(direction: Direction): Coordinate =
        copy(x = x + direction.x, y = y + direction.y)

    fun next(direction: CardinalDirection): Coordinate =
        copy(x = x + direction.x, y = y + direction.y)

    fun distanceTo(other: Coordinate): Int = abs(x - other.x) + abs(y - other.y)

    operator fun plus(other: Coordinate): Coordinate =
        Coordinate(x + other.x, y + other.y)

    operator fun minus(other: Coordinate): Coordinate =
        Coordinate(x - other.x, y - other.y)

    operator fun times(n: Int): Coordinate =
        Coordinate(x * n, y * n)
}

typealias Vector2D = Coordinate

data class LongCoordinate(val x: Long, val y: Long) {

    fun next(direction: Direction): LongCoordinate =
        copy(x = x + direction.x, y = y + direction.y)

    fun next(direction: CardinalDirection): LongCoordinate =
        copy(x = x + direction.x, y = y + direction.y)

    fun distanceTo(other: LongCoordinate): Long = abs(x - other.x) + abs(y - other.y)

    operator fun plus(other: LongCoordinate): LongCoordinate =
        LongCoordinate(x + other.x, y + other.y)

    operator fun minus(other: LongCoordinate): LongCoordinate =
        LongCoordinate(x - other.x, y - other.y)
}

typealias LongVector2D = LongCoordinate

data class Coordinate3D(val x: Int, val y: Int, val z: Int) : Comparable<Coordinate3D> {

    val xy2DView: Coordinate = Coordinate(x = x, y = y)

    operator fun minus(delta: Int): Coordinate3D = copy(z = z - delta)

    override fun compareTo(other: Coordinate3D): Int =
        x.compareTo(other.x).takeUnless { it == 0 }
            ?: y.compareTo(other.y).takeUnless { it == 0 }
            ?: z.compareTo(other.z)
}

class Cell<T>(val value: T, val coordinate: Coordinate, private val grid: Grid<T>) {

    fun next(direction: CardinalDirection): Cell<T>? =
        grid[coordinate.next(direction)]

    fun next(direction: Direction): Cell<T>? =
        grid[coordinate.next(direction)]

    fun nesw(): List<Cell<T>> =
        listOfNotNull(
            next(CardinalDirection.NORTH),
            next(CardinalDirection.EAST),
            next(CardinalDirection.SOUTH),
            next(CardinalDirection.WEST),
        )

    operator fun plus(direction: CardinalDirection): Cell<T>? =
        grid[coordinate.next(direction)]

    fun iterate(direction: CardinalDirection): Sequence<Cell<T>> =
        generateSequence(this) { it + direction }

    override fun toString(): String = "Cell(value=$value, coordinate=$coordinate)"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Cell<*>
        if (value != other.value) return false
        if (coordinate != other.coordinate) return false
        return true
    }

    override fun hashCode(): Int {
        var result = value?.hashCode() ?: 0
        result = 31 * result + coordinate.hashCode()
        return result
    }
}

interface Grid<T> : Iterable<Cell<T>>, Plain {

    val width: Int

    val height: Int

    val size: Int
        get() = width * height

    override val xRange: IntRange
        get() = 0..<width

    override val yRange: IntRange
        get() = 0..<height

    val indices: Sequence<Coordinate>
        get() = sequence {
            yRange.forEach { y ->
                xRange.forEach { x ->
                    yield(Coordinate(x, y))
                }
            }
        }

    val quadrants: List<Quadrant>
        get() = listOf(
            Quadrant(0..<width / 2, 0..<height / 2),
            Quadrant(0..<width / 2, height / 2 + height % 2..<height),
            Quadrant(width / 2 + width % 2..<width, 0..<height / 2),
            Quadrant(width / 2 + width % 2..<width, height / 2 + height % 2..<height),
        )

    operator fun get(coord: Coordinate): Cell<T>?

    fun with(coord: Coordinate, value: T): Grid<T>

    override fun iterator(): Iterator<Cell<T>> =
        indices.mapNotNull { this[it] }.iterator()
}

class SparseGrid<T>(val values: Map<Coordinate, T>, override val width: Int, override val height: Int) : Grid<T> {
    override fun get(coord: Coordinate): Cell<T>? = values[coord]?.let { Cell(it, coord, this) }

    override fun with(coord: Coordinate, value: T): Grid<T> =
        SparseGrid(values + (coord to value), width, height)
}

class CharGrid(private val grid: List<String>) : Grid<Char> {

    override val width: Int = grid[0].length

    override val height: Int = grid.size

    override operator fun get(coord: Coordinate): Cell<Char>? =
        if (coord.y in grid.indices && coord.x in grid[coord.y].indices) {
            Cell(grid[coord.y][coord.x], coord, this)
        } else {
            null
        }

    override fun with(coord: Coordinate, value: Char): CharGrid =
        CharGrid(
            grid.toMutableList().apply {
                this[coord.y] = grid[coord.y].replaceRange(coord.x, coord.x + 1, value.toString())
            }.toList()
        )

    override fun toString(): String = grid.joinToString("\n")
}

class GridByLists<T>(private val grid: List<List<T>>) : Grid<T> {

    override val width: Int = grid[0].size

    override val height: Int = grid.size

    override operator fun get(coord: Coordinate): Cell<T>? =
        if (coord.y in grid.indices && coord.x in grid[coord.y].indices) {
            Cell(grid[coord.y][coord.x], coord, this)
        } else {
            null
        }

    override fun with(coord: Coordinate, value: T): Grid<T> =
        GridByLists(
            grid.toMutableList()
                .apply {
                    this[coord.y] = grid[coord.y].toMutableList()
                        .apply { this[coord.x] = value }
                        .toList()
                }
                .toList()
        )

    override fun toString(): String = grid.joinToString("\n")
}

fun Cell<Char>.toString(dir: CardinalDirection, count: Int): String =
    iterate(dir).take(count).joinToString("") { it.value.toString() }

data class RealCoordinate3D(val x: Double, val y: Double, val z: Double) : Comparable<RealCoordinate3D> {

    val xy2DView: RealCoordinate = RealCoordinate(x = x, y = y)

    operator fun plus(other: RealCoordinate3D): RealCoordinate3D =
        RealCoordinate3D(x + other.x, y + other.y, z + other.z)

    operator fun times(t: Double): RealCoordinate3D =
        RealCoordinate3D(x * t, y * t, z * t)

    override fun compareTo(other: RealCoordinate3D): Int =
        x.compareTo(other.x).takeUnless { it == 0 }
            ?: y.compareTo(other.y).takeUnless { it == 0 }
            ?: z.compareTo(other.z)
}

data class RealCoordinate(val x: Double, val y: Double) {

    operator fun minus(other: RealCoordinate): RealCoordinate =
        RealCoordinate(x - other.x, y - other.y)

    operator fun plus(other: RealCoordinate): RealCoordinate =
        RealCoordinate(x + other.x, y + other.y)

    operator fun times(t: Double): RealCoordinate =
        RealCoordinate(x * t, y * t)

    fun isClose(other: RealCoordinate): Boolean = x.isClose(other.x) && y.isClose(other.y)
}

fun Double.isClose(other: Double): Boolean = abs(this - other) < 1E-6

data class Line(val m: Double, val b: Double) {

    fun isParallel(other: Line): Boolean = m == other.m

    companion object {
        fun from(p0: RealCoordinate, p1: RealCoordinate): Line {
            val m = (p1.y - p0.y) / (p1.x - p0.x)
            val b = m * p0.x * -1 + p0.y
            return Line(m, b)
        }

        fun from(p: RealCoordinate, m: Double): Line =
            Line(m, p.y - m * p.x)
    }
}

interface Plain {
    val xRange: IntRange
    val yRange: IntRange
    operator fun contains(p: Coordinate): Boolean =
        p.x in xRange && p.y in yRange
}

data class PlainImpl(override val xRange: IntRange, override val yRange: IntRange) : Plain

typealias Quadrant = PlainImpl

data class PathStep<T>(val cell: Cell<T>, val direction: Direction) {

    fun next(): PathStep<T>? = cell.next(direction)?.let { copy(it) }

    fun turn(newDir: MovementDirection): PathStep<T> = copy(direction = direction.headTo(newDir))

    fun next(newDir: Direction): PathStep<T>? = cell.next(newDir)?.let { PathStep(it, newDir) }
}
