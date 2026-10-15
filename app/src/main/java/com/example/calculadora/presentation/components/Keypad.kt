package com.example.calculadora.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.calculadora.domain.Operator
import com.example.calculadora.presentation.CalculatorAction

private enum class KeyType { NUMBER, OPERATOR, FUNCTION, EQUALS }

private data class Key(
    val label: String,
    val action: CalculatorAction,
    val type: KeyType,
    val span: Int = 1,
    val description: String? = null,
)

private fun digit(n: Int) = Key(n.toString(), CalculatorAction.Digit(n), KeyType.NUMBER)

private fun operator(op: Operator, description: String) =
    Key(op.symbol.toString(), CalculatorAction.Operation(op), KeyType.OPERATOR, description = description)

/** La distribución del teclado se define como datos: cambiarla no requiere tocar la UI. */
private val keypadLayout: List<List<Key>> = listOf(
    listOf(
        Key("AC", CalculatorAction.Clear, KeyType.FUNCTION, span = 2, description = "Borrar todo"),
        Key("⌫", CalculatorAction.Delete, KeyType.FUNCTION, description = "Borrar último carácter"),
        operator(Operator.DIVIDE, "Dividir"),
    ),
    listOf(digit(7), digit(8), digit(9), operator(Operator.MULTIPLY, "Multiplicar")),
    listOf(digit(4), digit(5), digit(6), operator(Operator.SUBTRACT, "Restar")),
    listOf(digit(1), digit(2), digit(3), operator(Operator.ADD, "Sumar")),
    listOf(
        Key("0", CalculatorAction.Digit(0), KeyType.NUMBER, span = 2),
        Key(".", CalculatorAction.Decimal, KeyType.NUMBER, description = "Punto decimal"),
        Key("=", CalculatorAction.Calculate, KeyType.EQUALS, description = "Igual"),
    ),
)

private val KeySpacing = 12.dp

@Composable
fun Keypad(
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(KeySpacing)) {
        keypadLayout.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(KeySpacing),
            ) {
                row.forEach { key ->
                    val (container, content) = key.type.colors()
                    CalculatorButton(
                        label = key.label,
                        onClick = { onAction(key.action) },
                        containerColor = container,
                        contentColor = content,
                        contentDescription = key.description,
                        modifier = Modifier
                            .weight(key.span.toFloat())
                            .aspectRatio(key.span.toFloat()),
                    )
                }
            }
        }
    }
}

/** Colores del tema de Material 3: se adaptan solos al modo claro/oscuro y al color dinámico. */
@Composable
private fun KeyType.colors(): Pair<Color, Color> = with(MaterialTheme.colorScheme) {
    when (this@colors) {
        KeyType.NUMBER -> surfaceVariant to onSurfaceVariant
        KeyType.OPERATOR -> secondaryContainer to onSecondaryContainer
        KeyType.FUNCTION -> tertiaryContainer to onTertiaryContainer
        KeyType.EQUALS -> primary to onPrimary
    }
}
