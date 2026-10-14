package cu.todus.download.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
fun DownloadsScreen(
    downloads: List<DownloadItem>,
    onOpenAddDialog: () -> Unit,
    onPauseResume: (DownloadItem) -> Unit,
    onCancel: (DownloadItem) -> Unit,
    onOpen: (DownloadItem) -> Unit,
    onShare: (DownloadItem) -> Unit,
    onBadgeClick: () -> Unit,
    onPauseAll: (() -> Unit)? = null,
    onResumeAll: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val extendedColors = AppTheme.colors
    val activeList = remember(downloads) {
        downloads.filter { it.status != DownloadItem.Status.COMPLETED }
    }
    val activeCount = remember(activeList) {
        activeList.count { it.status == DownloadItem.Status.DOWNLOADING || it.status == DownloadItem.Status.CONNECTING }
    }
    val pausedCount = remember(activeList) {
        activeList.count { it.status == DownloadItem.Status.PAUSED }
    }

    val totalSpeed = remember(activeList) { activeList.sumOf { it.speedBps } }
    val speedFormatted = when {
        totalSpeed >= 1024 * 1024 -> String.format(Locale.US, "%.2f MB/s", totalSpeed / (1024f * 1024f))
        totalSpeed >= 1024 -> String.format(Locale.US, "%.1f KB/s", totalSpeed / 1024f)
        else -> "$totalSpeed B/s"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp)
    ) {
        Spacer(Modifier.height(14.dp))

        // Hero Telemetry Card
        ModernCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Brand Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "toDus",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Download",
                                color = extendedColors.textPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            )
                        }
                        Text(
                            text = "Gestor de descargas de alta velocidad",
                            color = extendedColors.textSecondary,
                            fontSize = 11.sp
                        )
                    }

                    // Connection Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(extendedColors.success.copy(alpha = if (extendedColors.isDark) 0.15f else 0.12f))
                            .border(1.dp, extendedColors.success.copy(alpha = 0.35f), RoundedCornerShape(50))
                            .clickable { onBadgeClick() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(extendedColors.success)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "En línea",
                                color = extendedColors.success,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Bandwidth Speedometer & Live Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "VELOCIDAD TOTAL EN VIVO",
                            color = extendedColors.textMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = if (activeCount > 0) speedFormatted else "0.0 KB/s",
                                color = if (activeCount > 0) MaterialTheme.colorScheme.primary else extendedColors.textPrimary,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Metric Counters
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MetricBadge(
                            label = "Activas",
                            value = activeCount.toString(),
                            accentColor = if (activeCount > 0) extendedColors.success else extendedColors.textMuted
                        )
                        MetricBadge(
                            label = "En cola",
                            value = (activeList.size - activeCount).toString(),
                            accentColor = extendedColors.textSecondary
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Action Buttons Row: Add Link + Batch Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenAddDialog,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("add_download_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Añadir enlace",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    if (activeCount > 0 && onPauseAll != null) {
                        OutlinedButton(
                            onClick = onPauseAll,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pause,
                                contentDescription = "Pausar todo",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else if (pausedCount > 0 && onResumeAll != null) {
                        OutlinedButton(
                            onClick = onResumeAll,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Reanudar todo",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // Queue Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Cola de descargas",
                    color = extendedColors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Badge(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = activeList.size.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            if (activeList.isNotEmpty()) {
                Text(
                    text = if (activeCount > 0) "$activeCount en progreso" else "Todas en pausa",
                    color = if (activeCount > 0) extendedColors.success else extendedColors.warning,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // Active Queue List or Empty State
        AnimatedVisibility(
            visible = activeList.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.weight(1f)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(activeList, key = { it.id }) { item ->
                    DownloadCard(
                        item = item,
                        onPauseResume = onPauseResume,
                        onCancel = onCancel,
                        onOpen = onOpen,
                        onShare = onShare
                    )
                }
            }
        }

        if (activeList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    title = "Cola de descargas vacía",
                    message = "Pega un enlace o compártelo desde toDus, navegador u otra app para descargarlo a máxima velocidad.",
                    icon = Icons.Default.CloudDownload,
                    action = {
                        Button(
                            onClick = onOpenAddDialog,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Pegar enlace toDus",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun MetricBadge(
    label: String,
    value: String,
    accentColor: androidx.compose.ui.graphics.Color
) {
    val extendedColors = AppTheme.colors
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(extendedColors.surfaceCardAlt.copy(alpha = 0.6f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                color = accentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = extendedColors.textMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
