package com.example.calculadora.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Muestra la expresión que se está escribiendo y, debajo, el resultado en tiempo real
 * o el mensaje de error (por ejemplo, la división entre cero).
 */
@Composable
fun CalculatorDisplay(
    expression: String,
    preview: String,
    error: String?,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.BottomEnd) {
        // reverseScrolling mantiene visible el final si la expresión es muy larga.
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState(), reverseScrolling = true),
        ) {
            val fontSize = expressionFontSize(expression.length)
            Text(
                text = expression.ifEmpty { "0" },
                style = MaterialTheme.typography.displayLarge,
                fontSize = fontSize,
                lineHeight = fontSize * 1.15f,
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = when {
                    error != null -> error
                    preview.isNotEmpty() -> "= $preview"
                    else -> ""
                },
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.End,
                color = if (error != null) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { liveRegion = LiveRegionMode.Polite }, // TalkBack lo anuncia
            )
        }
    }
}

/** Reduce el tamaño del texto a medida que la expresión crece. */
private fun expressionFontSize(length: Int): TextUnit = when {
    length <= 7 -> 72.sp
    length <= 10 -> 56.sp
    length <= 14 -> 44.sp
    else -> 36.sp
}
