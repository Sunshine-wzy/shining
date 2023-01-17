package io.github.sunshinewzy.shining

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jsonMapper
import io.github.sunshinewzy.shining.api.ShiningPlugin
import io.github.sunshinewzy.shining.api.machine.IMachineManager
import io.github.sunshinewzy.shining.api.namespace.Namespace
import io.github.sunshinewzy.shining.core.data.DataManager
import io.github.sunshinewzy.shining.core.data.SerializationModules
import io.github.sunshinewzy.shining.core.data.legacy.internal.SLocationData
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.machine.MachineManager
import io.github.sunshinewzy.shining.core.machine.legacy.*
import io.github.sunshinewzy.shining.core.machine.legacy.custom.SMachineRecipe
import io.github.sunshinewzy.shining.core.machine.legacy.custom.SMachineRecipes
import io.github.sunshinewzy.shining.core.task.TaskProgress
import io.github.sunshinewzy.shining.listeners.SunSTSubscriber
import io.github.sunshinewzy.shining.objects.SItem
import io.github.sunshinewzy.shining.objects.legacy.SBlock
import io.github.sunshinewzy.shining.objects.machine.SunSTMachineManager
import io.github.sunshinewzy.shining.utils.SReflect
import io.github.sunshinewzy.shining.utils.ShiningTestApi
import io.github.sunshinewzy.shining.utils.giveItem
import io.github.sunshinewzy.shining.utils.subscribeEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.PluginManager
import org.jetbrains.exposed.sql.Database
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Platform
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.common.platform.function.pluginVersion
import taboolib.module.chat.colored
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.metrics.Metrics
import taboolib.platform.BukkitPlugin

@RuntimeDependencies(
    RuntimeDependency(value = "org.jetbrains.kotlin:kotlin-reflect:1.7.21", isolated = true),
    RuntimeDependency(value = "org.jetbrains.kotlinx:kotlinx-serialization-core:1.4.0", isolated = true),
    RuntimeDependency(value = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0", isolated = true),
    RuntimeDependency(value = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4", isolated = true),
    RuntimeDependency(value = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4", isolated = true),
    RuntimeDependency(value = "org.jetbrains.exposed:exposed-core:0.41.1", isolated = true),
    RuntimeDependency(value = "org.jetbrains.exposed:exposed-dao:0.41.1", isolated = true),
    RuntimeDependency(value = "org.jetbrains.exposed:exposed-jdbc:0.41.1", isolated = true),
    RuntimeDependency(value = "com.fasterxml.jackson.core:jackson-core:2.14.1", transitive = false, isolated = true),
    RuntimeDependency(value = "com.fasterxml.jackson.core:jackson-annotations:2.14.1", transitive = false, isolated = true),
    RuntimeDependency(value = "com.fasterxml.jackson.core:jackson-databind:2.14.1", transitive = false, isolated = true),
    RuntimeDependency(value = "com.fasterxml.jackson.module:jackson-module-kotlin:2.14.1", transitive = false, isolated = true),
    RuntimeDependency(value = "!com.zaxxer:HikariCP:4.0.3", isolated = true)
)
object Shining : Plugin(), ShiningPlugin {
    const val NAME = "Shining"
    const val COLOR_NAME = "§eShining"
    
    @Config
    lateinit var config: Configuration
        private set
    lateinit var database: Database
        private set
    
    val plugin: BukkitPlugin by lazy { BukkitPlugin.getInstance() }
    val pluginManager: PluginManager by lazy { Bukkit.getPluginManager() }
    @get:JvmName("prefix")
    val prefix: String by lazy { config.getString("prefix")?.colored() ?: COLOR_NAME }
    val machineManager: IMachineManager by lazy { MachineManager }
    val objectMapper: ObjectMapper = jsonMapper { 
        addModule(SerializationModules.shining)
        addModule(SerializationModules.bukkit)
    }
    val scope: CoroutineScope by lazy { CoroutineScope(SupervisorJob()) }
    
    private val namespace = Namespace[NAME.lowercase()]
    
    
    override fun onEnable() {
        registerSerialization()
        registerListeners()
        init()
        
        val metrics = Metrics(10212, pluginVersion, Platform.BUKKIT)
        
        info("Shining 加载成功！")
        
        if(System.getProperty("ShiningDebug") == "true")
            test()
    }

    override fun onDisable() {
        scope.cancel()
    }

    override fun getName(): String {
        return NAME
    }

    override fun getNamespace(): Namespace {
        return namespace
    }

    override fun getPrefix(): String {
        return prefix
    }

    private fun init() {
        try {
            SReflect.init()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        
        SItem.initAction()
        DataManager.init()
        SMachineWrench.init()
        SLocationData.init()
        SSingleMachine.init()
        SFlatMachine.init()
        SunSTMachineManager.register()
    }
    
    private fun registerListeners() {
        SunSTSubscriber.init()
    }
    
    private fun registerSerialization() {
        arrayOf(
            SBlock::class.java,
            TaskProgress::class.java,
            SMachineInformation::class.java, SSingleMachineInformation::class.java, SFlatMachineInformation::class.java,
            SMachineRecipe::class.java, SMachineRecipes::class.java
        ).forEach { 
            ConfigurationSerialization.registerClass(it)
        }
    }
    
    
    @ShiningTestApi
    private fun test() {
        /*
        val stoneCategory = GuideCategory(NamespacedId(Shining, "STONE_AGE"), SItem(Material.STONE, "&f石器时代", "&d一切的起源"))
        val steamCategory = GuideCategory(NamespacedId(Shining, "STEAM_AGE"), SItem(Material.IRON_INGOT, "&e蒸汽时代", "&d第一次工业革命"))
        val electricalCategory = GuideCategory(NamespacedId(Shining, "ELECTRICAL_AGE"), SItem(Material.NETHERITE_INGOT, "&a电器时代", "&d第二次工业革命"))
        val informationCateGory = GuideCategory(NamespacedId(Shining, "INFORMATION_AGE"), SItem(Material.DIAMOND, "&b信息时代", "&d技术爆炸"))
        
        steamCategory.registerDependency(stoneCategory)
        electricalCategory.registerDependency(steamCategory)
        informationCateGory.registerDependency(electricalCategory)
        
        val lockExperience = LockExperience(5)
        
        val newStoneCategory = GuideCategory(NamespacedId(Shining, "NEW_STONE_AGE"), SItem(Material.STONE_BRICKS, "&a新石器时代"))
        val stickItem = GuideItem(NamespacedId(Shining, "STICK"), SItem(Material.STICK, "&6工具的基石"))
        newStoneCategory.registerElement(stickItem)
        stoneCategory.registerElement(newStoneCategory)
        
        val oldStoneCategory = GuideCategory(NamespacedId(Shining, "OLD_STONE_AGE"), SItem(Material.COBBLESTONE, "&7旧石器时代"))
        stoneCategory.registerElement(oldStoneCategory)
        
        stickItem.registerLock(lockExperience)
        newStoneCategory.registerLock(lockExperience)
        newStoneCategory.registerLock(LockItem(SItem(Material.SNOWBALL, 3)))
        oldStoneCategory.registerLock(LockExperience(10, false))
        
        ShiningGuide.registerElement(electricalCategory, 12)
        ShiningGuide.registerElement(stoneCategory)
        ShiningGuide.registerElement(informationCateGory, 13)
        ShiningGuide.registerElement(steamCategory, 11)
        */
        
        val mapper = jsonMapper { 
            addModule(SerializationModules.bukkit)
        }
        
        subscribeEvent<PlayerInteractEvent>(ignoreCancelled = false) {
            if(hand != EquipmentSlot.HAND) return@subscribeEvent
            
            if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                val item = player.inventory.itemInMainHand
                when(item.type) {
                    Material.DIAMOND -> {
                        player.giveItem(ShiningGuide.getItem())
                    }
                    
                    Material.EMERALD -> {
                        ShiningGuide.fireworkCongratulate(player)
                    }
                    
                    Material.STICK -> {
                        clickedBlock?.let { block ->
                            player.sendMessage("""
                                > Block
                                ${block.type}
                                ${block.data}
                                ${block.blockData.asString}
                            """.trimIndent())
                        }
                    }
                    
                    Material.AIR -> {}
                    
                    else -> {
                        val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(item)
                        player.sendMessage(json)
//                        player.openMenu<Basic> { 
//                            rows(3)
//                            set(5 orderWith 2, mapper.readValue(json, ItemStack::class.java))
//                        }
                    }
                }
                
            }
        }
    }
    
}