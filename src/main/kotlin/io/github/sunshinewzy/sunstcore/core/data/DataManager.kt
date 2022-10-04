package io.github.sunshinewzy.sunstcore.core.data

import io.github.sunshinewzy.sunstcore.SunSTCore
import io.github.sunshinewzy.sunstcore.core.data.database.Database
import io.github.sunshinewzy.sunstcore.core.data.database.DatabaseSQL
import io.github.sunshinewzy.sunstcore.core.data.database.DatabaseSQLite
import io.github.sunshinewzy.sunstcore.core.data.legacy.SAutoSaveData
import io.github.sunshinewzy.sunstcore.core.data.legacy.internal.SunSTPlayerData
import io.github.sunshinewzy.sunstcore.core.guide.GuideGroups
import io.github.sunshinewzy.sunstcore.core.task.TaskProgress
import io.github.sunshinewzy.sunstcore.interfaces.Initable
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.submit
import taboolib.expansion.setupPlayerDatabase
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.database.HostSQL
import java.io.File
import java.sql.Connection

object DataManager : Initable {
    private val dir = getDataFolder()
    private val allReloadData = ArrayList<SAutoSaveData>()
    private val lazyOperations = arrayListOf<LazyOperational>()
    

    val databaseConfig: ConfigurationSection by lazy { SunSTCore.config.getConfigurationSection("database") ?: throw RuntimeException("Config 'database' does not exist.") }
    
    
    lateinit var database: Database<*>
        private set
    
    
    val allAutoSaveData = ArrayList<SAutoSaveData>()
    val sPlayerData = HashMap<String, SunSTPlayerData>()
    val firstJoinGiveOpenItems = HashMap<String, ItemStack>()
    
    val autoSavePeriod: Long by lazy { SunSTCore.config.getLong("auto_save_period", 6000L) }
    
    
    override fun init() {

        if(SunSTCore.config.getBoolean("database.enable")) {
            database = DatabaseSQL(HostSQL(databaseConfig))
            setupPlayerDatabase(databaseConfig, SunSTCore.config.getString("player_table").toString())
        } else {
            database = DatabaseSQLite(File(getDataFolder(), "data/data.db"))
            setupPlayerDatabase(File(getDataFolder(), "data/player.db"))
        }
        
        submit(async = true, delay = autoSavePeriod, period = autoSavePeriod) { 
            lazyOperations.forEach { 
                it.saveLazy()
            }
        }

        
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                GuideGroups
            )
        }

        TODO()
    }
    
    @Awake(LifeCycle.DISABLE)
    fun saveData() {
        allAutoSaveData.forEach { 
            it.save()
        }
        
        lazyOperations.forEach { 
            it.saveLazy()
        }
    }
    
    fun reloadData() {
        allReloadData.forEach { 
            it.save()
            it.load()
        }
    }
    
    fun addReloadData(data: SAutoSaveData) {
        allReloadData.add(data)
    }
    
    private fun loadFolderData(
        folderName: String,
        dirFolder: File = File(dir, folderName),
        block: (file: File, fileName: String) -> Unit
    ) {
        if(!dirFolder.exists()) return
        val files = dirFolder.listFiles() ?: return
        
        files.forEach {
            if(it.isFile){
                val extensionName = it.extension
                if(extensionName == "yml"){
                    val fileName = it.nameWithoutExtension
                    block(it, fileName)
                }
            }
            
//            else if(it.isDirectory){
//                loadFolderData()
//            }
        }
    }
    
    fun registerLazy(lazy: LazyOperational) {
        lazyOperations += lazy
    }
    
    
    fun Player.getSunSTData(): SunSTPlayerData {
        val uid = uniqueId.toString()
        
        sPlayerData[uid]?.let { 
            return it
        }
        
        val data = SunSTPlayerData(SunSTCore.plugin, uid)
        data.load()
        
        sPlayerData[uid] = data
        return data
    }
    
    fun Player.getTaskProgress(id: String): TaskProgress {
        val data = getSunSTData()
        data.taskProgress[id]?.let { 
            return it
        }
        
        val progress = TaskProgress()
        data.taskProgress[id] = progress
        return progress
    }
    
    inline fun <reified V> YamlConfiguration.getMap(
        key: String,
        map: MutableMap<String, V>
    ): Boolean {
        if(!contains(key))
            return false
        
        val root = getConfigurationSection(key) ?: return false
        val keys = root.getKeys(false)
        keys.forEach { 
            val value = root.get(it) ?: return@forEach
            if(value is V){
                map[it] = value
            }
        }

        return true
    }
    
}