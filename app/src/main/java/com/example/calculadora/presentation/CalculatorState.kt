package com.example.calculadora.presentation

/** Una entrada del historial: la operación y su resultado. */
data class HistoryItem(
    val expression: String,
    val result: String,
)

/**
 * Todo lo que la pantalla necesita para dibujarse. Es inmutable:
 * el ViewModel crea una copia nueva cada vez que algo cambia.
 */
data class CalculatorState(
    val expression: String = "",
    val preview: String = "",
    val error: String? = null,
    val isResult: Boolean = false,
    val history: List<HistoryItem> = emptyList(),
)
