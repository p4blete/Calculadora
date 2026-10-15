package com.example.calculadora.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.calculadora.domain.CalcResult
import com.example.calculadora.domain.CalculatorEngine
import com.example.calculadora.domain.Operator

/**
 * Recibe las acciones de la UI, aplica las reglas de entrada y expone el estado.
 * Al vivir en un ViewModel, el estado sobrevive a la rotación de pantalla.
 */
class CalculatorViewModel : ViewModel() {

    var state by mutableStateOf(CalculatorState())
        private set

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Digit -> enterDigit(action.digit)
            CalculatorAction.Decimal -> enterDecimal()
            is CalculatorAction.Operation -> enterOperator(action.operator)
            CalculatorAction.Calculate -> calculate()
            CalculatorAction.Delete -> delete()
            CalculatorAction.Clear -> clear()
            CalculatorAction.ClearHistory -> state = state.copy(history = emptyList())
            is CalculatorAction.RestoreFromHistory -> restore(action.item)
        }
    }

    private fun enterDigit(digit: Int) {
        // Si en pantalla hay un resultado, un dígito nuevo empieza otra operación.
        val base = if (state.isResult) "" else state.expression
        val current = currentNumber(base)

        val newExpression = when {
            current.count { it.isDigit() } >= MAX_DIGITS -> return
            current == "0" -> base.dropLast(1) + digit // evita "007"
            else -> base + digit
        }
        updateExpression(newExpression)
    }

    private fun enterDecimal() {
        val base = if (state.isResult) "" else state.expression
        val current = currentNumber(base)

        val newExpression = when {
            current.contains('.') -> return // un número solo puede tener un punto
            current.isEmpty() -> base + "0."
            else -> "$base."
        }
        updateExpression(newExpression)
    }

    private fun enterOperator(operator: Operator) {
        // Si hay un resultado, se sigue operando con él (p. ej. "8" → "8+").
        val expression = state.expression
        val lastOperator = expression.lastOrNull()?.let(Operator::fromChar)

        val newExpression = when {
            expression.isEmpty() ->
                if (operator == Operator.SUBTRACT) operator.symbol.toString() else return

            lastOperator == null -> expression.removeSuffix(".") + operator.symbol

            lastOperator == operator -> return

            // Permite números negativos tras × o ÷, por ejemplo "5×−3".
            operator == Operator.SUBTRACT &&
                (lastOperator == Operator.MULTIPLY || lastOperator == Operator.DIVIDE) ->
                expression + operator.symbol

            // Pulsar otro operador sustituye al anterior: "5+" → "5×".
            else -> {
                val withoutOperators = expression.trimEnd { Operator.isOperator(it) }
                if (withoutOperators.isEmpty()) return
                withoutOperators + operator.symbol
            }
        }
        updateExpression(newExpression)
    }

    private fun calculate() {
        val expression = state.expression
        // Si no hay ninguna operación (por ejemplo "42"), no hay nada que calcular.
        if (state.isResult || CalculatorEngine.preview(expression) == null) return

        when (val result = CalculatorEngine.evaluate(expression)) {
            is CalcResult.Success -> {
                val formatted = CalculatorEngine.format(result.value)
                val entry = HistoryItem(
                    expression = CalculatorEngine.trimIncomplete(expression),
                    result = formatted,
                )
                state = state.copy(
                    expression = formatted,
                    preview = "",
                    error = null,
                    isResult = true,
                    history = (listOf(entry) + state.history).take(MAX_HISTORY),
                )
            }
            is CalcResult.Error -> state = state.copy(preview = "", error = result.message)
        }
    }

    /** Borra el último carácter (dígito, punto u operador). */
    private fun delete() {
        updateExpression(state.expression.dropLast(1))
    }

    /** Borra toda la entrada, pero conserva el historial. */
    private fun clear() {
        state = CalculatorState(history = state.history)
    }

    private fun restore(item: HistoryItem) {
        state = state.copy(expression = item.result, preview = "", error = null, isResult = true)
    }

    /** Actualiza la expresión y recalcula el resultado en tiempo real. */
    private fun updateExpression(expression: String) {
        val preview = CalculatorEngine.preview(expression)
        state = state.copy(
            expression = expression,
            isResult = false,
            preview = (preview as? CalcResult.Success)?.let { CalculatorEngine.format(it.value) } ?: "",
            error = (preview as? CalcResult.Error)?.message,
        )
    }

    /** El número que se está escribiendo ahora mismo (lo que hay tras el último operador). */
    private fun currentNumber(expression: String): String =
        expression.takeLastWhile { it.isDigit() || it == '.' }

    private companion object {
        const val MAX_DIGITS = 15
        const val MAX_HISTORY = 50
    }
}
