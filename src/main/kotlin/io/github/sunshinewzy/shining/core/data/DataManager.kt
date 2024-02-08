package io.github.sunshinewzy.shining.core.data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.core.data.database.player.PlayerData
import io.github.sunshinewzy.shining.core.data.legacy.SAutoSaveData
import io.github.sunshinewzy.shining.core.data.legacy.internal.SunSTPlayerData
import io.github.sunshinewzy.shining.core.guide.draft.GuideDraftFolders
import io.github.sunshinewzy.shining.core.guide.draft.GuideDrafts
import io.github.sunshinewzy.shining.core.guide.element.GuideElementRegistry
import io.github.sunshinewzy.shining.core.guide.team.GuideTeams
import io.github.sunshinewzy.shining.core.task.TaskProgress
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import taboolib.common.LifeCycle
import taboolib.common.io.newFile
import taboolib.common.platform.Awake
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.submit
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.database.Database.settingsFile
import taboolib.module.database.Host
import taboolib.module.database.HostSQL
import taboolib.module.database.HostSQLite
import java.io.File
import java.sql.Connection
import javax.sql.DataSource


object DataManager {
    private val dir = getDataFolder()
    private val allReloadData = ArrayList<SAutoSaveData>()
    private val lazyOperations = arrayListOf<LazyOperational>()

    val databaseConfig: ConfigurationSection by lazy {
        Shining.config.getConfigurationSection("database")
            ?: throw RuntimeException("Config 'database' does not exist.")
    }

    lateinit var database: Database
        private set

    val allAutoSaveData = ArrayList<SAutoSaveData>()
    val sPlayerData = HashMap<String, SunSTPlayerData>()
    val firstJoinGiveOpenItems = HashMap<String, ItemStack>()

    val autoSavePeriod: Long by lazy { Shining.config.getLong("auto_save_period", 6000L) }


    suspend fun init() {
        database = if (Shining.config.getBoolean("database.enable")) {
            val hostSQL = HostSQL(databaseConfig)
            Database.connect(createDataSource(hostSQL))
        } else {
            Database.connect(createDataSource(HostSQLite(newFile(getDataFolder(), "data/data.db"))))
        }

        submit(async = true, delay = autoSavePeriod, period = autoSavePeriod) {
            lazyOperations.forEach {
                it.saveLazy()
            }
        }


        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        newSuspendedTransaction {
            SchemaUtils.createMissingTablesAndColumns(
                GuideTeams, PlayerData,
                GuideElementRegistry,
                GuideDrafts, GuideDraftFolders
            )
        }

    }


    fun createDataSource(host: Host<*>, hikariConfig: HikariConfig? = null): DataSource {
        return HikariDataSource(hikariConfig ?: createHikariConfig(host))
    }

    fun createHikariConfig(host: Host<*>): HikariConfig {
        val config = HikariConfig()
        config.jdbcUrl = host.connectionUrl
        when (host) {
            is HostSQL -> {
                config.driverClassName = settingsFile.getString("DefaultSettings.DriverClassName", "com.mysql.jdbc.Driver")
                config.username = host.user
                config.password = host.password
            }

            is HostSQLite -> {
                config.driverClassName = "org.sqlite.JDBC"
            }

            else -> {
                error("Unsupported host: $host")
            }
        }
        config.isAutoCommit = settingsFile.getBoolean("DefaultSettings.AutoCommit", true)
        config.minimumIdle = settingsFile.getInt("DefaultSettings.MinimumIdle", 1)
        config.maximumPoolSize = settingsFile.getInt("DefaultSettings.MaximumPoolSize", 10)
        config.validationTimeout = settingsFile.getLong("DefaultSettings.ValidationTimeout", 5000)
        config.connectionTimeout = settingsFile.getLong("DefaultSettings.ConnectionTimeout", 30000)
        config.idleTimeout = settingsFile.getLong("DefaultSettings.IdleTimeout", 600000)
        config.maxLifetime = settingsFile.getLong("DefaultSettings.MaxLifetime", 1800000)
        if (settingsFile.contains("DefaultSettings.ConnectionTestQuery")) {
            config.connectionTestQuery = settingsFile.getString("DefaultSettings.ConnectionTestQuery")
        }
        if (settingsFile.contains("DefaultSettings.DataSourceProperty")) {
            settingsFile.getConfigurationSection("DefaultSettings.DataSourceProperty")?.getKeys(false)?.forEach { key ->
                config.addDataSourceProperty(key, settingsFile.getString("DefaultSettings.DataSourceProperty.$key"))
            }
        }
        return config
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
        if (!dirFolder.exists()) return
        val files = dirFolder.listFiles() ?: return

        files.forEach {
            if (it.isFile) {
                val extensionName = it.extension
                if (extensionName == "yml") {
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

        val data = SunSTPlayerData(Shining.plugin, uid)
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
        if (!contains(key))
            return false

        val root = getConfigurationSection(key) ?: return false
        val keys = root.getKeys(false)
        keys.forEach {
            val value = root.get(it) ?: return@forEach
            if (value is V) {
                map[it] = value
            }
        }

        return true
    }

}