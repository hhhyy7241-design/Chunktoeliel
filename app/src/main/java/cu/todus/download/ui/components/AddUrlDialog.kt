package cu.todus.download.ui.components

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cu.todus.download.ui.theme.AppTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUrlDialog(
    initialUrl: String = "",
    defaultThreads: Int = 4,
    onDismiss: () -> Unit,
    onConfirm: (url: String, fileName: String, threads: Int) -> Unit
) {
    val context = LocalContext.current
    val extendedColors = AppTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var urlText by remember { mutableStateOf(initialUrl) }
    var customName by remember { mutableStateOf("") }
    var selectedThreads by remember { mutableIntStateOf(defaultThreads) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var clipboardUrlSuggestion by remember { mutableStateOf<String?>(null) }

    fun checkClipboard() {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            if (clipboard?.hasPrimaryClip() == true) {
                val item = clipboard.primaryClip?.getItemAt(0)
                val text = item?.coerceToText(context)?.toString()?.trim()
                if (!text.isNullOrBlank() && (text.startsWith("http://") || text.startsWith("https://"))) {
                    if (text != urlText) {
                        clipboardUrlSuggestion = text
                    }
                }
            }
        } catch (_: Exception) {}
    }

    LaunchedEffect(Unit) {
        if (initialUrl.isBlank()) {
            checkClipboard()
        }
    }

    fun pasteFromClipboard() {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            if (clipboard?.hasPrimaryClip() == true) {
                val item = clipboard.primaryClip?.getItemAt(0)
                val text = item?.coerceToText(context)?.toString()?.trim()
                if (!text.isNullOrBlank()) {
                    urlText = text
                    errorMessage = null
                    clipboardUrlSuggestion = null
                }
            }
        } catch (_: Exception) {}
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle(color = extendedColors.surfaceBorder) },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 20.dp)
        ) {
            // Header: Title + Close Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Nueva descarga",
                            color = extendedColors.textPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Multi-hilo con optimización de red",
                            color = extendedColors.textSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = extendedColors.textMuted
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // Clipboard suggestion chip if found
            if (!clipboardUrlSuggestion.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f))
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            urlText = clipboardUrlSuggestion ?: ""
                            clipboardUrlSuggestion = null
                            errorMessage = null
                        }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "¿Usar enlace del portapapeles?",
                                    color = extendedColors.textPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = clipboardUrlSuggestion ?: "",
                                    color = extendedColors.textSecondary,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        Text(
                            text = "Pegar",
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
            }

            // URL input label
            Text(
                text = "ENLACE DE DESCARGA",
                color = extendedColors.textSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = urlText,
                onValueChange = {
                    urlText = it
                    errorMessage = null
                },
                placeholder = {
                    Text(
                        text = "https://…",
                        color = extendedColors.textMuted,
                        fontSize = 14.sp
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = { pasteFromClipboard() },
                        modifier = Modifier.testTag("paste_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = "Pegar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                isError = errorMessage != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = extendedColors.textPrimary,
                    unfocusedTextColor = extendedColors.textPrimary,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = extendedColors.surfaceBorder,
                    focusedContainerColor = extendedColors.surfaceCard,
                    unfocusedContainerColor = extendedColors.surfaceCard,
                    errorContainerColor = extendedColors.surfaceCard
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("url_input_field")
            )

            if (errorMessage != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = errorMessage ?: "",
                    color = extendedColors.error,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(14.dp))

            // File Name field
            Text(
                text = "NOMBRE DEL ARCHIVO (OPCIONAL)",
                color = extendedColors.textSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = customName,
                onValueChange = { customName = it },
                placeholder = {
                    Text(
                        text = "Detectar automáticamente del enlace",
                        color = extendedColors.textMuted,
                        fontSize = 14.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = extendedColors.textPrimary,
                    unfocusedTextColor = extendedColors.textPrimary,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = extendedColors.surfaceBorder,
                    focusedContainerColor = extendedColors.surfaceCard,
                    unfocusedContainerColor = extendedColors.surfaceCard
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Concurrency selector (Chips + Synced Seekbar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CANALES SIMULTÁNEOS (HILOS)",
                    color = extendedColors.textSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${selectedThreads}x hilos",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(8.dp))

            // Quick Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(1, 2, 4, 8, 16).forEach { threads ->
                    val isSelected = selectedThreads == threads
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    extendedColors.surfaceCard
                                }
                            )
                            .border(
                                1.dp,
                                if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    extendedColors.surfaceBorder
                                },
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedThreads = threads }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "${threads}x",
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                extendedColors.textPrimary
                            },
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Perfectly Synchronized Seekbar
            Slider(
                value = selectedThreads.toFloat(),
                onValueChange = { floatVal ->
                    selectedThreads = floatVal.roundToInt().coerceIn(1, 16)
                },
                valueRange = 1f..16f,
                steps = 14,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = extendedColors.surfaceCard
                ),
                modifier = Modifier.testTag("dialog_threads_slider")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("1 hilo", color = extendedColors.textMuted, fontSize = 11.sp)
                Text("16 hilos (máximo)", color = extendedColors.textMuted, fontSize = 11.sp)
            }

            Spacer(Modifier.height(20.dp))

            // Submit Button
            Button(
                onClick = {
                    val trimmed = urlText.trim()
                    if (trimmed.isBlank()) {
                        errorMessage = "Por favor ingresa o pega un enlace válido."
                        return@Button
                    }
                    if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
                        errorMessage = "El enlace debe comenzar con https:// o http://"
                        return@Button
                    }
                    onConfirm(trimmed, customName.trim(), selectedThreads)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_download_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Iniciar descarga rápida",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
