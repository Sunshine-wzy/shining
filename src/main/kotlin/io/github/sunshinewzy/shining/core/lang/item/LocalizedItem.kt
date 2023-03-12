package io.github.sunshinewzy.shining.core.lang.item

import io.github.sunshinewzy.shining.api.lang.node.LanguageNode
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.lang.LanguageNodePrefix.ITEM
import io.github.sunshinewzy.shining.core.lang.getLanguageNode
import io.github.sunshinewzy.shining.core.lang.node.ListNode
import io.github.sunshinewzy.shining.core.lang.node.SectionNode
import io.github.sunshinewzy.shining.core.lang.node.TextNode
import io.github.sunshinewzy.shining.utils.localize
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.buildItem

open class LocalizedItem(item: ItemStack, val languageNode: LanguageNode) : ItemStack(item) {

    val shinyItem: ItemStack by lazy { buildItem(this) { shiny() } }


    init {
        localize(languageNode)
    }

    constructor(item: ItemStack, amount: Int, languageNode: LanguageNode) : this(item, languageNode) {
        this.amount = amount
    }

    constructor(type: Material, languageNode: LanguageNode) : this(ItemStack(type), languageNode)
    constructor(type: Material, amount: Int, languageNode: LanguageNode) : this(ItemStack(type, amount), languageNode)
    constructor(type: Material, damage: Short, amount: Int, languageNode: LanguageNode) : this(
        ItemStack(
            type,
            amount,
            damage
        ), languageNode
    )

    constructor(item: ItemStack, id: NamespacedId) : this(item, id.getLanguageNode(ITEM.prefix))
    constructor(item: ItemStack, amount: Int, id: NamespacedId) : this(item, amount, id.getLanguageNode(ITEM.prefix))
    constructor(type: Material, id: NamespacedId) : this(type, id.getLanguageNode(ITEM.prefix))
    constructor(type: Material, amount: Int, id: NamespacedId) : this(type, amount, id.getLanguageNode(ITEM.prefix))
    constructor(type: Material, damage: Short, amount: Int, id: NamespacedId) : this(
        type,
        damage,
        amount,
        id.getLanguageNode(ITEM.prefix)
    )


    fun getTextNode(): TextNode? = languageNode as? TextNode
    fun getListNode(): ListNode? = languageNode as? ListNode
    fun getSectionNode(): SectionNode? = languageNode as? SectionNode

    fun getSectionString(path: String): String? =
        getSectionNode()?.section?.getString(path)

}