package com.habitarchitect.presentation.screen.reflection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyReflectionScreen(
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit = {},
    viewModel: WeeklyReflectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weekly Reflection") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Text(
                text = "Take a moment to reflect on your week",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // What went well
            ReflectionCard(
                emoji = "👍",
                title = "What went well this week?",
                hint = "Celebrate your wins, no matter how small...",
                value = uiState.wentWell,
                onValueChange = { viewModel.updateWentWell(it) },
                backgroundColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // What didn't go well
            ReflectionCard(
                emoji = "👎",
                title = "What didn't go well?",
                hint = "Be honest with yourself. Growth starts with awareness...",
                value = uiState.didntGoWell,
                onValueChange = { viewModel.updateDidntGoWell(it) },
                backgroundColor = Color(0xFFE57373).copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // What did I learn
            ReflectionCard(
                emoji = "🎓",
                title = "What did I learn?",
                hint = "Every experience teaches something valuable...",
                value = uiState.learned,
                onValueChange = { viewModel.updateLearned(it) },
                backgroundColor = Color(0xFF2196F3).copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save button
            Button(
                onClick = {
                    viewModel.saveReflection()
                    onSaved()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.height(8.dp))
                Text(if (uiState.isSaving) "Saving..." else "Save Reflection")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ReflectionCard(
    emoji: String,
    title: String,
    hint: String,
    value: String,
    onValueChange: (String) -> Unit,
    backgroundColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = emoji,
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(hint) },
                minLines = 3,
                maxLines = 6
            )
        }
    }
}
