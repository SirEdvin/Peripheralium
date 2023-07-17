package site.siredvin.peripheralium.tests

import com.mojang.math.Axis
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import site.siredvin.peripheralium.extra.dsl.rml1.*
import kotlin.test.assertEquals

class RMLTests {

    @Test
    fun baseTest() {
        val parser = RMLParser()
        parser.injectDefault()
        val parsed = parser.parse("t(1, 2, 3);r(x, 90, 0, 0, 0)")
        assertEquals(
            parsed,
            listOf(
                Translate(1f, 2f, 3f),
                Rotation(Axis.XP, 90f, 0f, 0f, 0f),
            ),
        )
    }

    @Test
    fun failedTest() {
        val parser = RMLParser()
        parser.injectDefault()
        assertThrows<LexemeDoesNotExistsException> {
            parser.parse("tr(1, 2, 3);r(x, 90, 0, 0, 0)")
        }
    }
}
