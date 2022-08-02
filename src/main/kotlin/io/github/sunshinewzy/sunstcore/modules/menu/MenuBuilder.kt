package io.github.sunshinewzy.sunstcore.modules.menu

import io.github.sunshinewzy.sunstcore.modules.guide.SGuide
import io.github.sunshinewzy.sunstcore.objects.item.SunSTIcon
import io.github.sunshinewzy.sunstcore.objects.orderWith
import io.github.sunshinewzy.sunstcore.utils.PlayerChatSubscriber
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.ClickEvent
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked

object MenuBuilder {

    inline fun <reified T> Player.openMultiPageMenu(title: String = "chest", builder: Linked<T>.() -> Unit) {
        openMenu<Linked<T>>(title) { 
            buildMultiPage()
            
            builder(this)
        }
    }
    
    inline fun <reified T> Player.openSelectMenu(title: String = "chest", builder: Linked<T>.() -> Unit) {
        openMultiPageMenu<T>(title) { 
            set(8 orderWith 1, SunSTIcon.SEARCH.item) {
                PlayerChatSubscriber(this@openSelectMenu, "搜索") {
                    
                    
                    false
                }.register()
            }
        }
    }


    inline fun <reified T> Linked<T>.buildMultiPage() {
        rows(6)
        slots(SGuide.slotOrders)

        onBuild(onBuild = SGuide.onBuildEdge)

        setPreviousPage(2 orderWith 6) { page, hasPreviousPage ->
            if(hasPreviousPage) {
                SunSTIcon.PAGE_PRE_GLASS_PANE.item
            } else SunSTIcon.EDGE.item
        }

        setNextPage(8 orderWith 6) { page, hasNextPage ->
            if(hasNextPage) {
                SunSTIcon.PAGE_NEXT_GLASS_PANE.item
            } else SunSTIcon.EDGE.item
        }
    }
    
    fun <T> Linked<T>.onBack(
        slot: Int = 2 orderWith 1,
        item: ItemStack = SunSTIcon.BACK_LAST_PAGE.item,
        onClick: ClickEvent.() -> Unit
    ) {
        set(slot, item, onClick)
    }
    
}