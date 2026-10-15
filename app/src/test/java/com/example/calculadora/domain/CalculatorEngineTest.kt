package com.example.calculadora.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CalculatorEngineTest {

    private fun eval(expression: String): String =
        when (val result = CalculatorEngine.evaluate(expression)) {
            is CalcResult.Success -> CalculatorEngine.format(result.value)
            is CalcResult.Error -> result.message
        }

    @Test
    fun suma() {
        assertEquals("5", eval("2+3"))
    }

    @Test
    fun restaConResultadoNegativo() {
        assertEquals("−1", eval("2−3"))
    }

    @Test
    fun respetaLaPrecedencia() {
        assertEquals("14", eval("2+3×4"))
        assertEquals("1", eval("10−6÷2×3"))
    }

    @Test
    fun divisionConDecimales() {
        assertEquals("2.5", eval("5÷2"))
        assertEquals("0.3333333333", eval("1÷3"))
    }

    @Test
    fun sinErroresDeComaFlotante() {
        assertEquals("0.3", eval("0.1+0.2"))
    }

    @Test
    fun divisionEntreCero() {
        assertEquals(CalculatorEngine.ERROR_DIVISION_BY_ZERO, eval("8÷0"))
        assertEquals(CalculatorEngine.ERROR_DIVISION_BY_ZERO, eval("8÷0.0"))
    }

    @Test
    fun numerosNegativos() {
        assertEquals("−15", eval("5×−3"))
        assertEquals("−2", eval("−5+3"))
    }

    @Test
    fun ignoraOperadorFinal() {
        assertEquals("7", eval("7+"))
    }

    @Test
    fun numeroDemasiadoGrande() {
        assertEquals(CalculatorEngine.ERROR_OVERFLOW, eval("999999999999×999999999999"))
    }

    @Test
    fun previewSoloConOperacion() {
        assertNull(CalculatorEngine.preview("42"))
        assertNull(CalculatorEngine.preview("−5"))
        val preview = CalculatorEngine.preview("4+3×") as CalcResult.Success
        assertEquals("7", CalculatorEngine.format(preview.value))
    }
}
