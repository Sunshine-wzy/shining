package io.github.sunshinewzy.shining

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import io.github.sunshinewzy.shining.api.ShiningPlugin
import io.github.sunshinewzy.shining.api.event.ShiningDataLoadingCompleteEvent
import io.github.sunshinewzy.shining.api.guide.ElementDescription
import io.github.sunshinewzy.shining.api.guide.reward.GuideRewardRegistry
import io.github.sunshinewzy.shining.api.guide.state.GuideElementStateRegistry
import io.github.sunshinewzy.shining.api.item.ConsumableItemGroup
import io.github.sunshinewzy.shining.api.item.universal.DictionaryUniversalItem
import io.github.sunshinewzy.shining.api.item.universal.UniversalItemRegistry
import io.github.sunshinewzy.shining.api.item.universal.VanillaUniversalItem
import io.github.sunshinewzy.shining.api.machine.IMachineManager
import io.github.sunshinewzy.shining.api.namespace.Namespace
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.commands.CommandGuide
import io.github.sunshinewzy.shining.core.addon.ShiningAddonRegistry
import io.github.sunshinewzy.shining.core.data.DataManager
import io.github.sunshinewzy.shining.core.data.SerializationModules
import io.github.sunshinewzy.shining.core.data.legacy.internal.SLocationData
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.element.GuideCategory
import io.github.sunshinewzy.shining.core.guide.element.GuideElementRegistry
import io.github.sunshinewzy.shining.core.guide.element.GuideItem
import io.github.sunshinewzy.shining.core.guide.element.GuideMap
import io.github.sunshinewzy.shining.core.guide.lock.LockExperience
import io.github.sunshinewzy.shining.core.guide.lock.LockItem
import io.github.sunshinewzy.shining.core.guide.reward.GuideRewardCommand
import io.github.sunshinewzy.shining.core.guide.reward.GuideRewardItem
import io.github.sunshinewzy.shining.core.guide.settings.ShiningGuideSettings
import io.github.sunshinewzy.shining.core.guide.state.GuideCategoryState
import io.github.sunshinewzy.shining.core.guide.state.GuideItemState
import io.github.sunshinewzy.shining.core.guide.state.GuideMapState
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.machine.MachineManager
import io.github.sunshinewzy.shining.core.machine.legacy.*
import io.github.sunshinewzy.shining.core.machine.legacy.custom.SMachineRecipe
import io.github.sunshinewzy.shining.core.machine.legacy.custom.SMachineRecipes
import io.github.sunshinewzy.shining.core.task.TaskProgress
import io.github.sunshinewzy.shining.listeners.SunSTSubscriber
import io.github.sunshinewzy.shining.objects.SItem
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import io.github.sunshinewzy.shining.objects.coordinate.Coordinate2D
import io.github.sunshinewzy.shining.objects.legacy.SBlock
import io.github.sunshinewzy.shining.objects.machine.SunSTMachineManager
import io.github.sunshinewzy.shining.utils.SReflect
import io.github.sunshinewzy.shining.utils.ShiningTestApi
import io.github.sunshinewzy.shining.utils.giveItem
import io.github.sunshinewzy.shining.utils.registerBukkitListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Platform
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.common.platform.function.pluginVersion
import taboolib.common.platform.function.submit
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
    RuntimeDependency(value = "!com.zaxxer:HikariCP:4.0.3", isolated = true),
    RuntimeDependency(value = "com.fasterxml.jackson.module:jackson-module-kotlin:2.15.0", transitive = false, isolated = true)
)
object Shining : Plugin(), ShiningPlugin {
    const val NAME = "shining"
    const val COLOR_NAME = "§eshining"

    @Config
    lateinit var config: Configuration
        private set
    lateinit var database: Database
        private set

    val plugin: JavaPlugin by lazy { BukkitPlugin.getInstance() }
    val pluginManager: PluginManager by lazy { Bukkit.getPluginManager() }

    @get:JvmName("prefix")
    val prefix: String by lazy { config.getString("prefix")?.colored() ?: COLOR_NAME }
    val machineManager: IMachineManager by lazy { MachineManager }
    val objectMapper: ObjectMapper = jsonMapper {
        addModule(kotlinModule())
        addModule(SerializationModules.shining)
        addModule(SerializationModules.bukkit)
    }
    val yamlObjectMapper: ObjectMapper = ObjectMapper(YAMLFactory()).apply { 
        registerModule(kotlinModule())
        registerModule(SerializationModules.shining)
        registerModule(SerializationModules.bukkit)
    }
    val coroutineScope: CoroutineScope by lazy { CoroutineScope(SupervisorJob()) }

    private val namespace = Namespace[NAME.lowercase()]


    override fun onEnable() {
        init()

        val metrics = Metrics(19323, pluginVersion, Platform.BUKKIT)

        info("Shining 加载成功！")

        if (System.getProperty("shining.debug") == "true")
            test()
    }

    override fun onDisable() {
        ShiningAddonRegistry.disableJarAddons()
        coroutineScope.cancel()
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
        registerSerialization()
        registerClasses()
        registerListeners()
        registerPermissions()
        
        try {
            SReflect.init()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        // Load data
        ShiningDispatchers.launchDB {
            DataManager.init()
            GuideElementRegistry.init()
            
            submit {
                ShiningGuide.init()
                
                pluginManager.callEvent(ShiningDataLoadingCompleteEvent())
            }
        }
        
        SItem.initAction()
        SMachineWrench.init()
        SLocationData.init()
        SSingleMachine.init()
        SFlatMachine.init()
        SunSTMachineManager.register()
        
        ShiningAddonRegistry.loadAddons()
        submit { 
            ShiningAddonRegistry.activeJarAddons()
        }
    }

    private fun registerSerialization() {
        objectMapper.registerSubtypes(
            // UniversalItem
            VanillaUniversalItem::class.java, DictionaryUniversalItem::class.java,
            // IGuideReward
            GuideRewardItem::class.java, GuideRewardCommand::class.java,
            // UniversalItem
            VanillaUniversalItem::class.java, DictionaryUniversalItem::class.java
        )
        
        arrayOf(
            SBlock::class.java,
            TaskProgress::class.java,
            SMachineInformation::class.java, SSingleMachineInformation::class.java, SFlatMachineInformation::class.java,
            SMachineRecipe::class.java, SMachineRecipes::class.java
        ).forEach {
            ConfigurationSerialization.registerClass(it)
        }
    }
    
    private fun registerClasses() {
        GuideElementStateRegistry.register(mapOf(
            GuideItemState::class.java to NamespacedIdItem(Material.STICK, NamespacedId(Shining, "shining_guide-state-item")),
            GuideCategoryState::class.java to NamespacedIdItem(Material.BOOK, NamespacedId(Shining, "shining_guide-state-category")),
            GuideMapState::class.java to NamespacedIdItem(Material.MAP, NamespacedId(Shining, "shining_guide-state-map"))
        ))
        
        GuideRewardRegistry.register(mapOf(
            GuideRewardItem::class.java to GuideRewardItem.itemIcon,
            GuideRewardCommand::class.java to GuideRewardCommand.itemIcon
        ))
        
        UniversalItemRegistry.register(mapOf(
            VanillaUniversalItem::class.java to NamespacedIdItem(Material.GRASS_BLOCK, NamespacedId(Shining, "item-universal-vanilla-icon")),
            DictionaryUniversalItem::class.java to NamespacedIdItem(Material.BOOKSHELF, NamespacedId(Shining, "item-universal-dictionary-icon"))
        ))
    }

    private fun registerListeners() {
        SunSTSubscriber.init()
    }
    
    private fun registerPermission(permission: Permission) {
        pluginManager.addPermission(permission)
        pluginManager.recalculatePermissionDefaults(permission)
        permission.recalculatePermissibles()
    }
    
    private fun registerPermissions() {
        registerPermission(Permission(ShiningGuideSettings.PERMISSION_EDIT, PermissionDefault.OP))
        registerPermission(Permission(ShiningGuideSettings.PERMISSION_TEAM, PermissionDefault.TRUE))
        registerPermission(Permission(CommandGuide.PERMISSION_RELOAD, PermissionDefault.OP))
    }


    @ShiningTestApi
    private fun test() {
        registerBukkitListener<ShiningDataLoadingCompleteEvent> {
            val stoneCategory = GuideCategory(
                NamespacedId(Shining, "stone_age"),
                ElementDescription("&f石器时代", "&d一切的起源"),
                ItemStack(Material.STONE)
            )

            val lockExperience = LockExperience(5)

            val stickItem = GuideItem(
                NamespacedId(Shining, "stick"),
                ElementDescription("&6工具的基石"),
                ItemStack(Material.STICK),
                ConsumableItemGroup(true, VanillaUniversalItem(ItemStack(Material.STICK)))
            )
            stickItem.registerLock(lockExperience)
            stoneCategory.registerElement(stickItem)

            val newStoneCategory = GuideCategory(
                NamespacedId(Shining, "new_stone_age"),
                ElementDescription("&a新石器时代", "&6刀耕火种"),
                ItemStack(Material.STONE_BRICKS)
            )
            newStoneCategory.registerDependency(stickItem)
            stoneCategory.registerElement(newStoneCategory)

            stoneCategory.registerLock(lockExperience)
            val lockItem = LockItem(ItemStack(Material.DIAMOND, 3))
            stoneCategory.registerLock(lockItem)

            val pickaxeItem = GuideItem(
                NamespacedId(Shining, "pickaxe"),
                ElementDescription("&e生产力提高"),
                ItemStack(Material.STONE_PICKAXE),
                ConsumableItemGroup(false, VanillaUniversalItem(ItemStack(Material.STONE_PICKAXE)))
            )
            newStoneCategory.registerElement(pickaxeItem)

            stoneCategory.register()
            ShiningGuide.registerElement(stoneCategory)
            
            val bronzeAge = GuideCategory(
                NamespacedId(Shining, "bronze_age"),
                ElementDescription("&6青铜时代"),
                ItemStack(Material.BRICKS)
            )
            bronzeAge.register()
            ShiningGuide.registerElement(bronzeAge)
            
            
            val electricityAge = GuideMap(
                NamespacedId(Shining, "electricity_age"),
                ElementDescription("&b电力时代"),
                ItemStack(Material.REDSTONE_BLOCK)
            )
            val steelItem = GuideItem(
                NamespacedId(Shining, "steel_ingot"),
                ElementDescription("&f工业基础", "", "&7&l钢"),
                ItemStack(Material.IRON_INGOT),
                ConsumableItemGroup(true, VanillaUniversalItem(
                    SItem(Material.IRON_INGOT, "&7钢锭")
                ))
            )
            electricityAge.registerElement(steelItem, Coordinate2D.ORIGIN)
            val steelBlockItem = GuideItem(
                NamespacedId(Shining, "steel_block"),
                ElementDescription("&b大炼钢铁", "&a多快好省"),
                ItemStack(Material.IRON_BLOCK),
                ConsumableItemGroup(false, VanillaUniversalItem(
                    SItem(Material.IRON_BLOCK, "&7钢块")
                ))
            )
            electricityAge.registerElement(steelBlockItem, Coordinate2D(1, 0))
            
            electricityAge.register()
            ShiningGuide.registerElement(electricityAge)
        }

        val mapper = jsonMapper {
            addModule(SerializationModules.bukkit)
        }

        registerBukkitListener<PlayerInteractEvent>(ignoreCancelled = false) listener@{ event ->
            if (event.hand != EquipmentSlot.HAND) return@listener

            if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
                val item = event.player.inventory.itemInMainHand
                when (item.type) {
                    Material.DIAMOND -> {
                        event.player.giveItem(ShiningGuide.getItem())
                    }

                    Material.EMERALD -> {
                        ShiningGuide.fireworkCongratulate(event.player)
                    }

//                    Material.STICK -> {
//                        event.clickedBlock?.let { block ->
//                            event.player.sendMessage(
//                                """
//                                > Block
//                                ${block.type}
//                                ${block.data}
//                                ${block.blockData.asString}
//                            """.trimIndent()
//                            )
//                        }
//                    }
//
//                    Material.AIR -> {}

                    else -> {
//                        val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(item)
//                        event.player.sendMessage(json)
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