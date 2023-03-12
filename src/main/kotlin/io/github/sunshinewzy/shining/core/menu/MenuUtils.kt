package io.github.sunshinewzy.shining.core.menu

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.core.guide.EmptyGuideContext
import io.github.sunshinewzy.shining.core.guide.GuideTeam
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.PlayerChatSubscriber
import io.github.sunshinewzy.shining.utils.orderWith
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit
import taboolib.module.chat.uncolored
import taboolib.module.ui.ClickEvent
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked

inline fun <reified T> Player.openMultiPageMenu(title: String = "chest", builder: Linked<T>.() -> Unit) {
    openMenu<Linked<T>>(title) {
        buildMultiPage()
        builder(this)
    }
}

inline fun <reified T> Player.openSearchMenu(
    title: String = "chest",
    searchText: String = "",
    builder: Search<T>.() -> Unit
) {
    openMenu<Search<T>>(title) {
        buildMultiPage()

        set(8 orderWith 1, ShiningIcon.SEARCH.item) {
            PlayerChatSubscriber(this@openSearchMenu, "搜索") {
                search(message.uncolored())

                submit {
                    open(player)
                }

                true
            }.register()

            sendMessage("§f[${Shining.prefix}§f] 请输入要搜索的物品名 (输入'§c.§f'以取消)")
            closeInventory()
        }

        search(searchText)

        builder(this)
    }
}


inline fun <reified T> Linked<T>.buildMultiPage() {
    rows(6)
    slots(ShiningGuide.slotOrders)

    onBuild(true, ShiningGuide.onBuildEdge)

    setPreviousPage(2 orderWith 6) { page, hasPreviousPage ->
        if (hasPreviousPage) {
            ShiningIcon.PAGE_PREVIOUS_GLASS_PANE.item
        } else ShiningIcon.EDGE.item
    }

    setNextPage(8 orderWith 6) { page, hasNextPage ->
        if (hasNextPage) {
            ShiningIcon.PAGE_NEXT_GLASS_PANE.item
        } else ShiningIcon.EDGE.item
    }
}

fun Basic.onBack(
    slot: Int = 2 orderWith 1,
    item: ItemStack = ShiningIcon.BACK.item,
    onClick: ClickEvent.() -> Unit
) {
    set(slot, item, onClick)
}

fun Basic.onBackMenu(player: Player, team: GuideTeam, context: GuideContext = EmptyGuideContext, slot: Char = 'B') {
    set(slot, ShiningIcon.BACK_MENU.getLanguageItem().toLocalizedItem(player)) {
        if (clickEvent().isShiftClick) {
            ShiningGuide.openMainMenu(player, team, context)
        } else {
            ShiningGuide.openLastElement(player, team, context)
        }
    }
}

fun Basic.onBackMenu(player: Player, team: GuideTeam, context: GuideContext = EmptyGuideContext, slot: Int) {
    set(slot, ShiningIcon.BACK_MENU.getLanguageItem().toLocalizedItem(player)) {
        if (clickEvent().isShiftClick) {
            ShiningGuide.openMainMenu(player, team, context)
        } else {
            ShiningGuide.openLastElement(player, team, context)
        }
    }
}
