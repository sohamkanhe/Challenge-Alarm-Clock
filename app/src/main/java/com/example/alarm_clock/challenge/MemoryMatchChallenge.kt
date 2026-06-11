package com.example.alarm_clock.challenge

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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

@Composable
fun MemoryMatchChallenge(onSolvedChange: (Boolean) -> Unit) {
    val cards = remember {
        val labels = listOf("A", "B", "C", "D", "E", "F", "G", "H")
        (labels + labels)
            .shuffled()
            .mapIndexed { index, label -> MemoryCard(index, label) }
    }
    val matchedIds = remember { mutableStateListOf<Int>() }
    var firstPickId by rememberSaveable { mutableStateOf<Int?>(null) }
    var secondPickId by rememberSaveable { mutableStateOf<Int?>(null) }
    val visibleIds = setOfNotNull(firstPickId, secondPickId) + matchedIds
    val solved = matchedIds.size == cards.size

    LaunchedEffect(solved) {
        onSolvedChange(solved)
    }

    LaunchedEffect(firstPickId, secondPickId) {
        val firstId = firstPickId
        val secondId = secondPickId
        if (firstId == null || secondId == null) return@LaunchedEffect

        val first = cards.first { it.id == firstId }
        val second = cards.first { it.id == secondId }
        if (first.label == second.label) {
            matchedIds.add(firstId)
            matchedIds.add(secondId)
        } else {
            delay(700)
        }
        firstPickId = null
        secondPickId = null
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Match all pairs",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cards, key = { it.id }) { card ->
                val visible = card.id in visibleIds
                Card(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clickable(
                            enabled = !visible && secondPickId == null
                        ) {
                            if (firstPickId == null) {
                                firstPickId = card.id
                            } else {
                                secondPickId = card.id
                            }
                        },
                    shape = MaterialTheme.shapes.small,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = CardDefaults.cardColors(
                        containerColor = if (visible) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .background(Color.Transparent)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (visible) card.label else "",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

private data class MemoryCard(
    val id: Int,
    val label: String
)
