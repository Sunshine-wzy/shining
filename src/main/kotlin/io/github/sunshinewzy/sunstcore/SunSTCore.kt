package io.github.sunshinewzy.sunstcore

import io.github.sunshinewzy.sunstcore.interfaces.SPlugin
import io.github.sunshinewzy.sunstcore.listeners.SunSTSubscriber
import io.github.sunshinewzy.sunstcore.modules.data.DataManager
import io.github.sunshinewzy.sunstcore.modules.data.sunst.SLocationData
import io.github.sunshinewzy.sunstcore.modules.guide.SGuide
import io.github.sunshinewzy.sunstcore.modules.guide.element.GuideCategory
import io.github.sunshinewzy.sunstcore.modules.guide.element.GuideItem
import io.github.sunshinewzy.sunstcore.modules.guide.lock.LockExperience
import io.github.sunshinewzy.sunstcore.modules.guide.lock.LockItem
import io.github.sunshinewzy.sunstcore.modules.machine.*
import io.github.sunshinewzy.sunstcore.modules.machine.custom.SMachineRecipe
import io.github.sunshinewzy.sunstcore.modules.machine.custom.SMachineRecipes
import io.github.sunshinewzy.sunstcore.modules.task.TaskProgress
import io.github.sunshinewzy.sunstcore.objects.SBlock
import io.github.sunshinewzy.sunstcore.objects.SItem
import io.github.sunshinewzy.sunstcore.objects.item.SunSTItem
import io.github.sunshinewzy.sunstcore.objects.machine.SunSTMachineManager
import io.github.sunshinewzy.sunstcore.utils.SReflect
import io.github.sunshinewzy.sunstcore.utils.SunSTTestApi
import io.github.sunshinewzy.sunstcore.utils.subscribeEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.PluginManager
import taboolib.common.LifeCycle
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Platform
import taboolib.common.platform.Plugin
import taboolib.common.platform.SkipTo
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.info
import taboolib.common.platform.function.pluginVersion
import taboolib.expansion.getDataContainer
import taboolib.expansion.setupPlayerDatabase
import taboolib.module.chat.colored
import taboolib.module.metrics.Metrics
import taboolib.platform.BukkitPlugin
import java.io.File


@SkipTo(LifeCycle.ENABLE)
@RuntimeDependencies(
    RuntimeDependency(
        value = "org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.3",
        relocate = ["!kotlin.", "!kotlin@kotlin_version_escape@."]
    ),
    RuntimeDependency(
        value = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3",
        relocate = ["!kotlin.", "!kotlin@kotlin_version_escape@."]
    ),
    RuntimeDependency(
        value = "org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.3.3",
        relocate = ["!kotlin.", "!kotlin@kotlin_version_escape@."]
    )
)
object SunSTCore : Plugin(), SPlugin {
    const val name = "SunSTCore"
    const val colorName = "§eSunSTCore"
    
    
    val plugin: BukkitPlugin by lazy { BukkitPlugin.getInstance() }
    val pluginManager: PluginManager by lazy { Bukkit.getPluginManager() }
    val prefixName: String by lazy { plugin.config.getString("PrefixName")?.colored() ?: colorName }
    
    
    override fun onEnable() {
        
        registerSerialization()
        registerListeners()
        init()
        
        setupPlayerDatabase(File(getDataFolder(), "player/data.db"))
        
        val metrics = Metrics(10212, pluginVersion, Platform.BUKKIT)
        
        info("SunSTCore 加载成功！")
        
        if(System.getProperty("SunSTDebug") == "true")
            test()
    }

    override fun onDisable() {
        DataManager.saveData()
    }


    private fun init() {
        try {
            SReflect.init()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        
        SItem.initAction()
        DataManager.init()
        SunSTItem.init()
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
    
    
    @SunSTTestApi
    private fun test() {
        
        val stoneCategory = GuideCategory("STONE_AGE", SItem(Material.STONE, "&f石器时代", "&d一切的起源"))
        val steamCategory = GuideCategory("STEAM_AGE", SItem(Material.IRON_INGOT, "&e蒸汽时代", "&d第一次工业革命"))
        val electricalCategory = GuideCategory("ELECTRICAL_AGE", SItem(Material.NETHERITE_INGOT, "&a电器时代", "&d第二次工业革命"))
        val informationCateGory = GuideCategory("INFORMATION_AGE", SItem(Material.DIAMOND, "&b信息时代", "&d技术爆炸"))
        
        steamCategory.registerDependency(stoneCategory)
        electricalCategory.registerDependency(steamCategory)
        informationCateGory.registerDependency(electricalCategory)
        
        val lockExperience = LockExperience(5)
        
        val newStoneCategory = GuideCategory("NEW_STONE_AGE", SItem(Material.STONE_BRICKS, "&a新石器时代"))
        val stickItem = GuideItem("STICK", SItem(Material.STICK, "&6工具的基石"))
        newStoneCategory.registerElement(stickItem)
        stoneCategory.registerElement(newStoneCategory)
        
        val oldStoneCategory = GuideCategory("OLD_STONE_AGE", SItem(Material.COBBLESTONE, "&7旧石器时代"))
        stoneCategory.registerElement(oldStoneCategory)
        
        stickItem.registerLock(lockExperience)
        newStoneCategory.registerLock(lockExperience)
        newStoneCategory.registerLock(LockItem(SItem(Material.SNOWBALL, 3)))
        oldStoneCategory.registerLock(LockExperience(10, false))
        
        SGuide.registerElement(electricalCategory, 12)
        SGuide.registerElement(stoneCategory)
        SGuide.registerElement(informationCateGory, 13)
        SGuide.registerElement(steamCategory, 11)
        
        
        subscribeEvent<PlayerInteractEvent>(ignoreCancelled = false) {
            if(hand != EquipmentSlot.HAND) return@subscribeEvent
            
            if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                val type = player.inventory.itemInMainHand.type
                if(type == Material.DIAMOND) {
                    SGuide.openLastElement(player)
                } else if(type == Material.EMERALD) {
                    SGuide.fireworkCongratulate(player)
                }
            }
        }
        
        subscribeEvent<PlayerJoinEvent> { 
            player.getDataContainer()["test"] = 1
            player.getDataContainer()["awa"] = "emm"
        }
    }
    
}