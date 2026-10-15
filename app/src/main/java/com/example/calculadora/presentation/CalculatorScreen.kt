package com.example.calculadora.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculadora.presentation.components.CalculatorDisplay
import com.example.calculadora.presentation.components.HistoryPanel
import com.example.calculadora.presentation.components.Keypad

/** Conecta el ViewModel con la pantalla. */
@Composable
fun CalculatorRoute(
    modifier: Modifier = Modifier,
    viewModel: CalculatorViewModel = viewModel(),
) {
    CalculatorScreen(
        state = viewModel.state,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

/**
 * Pantalla sin estado propio (salvo si el historial está abierto):
 * recibe el estado y avisa de las acciones. Así se puede previsualizar y probar fácilmente.
 */
@Composable
fun CalculatorScreen(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showHistory by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Calculadora",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = { showHistory = !showHistory }) {
                Text(if (showHistory) "Volver" else "Historial")
            }
        }

        if (showHistory) {
            HistoryPanel(
                history = state.history,
                onItemClick = { item ->
                    onAction(CalculatorAction.RestoreFromHistory(item))
                    showHistory = false
                },
                onClear = { onAction(CalculatorAction.ClearHistory) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        } else {
            CalculatorDisplay(
                expression = state.expression,
                preview = state.preview,
                error = state.error,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Keypad(onAction = onAction)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CalculatorScreenPreview() {
    MaterialTheme {
        Surface {
            CalculatorScreen(
                state = CalculatorState(expression = "12+3×4", preview = "24"),
                onAction = {},
            )
        }
    }
}
