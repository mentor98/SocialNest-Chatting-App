package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.TextTertiary

@Composable
fun AlphabetIndex(
    selectedLetter: String?,
    onLetterSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val alphabet = listOf(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
        "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
        "U", "V", "W", "X", "Y", "Z"
    )

    Column(
        modifier = modifier
            .padding(vertical = 4.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        alphabet.forEach { letter ->
            val isSelected = selectedLetter.equals(letter, ignoreCase = true)
            val interactionSource = remember { MutableInteractionSource() }

            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        onLetterSelected(letter)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter,
                    fontSize = 9.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) PrimaryBlue else TextTertiary
                )
            }
        }
    }
}
