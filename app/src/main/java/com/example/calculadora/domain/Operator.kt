package com.example.calculadora.domain

/**
 * Operadores soportados por la calculadora.
 * El símbolo es el mismo que se muestra en pantalla y se guarda en la expresión.
 */
enum class Operator(val symbol: Char) {
    ADD('+'),
    SUBTRACT('−'),
    MULTIPLY('×'),
    DIVIDE('÷');

    companion object {
        fun fromChar(c: Char): Operator? = entries.firstOrNull { it.symbol == c }

        fun isOperator(c: Char): Boolean = fromChar(c) != null
    }
}
