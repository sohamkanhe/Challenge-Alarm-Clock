package com.example.alarm_clock.challenge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun MathPuzzleChallenge(onSolvedChange: (Boolean) -> Unit) {
    val puzzle = remember {
        val left = Random.nextInt(12, 41)
        val right = Random.nextInt(6, 24)
        val multiplier = Random.nextInt(2, 7)
        MathPuzzle(left, right, multiplier)
    }
    var answer by rememberSaveable { mutableStateOf("") }
    val solved = answer.toIntOrNull() == puzzle.answer

    LaunchedEffect(solved) {
        onSolvedChange(solved)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Solve to dismiss",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Text(
            text = "(${puzzle.left} + ${puzzle.right}) x ${puzzle.multiplier}",
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            value = answer,
            onValueChange = { answer = it.filter(Char::isDigit).take(5) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            label = { Text("Answer") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}

private data class MathPuzzle(
    val left: Int,
    val right: Int,
    val multiplier: Int
) {
    val answer: Int = (left + right) * multiplier
}
