package io.github.sunshinewzy.shining.core.menu

import io.github.sunshinewzy.shining.Shining
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
import taboolib.module.ui.type.Linked
import java.util.*

object MenuBuilder {

    inline fun <reified T> Player.openMultiPageMenu(title: String = "chest", builder: Linked<T>.() -> Unit) {
        openMenu<Linked<T>>(title) { 
            buildMultiPage()
            TreeMap<String, Int>()
            builder(this)
        }
    }
    
    inline fun <reified T> Player.openSearchMenu(title: String = "chest", searchText: String = "", builder: Search<T>.() -> Unit) {
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
            if(hasPreviousPage) {
                ShiningIcon.PAGE_PRE_GLASS_PANE.item
            } else ShiningIcon.EDGE.item
        }

        setNextPage(8 orderWith 6) { page, hasNextPage ->
            if(hasNextPage) {
                ShiningIcon.PAGE_NEXT_GLASS_PANE.item
            } else ShiningIcon.EDGE.item
        }
    }
    
    fun <T> Linked<T>.onBack(
        slot: Int = 2 orderWith 1,
        item: ItemStack = ShiningIcon.BACK_LAST_PAGE.item,
        onClick: ClickEvent.() -> Unit
    ) {
        set(slot, item, onClick)
    }
    
}