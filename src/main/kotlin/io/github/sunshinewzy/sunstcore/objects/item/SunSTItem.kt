package io.github.sunshinewzy.sunstcore.objects.item

import io.github.sunshinewzy.sunstcore.SunSTCore
import io.github.sunshinewzy.sunstcore.interfaces.Initable
import io.github.sunshinewzy.sunstcore.interfaces.Itemable
import io.github.sunshinewzy.sunstcore.objects.SItem
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.addRecipe
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.addToSunSTItem
import io.github.sunshinewzy.sunstcore.objects.SShapedRecipe
import io.github.sunshinewzy.sunstcore.utils.sendMsg
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
            SunSTCore.plugin,
            SShapedRecipe(
                NamespacedKey(SunSTCore.plugin, key),
                item,
                ingredient,
                line1, line2, line3
            )
        )
    }


    override fun getSItem(): ItemStack = item

    companion object : Initable {
        override fun init() {}
    }
}