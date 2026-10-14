package cu.todus.download.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cu.todus.download.data.DownloadItem
import cu.todus.download.ui.components.DownloadCard
import cu.todus.download.ui.components.EmptyState
import cu.todus.download.ui.components.ModernCard
import cu.todus.download.ui.theme.AppTheme
import java.util.Locale

@Composable
fun HistoryScreen(
    downloads: List<DownloadItem>,
    onOpen: (DownloadItem) -> Unit,
    onShare: (DownloadItem) -> Unit,
    onDelete: (DownloadItem) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = AppTheme.colors
    var search by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("Todo") }
    var showClearConfirmation by remember { mutableStateOf(false) }

    val completed = remember(downloads) {
        downloads.filter { it.status == DownloadItem.Status.COMPLETED }
    }

    val totalBytes = remember(completed) {
        completed.sumOf { it.downloadedBytes.coerceAtLeast(it.totalBytes) }
    }

    val totalStorageFormatted = when {
        totalBytes >= 1024L * 1024L * 1024L -> String.format(Locale.US, "%.2f GB", totalBytes / (1024.0 * 1024.0 * 1024.0))
        totalBytes >= 1024L * 1024L -> String.format(Locale.US, "%.1f MB", totalBytes / (1024.0 * 1024.0))
        totalBytes >= 1024L -> String.format(Locale.US, "%.0f KB", totalBytes / 1024.0)
        else -> "$totalBytes B"
    }

    val filters = listOf("Todo", "Vídeo", "Audio", "Documentos", "Otros")

    val visible = remember(completed, search, filter) {
        completed.filter { item ->
            val typeMatches = when (filter) {
                "Vídeo" -> item.isVideo
                "Audio" -> item.isAudio
                "Documentos" -> item.isDocument
                "Otros" -> !item.isVideo && !item.isAudio && !item.isDocument
                else -> true
            }
            typeMatches && (search.isBlank() || item.name.contains(search, ignoreCase = true))
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp)
    ) {
        Spacer(Modifier.height(14.dp))

        // Screen Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Historial",
                    color = extendedColors.textPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "${completed.size} archivos descargados  •  $totalStorageFormatted",
                    color = extendedColors.textSecondary,
                    fontSize = 12.sp
                )
            }

            if (completed.isNotEmpty()) {
                IconButton(
                    onClick = { showClearConfirmation = true },
                    modifier = Modifier.testTag("clear_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Limpiar historial",
                        tint = extendedColors.textMuted
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Search bar
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (search.isNotBlank()) {
                    IconButton(onClick = { search = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Borrar búsqueda",
                            tint = extendedColors.textMuted
                        )
                    }
                }
            },
            placeholder = {
                Text(
                    text = "Buscar en archivos guardados…",
                    color = extendedColors.textMuted,
                    fontSize = 13.sp
                )
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = extendedColors.textPrimary,
                unfocusedTextColor = extendedColors.textPrimary,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = extendedColors.surfaceBorder,
                focusedContainerColor = extendedColors.surfaceCard,
                unfocusedContainerColor = extendedColors.surfaceCard
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_history_input")
        )

        // Filter chips row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            filters.forEach { option ->
                val selected = filter == option
                FilterChip(
                    selected = selected,
                    onClick = { filter = option },
                    label = {
                        Text(
                            text = option,
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = extendedColors.surfaceCard,
                        labelColor = extendedColors.textSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selected,
                        borderColor = extendedColors.surfaceBorder,
                        selectedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // List or Empty
        AnimatedVisibility(
            visible = visible.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.weight(1f)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(visible, key = { it.id }) { item ->
                    DownloadCard(
                        item = item,
                        onPauseResume = {},
                        onCancel = { onDelete(item) },
                        onOpen = onOpen,
                        onShare = onShare
                    )
                }
            }
        }

        if (visible.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    title = if (search.isBlank()) "Sin descargas guardadas" else "Sin coincidencias",
                    message = if (search.isBlank()) {
                        "Tus descargas finalizadas se organizarán aquí automáticamente por fecha y tipo."
                    } else {
                        "No se encontró ningún archivo con el término \"$search\"."
                    },
                    icon = Icons.Default.FolderOpen
                )
            }
        }
    }

    // Confirmation Alert Dialog
    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            title = {
                Text(
                    text = "¿Limpiar historial?",
                    fontWeight = FontWeight.Bold,
                    color = extendedColors.textPrimary
                )
            },
            text = {
                Text(
                    text = "Se eliminará la lista de descargas completadas. Los archivos permanecerán intactos en la carpeta de tu dispositivo.",
                    color = extendedColors.textSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAll()
                        showClearConfirmation = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = extendedColors.error)
                ) {
                    Text("Limpiar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmation = false }) {
                    Text("Cancelar", color = extendedColors.textSecondary)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
