package cu.todus.download.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cu.todus.download.data.DownloadItem
import cu.todus.download.ui.theme.AppTheme

@Composable
fun DownloadCard(
    item: DownloadItem,
    onPauseResume: (DownloadItem) -> Unit,
    onCancel: (DownloadItem) -> Unit,
    onOpen: (DownloadItem) -> Unit,
    onShare: (DownloadItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val extendedColors = AppTheme.colors
    var expanded by remember { mutableStateOf(false) }

    val fileTypeConfig = when {
        item.isVideo -> FileTypeConfig(
            icon = Icons.Default.Movie,
            color = Color(0xFF0284C7),
            containerColor = Color(0xFFE0F2FE),
            label = "VIDEO"
        )
        item.isAudio -> FileTypeConfig(
            icon = Icons.Default.MusicNote,
            color = Color(0xFF7C3AED),
            containerColor = Color(0xFFEDE9FE),
            label = "AUDIO"
        )
        item.isDocument -> FileTypeConfig(
            icon = Icons.Default.Description,
            color = Color(0xFFD97706),
            containerColor = Color(0xFFFEF3C7),
            label = "DOC"
        )
        item.isArchive -> FileTypeConfig(
            icon = Icons.Default.FolderZip,
            color = Color(0xFF059669),
            containerColor = Color(0xFFD1FAE5),
            label = "ZIP"
        )
        else -> FileTypeConfig(
            icon = Icons.Default.Description,
            color = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            label = item.fileExtension.uppercase().ifBlank { "ARCHIVO" }
        )
    }

    val (statusLabel, statusColor, statusBg) = when (item.status) {
        DownloadItem.Status.DOWNLOADING -> Triple(
            "Descargando",
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        )
        DownloadItem.Status.CONNECTING -> Triple(
            "Conectando…",
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        )
        DownloadItem.Status.PAUSED -> Triple(
            "En pausa",
            extendedColors.warning,
            extendedColors.warningContainer
        )
        DownloadItem.Status.QUEUED -> Triple(
            "En cola",
            extendedColors.textSecondary,
            extendedColors.surfaceCardAlt
        )
        DownloadItem.Status.COMPLETED -> Triple(
            "Completado",
            extendedColors.success,
            extendedColors.successContainer
        )
        DownloadItem.Status.ERROR -> Triple(
            "Error",
            extendedColors.error,
            extendedColors.errorContainer
        )
        DownloadItem.Status.CANCELLED -> Triple(
            "Cancelado",
            extendedColors.textMuted,
            extendedColors.surfaceCardAlt
        )
    }

    val animatedProgress by animateFloatAsState(
        targetValue = (item.percentage / 100f).coerceIn(0f, 1f),
        label = "download_progress_${item.id}"
    )

    ModernCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("download_card_${item.id}"),
        onClick = { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Type Icon + Name + Action/Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // File Type Avatar
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (extendedColors.isDark) {
                                fileTypeConfig.color.copy(alpha = 0.2f)
                            } else {
                                fileTypeConfig.containerColor
                            }
                        )
                        .border(
                            1.dp,
                            fileTypeConfig.color.copy(alpha = if (extendedColors.isDark) 0.4f else 0.3f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = fileTypeConfig.icon,
                        contentDescription = fileTypeConfig.label,
                        tint = fileTypeConfig.color,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                // Name and Status Badges
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        color = extendedColors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(3.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Status Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(statusBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = statusLabel,
                                color = statusColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Threads Badge (if downloading or queued)
                        if (item.status != DownloadItem.Status.COMPLETED) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(extendedColors.surfaceCardAlt)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${item.threads}x hilos",
                                    color = extendedColors.textSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.width(8.dp))

                // Primary Quick Action
                when (item.status) {
                    DownloadItem.Status.COMPLETED -> {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(extendedColors.success.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Descarga completada",
                                tint = extendedColors.success,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    DownloadItem.Status.ERROR -> {
                        FilledIconButton(
                            onClick = { onPauseResume(item) },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = extendedColors.errorContainer,
                                contentColor = extendedColors.error
                            ),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reintentar",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    else -> {
                        val isDownloading = item.status == DownloadItem.Status.DOWNLOADING ||
                                item.status == DownloadItem.Status.CONNECTING
                        FilledIconButton(
                            onClick = { onPauseResume(item) },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = if (isDownloading) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    extendedColors.surfaceCardAlt
                                },
                                contentColor = if (isDownloading) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    extendedColors.textPrimary
                                }
                            ),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isDownloading) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isDownloading) "Pausar" else "Reanudar",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Progress Bar (for in-progress/paused)
            if (item.status != DownloadItem.Status.COMPLETED) {
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    color = statusColor,
                    trackColor = extendedColors.surfaceCardAlt,
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )

                Spacer(Modifier.height(8.dp))

                // Stats Row: Percentage + Size on left, Speed + ETA on right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${item.percentage}%  •  ${item.humanSize(item.downloadedBytes)} / ${item.humanSize()}",
                        color = extendedColors.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )

                    val telemetryText = if (item.status == DownloadItem.Status.DOWNLOADING) {
                        val eta = item.etaString()
                        if (eta.isNotBlank()) "⚡ ${item.humanSpeed()}  •  $eta" else "⚡ ${item.humanSpeed()}"
                    } else {
                        item.humanSpeed()
                    }

                    Text(
                        text = telemetryText,
                        color = if (item.status == DownloadItem.Status.DOWNLOADING) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            extendedColors.textMuted
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Error Message (if any)
            if (!item.errorMsg.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(extendedColors.errorContainer)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = item.errorMsg,
                        color = extendedColors.error,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Completed actions bar
            if (item.status == DownloadItem.Status.COMPLETED) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { onOpen(item) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Abrir archivo",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    OutlinedButton(
                        onClick = { onShare(item) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartir",
                            tint = extendedColors.textPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = { onCancel(item) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Eliminar",
                            tint = extendedColors.textMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Expandable details (URL, Destination, Controls)
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(extendedColors.surfaceCardAlt.copy(alpha = 0.5f))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "ENLACE DE ORIGEN",
                        color = extendedColors.textMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.url,
                        color = extendedColors.textSecondary,
                        fontSize = 11.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (item.destinationPath.isNotBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "UBICACIÓN EN DISCO",
                            color = extendedColors.textMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = item.destinationPath,
                            color = extendedColors.textSecondary,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (item.status != DownloadItem.Status.COMPLETED) {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Cancelar descarga",
                                color = extendedColors.error,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { onCancel(item) }
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class FileTypeConfig(
    val icon: ImageVector,
    val color: Color,
    val containerColor: Color,
    val label: String
)
