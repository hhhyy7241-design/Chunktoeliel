package com.example

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cu.todus.download.data.AppPreferences
import cu.todus.download.data.DownloadItem
import cu.todus.download.data.DownloadRepository
import cu.todus.download.data.ThemeMode
import cu.todus.download.engine.DownloadEngine
import cu.todus.download.engine.TodusNetworkClient
import cu.todus.download.service.DownloadService
import cu.todus.download.ui.components.AddUrlDialog
import cu.todus.download.ui.components.FloatingBottomBar
import cu.todus.download.ui.components.NavBarItem
import cu.todus.download.ui.screens.DownloadsScreen
import cu.todus.download.ui.screens.HistoryScreen
import cu.todus.download.ui.screens.SettingsScreen
import cu.todus.download.ui.theme.AppAccent
import cu.todus.download.ui.theme.TodusDownloadTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

enum class AppTab(val title: String, val icon: ImageVector) {
    DOWNLOADS("Descargas", Icons.Default.CloudDownload),
    HISTORY("Historial", Icons.Default.Folder),
    SETTINGS("Ajustes", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

    private lateinit var preferences: AppPreferences
    private lateinit var repository: DownloadRepository
    private lateinit var engine: DownloadEngine
    private lateinit var networkClient: TodusNetworkClient

    private var sharedUrlFromIntent by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        preferences = AppPreferences(this)
        repository = DownloadService.getRepository(this)
        engine = DownloadService.getEngine(this)
        networkClient = TodusNetworkClient(preferences)

        handleIncomingIntent(intent)

        setContent {
            var themeMode by remember { mutableStateOf(preferences.themeMode) }
            var accentPalette by remember { mutableStateOf(preferences.accentPalette) }
            var dynamicColors by remember { mutableStateOf(preferences.dynamicColors) }

            TodusDownloadTheme(
                themeMode = themeMode,
                dynamicColor = dynamicColors,
                accent = AppAccent.fromId(accentPalette)
            ) {
                MainAppScreen(
                    onThemeChange = {
                        themeMode = preferences.themeMode
                        accentPalette = preferences.accentPalette
                        dynamicColors = preferences.dynamicColors
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (!text.isNullOrBlank()) {
                val urlRegex = Regex("https?://[^\\s]+")
                val match = urlRegex.find(text)
                sharedUrlFromIntent = match?.value ?: text.trim()
            }
        }
    }

    private fun openFile(item: DownloadItem) {
        val file = File(item.destinationPath)
        if (!file.exists()) {
            Toast.makeText(this, "El archivo ya no existe en el almacenamiento", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val uri: Uri = FileProvider.getUriForFile(
                this,
                "$packageName.provider",
                file
            )

            val extension = file.extension.lowercase()
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"

            val openIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(Intent.createChooser(openIntent, "Abrir con"))
        } catch (e: Exception) {
            Toast.makeText(this, "No hay aplicación compatible para abrir este archivo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareFile(item: DownloadItem) {
        val file = File(item.destinationPath)
        if (!file.exists()) {
            Toast.makeText(this, "El archivo no se encuentra disponible", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val uri: Uri = FileProvider.getUriForFile(
                this,
                "$packageName.provider",
                file
            )

            val extension = file.extension.lowercase()
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Compartir archivo toDus"))
        } catch (e: Exception) {
            Toast.makeText(this, "Error al compartir archivo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startDownloadItem(url: String, customName: String, threads: Int) {
        val id = UUID.randomUUID().toString()
        val defaultName = if (customName.isNotBlank()) {
            customName
        } else {
            url.substringBefore("?").substringAfterLast("/").ifBlank { "todus_file_${System.currentTimeMillis()}.bin" }
        }

        val item = DownloadItem(
            id = id,
            url = url,
            name = defaultName,
            threads = threads,
            status = DownloadItem.Status.QUEUED
        )

        repository.addItem(item)
        engine.startDownload(item)

        // Start Foreground Service
        val serviceIntent = Intent(this, DownloadService::class.java).apply {
            action = DownloadService.ACTION_START_DOWNLOAD
            putExtra(DownloadService.EXTRA_DOWNLOAD_ID, id)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    @Composable
    fun MainAppScreen(onThemeChange: () -> Unit) {
        var currentTab by remember { mutableStateOf(AppTab.DOWNLOADS) }
        var showAddDialog by remember { mutableStateOf(false) }
        var initialDialogUrl by remember { mutableStateOf("") }

        val downloads by repository.downloads.collectAsStateWithLifecycle()

        // Predictive back support: return to Downloads tab first
        BackHandler(enabled = currentTab != AppTab.DOWNLOADS) {
            currentTab = AppTab.DOWNLOADS
        }

        // Notification permission launcher for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { _ -> }
            LaunchedEffect(Unit) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Handle shared intent
        LaunchedEffect(sharedUrlFromIntent) {
            sharedUrlFromIntent?.let {
                initialDialogUrl = it
                showAddDialog = true
                sharedUrlFromIntent = null
            }
        }

        val activeDownloadsCount = remember(downloads) {
            downloads.count { it.status == DownloadItem.Status.DOWNLOADING || it.status == DownloadItem.Status.CONNECTING }
        }

        val haptics = LocalHapticFeedback.current

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                FloatingBottomBar(
                    items = AppTab.values().map { tab ->
                        NavBarItem(
                            label = tab.title,
                            icon = tab.icon,
                            badgeCount = if (tab == AppTab.DOWNLOADS) activeDownloadsCount else 0
                        )
                    },
                    selectedIndex = currentTab.ordinal,
                    onSelected = { index ->
                        haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        currentTab = AppTab.values()[index]
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentTab) {
                    AppTab.DOWNLOADS -> {
                        DownloadsScreen(
                            downloads = downloads,
                            onOpenAddDialog = {
                                initialDialogUrl = ""
                                showAddDialog = true
                            },
                            onPauseResume = { item ->
                                if (item.status == DownloadItem.Status.DOWNLOADING || item.status == DownloadItem.Status.CONNECTING) {
                                    engine.pauseDownload(item.id)
                                } else {
                                    engine.startDownload(item)
                                    val serviceIntent = Intent(this@MainActivity, DownloadService::class.java).apply {
                                        action = DownloadService.ACTION_START_DOWNLOAD
                                        putExtra(DownloadService.EXTRA_DOWNLOAD_ID, item.id)
                                    }
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        startForegroundService(serviceIntent)
                                    } else {
                                        startService(serviceIntent)
                                    }
                                }
                            },
                            onCancel = { item ->
                                engine.cancelDownload(item.id)
                            },
                            onOpen = { item ->
                                openFile(item)
                            },
                            onShare = { item ->
                                shareFile(item)
                            },
                            onBadgeClick = {
                                currentTab = AppTab.SETTINGS
                            },
                            onPauseAll = {
                                downloads.filter { it.status == DownloadItem.Status.DOWNLOADING || it.status == DownloadItem.Status.CONNECTING }
                                    .forEach { engine.pauseDownload(it.id) }
                            },
                            onResumeAll = {
                                downloads.filter { it.status == DownloadItem.Status.PAUSED }
                                    .forEach { item ->
                                        engine.startDownload(item)
                                        val serviceIntent = Intent(this@MainActivity, DownloadService::class.java).apply {
                                            action = DownloadService.ACTION_START_DOWNLOAD
                                            putExtra(DownloadService.EXTRA_DOWNLOAD_ID, item.id)
                                        }
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            startForegroundService(serviceIntent)
                                        } else {
                                            startService(serviceIntent)
                                        }
                                    }
                            }
                        )
                    }

                    AppTab.HISTORY -> {
                        HistoryScreen(
                            downloads = downloads,
                            onOpen = { item ->
                                openFile(item)
                            },
                            onShare = { item ->
                                shareFile(item)
                            },
                            onDelete = { item ->
                                repository.removeItem(item.id)
                            },
                            onClearAll = {
                                repository.clearCompleted()
                            }
                        )
                    }

                    AppTab.SETTINGS -> {
                        SettingsScreen(
                            preferences = preferences,
                            onTestConnection = {
                                withContext(Dispatchers.IO) {
                                    networkClient.testConnection()
                                }
                            },
                            onThemeChange = onThemeChange
                        )
                    }
                }

                if (showAddDialog) {
                    AddUrlDialog(
                        initialUrl = initialDialogUrl,
                        defaultThreads = preferences.defaultThreads,
                        onDismiss = { showAddDialog = false },
                        onConfirm = { url, name, threads ->
                            startDownloadItem(url, name, threads)
                            showAddDialog = false
                            currentTab = AppTab.DOWNLOADS
                        }
                    )
                }
            }
        }
    }
}
