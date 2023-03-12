package io.github.sunshinewzy.shining.core.lang.item

import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.lang.LanguageNodePrefix
import io.github.sunshinewzy.shining.core.lang.getLanguageNode
import io.github.sunshinewzy.shining.utils.getMeta
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta

open class NamespacedIdItem(item: ItemStack, val id: NamespacedId) :
    LanguageItem(item, { locale -> id.getLanguageNode(LanguageNodePrefix.ITEM.prefix, locale) }) {

    constructor(item: ItemStack, amount: Int, id: NamespacedId) : this(item, id) {
        this.amount = amount
    }

    constructor(type: Material, id: NamespacedId) : this(ItemStack(type), id)
    constructor(type: Material, amount: Int, id: NamespacedId) : this(ItemStack(type, amount), id)
    constructor(type: Material, damage: Short, amount: Int, id: NamespacedId) : this(
        ItemStack(type, amount, damage),
        id
    )


    override fun shiny(): NamespacedIdItem {
        val meta = getMeta()
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        if (meta is EnchantmentStorageMeta) {
            meta.addStoredEnchant(Enchantment.LURE, 1, true)
        } else {
            meta.addEnchant(Enchantment.LURE, 1, true)
        }
        itemMeta = meta
        return this
    }

}