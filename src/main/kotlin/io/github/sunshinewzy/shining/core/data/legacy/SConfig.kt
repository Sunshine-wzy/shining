package io.github.sunshinewzy.shining.core.data.legacy

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.interfaces.Initable
import io.github.sunshinewzy.shining.utils.SManager
import io.github.sunshinewzy.shining.utils.getDataPath
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException

/**
 * 只读的配置文件
 */
abstract class SConfig(
    private val plugin: JavaPlugin,
    val name: String,
    val path: String = "",
) : Initable {
    protected val file = File(
        plugin.dataFolder,
        "config/" + if (path == "") "$name.yml"
        else "${path.replace("\\", "/")}/$name.yml"
    )
    
    val config: YamlConfiguration = YamlConfiguration.loadConfiguration(file)


    constructor(plugin: JavaPlugin, name: String, file: File): this(
        plugin,
        name,
        file.getDataPath(plugin)
    )

    init {

        if(file.exists()){
            Bukkit.getScheduler().runTaskLater(Shining.plugin, Runnable {
                load()
            }, 1)
        } else create()

    }


    /**
     * 创建文件时调用
     */
    open fun YamlConfiguration.createConfig() { }


    /**
     * 加载文件后调用
     */
    open fun YamlConfiguration.loadConfig() { }

    
    fun get(key: String, default: Any): Any {
        config.get(key)?.let { 
            return it
        }
        
        return default
    }
    

    /**
     * 创建配置文件
     */
    private fun create() {
        val config = YamlConfiguration()
        config.createConfig()

        try {
            config.save(file)
        } catch (ex: IOException){
            ex.printStackTrace()
        }
    }


    /**
     * 加载配置文件
     */
    open fun load() {
        config.loadConfig()
    }


    override fun init() {

    }

    
    companion object {
        inline fun <reified T> File.loadYamlConfig(target: MutableList<T>, keys: List<String>) {
            val fileConfig = YamlConfiguration.loadConfiguration(this)

            keys.forEach {
                if(fileConfig.contains(it)){
                    val list = SManager.castList(fileConfig.get(it), T::class.java) ?: return@forEach
                    target.addAll(list)
                }
            }
        }

        inline fun <reified T> File.loadYamlConfig(target: MutableMap<String, MutableList<T>>) {
            val fileConfig = YamlConfiguration.loadConfiguration(this)

            target.forEach { (key, value) ->
                if(fileConfig.contains(key)){
                    val list = SManager.castList(key, T::class.java) ?: return@forEach
                    value.addAll(list)
                }
            }

        }
    }
    
    
}