package twocheg.mod.managers

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.json.JsonPrimitive
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

typealias JsonMap = MutableMap<String, Any>

@Suppress("UNCHECKED_CAST")
class ConfigManager(
    var keyPath: String? = null
) {
    fun <T> get(key: String, default: T): T {
        return get(getCurrent(), key, default)
    }
    fun <T> get(path: Path, key: String, default: T): T {
        var key = key
        val json = readJson(path)
        if (keyPath != null) {
            key = "$keyPath.$key"
        }
        return json.getOrDefault(key, default) as T
    }

    fun <T> set(key: String, value: T) {
        set(getCurrent(), key, value)
    }
    fun <T> set(path: Path, key: String, value: T) {
        var key = key
        if (keyPath != null) key = "$keyPath.$key"
        val readJson = readJson(path)
        readJson[key] = value as Any
        writeJson(path, readJson)
    }

    companion object {
        val GSON: Gson = GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create()

        val CONFIG_DIR: Path = Path.of(System.getProperty("user.home"), ".wk")

        const val CURRENT_KEY: String = "current"
        const val KEYBIND_KEY: String = "keybind"
        const val ENABLE_KEY: String = "enable"
        const val FILE_EXT: String = ".json"
        const val DEFAULT_NAME: String = "default"

        fun onChangeCurrent(old: Path, new: Path) {}

        fun getCurrent(): Path {
            if (!Files.exists(CONFIG_DIR)) {
                Files.createDirectories(CONFIG_DIR)
            }

            val files = Files.list(CONFIG_DIR)
                .filter { Files.isRegularFile(it) && it.toString().endsWith(FILE_EXT) }
                .toList()

            if (files.isEmpty()) {
                val exampleFile = CONFIG_DIR.resolve("$DEFAULT_NAME$FILE_EXT")
                return exampleFile
            } else {
                files.forEach {
                    val json = readJson(it)
                    val current = json[CURRENT_KEY] as? JsonPrimitive
                    val isCurrent = current?.toString().toBoolean()
                    if (isCurrent) return it
                }
                return files.first()
            }
        }
        fun setCurrent(new: Path) {
            val old = getCurrent()
            if (old == new) return

            val json1 = readJson(new)
            json1[CURRENT_KEY] = true
            writeJson(new, json1)

            val json2 = readJson(old)
            json1[CURRENT_KEY] = false
            writeJson(new, json2)

            onChangeCurrent(old, new)
        }

        fun writeJson(path: Path, json: JsonMap) {
            val jsonOutput: String = GSON.toJson(json)
            Files.writeString(
                path,
                jsonOutput,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            )
        }
        fun writeJson(json: JsonMap) {
            writeJson(getCurrent(), json)
        }
        fun readJson(path: Path): JsonMap {
            try {
                if (Files.exists(path) && Files.isRegularFile(path)) {
                    val content = Files.readString(path, StandardCharsets.UTF_8)
                    if (content.isNotBlank()) {
                        val type = object : TypeToken<JsonMap>() {}.type
                        val json = GSON.fromJson<JsonMap>(content, type)
                        return json ?: mutableMapOf()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return mutableMapOf()
        }
        fun readJson(): JsonMap {
            return readJson(getCurrent())
        }
    }
}