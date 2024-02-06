package io.github.sunshinewzy.shining.core.machine.creator

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.dictionary.behavior.ItemBehavior
import io.github.sunshinewzy.shining.api.machine.IMachine
import io.github.sunshinewzy.shining.api.machine.IMachineWrench
import io.github.sunshinewzy.shining.api.machine.MachineProperty
import io.github.sunshinewzy.shining.api.machine.structure.IMachineStructure
import io.github.sunshinewzy.shining.api.machine.structure.MachineStructureType
import io.github.sunshinewzy.shining.api.machine.structure.MachineStructureType.*
import io.github.sunshinewzy.shining.api.namespace.Namespace
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.blueprint.BlueprintNodeTree
import io.github.sunshinewzy.shining.core.blueprint.node.BranchNode
import io.github.sunshinewzy.shining.core.dictionary.DictionaryRegistry
import io.github.sunshinewzy.shining.core.editor.chat.openChatEditor
import io.github.sunshinewzy.shining.core.editor.chat.type.Text
import io.github.sunshinewzy.shining.core.editor.chat.type.TextMap
import io.github.sunshinewzy.shining.core.guide.state.GuideElementState
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.LocalizedItem
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.lang.sendPrefixedLangText
import io.github.sunshinewzy.shining.core.machine.Machine
import io.github.sunshinewzy.shining.core.machine.MachineRegistry
import io.github.sunshinewzy.shining.core.machine.MachineWrenchRegistry
import io.github.sunshinewzy.shining.core.machine.ShiningMachineWrench
import io.github.sunshinewzy.shining.core.machine.node.MachineInteractEventNode
import io.github.sunshinewzy.shining.core.machine.node.MachineRunEventNode
import io.github.sunshinewzy.shining.core.machine.structure.MachineStructureRegistry
import io.github.sunshinewzy.shining.core.machine.structure.MultipleMachineStructure
import io.github.sunshinewzy.shining.core.machine.structure.SingleMachineStructure
import io.github.sunshinewzy.shining.core.menu.openMultiPageMenu
import io.github.sunshinewzy.shining.core.universal.block.VanillaUniversalBlock
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.getDisplayName
import io.github.sunshinewzy.shining.utils.isClickBlock
import io.github.sunshinewzy.shining.utils.position3D
import io.github.sunshinewzy.shining.utils.toCurrentLocalizedItem
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
import taboolib.platform.util.buildItem
import java.util.*

object MachineCreator {
    val creatorId = NamespacedId(Shining, "machine_creator")
    val creatorItem = DictionaryRegistry.registerItem(
        creatorId, LocalizedItem(Material.IRON_AXE, creatorId),
        object : ItemBehavior() {
            override fun onInteract(event: PlayerInteractEvent, player: Player, item: ItemStack, action: Action) {
                event.isCancelled = true
                creatorInteract(event)
            }
        }
    )
    
    private const val PERIOD = 20L   // tick
    private val contextMap: MutableMap<UUID, MachineCreatorContext> = HashMap()
    private val lastStructureMap: MutableMap<UUID, IMachineStructure> = HashMap()
    private val lastMachineMap: MutableMap<UUID, IMachine> = HashMap()
    
    private val itemMachineType = NamespacedIdItem(Material.GLASS, NamespacedId(Shining, "machine-creator-type"))
    private val itemMachineAutoType = NamespacedIdItem(Material.REDSTONE_BLOCK, NamespacedId(Shining, "machine-creator-auto"))
    private val itemBlueprint = NamespacedIdItem(Material.CHEST, NamespacedId(Shining, "machine-creator-blueprint"))
    

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
                            openCreateMachineMenu(player, MachineProperty(NamespacedId.NULL, ""), structure, ShiningMachineWrench)
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
            
            set('a', SingleMachineStructure.itemIcon.toLocalizedItem(player)) {
                
            }
            
            set('b', MultipleMachineStructure.itemIcon.toLocalizedItem(player)) {
                
            }
            
            onClick(lock = true)
        }
    }
    
    fun openCreateMachineMenu(player: Player, property: MachineProperty, structure: IMachineStructure, wrench: IMachineWrench) {
        player.openMenu<Basic>(player.getLangText("menu-machine-creator-create-title")) {
            rows(4)
            
            map(
                "---------",
                "- ab  c -",
                "- sw  d -",
                "---------",
            )
            
            set('-', ShiningIcon.EDGE.item)
            
            set('a', GuideElementState.itemEditId.toCurrentLocalizedItem(player, property.id.toString())) {
                player.openChatEditor<TextMap>(GuideElementState.itemEditId.toLocalizedItem(player).getDisplayName()) {
                    map(mapOf("namespace" to property.id.namespace.toString(), "id" to property.id.id))

                    predicate {
                        when (index) {
                            "" -> {
                                val namespacedId = NamespacedId.fromString(it) ?: return@predicate false
                                content["namespace"] = namespacedId.namespace.toString()
                                content["id"] = namespacedId.id
                                checkCorrect()
                                true
                            }
                            "namespace" -> Namespace.VALID_NAMESPACE.matcher(it).matches()
                            "id" -> NamespacedId.VALID_ID.matcher(it).matches()
                            else -> false
                        }
                    }

                    onSubmit { content ->
                        val theNamespace = content["namespace"] ?: return@onSubmit
                        val theId = content["id"] ?: return@onSubmit

                        val namespacedId = NamespacedId.fromString("$theNamespace:$theId") ?: return@onSubmit
                        if (namespacedId == NamespacedId.NULL || MachineRegistry.hasMachine(namespacedId)) {
                            player.sendPrefixedLangText("text-shining_guide-editor-state-element-basic-id-duplication")
                            return@onSubmit
                        }

                        property.id = namespacedId
                    }

                    onFinal {
                        openCreateMachineMenu(player, property, structure, wrench)
                    }
                }
            }
            
            set('b', GuideElementState.itemEditDescriptionName.toCurrentLocalizedItem(player, property.name)) {
                player.openChatEditor<Text>(GuideElementState.itemEditDescriptionName.toLocalizedItem(player).getDisplayName()) {
                    text(property.name)

                    onSubmit {
                        property.name = content
                    }

                    onFinal {
                        openCreateMachineMenu(player, property, structure, wrench)
                    }
                }
            }
            
            val structureIcon = MachineStructureRegistry.getIcon(structure::class.java) ?: return
            set('s', structureIcon.toLocalizedItemStack(player))
            
            set('w', ShiningMachineWrench.itemMachineWrench.toLocalizedItem(player)) {
                openSelectWrenchMenu(player, property, structure, wrench)
            }
            
            set('c', ShiningIcon.CONFIRM.toLocalizedItem(player)) {
                if (property.id == NamespacedId.NULL || MachineRegistry.hasMachine(property.id)) {
                    player.sendPrefixedLangText("text-shining_guide-editor-state-element-basic-id-duplication")
                    return@set
                }
                
                val machine = Machine(property, structure)
                machine.register(wrench)
                lastMachineMap[player.uniqueId] = machine

                val nodeTrees = machine.blueprint.nodeTrees
                val tree = BlueprintNodeTree()
                nodeTrees += tree
                for (i in 1..6) {
                    val theTree = BlueprintNodeTree()
                    theTree.root = MachineInteractEventNode()
                    nodeTrees += theTree
                }
                tree.root = MachineRunEventNode()
                val branchNode = BranchNode()
                tree.root.successors[0] = branchNode
                val b1 = BranchNode()
                val b2 = BranchNode()
                val b11 = BranchNode()
                val b111 = BranchNode()
                val b112 = BranchNode()
                val b12 = BranchNode()
                val b121 = BranchNode()
                val b21 = BranchNode()
                val b22 = BranchNode()
                branchNode.successors[0] = b1
                branchNode.successors[1] = b2
                b1.successors[0] = b11
                b1.successors[1] = b12
                b2.successors[0] = b21
                b2.successors[1] = b22
                b11.successors[0] = b111
                b11.successors[1] = b112
                b12.successors[0] = b121

                machine.blueprint.edit(player)
            }
            
            set('d', ShiningIcon.CANCEL.toLocalizedItem(player)) {
                player.closeInventory()
            }
            
            onClick(lock = true)
        }
    }
    
    fun openSelectWrenchMenu(player: Player, property: MachineProperty, structure: IMachineStructure, wrench: IMachineWrench) {
        player.openMultiPageMenu<IMachineWrench>(player.getLangText("menu-machine-creator-create-wrench-title")) { 
            elements { MachineWrenchRegistry.getAllWrenches() }
            
            onGenerate { player, element, index, slot -> 
                buildItem(element.getItemStack()) {
                    lore.addAll(0, listOf("&f${element.getId()}", ""))
                    colored()
                }
            }
            
            onClick { event, element -> 
                openCreateMachineMenu(player, property, structure, element)
            }
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