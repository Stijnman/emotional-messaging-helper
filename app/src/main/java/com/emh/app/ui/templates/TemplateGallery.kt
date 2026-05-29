package com.emh.app.ui.templates

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val QUICK_TEMPLATES = listOf(
    "I really appreciate you sharing that with me.",
    "That makes a lot of sense. Tell me more about how you're feeling.",
    "I've been thinking about this too. Can we talk about it properly soon?",
    "Thank you for being honest with me. It means a lot.",
    "I'm here. You don't have to go through this alone."
)

@Composable
fun TemplateGallery(
    onTemplateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            "Quick Templates",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        QUICK_TEMPLATES.forEach { template ->
            OutlinedButton(
                onClick = { onTemplateSelected(template) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = template,
                    maxLines = 2,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}