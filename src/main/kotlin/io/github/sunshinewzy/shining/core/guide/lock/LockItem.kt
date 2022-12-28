package io.github.sunshinewzy.shining.core.guide.lock

import io.github.sunshinewzy.shining.core.guide.ElementLock
import io.github.sunshinewzy.shining.core.guide.SGuide
import io.github.sunshinewzy.shining.objects.SItem.Companion.getName
import io.github.sunshinewzy.shining.objects.item.SunSTIcon
import io.github.sunshinewzy.shining.utils.containsItem
import io.github.sunshinewzy.shining.utils.removeSItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic

class LockItem(
    var item: ItemStack,
    isConsume: Boolean = true
) : ElementLock("物品 ${item.getName()}", isConsume) {

    override fun check(player: Player): Boolean =
        player.inventory.containsItem(item)

    override fun consume(player: Player) {
        player.inventory.removeSItem(item)
    }

    override fun tip(player: Player) {
        player.openMenu<Basic>(SGuide.TITLE) { 
            rows(3)
            
            map(
                "#",
                "ooooa"
            )
            
            set('#', SunSTIcon.BACK.item)
            set('a', item)
            
            onClick('#', SGuide.onClickBack)
            
            onClick(lock = true)
        }
    }
}