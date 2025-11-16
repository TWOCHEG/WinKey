package twocheg.mod.managers

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

typealias JsonMap = MutableMap<String, Any>

val GSON: Gson = GsonBuilder()
    .disableHtmlEscaping()
    .setPrettyPrinting()
    .create()

val CONFIG_DIR: Path = Path.of(System.getProperty("user.home"), ".dl")
const val FILE_EXT: String = ".json"
const val DEFAULT_PROFILE = "default"

fun ensureConfigDir(): Path = Files.createDirectories(CONFIG_DIR)

data class ConfigProfile(val name: String, val path: Path) {
    companion object {
        fun current(): ConfigProfile = ConfigProfile(getCurrentProfileName(), getCurrentProfilePath())
    }
}

fun getCurrentProfilePath(): Path {
    ensureConfigDir()
    val files = Files.list(CONFIG_DIR)
        .filter { it.toString().endsWith(FILE_EXT) && Files.isRegularFile(it) }
        .toList()

    if (files.isEmpty()) {
        return createDefaultProfile()
    }

    files.forEach { path ->
        val json = readJson(path)
        if (json["current"] == true) return path
    }
    return files.first()
}

fun getCurrentProfileName(): String = getCurrentProfilePath().fileName.toString().removeSuffix(FILE_EXT)

private fun createDefaultProfile(): Path {
    val path = CONFIG_DIR.resolve("$DEFAULT_PROFILE$FILE_EXT")
    writeJson(path, mutableMapOf("current" to true))
    return path
}

fun setCurrentProfile(name: String) {
    val newPath = CONFIG_DIR.resolve("$name$FILE_EXT")
    if (!Files.exists(newPath)) {
        writeJson(newPath, mutableMapOf("current" to true))
    }
    val oldPath = getCurrentProfilePath()
    if (oldPath == newPath) return

    writeJson(newPath, readJson(newPath).apply { this["current"] = true })
    writeJson(oldPath, readJson(oldPath).apply { this["current"] = false })
}

fun readJson(path: Path): JsonMap = try {
    if (Files.isRegularFile(path)) {
        val content = Files.readString(path, StandardCharsets.UTF_8)
        if (content.isNotBlank()) {
            GSON.fromJson(content, object : com.google.gson.reflect.TypeToken<JsonMap>() {}.type) ?: mutableMapOf()
        } else mutableMapOf()
    } else mutableMapOf()
} catch (e: Exception) {
    e.printStackTrace()
    mutableMapOf()
}

fun writeJson(path: Path, json: JsonMap) {
    val content = GSON.toJson(json)
    Files.writeString(path, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
}

class ConfigManager(private val keyPath: String? = null) {
    private val currentPath get() = getCurrentProfilePath()

    operator fun <T> get(key: String, default: T): T = get(currentPath, key, default)
    operator fun <T> get(path: Path, key: String, default: T): T {
        val fullKey = if (keyPath != null) "$keyPath.$key" else key
        val value = readJson(path)[fullKey] ?: return default

        @Suppress("UNCHECKED_CAST")
        return when (default) {
            is Boolean -> when (value) {
                is Boolean -> value as T
                is String -> value.toBoolean() as T
                is Number -> (value.toInt() != 0) as T
                else -> default
            }
            is Int -> (value as? Number)?.toInt() as T ?: default
            is Long -> (value as? Number)?.toLong() as T ?: default
            is Float -> (value as? Number)?.toFloat() as T ?: default
            is Double -> (value as? Number)?.toDouble() as T ?: default
            is String -> value.toString() as T
            else -> try { value as T } catch (e: Exception) { default }
        }
    }

    operator fun <T> set(key: String, value: T) = set(currentPath, key, value)
    operator fun <T> set(path: Path, key: String, value: T) {
        val fullKey = if (keyPath != null) "$keyPath.$key" else key
        val json = readJson(path)
        json[fullKey] = value as Any
        writeJson(path, json)
    }

    fun sub(path: String) = ConfigManager(if (keyPath != null) "$keyPath.$path" else path)
}