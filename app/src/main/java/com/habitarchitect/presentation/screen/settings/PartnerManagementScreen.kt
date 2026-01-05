package com.habitarchitect.presentation.screen.settings

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habitarchitect.domain.model.PartnershipStatus

/**
 * Screen for managing accountability partners.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerManagementScreen(
    onNavigateBack: () -> Unit,
    onViewPartner: (String) -> Unit = {},
    viewModel: PartnerManagementViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val uiState by viewModel.uiState.collectAsState()

    fun shareInviteLink(inviteCode: String) {
        val shareUrl = "https://habitarchitect.app/invite/$inviteCode"
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Join me on Habit Architect!")
            putExtra(Intent.EXTRA_TEXT, "Be my accountability partner on Habit Architect!\n\n$shareUrl")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share invite"))
    }

    fun copyInviteCode(inviteCode: String) {
        clipboardManager.setText(AnnotatedString("https://habitarchitect.app/invite/$inviteCode"))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Partners") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.createInvite() }) {
                Icon(Icons.Default.Add, contentDescription = "Invite partner")
            }
        }
    ) { paddingValues ->
        if (uiState.partnerships.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No partners yet",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Tap + to invite an accountability partner",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.partnerships) { partnership ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (partnership.status == PartnershipStatus.PENDING)
                                            "Pending Invite"
                                        else
                                            "Partner Connected",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = when (partnership.status) {
                                            PartnershipStatus.PENDING -> "Waiting for partner to accept"
                                            PartnershipStatus.ACTIVE -> "Active partnership"
                                            PartnershipStatus.REVOKED -> "Revoked"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (partnership.status == PartnershipStatus.PENDING) {
                                    Row {
                                        FilledTonalIconButton(
                                            onClick = { copyInviteCode(partnership.inviteCode) }
                                        ) {
                                            Icon(
                                                Icons.Default.ContentCopy,
                                                contentDescription = "Copy link"
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        FilledTonalIconButton(
                                            onClick = { shareInviteLink(partnership.inviteCode) }
                                        ) {
                                            Icon(
                                                Icons.Default.Share,
                                                contentDescription = "Share link"
                                            )
                                        }
                                    }
                                } else {
                                    Row {
                                        FilledTonalIconButton(
                                            onClick = { onViewPartner(partnership.partnerId) }
                                        ) {
                                            Icon(
                                                Icons.Default.Visibility,
                                                contentDescription = "View partner's habits"
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        FilledTonalIconButton(
                                            onClick = { viewModel.revokePartnership(partnership.id) }
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Remove partner"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
