package com.example.braces2.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CountSelector(
    label: String,
    currentValue: Int,
    minValue: Int,
    maxValue: Int,
    onValueChange: (Int) -> Unit,
    backgroundColor: androidx.compose.ui.graphics.Color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
    textColor: androidx.compose.ui.graphics.Color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(text = label)
        androidx.compose.material3.Button(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = textColor
            )
        ) {
            Text(text = currentValue.toString())
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            (minValue..maxValue).forEach { value ->
                DropdownMenuItem(
                    text = { Text(text = value.toString()) },
                    onClick = {
                        onValueChange(value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CountSelectors(
    forwardCount: Int,
    backwardCount: Int,
    onForwardCountChange: (Int) -> Unit,
    onBackwardCountChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        CountSelector(
            label = "正方向次数",
            currentValue = forwardCount,
            minValue = 1,
            maxValue = 5,
            onValueChange = onForwardCountChange,
            backgroundColor = androidx.compose.ui.graphics.Color(0xFFFFEBEE), // Red background for forward
            textColor = androidx.compose.ui.graphics.Color.Black,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        CountSelector(
            label = "反方向次数",
            currentValue = backwardCount,
            minValue = 1,
            maxValue = 5,
            onValueChange = onBackwardCountChange,
            backgroundColor = androidx.compose.ui.graphics.Color(0xFFE8F5E9), // Green background for backward
            textColor = androidx.compose.ui.graphics.Color.Black,
            modifier = Modifier.weight(1f)
        )
    }
}