package com.example.calculadora.domain

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/** Resultado de evaluar una expresión: o un número o un mensaje de error. */
sealed interface CalcResult {
    data class Success(val value: BigDecimal) : CalcResult
    data class Error(val message: String) : CalcResult
}

/**
 * Lógica pura de cálculo, sin dependencias de Android, para poder probarla con tests unitarios.
 *
 * Evalúa expresiones como "2+3×4" respetando la precedencia (× y ÷ antes que + y −).
 * Usa BigDecimal para evitar errores de coma flotante (0.1 + 0.2 = 0.3, no 0.30000000000000004).
 */
object CalculatorEngine {

    const val ERROR_DIVISION_BY_ZERO = "No se puede dividir entre 0"
    const val ERROR_INVALID = "Expresión no válida"
    const val ERROR_OVERFLOW = "Número demasiado grande"

    private const val MAX_DECIMALS = 10
    private val MAX_VALUE: BigDecimal = BigDecimal.TEN.pow(18)
    private val mathContext = MathContext(20, RoundingMode.HALF_UP)

    private class DivisionByZeroException : ArithmeticException()

    /** Evalúa la expresión completa. Ignora operadores o puntos sueltos al final. */
    fun evaluate(expression: String): CalcResult {
        val clean = trimIncomplete(expression)
        if (clean.isEmpty()) return CalcResult.Error(ERROR_INVALID)

        return try {
            val (numbers, operators) = tokenize(clean)
            val result = compute(numbers, operators)
            if (result.abs() >= MAX_VALUE) {
                CalcResult.Error(ERROR_OVERFLOW)
            } else {
                CalcResult.Success(result)
            }
        } catch (e: DivisionByZeroException) {
            CalcResult.Error(ERROR_DIVISION_BY_ZERO)
        } catch (e: ArithmeticException) {
            CalcResult.Error(ERROR_OVERFLOW)
        } catch (e: NumberFormatException) {
            CalcResult.Error(ERROR_INVALID)
        }
    }

    /**
     * Resultado en tiempo real. Devuelve null si todavía no hay ninguna operación
     * (por ejemplo "42" o "−5"), porque no tendría sentido mostrar "= 42".
     */
    fun preview(expression: String): CalcResult? {
        val clean = trimIncomplete(expression)
        val hasOperation = clean.drop(1).any { Operator.isOperator(it) }
        return if (hasOperation) evaluate(clean) else null
    }

    /** Quita operadores y puntos decimales que el usuario aún no ha completado. */
    fun trimIncomplete(expression: String): String =
        expression.trimEnd { Operator.isOperator(it) || it == '.' }

    /** Convierte un número a texto: máximo 10 decimales, sin ceros sobrantes y con "−" tipográfico. */
    fun format(value: BigDecimal): String {
        val rounded = value.setScale(MAX_DECIMALS, RoundingMode.HALF_UP).stripTrailingZeros()
        if (rounded.signum() == 0) return "0"
        val plain = rounded.toPlainString()
        return if (plain.startsWith("-")) Operator.SUBTRACT.symbol + plain.drop(1) else plain
    }

    /**
     * Separa la expresión en números y operadores.
     * Un "−" al principio o justo después de otro operador se interpreta como signo negativo.
     */
    private fun tokenize(expression: String): Pair<List<BigDecimal>, List<Operator>> {
        val numbers = mutableListOf<BigDecimal>()
        val operators = mutableListOf<Operator>()
        val buffer = StringBuilder()
        var expectingNumber = true

        for (c in expression) {
            val operator = Operator.fromChar(c)
            when {
                operator == null -> {
                    buffer.append(c)
                    expectingNumber = false
                }
                expectingNumber && operator == Operator.SUBTRACT && buffer.isEmpty() -> {
                    buffer.append('-')
                }
                expectingNumber -> throw NumberFormatException("Operador inesperado: $c")
                else -> {
                    numbers += BigDecimal(buffer.toString())
                    buffer.clear()
                    operators += operator
                    expectingNumber = true
                }
            }
        }

        if (expectingNumber) throw NumberFormatException("La expresión está incompleta")
        numbers += BigDecimal(buffer.toString())
        return numbers to operators
    }

    /** Dos pasadas: primero × y ÷, después + y −. */
    private fun compute(numbers: List<BigDecimal>, operators: List<Operator>): BigDecimal {
        val terms = mutableListOf(numbers.first())
        val lowPriorityOps = mutableListOf<Operator>()

        operators.forEachIndexed { index, operator ->
            val next = numbers[index + 1]
            when (operator) {
                Operator.MULTIPLY -> terms[terms.lastIndex] = terms.last().multiply(next, mathContext)
                Operator.DIVIDE -> {
                    if (next.signum() == 0) throw DivisionByZeroException()
                    terms[terms.lastIndex] = terms.last().divide(next, mathContext)
                }
                Operator.ADD, Operator.SUBTRACT -> {
                    lowPriorityOps += operator
                    terms += next
                }
            }
        }

        var result = terms.first()
        lowPriorityOps.forEachIndexed { index, operator ->
            val next = terms[index + 1]
            result = if (operator == Operator.ADD) {
                result.add(next, mathContext)
            } else {
                result.subtract(next, mathContext)
            }
        }
        return result
    }
}
