package io.github.sunshinewzy.shining.core.menu

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.core.guide.SGuide
import io.github.sunshinewzy.shining.objects.item.SunSTIcon
import io.github.sunshinewzy.shining.objects.orderWith
import io.github.sunshinewzy.shining.utils.PlayerChatSubscriber
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.util.sync
import taboolib.module.chat.uncolored
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
    
    inline fun <reified T> Player.openSearchMenu(title: String = "chest", searchText: String = "", builder: Search<T>.() -> Unit) {
        openMenu<Search<T>>(title) { 
            buildMultiPage()
            
            set(8 orderWith 1, SunSTIcon.SEARCH.item) {
                PlayerChatSubscriber(this@openSearchMenu, "搜索") {
                    search(message.uncolored())
                    
                    sync {
                        open(player)
                    }

                    true
                }.register()

                sendMessage("§f[${Shining.prefixName}§f] 请输入要搜索的物品名 (输入'§c.§f'以取消)")
                closeInventory()
            }

            search(searchText)
            
            builder(this)
        }
    }


    inline fun <reified T> Linked<T>.buildMultiPage() {
        rows(6)
        slots(SGuide.slotOrders)

        onBuild(true, SGuide.onBuildEdge)

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