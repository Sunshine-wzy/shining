package io.github.sunshinewzy.shining.api.lang.item

import io.github.sunshinewzy.shining.api.lang.node.IListNode
import io.github.sunshinewzy.shining.api.lang.node.ISectionNode
import io.github.sunshinewzy.shining.api.lang.node.ITextNode
import io.github.sunshinewzy.shining.api.lang.node.LanguageNode
import org.bukkit.inventory.ItemStack

interface ILocalizedItem {

    val languageNode: LanguageNode
    

    fun getTextNode(): ITextNode? = languageNode as? ITextNode
    
    fun getListNode(): IListNode? = languageNode as? IListNode

    fun getSectionNode(): ISectionNode? = languageNode as? ISectionNode

    fun getSectionString(path: String): String? =
        getSectionNode()?.section?.getString(path)
    
    fun getItemStack(): ItemStack
    
}