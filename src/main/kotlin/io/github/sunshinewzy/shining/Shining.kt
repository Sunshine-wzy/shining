package io.github.sunshinewzy.shining

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import io.github.sunshinewzy.shining.api.IShiningAPI
import io.github.sunshinewzy.shining.api.ShiningAPIProvider
import io.github.sunshinewzy.shining.api.ShiningPlugin
import io.github.sunshinewzy.shining.api.event.ShiningDataLoadingCompleteEvent
import io.github.sunshinewzy.shining.api.guide.ElementDescription
import io.github.sunshinewzy.shining.api.machine.MachineProperty
import io.github.sunshinewzy.shining.api.namespace.Namespace
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.api.objects.coordinate.Coordinate2D
import io.github.sunshinewzy.shining.commands.CommandGuide
import io.github.sunshinewzy.shining.core.ShiningAPI
import io.github.sunshinewzy.shining.core.addon.ShiningAddonRegistry
import io.github.sunshinewzy.shining.core.data.DataManager
import io.github.sunshinewzy.shining.core.data.SerializationModules
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.element.GuideCategory
import io.github.sunshinewzy.shining.core.guide.element.GuideElementRegistry
import io.github.sunshinewzy.shining.core.guide.element.GuideItem
import io.github.sunshinewzy.shining.core.guide.element.GuideMap
import io.github.sunshinewzy.shining.core.guide.lock.LockExperience
import io.github.sunshinewzy.shining.core.guide.lock.LockItem
import io.github.sunshinewzy.shining.core.guide.reward.GuideRewardCommand
import io.github.sunshinewzy.shining.core.guide.reward.GuideRewardItem
import io.github.sunshinewzy.shining.core.guide.reward.GuideRewardRegistry
import io.github.sunshinewzy.shining.core.guide.state.*
import io.github.sunshinewzy.shining.core.item.ConsumableItemGroup
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.machine.Machine
import io.github.sunshinewzy.shining.core.machine.ShiningMachineWrench
import io.github.sunshinewzy.shining.core.machine.legacy.SFlatMachineInformation
import io.github.sunshinewzy.shining.core.machine.legacy.SMachineInformation
import io.github.sunshinewzy.shining.core.machine.legacy.SSingleMachineInformation
import io.github.sunshinewzy.shining.core.machine.legacy.custom.SMachineRecipe
import io.github.sunshinewzy.shining.core.machine.legacy.custom.SMachineRecipes
import io.github.sunshinewzy.shining.core.machine.structure.MachineStructureRegistry
import io.github.sunshinewzy.shining.core.machine.structure.MultipleMachineStructure
import io.github.sunshinewzy.shining.core.machine.structure.SingleMachineStructure
import io.github.sunshinewzy.shining.core.menu.MapChest
import io.github.sunshinewzy.shining.core.menu.PageableCategoryChest
import io.github.sunshinewzy.shining.core.menu.PageableGroupChest
import io.github.sunshinewzy.shining.core.menu.impl.MapChestImpl
import io.github.sunshinewzy.shining.core.menu.impl.PageableCategoryChestImpl
import io.github.sunshinewzy.shining.core.menu.impl.PageableGroupChestImpl
import io.github.sunshinewzy.shining.core.task.TaskProgress
import io.github.sunshinewzy.shining.core.universal.block.VanillaUniversalBlock
import io.github.sunshinewzy.shining.core.universal.item.DictionaryUniversalItem
import io.github.sunshinewzy.shining.core.universal.item.UniversalItemRegistry
import io.github.sunshinewzy.shining.core.universal.item.VanillaUniversalItem
import io.github.sunshinewzy.shining.listeners.SunSTSubscriber
import io.github.sunshinewzy.shining.objects.SItem
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import io.github.sunshinewzy.shining.objects.legacy.SBlock
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
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import taboolib.common.classloader.IsolatedClassLoader
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
import taboolib.module.ui.Menu
import taboolib.platform.BukkitPlugin

@RuntimeDependencies(
    RuntimeDependency(value = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.7.21", transitive = false),
    RuntimeDependency(value = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.21", transitive = false),
    RuntimeDependency(value = "org.jetbrains.kotlin:kotlin-reflect:1.7.21", transitive = false),
    RuntimeDependency(value = "org.jetbrains.exposed:exposed-core:0.41.1", transitive = false),
    RuntimeDependency(value = "org.jetbrains.exposed:exposed-dao:0.41.1", transitive = false),
    RuntimeDependency(value = "org.jetbrains.exposed:exposed-jdbc:0.41.1", transitive = false),
    RuntimeDependency(value = "com.fasterxml.jackson.core:jackson-core:2.15.0", transitive = false),
    RuntimeDependency(value = "com.fasterxml.jackson.core:jackson-databind:2.15.0", transitive = false),
    RuntimeDependency(value = "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.0", transitive = false),
    RuntimeDependency(value = "com.fasterxml.jackson.module:jackson-module-kotlin:2.15.0", transitive = false),
    RuntimeDependency(value = "!com.zaxxer:HikariCP:4.0.3")
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
    val objectMapper: ObjectMapper = jsonMapper {
        typeFactory(TypeFactory.defaultInstance().withClassLoader(IsolatedClassLoader.INSTANCE))
        
        addModule(kotlinModule())
        addModule(SerializationModules.shining)
        addModule(SerializationModules.bukkit)
    }
    val yamlObjectMapper: ObjectMapper = ObjectMapper(YAMLFactory()).apply { 
        setTypeFactory(TypeFactory.defaultInstance().withClassLoader(IsolatedClassLoader.INSTANCE))
        
        registerModule(kotlinModule())
        registerModule(SerializationModules.shining)
        registerModule(SerializationModules.bukkit)
    }
    val coroutineScope: CoroutineScope by lazy { CoroutineScope(SupervisorJob()) }

    private val namespace = Namespace.get(NAME.lowercase())


    override fun onEnable() {
        init()

        val metrics = Metrics(19323, pluginVersion, Platform.BUKKIT)

        info("Shining loaded successfully!")

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
        // Initialize API
        val api = ShiningAPI()
        ShiningAPIProvider.api = api
        Bukkit.getServicesManager().register(IShiningAPI::class.java, api, plugin, ServicePriority.Normal)
        
        registerSerialization()
        registerClasses()
        registerListeners()
        registerPermissions()
        registerMenus()
        
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
                ShiningGuide.reload()
                pluginManager.callEvent(ShiningDataLoadingCompleteEvent())
            }
        }
        
        SItem.initAction()
        
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
            VanillaUniversalItem::class.java, DictionaryUniversalItem::class.java,
            // UniversalBlock
            VanillaUniversalBlock::class.java,
            // IMachineStructure
            SingleMachineStructure::class.java, MultipleMachineStructure::class.java
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
            GuideMapState::class.java to NamespacedIdItem(Material.MAP, NamespacedId(Shining, "shining_guide-state-map")),
            GuideEmptyState::class.java to NamespacedIdItem(Material.PAPER, NamespacedId(Shining, "shining_guide-state-empty")),
            GuideCraftItemState::class.java to NamespacedIdItem(Material.CRAFTING_TABLE, NamespacedId(Shining, "shining_guide-state-craft_item"))
        ))
        
        GuideRewardRegistry.register(mapOf(
            GuideRewardItem::class.java to GuideRewardItem.itemIcon,
            GuideRewardCommand::class.java to GuideRewardCommand.itemIcon
        ))
        
        UniversalItemRegistry.register(mapOf(
            VanillaUniversalItem::class.java to NamespacedIdItem(Material.GRASS_BLOCK, NamespacedId(Shining, "item-universal-vanilla-icon")),
            DictionaryUniversalItem::class.java to NamespacedIdItem(Material.BOOKSHELF, NamespacedId(Shining, "item-universal-dictionary-icon"))
        ))
        
        MachineStructureRegistry.register(mapOf(
            SingleMachineStructure::class.java to SingleMachineStructure.itemIcon,
            MultipleMachineStructure::class.java to MultipleMachineStructure.itemIcon
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
        registerPermission(Permission(CommandGuide.PERMISSION_EDIT, PermissionDefault.OP))
        registerPermission(Permission(CommandGuide.PERMISSION_TEAM, PermissionDefault.OP))
        registerPermission(Permission(CommandGuide.PERMISSION_OPEN_TEAM, PermissionDefault.TRUE))
        registerPermission(Permission(CommandGuide.PERMISSION_RELOAD, PermissionDefault.OP))
        registerPermission(Permission(CommandGuide.PERMISSION_GIVE, PermissionDefault.OP))
    }
    
    private fun registerMenus() {
        Menu.registerImplementation(MapChest::class.java, MapChestImpl::class.java)
        Menu.registerImplementation(PageableGroupChest::class.java, PageableGroupChestImpl::class.java)
        Menu.registerImplementation(PageableCategoryChest::class.java, PageableCategoryChestImpl::class.java)
    }


    @ShiningTestApi
    private fun test() {
        val copperMachine = Machine(
            MachineProperty(NamespacedId(Shining, "copper_machine"), "Copper Machine"),
            SingleMachineStructure(VanillaUniversalBlock(Material.COPPER_BLOCK))
        ).register(ShiningMachineWrench)
        
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
                ConsumableItemGroup(true, VanillaUniversalItem(SItem(Material.IRON_INGOT, "&7钢锭")))
            )
            electricityAge.registerElement(steelItem, Coordinate2D.ORIGIN)
            val steelBlockItem = GuideItem(
                NamespacedId(Shining, "steel_block"),
                ElementDescription("&b大炼钢铁", "&a多快好省"),
                ItemStack(Material.IRON_BLOCK),
                ConsumableItemGroup(false, VanillaUniversalItem(SItem(Material.IRON_BLOCK, "&7钢块")))
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
                        event.player.giveItem(ShiningGuide.getItemStack())
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
                    }
                }

            }
        }
    }

}