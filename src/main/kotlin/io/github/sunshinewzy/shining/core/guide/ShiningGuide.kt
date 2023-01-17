package io.github.sunshinewzy.shining.core.guide

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.ElementCondition
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.dictionary.DictionaryItem
import io.github.sunshinewzy.shining.core.dictionary.DictionaryRegistry
import io.github.sunshinewzy.shining.core.dictionary.item.behavior.ItemBehavior
import io.github.sunshinewzy.shining.core.guide.GuideTeam.Companion.getGuideTeam
import io.github.sunshinewzy.shining.core.guide.GuideTeam.Companion.setupGuideTeam
import io.github.sunshinewzy.shining.core.guide.ShiningGuideEditor.setEditor
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.LocalizedItem
import io.github.sunshinewzy.shining.objects.SCollection
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.orderWith
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit
import taboolib.module.ui.ClickEvent
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.isAir
import java.util.*

object ShiningGuide {
    private val elementMap = TreeMap<Int, MutableList<IGuideElement>>()
    private val guideItem: DictionaryItem = NamespacedId(Shining, "shining_guide").let { id ->
        DictionaryRegistry.registerItem(
            id, LocalizedItem(Material.ENCHANTED_BOOK, id),
            object : ItemBehavior() {
                override fun onInteract(event: PlayerInteractEvent, player: Player, item: ItemStack, action: Action) {
                    if(event.hand != EquipmentSlot.HAND) return

                    if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                        event.isCancelled = true
                        openLastElement(player)
                    } else if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                        event.isCancelled = true
                        openMainMenu(player)
                    }
                }
            }
        )
    }
    
    
    val soundOpen: SoundSettings = SoundSettings(Sound.ENTITY_HORSE_ARMOR, 1.2f)
    
    
    val onBuildEdge: (Player, Inventory) -> Unit = { player, inv ->
        edgeOrders.forEach { index ->
            inv.getItem(index)?.let { 
                if(!it.isAir()) return@forEach
            }
            
            inv.setItem(index, ShiningIcon.EDGE.item)
        }
    }
    val onClickBack: (ClickEvent) -> Unit = {
        if(it.clickEvent().isShiftClick) {
            openMainMenu(it.clicker)
        } else {
            openLastElement(it.clicker)
        }
    }
    
    
    const val TITLE = "menu-shining_guide-title"
    
    
    val edgeOrders = (((1 orderWith 1)..(9 orderWith 1)) + ((1 orderWith 6)..(9 orderWith 6)))
    val slotOrders = ((1 orderWith 2)..(9 orderWith 5)).toList()
    val playerLastOpenElementMap = HashMap<UUID, IGuideElement>()
    
    
    fun openMainMenu(player: Player) {
        Shining.scope.launch(Dispatchers.IO) {
            val team = player.getGuideTeam() ?: kotlin.run {
                submit {
                    player.setupGuideTeam()
                }
                return@launch
            }

            submit {
                openMainMenu(player, team)
            }
        }
    }
    
    fun openMainMenu(player: Player, team: GuideTeam) {
        playerLastOpenElementMap -= player.uniqueId
        soundOpen.playSound(player)
        
        player.openMenu<Linked<IGuideElement>>(player.getLangText(TITLE)) {
            rows(6)
            slots(slotOrders)

            elements { getElements() }

            val lockedElements = LinkedList<IGuideElement>()
            onGenerate(true) { player, element, index, slot ->
                if(ShiningGuideEditor.isEditModeEnabled(player)) {
                    return@onGenerate element.getSymbolByCondition(player, team, ElementCondition.UNLOCKED)
                }

                val condition = element.getCondition(team)
                if(condition == ElementCondition.LOCKED_DEPENDENCY || condition == ElementCondition.LOCKED_LOCK)
                    lockedElements += element
                element.getSymbolByCondition(player, team, condition)
            }

            onBuild(true, onBuildEdge)

            setPreviousPage(2 orderWith 6) { page, hasPreviousPage ->
                if(hasPreviousPage) {
                    ShiningIcon.PAGE_PREVIOUS_GLASS_PANE.item
                } else ShiningIcon.EDGE.item
            }

            setNextPage(8 orderWith 6) { page, hasNextPage ->
                if(hasNextPage) {
                    ShiningIcon.PAGE_NEXT_GLASS_PANE.item
                } else ShiningIcon.EDGE.item
            }

            onClick { event, element ->
                if(ShiningGuideEditor.isEditorEnabled(player)) {
                    ShiningGuideEditor.openEditMenu(player, team, element)
                    return@onClick
                }

                if(element in lockedElements) return@onClick

                element.open(event.clicker, team, null)
            }

            if(ShiningGuideEditor.isEditorEnabled(player)) {
                onClick(lock = true) {
                    if(it.rawSlot in slotOrders && it.currentItem.isAir()) {
                        ShiningGuideEditor.openEditMenu(player, team, null)
                    }
                }
            }

            setEditor(player) {
                openMainMenu(player, team)
            }

            set(5 orderWith 1, ShiningIcon.SETTINGS.item) {
                ShiningGuideSettings.openSettingsMenu(player, team)
            }

        }
    }
    
    fun openLastElement(player: Player) {
        Shining.scope.launch(Dispatchers.IO) {
            val team = player.getGuideTeam() ?: kotlin.run {
                submit {
                    player.setupGuideTeam()
                }
                return@launch
            }
            
            submit {
                openLastElement(player, team)
            }
        }
    }
    
    fun openLastElement(player: Player, team: GuideTeam) {
        playerLastOpenElementMap[player.uniqueId]?.let {
            it.open(player, team)
            return
        }

        openMainMenu(player, team)
    }
    
    
    fun registerElement(element: IGuideElement, priority: Int = 10) {
        elementMap.getOrPut(priority) { ArrayList() }.add(element)
    }
    
    fun fireworkCongratulate(player: Player) {
        val firework = player.world.spawnEntity(player.location, EntityType.FIREWORK) as Firework
        val meta = firework.fireworkMeta
        meta.addEffect(FireworkEffect.builder().with(SCollection.fireworkEffectTypes.random()).withColor(SCollection.colors.random()).build())
        meta.power = 1
        firework.fireworkMeta = meta
    }
    
    fun getItem(): ItemStack {
        return guideItem.item.clone()
    }
    
    
    private fun getElements(): List<IGuideElement> =
        elementMap.flatMap { it.value }
    
}