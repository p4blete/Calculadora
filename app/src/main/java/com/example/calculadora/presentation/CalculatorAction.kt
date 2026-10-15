package com.example.calculadora.presentation

import com.example.calculadora.domain.Operator

/** Todo lo que el usuario puede hacer en la pantalla. */
sealed interface CalculatorAction {
    data class Digit(val digit: Int) : CalculatorAction
    data object Decimal : CalculatorAction
    data class Operation(val operator: Operator) : CalculatorAction
    data object Calculate : CalculatorAction
    data object Delete : CalculatorAction
    data object Clear : CalculatorAction
    data object ClearHistory : CalculatorAction
    data class RestoreFromHistory(val item: HistoryItem) : CalculatorAction
}
