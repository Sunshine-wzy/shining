package io.github.sunshinewzy.shining.core.data.legacy

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.core.data.DataManager
import io.github.sunshinewzy.shining.interfaces.Initable
import io.github.sunshinewzy.shining.utils.getDataPath
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException

/**
 * 自动保存, 可读可写 ,但不覆盖原有数据的配置文件
 * 
 * @param plugin 插件实例
 * @param name 保存的文件名
 * @param path 保存的路径(保存到 plugins/插件名/路径 下)
 * @param saveTime 自动保存时间间隔
 */
abstract class SAutoSaveData(
    private val plugin: JavaPlugin,
    val name: String,
    val path: String = "",
    val saveTime: Long = 12_000
) : Initable {
    protected val file = File(
        plugin.dataFolder,
        if (path == "") "data/$name.yml"
        else "${path.replace("\\", "/")}/$name.yml"
    )
    
    
    constructor(plugin: JavaPlugin, name: String, file: File): this(
        plugin,
        name,
        file.getDataPath(plugin)
    )
    
    init {
        DataManager.allAutoSaveData.add(this)
        
        if(file.exists()){
            Bukkit.getScheduler().runTaskLater(Shining.plugin, Runnable {
                load()
            }, 1)
        } else create()
        
        Bukkit.getScheduler().runTaskTimer(Shining.plugin, Runnable {
            save()
        }, saveTime, saveTime)
    }


    /**
     * 创建文件时调用
     */
    open fun YamlConfiguration.createConfig() { }
    
    /**
     * 保存文件前调用
     */
    abstract fun YamlConfiguration.modifyConfig()

    /**
     * 加载文件后调用
     */
    abstract fun YamlConfiguration.loadConfig()


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
     * 保存配置文件
     */
    open fun save() {
        val config = getConfig()
        config.modifyConfig()
        
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
        getConfig().loadConfig()
    }


    override fun init() {
        
    }

    fun getConfig(): YamlConfiguration = YamlConfiguration.loadConfiguration(file)
    
}