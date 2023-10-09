package io.github.sunshinewzy.shining.core.guide

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.dictionary.behavior.ItemBehavior
import io.github.sunshinewzy.shining.api.event.guide.ShiningGuideOpenEvent
import io.github.sunshinewzy.shining.api.guide.ElementDescription
import io.github.sunshinewzy.shining.api.guide.IShiningGuide
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.team.CompletedGuideTeam
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.dictionary.DictionaryRegistry
import io.github.sunshinewzy.shining.core.guide.context.GuideEditModeContext
import io.github.sunshinewzy.shining.core.guide.element.GuideCategory
import io.github.sunshinewzy.shining.core.guide.element.GuideElementRegistry
import io.github.sunshinewzy.shining.core.guide.settings.SoundSettings
import io.github.sunshinewzy.shining.core.guide.team.GuideTeam.Companion.getGuideTeam
import io.github.sunshinewzy.shining.core.guide.team.GuideTeam.Companion.setupGuideTeam
import io.github.sunshinewzy.shining.core.lang.getDefaultLangText
import io.github.sunshinewzy.shining.core.lang.item.LocalizedItem
import io.github.sunshinewzy.shining.objects.SCollection
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.orderWith
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
import taboolib.platform.util.isAir
import java.util.*

object ShiningGuide : GuideCategory(
    NamespacedId(Shining, "shining_guide"),
    ElementDescription(getDefaultLangText("item-shining-shining_guide")),
    ItemStack(Material.ENCHANTED_BOOK)
), IShiningGuide {
    private val guideItemId = NamespacedId(Shining, "shining_guide")
    private val guideItem = DictionaryRegistry.registerItem(
        guideItemId, LocalizedItem(Material.ENCHANTED_BOOK, guideItemId),
        object : ItemBehavior() {
            override fun onInteract(event: PlayerInteractEvent, player: Player, item: ItemStack, action: Action) {
                if (event.hand != EquipmentSlot.HAND) return

                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    event.isCancelled = true
                    openLastElement(player)
                } else if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                    event.isCancelled = true
                    openMainMenu(player)
                }
            }
        }
    )
    private val playerLastOpenElementMap: MutableMap<UUID, IGuideElement> = HashMap()
    private val playerElementAdditionalContextMap: MutableMap<UUID, MutableMap<NamespacedId, GuideContext>> = HashMap()
    

    val soundOpen: SoundSettings = SoundSettings(Sound.ENTITY_HORSE_ARMOR, 1.2f)

    val onBuildEdge: (Player, Inventory) -> Unit = { player, inv ->
        edgeOrders.forEach { index ->
            inv.getItem(index)?.let {
                if (!it.isAir()) return@forEach
            }

            inv.setItem(index, ShiningIcon.EDGE.item)
        }
    }
    val onClickBack: (ClickEvent) -> Unit = {
        if (it.clickEvent().isShiftClick) {
            openMainMenu(it.clicker)
        } else {
            openLastElement(it.clicker)
        }
    }

    const val TITLE = "menu-shining_guide-title"

    val edgeOrders: List<Int> = (((1 orderWith 1)..(9 orderWith 1)) + ((1 orderWith 6)..(9 orderWith 6)))
    val slotOrders: List<Int> = ((1 orderWith 2)..(8 orderWith 5)).toList()

    
    override fun reload() {
        GuideElementRegistry.getState(getId())?.let { 
            update(it, true)
        } ?: kotlin.run { 
            ShiningDispatchers.launchIO {
                GuideElementRegistry.saveElement(this@ShiningGuide)
            }
        }
    }
    

    override fun openMainMenu(player: Player, context: GuideContext) {
        if (!ShiningGuideOpenEvent(player, context, false).call()) return
        
        ShiningDispatchers.launchDB {
            val team = player.getGuideTeam() ?: kotlin.run {
                submit {
                    player.setupGuideTeam()
                }
                return@launchDB
            }

            submit {
                openMainMenu(player, team, context)
            }
        }
    }

    override fun openMainMenu(player: Player, team: IGuideTeam, context: GuideContext) {
        playerLastOpenElementMap -= player.uniqueId
        soundOpen.playSound(player)

        var ctxt = context
        if (ctxt[GuideEditModeContext] == null && ShiningGuideEditor.isEditModeEnabled(player)) {
            ctxt += GuideEditModeContext()
        }
        openMenu(player, team, ctxt)
    }

    override fun openLastElement(player: Player, context: GuideContext) {
        if (!ShiningGuideOpenEvent(player, context, true).call()) return
        
        ShiningDispatchers.launchDB {
            val team = player.getGuideTeam() ?: kotlin.run {
                submit {
                    player.setupGuideTeam()
                }
                return@launchDB
            }

            submit {
                openLastElement(player, team, context)
            }
        }
    }

    override fun openLastElement(player: Player, team: IGuideTeam, context: GuideContext) {
        playerLastOpenElementMap[player.uniqueId]?.let {
            var ctxt = context
            if (ctxt[GuideEditModeContext] == null && ShiningGuideEditor.isEditModeEnabled(player)) {
                ctxt += GuideEditModeContext()
            }
            it.open(player, team, null, ctxt)
            return
        }

        openMainMenu(player, team, context)
    }

    override fun openCompletedMainMenu(player: Player, context: GuideContext) {
        playerLastOpenElementMap -= player.uniqueId
        soundOpen.playSound(player)

        var ctxt = context
        if (ctxt[GuideEditModeContext] == null) {
            ctxt += GuideEditModeContext(false)
        }
        openMainMenu(player, CompletedGuideTeam.getInstance(), ctxt)
    }

    override fun openCompletedLastElement(player: Player, context: GuideContext) {
        playerLastOpenElementMap[player.uniqueId]?.let {
            var ctxt = context
            if (ctxt[GuideEditModeContext] == null) {
                ctxt += GuideEditModeContext(false)
            }
            it.open(player, CompletedGuideTeam.getInstance(), null, ctxt)
            return
        }

        openCompletedMainMenu(player, context)
    }

    override fun recordLastOpenElement(uuid: UUID, element: IGuideElement) {
        playerLastOpenElementMap[uuid] = element
    }
    
    override fun recordElementAdditionalContext(uuid: UUID, element: IGuideElement, context: GuideContext) {
        val map = playerElementAdditionalContextMap.getOrPut(uuid) { HashMap() }
        map[element.getId()] = context
    }
    
    override fun getElementAdditionalContext(uuid: UUID, element: IGuideElement): GuideContext? {
        val map = playerElementAdditionalContextMap[uuid] ?: return null
        return map[element.getId()]
    }

    override fun fireworkCongratulate(player: Player) {
        val firework = player.world.spawnEntity(player.location, EntityType.FIREWORK) as Firework
        val meta = firework.fireworkMeta
        meta.addEffect(
            FireworkEffect.builder().with(SCollection.fireworkEffectTypes.random())
                .withColor(SCollection.colors.random()).build()
        )
        meta.power = 1
        firework.fireworkMeta = meta
    }

    override fun getItemStack(): ItemStack = guideItem.getItemStack()
    
    fun isClickEmptySlot(event: ClickEvent): Boolean =
        event.rawSlot in slotOrders && event.currentItem.isAir()

}