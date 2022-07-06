package io.github.sunshinewzy.sunstcore

import io.github.sunshinewzy.sunstcore.commands.SunSTCommand
import io.github.sunshinewzy.sunstcore.interfaces.SPlugin
import io.github.sunshinewzy.sunstcore.listeners.SunSTSubscriber
import io.github.sunshinewzy.sunstcore.modules.data.DataManager
import io.github.sunshinewzy.sunstcore.modules.data.sunst.SLocationData
import io.github.sunshinewzy.sunstcore.modules.guide.SGuide
import io.github.sunshinewzy.sunstcore.modules.guide.element.GuideCategory
import io.github.sunshinewzy.sunstcore.modules.machine.*
import io.github.sunshinewzy.sunstcore.modules.machine.custom.SMachineRecipe
import io.github.sunshinewzy.sunstcore.modules.machine.custom.SMachineRecipes
import io.github.sunshinewzy.sunstcore.modules.task.TaskProgress
import io.github.sunshinewzy.sunstcore.objects.SBlock
import io.github.sunshinewzy.sunstcore.objects.SCraftRecipe
import io.github.sunshinewzy.sunstcore.objects.SHashMap
import io.github.sunshinewzy.sunstcore.objects.SItem
import io.github.sunshinewzy.sunstcore.objects.item.SunSTItem
import io.github.sunshinewzy.sunstcore.objects.machine.SunSTMachineManager
import io.github.sunshinewzy.sunstcore.utils.SReflect
import io.github.sunshinewzy.sunstcore.utils.SunSTTestApi
import io.github.sunshinewzy.sunstcore.utils.subscribeEvent
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryPickupItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.PluginManager
import taboolib.common.LifeCycle
import taboolib.common.platform.Platform
import taboolib.common.platform.Plugin
import taboolib.common.platform.SkipTo
import taboolib.common.platform.function.info
import taboolib.common.platform.function.pluginVersion
import taboolib.common.platform.function.submit
import taboolib.module.metrics.Metrics
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.BukkitPlugin


@SkipTo(LifeCycle.ENABLE)
object SunSTCore : Plugin(), SPlugin {
    const val name = "SunSTCore"
    const val colorName = "§eSunSTCore"
    val plugin: BukkitPlugin by lazy { BukkitPlugin.getInstance() }
    val pluginManager: PluginManager by lazy { Bukkit.getPluginManager() }

    override fun onEnable() {
        
        registerSerialization()
        registerListeners()
        init()
        
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
        SunSTCommand.init()
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
        SGuide.registerElement(GuideCategory("ElectricalAge", SItem(Material.NETHERITE_INGOT, "&a电器时代")), 12)
        SGuide.registerElement(GuideCategory("StoneAge", SItem(Material.STONE, "&e石器时代")))
        SGuide.registerElement(GuideCategory("InformationAge", SItem(Material.DIAMOND, "&b信息时代")), 13)
        SGuide.registerElement(GuideCategory("SteamAge", SItem(Material.IRON_INGOT, "&e蒸汽时代")), 11)
        
        
        subscribeEvent<PlayerInteractEvent> { 
            if(player.inventory.itemInMainHand.type != Material.DIAMOND) return@subscribeEvent
            
            if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                SGuide.open(player)
            }
        }
        
        subscribeEvent<InventoryPickupItemEvent> { 
            info(inventory)
        }
    }
    
}