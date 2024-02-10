package io.github.sunshinewzy.shining.core.menu

import io.github.sunshinewzy.shining.api.guide.context.EmptyGuideContext
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import io.github.sunshinewzy.shining.core.editor.chat.openChatEditor
import io.github.sunshinewzy.shining.core.editor.chat.type.Text
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.addLore
import io.github.sunshinewzy.shining.utils.orderWith
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.module.chat.uncolored
import taboolib.module.ui.ClickEvent
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Chest
import taboolib.module.ui.type.PageableChest
import taboolib.platform.util.isNotAir

inline fun <reified T> Player.openMultiPageMenu(title: String = "chest", builder: PageableChest<T>.() -> Unit) {
    openMenu<PageableChest<T>>(title) {
        buildMultiPage(this@openMultiPageMenu)
        builder(this)
    }
}

inline fun <reified T> Player.openSearchMenu(
    title: String = "chest",
    searchText: String = "",
    builder: SearchChest<T>.() -> Unit
) {
    openMenu<SearchChest<T>>(title) {
        buildMultiPage(this@openSearchMenu)

        set(8 orderWith 1, ShiningIcon.SEARCH.item) {
            openChatEditor<Text>(getLangText("menu-search-prompt")) { 
                text(searchText)
                
                onSubmit {
                    search(it.uncolored())
                }
                
                onFinal {
                    open(this@openSearchMenu)
                }
            }
        }

        search(searchText)
        builder(this)
    }
}

open class ConfirmMenuBuilder {
    var confirmAction: ClickEvent.() -> Unit = {}
        private set
    var cancelAction: ClickEvent.() -> Unit = {}
        private set
    var finalAction: ClickEvent.() -> Unit = {}
        private set
    var buildAction: Chest.() -> Unit = {}
        private set
    
    fun onConfirm(action: ClickEvent.() -> Unit) {
        confirmAction = action
    }
    
    fun onCancel(action: ClickEvent.() -> Unit) {
        cancelAction = action
    }
    
    fun onFinal(action: ClickEvent.() -> Unit) {
        finalAction = action
    }
    
    fun onBuild(action: Chest.() -> Unit) {
        buildAction = action
    }
}

inline fun Player.openConfirmMenu(
    title: String,
    description: String,
    builder: ConfirmMenuBuilder.() -> Unit
) {
    val menuBuilder = ConfirmMenuBuilder()
    builder(menuBuilder)
    
    openMenu<Chest>(title) { 
        rows(3)

        map(
            "---------",
            "-  a b  -",
            "---------"
        )

        set('-', ShiningIcon.EDGE.item)
        
        set('a', ShiningIcon.CONFIRM.toLocalizedItem(this@openConfirmMenu).clone().addLore("&f$description")) {
            menuBuilder.confirmAction(this)
            menuBuilder.finalAction(this)
        }
        
        set('b', ShiningIcon.CANCEL.toLocalizedItem(this@openConfirmMenu).clone().addLore("&f$description")) {
            menuBuilder.cancelAction(this)
            menuBuilder.finalAction(this)
        }
        
        onClick(lock = true)
        
        menuBuilder.buildAction(this)
    }
}

inline fun Player.openConfirmMenu(description: String, builder: ConfirmMenuBuilder.() -> Unit) {
    openConfirmMenu("${getLangText("menu-confirm-title")} $description", description, builder)
}

inline fun Player.openDeleteConfirmMenu(builder: ConfirmMenuBuilder.() -> Unit) {
    openConfirmMenu(this.getLangText("menu-confirm-delete"), builder)
}

open class CascadeDeleteConfirmMenuBuilder : ConfirmMenuBuilder() {
    var cascadeDeleteAction: ClickEvent.() -> Unit = {}
        private set
    
    fun onCascadeDelete(action: ClickEvent.() -> Unit) {
        cascadeDeleteAction = action
    }
}

inline fun Player.openCascadeDeleteConfirmMenu(
    title: String,
    description: String,
    hasConfirm: Boolean = true,
    builder: CascadeDeleteConfirmMenuBuilder.() -> Unit
) {
    val menuBuilder = CascadeDeleteConfirmMenuBuilder()
    builder(menuBuilder)

    openMenu<Chest>(title) {
        rows(3)

        map(
            "---------",
            "- c a b -",
            "---------"
        )

        set('-', ShiningIcon.EDGE.item)

        if (hasConfirm) {
            set('a', ShiningIcon.CONFIRM.toLocalizedItem(this@openCascadeDeleteConfirmMenu).clone().addLore("&f$description")) {
                menuBuilder.confirmAction(this)
                menuBuilder.finalAction(this)
            }
        }

        set('b', ShiningIcon.CANCEL.toLocalizedItem(this@openCascadeDeleteConfirmMenu).clone().addLore("&f$description")) {
            menuBuilder.cancelAction(this)
            menuBuilder.finalAction(this)
        }
        
        set('c', ShiningIcon.REMOVE_CASCADE.toLocalizedItem(this@openCascadeDeleteConfirmMenu)) {
            openConfirmMenu(getLangText("menu-confirm-delete-cascade")) { 
                onConfirm { menuBuilder.cascadeDeleteAction(this) }
                onCancel { menuBuilder.cancelAction(this) }
                onFinal { menuBuilder.finalAction(this) }
            }
        }

        onClick(lock = true)

        menuBuilder.buildAction(this)
    }
}

inline fun Player.openCascadeDeleteConfirmMenu(hasConfirm: Boolean = true, builder: CascadeDeleteConfirmMenuBuilder.() -> Unit) {
    val deleteCascade = getLangText("menu-confirm-delete")
    openCascadeDeleteConfirmMenu("${getLangText("menu-confirm-title")} $deleteCascade", deleteCascade, hasConfirm, builder)
}

fun <T> PageableChest<T>.buildMultiPage(player: Player) {
    rows(6)
    slots(ShiningGuide.slotOrders)

    onBuild(false, ShiningGuide.onBuildEdge)

    setPreviousPage(2 orderWith 6) { _, hasPreviousPage ->
        if (hasPreviousPage) ShiningIcon.PAGE_PREVIOUS_GLASS_PANE.toLocalizedItem(player)
        else ShiningIcon.EDGE.item
    }

    setNextPage(8 orderWith 6) { _, hasNextPage ->
        if (hasNextPage) ShiningIcon.PAGE_NEXT_GLASS_PANE.toLocalizedItem(player)
        else ShiningIcon.EDGE.item
    }
}

fun Chest.onBuildEdge(edgeOrders: Collection<Int>, action: ((Player, Inventory) -> Unit)? = null) {
    onBuild(false) { player, inv ->
        edgeOrders.forEach { index ->
            inv.getItem(index)?.let {
                if (it.isNotAir()) return@forEach
            }

            inv.setItem(index, ShiningIcon.EDGE.item)
        }
        
        action?.let { it(player, inv) }
    }
}

fun Chest.onBack(
    slot: Int = 2 orderWith 1,
    item: ItemStack = ShiningIcon.BACK.item,
    onClick: ClickEvent.() -> Unit
) {
    set(slot, item, onClick)
}

fun Chest.onBack(
    player: Player,
    slot: Int = 2 orderWith 1,
    item: ItemStack = ShiningIcon.BACK.toLocalizedItem(player),
    onClick: ClickEvent.() -> Unit
) {
    onBack(slot, item, onClick)
}

fun Chest.onBackMenu(player: Player, team: IGuideTeam, context: GuideContext = EmptyGuideContext, slot: Char = 'B') {
    set(slot, ShiningIcon.BACK_MENU.toLocalizedItem(player)) {
        if (clickEvent().isShiftClick) {
            ShiningGuide.openMainMenu(player, team, context)
        } else {
            ShiningGuide.openLastElement(player, team, context)
        }
    }
}

fun Chest.onBackMenu(player: Player, team: IGuideTeam, context: GuideContext = EmptyGuideContext, slot: Int) {
    set(slot, ShiningIcon.BACK_MENU.getLanguageItem().toLocalizedItem(player)) {
        if (clickEvent().isShiftClick) {
            ShiningGuide.openMainMenu(player, team, context)
        } else {
            ShiningGuide.openLastElement(player, team, context)
        }
    }
}
