package com.habitarchitect.presentation.screen.templates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habitarchitect.domain.model.HabitType

/**
 * One-tap template confirmation screen.
 * Shows template details and allows instant habit creation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateConfirmScreen(
    onNavigateBack: () -> Unit,
    onHabitCreated: () -> Unit,
    onCustomize: (String) -> Unit,
    viewModel: TemplateConfirmViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.habitCreated) {
        if (uiState.habitCreated) {
            onHabitCreated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Habit") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        uiState.template?.let { template ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Big emoji
                Text(
                    text = template.iconEmoji,
                    fontSize = 72.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Template name
                Text(
                    text = template.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = template.category,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = template.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // What's included card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "What's Included",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (template.type == HabitType.BUILD) {
                            template.defaultMinimumVersion?.let {
                                IncludedItem("2-Minute Version", it)
                            }
                            template.defaultStackAnchors.firstOrNull()?.let {
                                IncludedItem("Habit Stack", it)
                            }
                            IncludedItem(
                                "Attraction Items",
                                "${template.defaultAttractionItems.size} reasons to stay motivated"
                            )
                        } else {
                            IncludedItem(
                                "Resistance Items",
                                "${template.defaultResistanceItems.size} reasons to resist"
                            )
                            IncludedItem(
                                "Friction Strategies",
                                "${template.defaultFrictionStrategies.size} ways to make it harder"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Action buttons
                Button(
                    onClick = { viewModel.createHabitFromTemplate() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isCreating
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(if (uiState.isCreating) "Creating..." else "Add Habit")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { onCustomize(template.type.name) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Customize First")
                }
            }
        } ?: run {
            // Loading or not found
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Loading template...")
            }
        }
    }
}

@Composable
private fun IncludedItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f, fill = false),
            maxLines = 1
        )
    }
}
