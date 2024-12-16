package year2024

import kotlin.test.Test

class RunAllTest {
    @Test
    fun runAll() {
        (1..25).forEach { day ->
            println("Testing day $day...")
            runCatching { test(day, 2024) }
                .onFailure { ex -> println("Failed on day $day: ${ex.message}.") }
        }
    }

    private fun test(day: Int, year: Int) {
        Class.forName("year$year.Day${day.toString().padStart(2, '0')}Kt")
            .getMethod("main")
            .invoke(null)
    }
}
