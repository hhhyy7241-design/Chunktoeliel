package cu.todus.download.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class DownloadRepository(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val file = File(context.filesDir, "downloads_cache.json")
    private val saveMutex = Mutex()

    private val _downloads = MutableStateFlow<List<DownloadItem>>(emptyList())
    val downloads: StateFlow<List<DownloadItem>> = _downloads.asStateFlow()

    init {
        loadFromDisk()
    }

    private fun loadFromDisk() {
        if (!file.exists()) {
            _downloads.value = emptyList()
            return
        }

        try {
            val jsonStr = file.readText()
            val array = JSONArray(jsonStr)
            val list = mutableListOf<DownloadItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val statusStr = obj.optString("status", "QUEUED")
                val status = try {
                    DownloadItem.Status.valueOf(statusStr)
                } catch (_: Exception) {
                    DownloadItem.Status.QUEUED
                }
                list.add(
                    DownloadItem(
                        id = obj.getString("id"),
                        url = obj.getString("url"),
                        name = obj.getString("name"),
                        totalBytes = obj.optLong("totalBytes", 0L),
                        downloadedBytes = obj.optLong("downloadedBytes", 0L),
                        speedBps = 0L,
                        status = if (status == DownloadItem.Status.DOWNLOADING || status == DownloadItem.Status.CONNECTING) DownloadItem.Status.PAUSED else status,
                        threads = obj.optInt("threads", 4),
                        destinationPath = obj.optString("destinationPath", ""),
                        errorMsg = if (obj.has("errorMsg")) obj.getString("errorMsg") else null,
                        addedTimestamp = obj.optLong("addedTimestamp", System.currentTimeMillis()),
                        completedTimestamp = obj.optLong("completedTimestamp", 0L),
                        mimeType = obj.optString("mimeType", "")
                    )
                )
            }
            _downloads.value = list
        } catch (_: Exception) {
            _downloads.value = emptyList()
        }
    }

    private fun saveToDisk(list: List<DownloadItem>) {
        scope.launch {
            saveMutex.withLock {
                try {
                    val array = JSONArray()
                    for (item in list) {
                        val obj = JSONObject().apply {
                            put("id", item.id)
                            put("url", item.url)
                            put("name", item.name)
                            put("totalBytes", item.totalBytes)
                            put("downloadedBytes", item.downloadedBytes)
                            put("status", item.status.name)
                            put("threads", item.threads)
                            put("destinationPath", item.destinationPath)
                            item.errorMsg?.let { put("errorMsg", it) }
                            put("addedTimestamp", item.addedTimestamp)
                            put("completedTimestamp", item.completedTimestamp)
                            put("mimeType", item.mimeType)
                        }
                        array.put(obj)
                    }
                    val tempFile = File(file.parentFile, "${file.name}.tmp")
                    tempFile.writeText(array.toString(2))
                    if (!tempFile.renameTo(file)) {
                        file.writeText(array.toString(2))
                        tempFile.delete()
                    }
                } catch (_: Exception) {
                    // La siguiente actualización volverá a intentar persistir el estado.
                }
            }
        }
    }

    @Synchronized
    fun addItem(item: DownloadItem) {
        val current = _downloads.value.toMutableList()
        current.add(0, item)
        _downloads.value = current
        saveToDisk(current)
    }

    @Synchronized
    fun updateItem(item: DownloadItem) {
        val current = _downloads.value.toMutableList()
        val index = current.indexOfFirst { it.id == item.id }
        if (index != -1) {
            current[index] = item
            _downloads.value = current
            saveToDisk(current)
        }
    }

    @Synchronized
    fun removeItem(id: String) {
        val current = _downloads.value.toMutableList()
        current.removeAll { it.id == id }
        _downloads.value = current
        saveToDisk(current)
    }

    @Synchronized
    fun clearCompleted() {
        val current = _downloads.value.filter { it.status != DownloadItem.Status.COMPLETED }
        _downloads.value = current
        saveToDisk(current)
    }
}
