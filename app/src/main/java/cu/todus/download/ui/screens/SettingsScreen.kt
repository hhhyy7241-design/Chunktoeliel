package cu.todus.download.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cu.todus.download.data.AppPreferences
import cu.todus.download.data.ThemeMode
import cu.todus.download.engine.PingResult
import cu.todus.download.ui.components.SettingsGroup
import cu.todus.download.ui.components.SettingsRow
import cu.todus.download.ui.theme.AppAccent
import cu.todus.download.ui.theme.AppTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    preferences: AppPreferences,
    onTestConnection: suspend () -> PingResult,
    onThemeChange: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val extendedColors = AppTheme.colors
    val scope = rememberCoroutineScope()

    var themeMode by remember { mutableStateOf(preferences.themeMode) }
    var accentPalette by remember { mutableStateOf(preferences.accentPalette) }
    var dynamicColors by remember { mutableStateOf(preferences.dynamicColors) }
    var threads by remember { mutableIntStateOf(preferences.defaultThreads) }
    var autoResume by remember { mutableStateOf(preferences.autoResume) }

    var isTestingConnection by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<PingResult?>(null) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column {
            Text(
                text = "Ajustes",
                color = extendedColors.textPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "Personalización, motor de descarga y red",
                color = extendedColors.textSecondary,
                fontSize = 12.sp
            )
        }

        // 1. Apariencia & Temas
        SettingsGroup("Apariencia y tema", Icons.Default.Palette) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "MODO DE COLOR",
                    color = extendedColors.textSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.height(10.dp))

                // Theme Mode Selector: Sistema, Claro, Oscuro
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val themeOptions = listOf(
                        Triple(ThemeMode.SYSTEM, "Sistema", Icons.Default.BrightnessAuto),
                        Triple(ThemeMode.LIGHT, "Claro", Icons.Default.LightMode),
                        Triple(ThemeMode.DARK, "Oscuro", Icons.Default.DarkMode)
                    )

                    themeOptions.forEach { (mode, label, icon) ->
                        val isSelected = themeMode == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        extendedColors.surfaceCardAlt
                                    }
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else extendedColors.surfaceBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    themeMode = mode
                                    preferences.themeMode = mode
                                    onThemeChange()
                                }
                                .padding(vertical = 10.dp)
                                .testTag("theme_option_${label.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        extendedColors.textPrimary
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = label,
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
                }

                Spacer(Modifier.height(18.dp))

                // Paleta de Colores de Acento
                Text(
                    text = "PALETA DE COLORES DE ACENTO",
                    color = extendedColors.textSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppAccent.values().forEach { palette ->
                        val isSelected = accentPalette.equals(palette.id, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(palette.previewColor)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) extendedColors.textPrimary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable {
                                    accentPalette = palette.id
                                    preferences.accentPalette = palette.id
                                    onThemeChange()
                                }
                                .testTag("palette_${palette.id.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Seleccionado",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))
                val currentAccent = AppAccent.fromId(accentPalette)
                Text(
                    text = "Color activo: ${currentAccent.displayName}",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(14.dp))

                // Dynamic Colors (Material You)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Colores dinámicos (Material You)",
                            color = extendedColors.textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Extrae los tonos principales del fondo de pantalla",
                            color = extendedColors.textMuted,
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = dynamicColors,
                        onCheckedChange = {
                            dynamicColors = it
                            preferences.dynamicColors = it
                            onThemeChange()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        // 2. Motor de Descargas
        SettingsGroup("Motor y rendimiento", Icons.Default.Tune) {
            Column(Modifier.padding(14.dp)) {
                val threadValues = listOf(1, 2, 4, 8, 16)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hilos simultáneos por descarga",
                            color = extendedColors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Canales paralelos para acelerar la transferencia",
                            color = extendedColors.textMuted,
                            fontSize = 12.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${threads}x hilos",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Quick Preset Chips (1x, 2x, 4x, 8x, 16x)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    threadValues.forEach { value ->
                        val isSelected = threads == value
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        extendedColors.surfaceCardAlt
                                    }
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else extendedColors.surfaceBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    threads = value
                                    preferences.defaultThreads = value
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "${value}x",
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

                Spacer(Modifier.height(12.dp))

                // Smooth Seekbar / Slider perfectly synced from 1 to 16
                Slider(
                    value = threads.toFloat(),
                    onValueChange = { floatVal ->
                        val newThreads = floatVal.roundToInt().coerceIn(1, 16)
                        threads = newThreads
                        preferences.defaultThreads = newThreads
                    },
                    valueRange = 1f..16f,
                    steps = 14,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = extendedColors.surfaceCardAlt
                    ),
                    modifier = Modifier.testTag("threads_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("1 hilo", color = extendedColors.textMuted, fontSize = 11.sp)
                    Text("16 hilos (máximo)", color = extendedColors.textMuted, fontSize = 11.sp)
                }
            }

            // Auto-resume switch row
            SettingsRow(
                icon = Icons.Default.Speed,
                iconTint = Color(0xFF16A34A),
                iconBgColor = Color(0xFFDCFCE7),
                title = "Reanudación automática",
                subtitle = "Reanuda al recuperar conexión a internet",
                trailing = {
                    Switch(
                        checked = autoResume,
                        onCheckedChange = {
                            autoResume = it
                            preferences.autoResume = it
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            )
        }

        // 3. Conexión y Diagnóstico de Red
        SettingsGroup("Red y conectividad", Icons.Default.Wifi) {
            SettingsRow(
                icon = Icons.Default.Wifi,
                iconTint = Color(0xFF0284C7),
                iconBgColor = Color(0xFFE0F2FE),
                title = "Servicio de descarga de alta velocidad",
                subtitle = "Canal directo de transferencia optimizado",
                trailing = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(extendedColors.success.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Activo",
                            color = extendedColors.success,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )

            Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Button(
                    onClick = {
                        if (!isTestingConnection) {
                            isTestingConnection = true
                            testResult = null
                            scope.launch {
                                val result = onTestConnection()
                                testResult = result
                                isTestingConnection = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = extendedColors.surfaceCardAlt,
                        contentColor = extendedColors.textPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isTestingConnection) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Verificando servidor…", fontSize = 13.sp)
                    } else {
                        Icon(
                            imageVector = Icons.Default.NetworkCheck,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Comprobar estado del servidor",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                testResult?.let { res ->
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (res.isSuccess) extendedColors.success.copy(alpha = 0.15f) else extendedColors.error.copy(alpha = 0.15f)
                            )
                            .padding(10.dp)
                    ) {
                        Text(
                            text = if (res.isSuccess) {
                                "✓ Conexión establecida con el servidor: Latencia ${res.latencyMs} ms"
                            } else {
                                "✗ No se pudo establecer comunicación: ${res.message}"
                            },
                            color = if (res.isSuccess) extendedColors.success else extendedColors.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 4. Ubicación de almacenamiento
        SettingsGroup("Almacenamiento", Icons.Default.Folder) {
            SettingsRow(
                icon = Icons.Default.Folder,
                iconTint = Color(0xFFEA580C),
                iconBgColor = Color(0xFFFFEDD5),
                title = "Carpeta de descargas",
                subtitle = "Download/${preferences.subFolder}"
            )
        }

        // 5. Acerca de la aplicación
        SettingsGroup("Información del sistema", Icons.Default.Info) {
            SettingsRow(
                icon = Icons.Default.Info,
                iconTint = Color(0xFF64748B),
                iconBgColor = Color(0xFFF1F5F9),
                title = "toDus Download 2026 Pro",
                subtitle = "Versión 2.0.0 · Jetpack Compose Material 3"
            )
        }

        Spacer(Modifier.height(10.dp))
    }
}
