package io.github.sunshinewzy.shining.objects.item

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.Itemable
import io.github.sunshinewzy.shining.interfaces.Initable
import io.github.sunshinewzy.shining.objects.SItem
import io.github.sunshinewzy.shining.objects.SItem.Companion.addRecipe
import io.github.sunshinewzy.shining.objects.SItem.Companion.addToSunSTItem
import io.github.sunshinewzy.shining.objects.SShapedRecipe
import io.github.sunshinewzy.shining.utils.sendMsg
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.block.Action
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

enum class SunSTItem(val item: ItemStack) : Itemable {
    TOOL_BLOCK_INFO(SItem(Material.WOODEN_SHOVEL, "§a方块信息查看器").addAction { 
        val clickedBlock = clickedBlock ?: return@addAction
        if(hand == EquipmentSlot.HAND && action == Action.RIGHT_CLICK_BLOCK){
            player.sendMsg("§a方块信息查看器", "${clickedBlock.type}:${clickedBlock.state.data.toItemStack(1).durability}")
        }
    })
    
    ;

    
    init {
        item.addToSunSTItem(toString())
    }

    constructor(
        item: SItem,
        key: String,
        ingredient: Map<Char, Material>,
        line1: String = "",
        line2: String = "",
        line3: String = ""
    ) : this(item) {
        item.addRecipe(
            Shining.plugin,
            SShapedRecipe(
                NamespacedKey(Shining.plugin, key),
                item,
                ingredient,
                line1, line2, line3
            )
        )
    }


    override fun getItemStack(): ItemStack = item

    companion object : Initable {
        override fun init() {}
    }
}