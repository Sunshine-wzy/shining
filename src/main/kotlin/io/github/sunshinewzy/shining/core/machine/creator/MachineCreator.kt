package io.github.sunshinewzy.shining.core.machine.creator

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.dictionary.behavior.ItemBehavior
import io.github.sunshinewzy.shining.api.machine.structure.IMachineStructure
import io.github.sunshinewzy.shining.api.machine.structure.MachineStructureType
import io.github.sunshinewzy.shining.api.machine.structure.MachineStructureType.*
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.dictionary.DictionaryItem
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.lang.sendPrefixedLangText
import io.github.sunshinewzy.shining.core.machine.structure.MultipleMachineStructure
import io.github.sunshinewzy.shining.core.machine.structure.SingleMachineStructure
import io.github.sunshinewzy.shining.core.universal.block.VanillaUniversalBlock
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.isClickBlock
import io.github.sunshinewzy.shining.utils.position3D
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.submit
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import java.util.*

object MachineCreator {
    val creatorId = NamespacedId(Shining, "machine_creator")
    val creatorItem = DictionaryItem(
        creatorId, ItemStack(Material.IRON_AXE),
        object : ItemBehavior() {
            override fun onInteract(event: PlayerInteractEvent, player: Player, item: ItemStack, action: Action) {
                creatorInteract(event)
            }
        }
    )
    
    private const val PERIOD = 20L   // tick
    private val contextMap: MutableMap<UUID, MachineCreatorContext> = HashMap()
    private val lastStructureMap: MutableMap<UUID, IMachineStructure> = HashMap()
    
    private val itemMachineType = NamespacedIdItem(Material.GLASS, NamespacedId(Shining, "machine-creator-type"))
    private val itemMachineAutoType = NamespacedIdItem(Material.REDSTONE_BLOCK, NamespacedId(Shining, "machine-creator-auto"))
    private val itemMachineTypeSingle = NamespacedIdItem(Material.IRON_BLOCK, NamespacedId(Shining, "machine-creator-type-single"))
    private val itemMachineTypeMultiple = NamespacedIdItem(Material.IRON_BLOCK, NamespacedId(Shining, "machine-creator-type-multiple"))


    fun open(player: Player) {
        player.openMenu<Basic>(player.getLangText("menu-machine-creator-create-title")) {
            rows(3)

            map(
                "---------",
                "-  a b  -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)
            
            set('a', itemMachineType.toLocalizedItem(player)) {
                openCreateByTypeMenu(player)
            }
            
            set('b', itemMachineAutoType.toLocalizedItem(player)) {
                contextMap[player.uniqueId]?.let { context ->
                    autoType(context)?.let { type ->
                        scan(context, type)?.let { structure ->
                            lastStructureMap[player.uniqueId] = structure
                        }
                    }
                }
            }

            onClick(lock = true)
        }
    }
    
    fun openCreateByTypeMenu(player: Player) {
        player.openMenu<Basic>(player.getLangText("menu-machine-creator-create-type-title")) {
            rows(3)
            
            map(
                "---------",
                "-  a b  -",
                "---------"
            )
            
            set('a', itemMachineTypeSingle.toLocalizedItem(player)) {
                
            }
            
            set('b', itemMachineTypeMultiple.toLocalizedItem(player)) {
                
            }
            
            onClick(lock = true)
        }
    }

    fun creatorInteract(event: PlayerInteractEvent) {
        if (event.hand != EquipmentSlot.HAND) return
        if (!event.action.isClickBlock()) return

        event.clickedBlock?.also { block ->
            val context = contextMap.getOrPut(event.player.uniqueId) { MachineCreatorContext() }

            if (event.player.isSneaking) {
                if (event.action == Action.LEFT_CLICK_BLOCK) {
                    // Select center
                    val position = block.location.position3D
                    context.center = position
                    context.direction = event.blockFace
                    event.player.sendPrefixedLangText("text-machine-creator-select-center", Shining.prefix, "($position)")
                } else if (event.action == Action.RIGHT_CLICK_BLOCK) {
                    // Create machine
                    open(event.player)
                }
            } else if (event.action == Action.LEFT_CLICK_BLOCK) {
                // Select first position
                val position = block.location.position3D
                context.first = position
                event.player.sendPrefixedLangText("text-machine-creator-select-position", Shining.prefix, "1", "($position)")
            } else if (event.action == Action.RIGHT_CLICK_BLOCK) {
                // Select second position
                val position = block.location.position3D
                context.second = position
                event.player.sendPrefixedLangText("text-machine-creator-select-position", Shining.prefix, "2", "($position)")
            }
        }
    }
    
    fun autoType(context: MachineCreatorContext): MachineStructureType? {
        if ((context.center != null && (context.first == null || context.second == null)) || (context.first != null && context.first == context.second)) {
            return SINGLE
        } else if (context.center != null) {
            return if (context.direction == null) MULTIPLE_WITHOUT_DIRECTION else MULTIPLE_WITH_DIRECTION
        }
        return null
    }
    
    fun scan(context: MachineCreatorContext, type: MachineStructureType): IMachineStructure? {
        val (center, direction, first, second) = context
        when (type) {
            SINGLE -> {
                val position = center ?: first ?: second ?: return null
                return SingleMachineStructure(VanillaUniversalBlock(position.getBlock().blockData))
            }
            
            MULTIPLE, MULTIPLE_WITH_DIRECTION, MULTIPLE_WITHOUT_DIRECTION -> {
                if (center == null || first == null || second == null) return null
                val structure = MultipleMachineStructure()
                if (!structure.scan(center.toLocation(), first.toLocation(), second.toLocation(), direction ?: BlockFace.SELF))
                    return null
                return structure
            }
        }
    }

    @Awake(LifeCycle.ENABLE)
    fun particleDispatcher() {
        submit(period = PERIOD, delay = PERIOD) {
            contextMap.forEach { (uuid, context) ->
                Bukkit.getPlayer(uuid)?.let { context.playParticle(it) }
            }
        }
    }

    fun getLastStructure(uuid: UUID): IMachineStructure? = lastStructureMap[uuid]

}