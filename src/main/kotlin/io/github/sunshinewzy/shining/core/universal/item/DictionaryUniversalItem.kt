package io.github.sunshinewzy.shining.core.universal.item

import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.dictionary.IDictionaryItem
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.api.namespace.Namespace
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.api.universal.item.UniversalItem
import io.github.sunshinewzy.shining.core.dictionary.DictionaryRegistry
import io.github.sunshinewzy.shining.core.dictionary.containsDictionaryItem
import io.github.sunshinewzy.shining.core.dictionary.removeDictionaryItem
import io.github.sunshinewzy.shining.core.editor.chat.openChatEditor
import io.github.sunshinewzy.shining.core.editor.chat.type.Text
import io.github.sunshinewzy.shining.core.editor.chat.type.TextMap
import io.github.sunshinewzy.shining.core.guide.context.GuideEditorContext
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.getDisplayName
import io.github.sunshinewzy.shining.utils.toCurrentLocalizedItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic

@JsonTypeName("dictionary")
class DictionaryUniversalItem(
    var name: NamespacedId,
    var amount: Int
) : UniversalItem {
    
    constructor(dictionaryItem: IDictionaryItem) : this(dictionaryItem.getName(), 1)
    
    constructor() : this(NamespacedId.NULL, 1)
    
    
    override fun getItemStack(): ItemStack =
        DictionaryRegistry.get(name)?.getItemStack()?.also { 
            it.amount = amount
        } ?: ItemStack(Material.AIR)

    override fun getItemAmount(): Int = amount

    override fun contains(inventory: Inventory): Boolean =
        inventory.containsDictionaryItem(name, amount)

    override fun contains(inventory: Inventory, amount: Int): Boolean =
        inventory.containsDictionaryItem(name, amount)

    override fun consume(inventory: Inventory): Boolean =
        inventory.removeDictionaryItem(name, amount)

    override fun openEditor(player: Player, context: GuideContext) {
        player.openMenu<Basic>(player.getLangText("menu-item-universal-dictionary-title")) {
            rows(3)
            
            map(
                "-B-------",
                "-c-i-a b-",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)
            set('c', VanillaUniversalItem.itemCurrent.toLocalizedItem(player))
            set('i', getItemStack())
            
            val theItemEditName = itemEditName.toCurrentLocalizedItem(player, "&f$name")
            set('a', theItemEditName) {
                player.openChatEditor<TextMap>(theItemEditName.getDisplayName()) {
                    map(mapOf(
                        "namespace" to this@DictionaryUniversalItem.name.namespace.toString(),
                        "id" to this@DictionaryUniversalItem.name.id
                    ))

                    predicate {
                        when (index) {
                            "" -> {
                                val namespacedId = NamespacedId.fromString(it) ?: return@predicate false
                                content["namespace"] = namespacedId.namespace.toString()
                                content["id"] = namespacedId.id
                                checkCorrect()
                                true
                            }
                            "namespace" -> Namespace.VALID_NAMESPACE.matcher(it).matches()
                            "id" -> NamespacedId.VALID_ID.matcher(it).matches()
                            else -> false
                        }
                    }

                    onSubmit { content ->
                        val theNamespace = content["namespace"] ?: return@onSubmit
                        val theId = content["id"] ?: return@onSubmit

                        NamespacedId.fromString("$theNamespace:$theId")?.let {
                            this@DictionaryUniversalItem.name = it
                        }
                    }

                    onFinal {
                        openEditor(player, context)
                    }
                }
            }
            
            val theItemEditAmount = itemEditAmount.toCurrentLocalizedItem(player, "&f$amount")
            set('b', theItemEditAmount) {
                player.openChatEditor<Text>(theItemEditAmount.getDisplayName()) { 
                    text(amount.toString())
                    
                    predicate { it.toIntOrNull() != null }
                    
                    onSubmit { amount = it.toInt() }
                    
                    onFinal { openEditor(player, context) }
                }
            }

            context[GuideEditorContext.Back]?.let {
                set('B', ShiningIcon.BACK.toLocalizedItem(player)) { it.onBack(this) }
            } ?: set('B', ShiningIcon.EDGE.item)
            
            onClick(lock = true)
        }
    }

    override fun clone(): DictionaryUniversalItem = DictionaryUniversalItem(name, amount)

    override fun isSimilar(other: UniversalItem, checkAmount: Boolean): Boolean {
        if (this === other) return true
        if (other !is DictionaryUniversalItem) return false
        
        if (name != other.name) return false
        return !checkAmount || amount == other.amount
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DictionaryUniversalItem) return false

        if (name != other.name) return false
        return amount == other.amount
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + amount
        return result
    }


    companion object {
        private val itemEditName = NamespacedIdItem(Material.NAME_TAG, NamespacedId(Shining, "item-universal-dictionary-name"))
        private val itemEditAmount = NamespacedIdItem(Material.TORCH, NamespacedId(Shining, "item-universal-dictionary-amount"))
    }
    
}