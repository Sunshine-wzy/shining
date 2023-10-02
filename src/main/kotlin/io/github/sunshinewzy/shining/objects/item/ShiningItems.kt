package io.github.sunshinewzy.shining.objects.item

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.dictionary.behavior.ItemBehavior
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.dictionary.DictionaryRegistry
import io.github.sunshinewzy.shining.objects.SItem
import io.github.sunshinewzy.shining.utils.sendMsg
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

object ShiningItems {

    val TOOL_BLOCK_INFO = NamespacedId(Shining, "tool-block_info").let { id ->
        DictionaryRegistry.registerItem(
            id, SItem(Material.WOODEN_SHOVEL, "§a方块信息查看器"),
            object : ItemBehavior() {
                override fun onInteract(event: PlayerInteractEvent, player: Player, item: ItemStack, action: Action) {
                    val clickedBlock = event.clickedBlock ?: return
                    if (event.hand == EquipmentSlot.HAND && action == Action.RIGHT_CLICK_BLOCK) {
                        player.sendMsg(
                            "§a方块信息查看器",
                            "${clickedBlock.type}:${clickedBlock.state.data.toItemStack(1).durability}"
                        )
                    }
                }
            }
        )
    }

}