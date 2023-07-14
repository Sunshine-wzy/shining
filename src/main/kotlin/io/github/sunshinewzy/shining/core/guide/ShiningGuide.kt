package io.github.sunshinewzy.shining.core.guide

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.ElementDescription
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.dictionary.DictionaryItem
import io.github.sunshinewzy.shining.core.dictionary.DictionaryRegistry
import io.github.sunshinewzy.shining.core.dictionary.item.behavior.ItemBehavior
import io.github.sunshinewzy.shining.core.guide.GuideTeam.Companion.getGuideTeam
import io.github.sunshinewzy.shining.core.guide.GuideTeam.Companion.setupGuideTeam
import io.github.sunshinewzy.shining.core.guide.context.EmptyGuideContext
import io.github.sunshinewzy.shining.core.guide.context.GuideEditorContext
import io.github.sunshinewzy.shining.core.guide.element.GuideCategory
import io.github.sunshinewzy.shining.core.guide.settings.SoundSettings
import io.github.sunshinewzy.shining.core.lang.getDefaultLangText
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
import taboolib.platform.util.isAir
import java.util.*

object ShiningGuide : GuideCategory(
    NamespacedId(Shining, "shining_guide"),
    ElementDescription(getDefaultLangText("item-shining-shining_guide")),
    ItemStack(Material.ENCHANTED_BOOK)
) {
    private val guideItem: DictionaryItem = NamespacedId(Shining, "shining_guide").let { id ->
        DictionaryRegistry.registerItem(
            id, LocalizedItem(Material.ENCHANTED_BOOK, id),
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
    }


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


    val edgeOrders = (((1 orderWith 1)..(9 orderWith 1)) + ((1 orderWith 6)..(9 orderWith 6)))
    val slotOrders = ((1 orderWith 2)..(9 orderWith 5)).toList()
    val playerLastOpenElementMap = HashMap<UUID, IGuideElement>()


    @JvmOverloads
    fun openMainMenu(player: Player, context: GuideContext = EmptyGuideContext) {
        Shining.scope.launch(Dispatchers.IO) {
            val team = player.getGuideTeam() ?: kotlin.run {
                submit {
                    player.setupGuideTeam()
                }
                return@launch
            }

            submit {
                openMainMenu(player, team, context)
            }
        }
    }

    @JvmOverloads
    fun openMainMenu(player: Player, team: GuideTeam, context: GuideContext = EmptyGuideContext) {
        playerLastOpenElementMap -= player.uniqueId
        soundOpen.playSound(player)

        var ctxt = context
        if (ctxt[GuideEditorContext] == null && ShiningGuideEditor.isEditModeEnabled(player)) {
            ctxt += GuideEditorContext()
        }
        openMenu(player, team, ctxt)
    }

    @JvmOverloads
    fun openLastElement(player: Player, context: GuideContext = EmptyGuideContext) {
        Shining.scope.launch(Dispatchers.IO) {
            val team = player.getGuideTeam() ?: kotlin.run {
                submit {
                    player.setupGuideTeam()
                }
                return@launch
            }

            submit {
                openLastElement(player, team, context)
            }
        }
    }

    @JvmOverloads
    fun openLastElement(player: Player, team: GuideTeam, context: GuideContext = EmptyGuideContext) {
        playerLastOpenElementMap[player.uniqueId]?.let {
            var ctxt = context
            if (ctxt[GuideEditorContext] == null && ShiningGuideEditor.isEditModeEnabled(player)) {
                ctxt += GuideEditorContext()
            }
            it.open(player, team, null, ctxt)
            return
        }

        openMainMenu(player, team, context)
    }

    @JvmOverloads
    fun openCompletedMainMenu(player: Player, context: GuideContext = EmptyGuideContext) {
        playerLastOpenElementMap -= player.uniqueId
        soundOpen.playSound(player)

        var ctxt = context
        if (ctxt[GuideEditorContext] == null) {
            ctxt += GuideEditorContext(false)
        }
        openMainMenu(player, GuideTeam.CompletedTeam, ctxt)
    }

    @JvmOverloads
    fun openCompletedLastElement(player: Player, context: GuideContext = EmptyGuideContext) {
        playerLastOpenElementMap[player.uniqueId]?.let {
            var ctxt = context
            if (ctxt[GuideEditorContext] == null) {
                ctxt += GuideEditorContext(false)
            }
            it.open(player, GuideTeam.CompletedTeam, null, ctxt)
            return
        }

        openCompletedMainMenu(player, context)
    }

//    /**
//     * @param tier Higher tier will make this [element] appear further down in the guide
//     */
//    fun registerElement(element: IGuideElement, tier: Int = 10) {
//        elementMap.getOrPut(tier) { ArrayList() }.add(element)
//    }

    fun fireworkCongratulate(player: Player) {
        val firework = player.world.spawnEntity(player.location, EntityType.FIREWORK) as Firework
        val meta = firework.fireworkMeta
        meta.addEffect(
            FireworkEffect.builder().with(SCollection.fireworkEffectTypes.random())
                .withColor(SCollection.colors.random()).build()
        )
        meta.power = 1
        firework.fireworkMeta = meta
    }

    fun getItem(): ItemStack {
        return guideItem.item.clone()
    }

}