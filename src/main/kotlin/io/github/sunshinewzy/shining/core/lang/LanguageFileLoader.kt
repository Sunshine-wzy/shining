package io.github.sunshinewzy.shining.core.lang

import io.github.sunshinewzy.shining.api.lang.LanguageFile
import io.github.sunshinewzy.shining.api.lang.node.LanguageNode
import io.github.sunshinewzy.shining.core.lang.node.ListNode
import io.github.sunshinewzy.shining.core.lang.node.SectionNode
import io.github.sunshinewzy.shining.core.lang.node.TextNode
import org.bukkit.configuration.Configuration
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.warning
import taboolib.common5.FileWatcher
import java.io.File
import java.text.SimpleDateFormat

object LanguageFileLoader {

    private val isFileWatcherHook by lazy {
        try {
            FileWatcher.INSTANCE
            true
        } catch (ex: NoClassDefFoundError) {
            false
        }
    }
    private val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm")


    @JvmOverloads
    fun loadLanguageFiles(
        languageCode: Set<String>,
        classLoader: ClassLoader,
        path: String = "lang",
        checkMissingNodes: Boolean = true
    ): Map<String, LanguageFile> {
        val fileMap = HashMap<String, LanguageFile>()

        languageCode.forEach { code ->
            classLoader.getResourceAsStream("$path/$code.yml")?.use { resourceAsStream ->
                val nodes = HashMap<String, LanguageNode>()
                val configuration = YamlConfiguration.loadConfiguration(resourceAsStream.reader())
                // Load the language file from the jar file
                loadNodeMap(configuration, nodes, code)
                // Release the language file in the data folder
                val file = releaseResourceFile("$path/$code.yml")
                // Remove the file listener
                if (isFileWatcherHook) {
                    FileWatcher.INSTANCE.removeListener(file)
                }
                val exists = HashMap<String, LanguageNode>()
                // Load the language file from the data folder
                loadNodeMap(YamlConfiguration.loadConfiguration(file), exists, code)
                // Check missing nodes
                if (checkMissingNodes) {
                    val missingNodes = nodes.keys.filter { !exists.containsKey(it) }
                    if (missingNodes.isNotEmpty()) {
                        // Update the file
                        migrateFile(missingNodes, configuration, file)
                    }
                }
                nodes += exists

                fileMap[code.lowercase()] = LanguageFile(file, nodes).also {
                    // Watch the file change
                    if (isFileWatcherHook) {
                        FileWatcher.INSTANCE.addSimpleListener(file) {
                            it.nodeMap.clear()
                            loadNodeMap(configuration, it.nodeMap, code)
                            loadNodeMap(YamlConfiguration.loadConfiguration(file), it.nodeMap, code)
                        }
                    }
                }
            }
        }

        return fileMap
    }

    fun loadNodeMap(configuration: Configuration, nodeMap: MutableMap<String, LanguageNode>, code: String) {
        configuration.getKeys(false).forEach { node ->
            when (val obj = configuration[node]) {
                is String -> nodeMap[node] = TextNode(obj)
                is List<*> -> nodeMap[node] = ListNode(
                    obj.mapNotNull {
                        loadNode(it)
                    }
                )

                is ConfigurationSection -> nodeMap[node] = SectionNode(obj)
                else -> warning("Unsupported language node: $node ($code)")
            }
        }
    }

    fun loadNode(obj: Any?): LanguageNode? {
        if (obj == null) return null

        return when (obj) {
            is String -> TextNode(obj)
            is List<*> -> ListNode(
                obj.mapNotNull {
                    loadNode(it)
                }
            )

            is ConfigurationSection -> SectionNode(obj)
            else -> null
        }
    }

    fun migrateFile(missingNodes: List<String>, configuration: Configuration, file: File) {
        submit(async = true) {
            val builder = buildString {
                appendLine("# ------------------------- #")
                appendLine("#  UPDATE ${dateFormat.format(System.currentTimeMillis())}  #")
                appendLine("# ------------------------- #")
                appendLine()

                missingNodes.forEach { key ->
                    configuration[key]?.let {
                        appendLine(dumpAll(key, it))
                    }
                }
            }

            file.appendText("\n$builder")
        }
    }

    fun dumpAll(key: String, value: Any?, space: Int = 2): String {
        return if (key.contains('.')) {
            "${key.substringBefore('.')}:\n${" ".repeat(space)}${dumpAll(key.substringAfter('.'), value, space + 2)}"
        } else {
            val dump = dump(value)
            when {
                dump.startsWith("-") -> "$key:\n$dump"
                value is List<*> && value.isEmpty() -> "$key: []"
                value is Map<*, *> -> if (value.isEmpty()) "$key: {}" else "$key:\n$dump"
                value is ConfigurationSection -> "$key:\n  ${dump.replace("\n", "\n  ")}"
                else -> "$key: $dump"
            }
        }
    }

    fun dump(data: Any?): String {
        if (data == null) {
            return ""
        }
        var single = false
        val dump = YamlConfiguration()
        when (data) {
            is ConfigurationSection -> {
                data.getValues(false).forEach { (path, value) -> dump[path] = value }
            }
            is Map<*, *> -> {
                data.forEach { (k, v) -> dump[k.toString()] = v }
            }
            else -> {
                single = true
                dump["value"] = data
            }
        }
        val save = if (single) {
            dump.saveToString().substring("value:".length).trim().split('\n').toTypedArray()
        } else {
            dump.saveToString().trim().split('\n').toTypedArray()
        }
        return java.lang.String.join("\n", *save)
    }

}