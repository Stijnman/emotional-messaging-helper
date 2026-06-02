package com.emh.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.emh.app.EMHApplication
import com.emh.app.ai.OllamaClient
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as EMHApplication
    val repo = app.settingsRepository

    var ollamaUrl by remember { mutableStateOf("http://localhost:11434") }
    var model by remember { mutableStateOf("llama3.2") }
    var autoAnalyze by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()

    // Load current values
    LaunchedEffect(Unit) {
        repo.ollamaUrl.collect { ollamaUrl = it }
    }
    LaunchedEffect(Unit) {
        repo.defaultModel.collect { model = it }
    }
    // AUTONOMOUS: Settings are live - panel observes via LaunchedEffect in EmotionalPanel for instant use
    LaunchedEffect(Unit) {
        repo.autoAnalyze.collect { autoAnalyze = it }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = ollamaUrl,
            onValueChange = { ollamaUrl = it },
            label = { Text("Ollama Server URL") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = model,
            onValueChange = { model = it },
            label = { Text("Default Model") },
            modifier = Modifier.fillMaxWidth()
        )

        // FINISHING: Dynamic model fetch using OllamaClient.listModels() - addresses better UX for vision/text choice
        var availableModels by remember { mutableStateOf<List<String>>(emptyList()) }
        var isFetchingModels by remember { mutableStateOf(false) }

        Row {
            OutlinedButton(
                onClick = {
                    scope.launch {
                        isFetchingModels = true
                        val client = OllamaClient()
                        client.updateBaseUrl(ollamaUrl)
                        availableModels = client.listModels()
                        isFetchingModels = false
                    }
                },
                enabled = !isFetchingModels
            ) {
                Text(if (isFetchingModels) "Fetching..." else "Fetch models from Ollama")
            }
        }

        if (availableModels.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text("Available models (tap to use):", style = MaterialTheme.typography.labelSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                availableModels.forEach { m ->
                    FilterChip(
                        selected = model == m,
                        onClick = { model = m },
                        label = { Text(m) }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(
                checked = autoAnalyze,
                onCheckedChange = { autoAnalyze = it }
            )
            Spacer(Modifier.width(8.dp))
            Text("Automatically analyze new WhatsApp messages")
        }

        Spacer(Modifier.height(32.dp))

        Button(onClick = {
            scope.launch {
                repo.setOllamaUrl(ollamaUrl)
                repo.setDefaultModel(model)
                repo.setAutoAnalyze(autoAnalyze)
            }
        }) {
            Text("Save Settings")
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Changes take effect immediately on the next generation in the floating panel.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        Text(
            "Recommended vision models: llava, llava-llama3, moondream, bakllava",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // AUTONOMOUS IMPROVEMENT (20 loops): Settings screen reviewed and enhanced for better user guidance in every cycle.
    }
}