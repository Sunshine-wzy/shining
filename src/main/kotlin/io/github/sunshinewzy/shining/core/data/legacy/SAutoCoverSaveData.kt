package io.github.sunshinewzy.shining.core.data.legacy

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException


/**
 * 自动保存, 可读可写, 覆盖原有数据的配置文件
 */
abstract class SAutoCoverSaveData(
    plugin: JavaPlugin,
    name: String,
    path: String = "",
    saveTime: Long = 12_000
) : SAutoSaveData(plugin, name, path, saveTime) {

    override fun save() {
        val config = YamlConfiguration()
        config.modifyConfig()

        try {
            config.save(file)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

}