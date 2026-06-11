package com.example.alarm_clock.challenge

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun SequenceRecallChallenge(onSolvedChange: (Boolean) -> Unit) {
    val pads = remember {
        listOf(
            SequencePad(0, "1", Color(0xFFE95D5D)),
            SequencePad(1, "2", Color(0xFF4DAA7D)),
            SequencePad(2, "3", Color(0xFF4C7EDB)),
            SequencePad(3, "4", Color(0xFFE4B84A))
        )
    }
    val sequence = remember { List(6) { Random.nextInt(0, pads.size) } }
    var acceptingInput by rememberSaveable { mutableStateOf(false) }
    var playing by rememberSaveable { mutableStateOf(true) }
    var highlightedPad by rememberSaveable { mutableIntStateOf(-1) }
    var progress by rememberSaveable { mutableIntStateOf(0) }
    val solved = progress == sequence.size

    LaunchedEffect(solved) {
        onSolvedChange(solved)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Repeat the sequence",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Text(
            text = if (playing) "Watch" else "Step ${progress + 1} of ${sequence.size}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            pads.chunked(2).forEach { rowPads ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowPads.forEach { pad ->
                        SequencePadButton(
                            pad = pad,
                            highlighted = pad.id == highlightedPad,
                            enabled = acceptingInput && !solved,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (pad.id == sequence[progress]) {
                                progress += 1
                            } else {
                                progress = 0
                            }
                        }
                    }
                }
            }
        }
        Button(
            enabled = !playing && !solved,
            onClick = {
                acceptingInput = false
                playing = true
                progress = 0
            }
        ) {
            Text("Replay")
        }
    }

    LaunchedEffect(playing) {
        if (playing) {
            playSequence(sequence) { highlightedPad = it }
            playing = false
            acceptingInput = true
        }
    }
}

@Composable
private fun SequencePadButton(
    pad: SequencePad,
    highlighted: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val containerColor = if (highlighted) pad.color else pad.color.copy(alpha = 0.55f)
    Card(
        modifier = modifier
            .aspectRatio(1.35f)
            .clickable(enabled = enabled, onClick = onClick),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.35f)
                .background(Color.Transparent)
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = pad.label,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        }
    }
}

private suspend fun playSequence(
    sequence: List<Int>,
    onHighlightChanged: (Int) -> Unit
) {
    delay(450)
    sequence.forEach { pad ->
        onHighlightChanged(pad)
        delay(450)
        onHighlightChanged(-1)
        delay(180)
    }
}

private data class SequencePad(
    val id: Int,
    val label: String,
    val color: Color
)
