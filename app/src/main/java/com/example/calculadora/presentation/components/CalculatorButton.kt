package com.example.calculadora.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight

/**
 * Botón redondo de la calculadora. Con un ancho doble se convierte en una píldora.
 * [contentDescription] es lo que lee TalkBack (útil para símbolos como "⌫").
 */
@Composable
fun CalculatorButton(
    label: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = containerColor,
        contentColor = contentColor,
        modifier = modifier,
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = label,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Medium,
                modifier = if (contentDescription != null) {
                    Modifier.clearAndSetSemantics { this.contentDescription = contentDescription }
                } else {
                    Modifier
                },
            )
        }
    }
}
