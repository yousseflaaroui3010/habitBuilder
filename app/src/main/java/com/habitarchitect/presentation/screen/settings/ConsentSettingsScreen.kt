package com.habitarchitect.presentation.screen.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habitarchitect.domain.analytics.ConsentTier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsentSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ConsentSettingsViewModel = hiltViewModel()
) {
    val currentTier by viewModel.consentTier.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Data & Privacy") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Choose how your data helps improve the app",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            ConsentTierCard(
                tier = ConsentTier.ESSENTIAL,
                title = "Essential Only",
                subtitle = "Anonymous crash reports",
                icon = Icons.Default.Lock,
                isSelected = currentTier == ConsentTier.ESSENTIAL,
                onSelect = { viewModel.setConsentTier(ConsentTier.ESSENTIAL) },
                benefits = listOf(
                    "App stability monitoring",
                    "Basic error reporting"
                ),
                dataCollected = listOf(
                    "Crash logs (anonymous)",
                    "App version info"
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            ConsentTierCard(
                tier = ConsentTier.ENHANCED,
                title = "Enhanced Analytics",
                subtitle = "Personalized insights",
                icon = Icons.Default.Analytics,
                isSelected = currentTier == ConsentTier.ENHANCED,
                onSelect = { viewModel.setConsentTier(ConsentTier.ENHANCED) },
                benefits = listOf(
                    "See your best times for habits",
                    "Personalized recommendations",
                    "Success pattern insights"
                ),
                dataCollected = listOf(
                    "When you complete habits (time of day)",
                    "Success/failure patterns",
                    "Which strategies you use"
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            ConsentTierCard(
                tier = ConsentTier.RESEARCH,
                title = "Help Research",
                subtitle = "Improve for everyone",
                icon = Icons.Default.Science,
                isSelected = currentTier == ConsentTier.RESEARCH,
                onSelect = { viewModel.setConsentTier(ConsentTier.RESEARCH) },
                benefits = listOf(
                    "All Enhanced benefits",
                    "Help train better ML models",
                    "Contribute to habit science"
                ),
                dataCollected = listOf(
                    "De-identified usage patterns",
                    "Aggregated success rates",
                    "Strategy effectiveness data"
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            ConsentTierCard(
                tier = ConsentTier.PERSONAL,
                title = "Full Insights",
                subtitle = "Maximum personalization",
                icon = Icons.Default.Person,
                isSelected = currentTier == ConsentTier.PERSONAL,
                onSelect = { viewModel.setConsentTier(ConsentTier.PERSONAL) },
                benefits = listOf(
                    "All Research benefits",
                    "Detailed personal reports",
                    "Full data export"
                ),
                dataCollected = listOf(
                    "All previous data types",
                    "Reflection sentiment analysis",
                    "Linked to your account for export"
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Your data is always encrypted and never sold. You can change this anytime or delete all data in Settings.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Suppress("UNUSED_PARAMETER")
@Composable
private fun ConsentTierCard(
    tier: ConsentTier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onSelect: () -> Unit,
    benefits: List<String>,
    dataCollected: List<String>
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = onSelect
                    )
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }

                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(start = 48.dp, top = 8.dp)) {
                    Text(
                        text = "Benefits:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    benefits.forEach { benefit ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = benefit,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Data collected:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    dataCollected.forEach { data ->
                        Text(
                            text = "- $data",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }
                }
            }
        }
    }
}
