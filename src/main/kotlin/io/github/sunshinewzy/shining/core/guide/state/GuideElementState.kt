package io.github.sunshinewzy.shining.core.guide.state

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.lock.ElementLock
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.editor.chat.openChatEditor
import io.github.sunshinewzy.shining.core.editor.chat.type.Text
import io.github.sunshinewzy.shining.core.editor.chat.type.TextList
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.addLore
import io.github.sunshinewzy.shining.utils.getDisplayName
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import java.util.*

abstract class GuideElementState(private var element: IGuideElement? = null) : IGuideElementState {
    
    var id: NamespacedId? = null
    var descriptionName: String? = null
    var descriptionLore: MutableList<String> = LinkedList()
    
    val dependencyMap: MutableMap<NamespacedId, IGuideElement> = HashMap()
    val locks: MutableList<ElementLock> = LinkedList()
    
    
    override fun update(): Boolean =
        element?.update(this) ?: false

    override fun openEditor(player: Player) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-editor-state-title")) {
            rows(6)

            map(
                "-B-------",
                "-abc    -",
                "-       -",
                "-       -",
                "-       -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)

            set('B', ShiningIcon.BACK.getLanguageItem().toLocalizedItem(player), ShiningGuide.onClickBack)

            set('a', itemEditId.toCurrentLocalizedItem(player, "&f$id")) {
                player.openChatEditor<Text>(itemEditId.toLocalizedItem(player).getDisplayName()) {
                    text(id.toString())
                    
                    predicate { NamespacedId.fromString(it) != null }
                    
                    onSubmit { content ->
                        NamespacedId.fromString(content)?.let {
                            id = it
                        }
                    }

                    onFinal {
                        openEditor(player)
                    }
                }
            }
            
            set('b', itemEditDescriptionName.toCurrentLocalizedItem(player, descriptionName)) {
                player.openChatEditor<Text>(itemEditDescriptionName.toLocalizedItem(player).getDisplayName()) {
                    text(descriptionName)
                    
                    onSubmit { 
                        descriptionName = content
                    }
                    
                    onFinal {
                        openEditor(player)
                    }
                }
            }
            
            set('c', itemEditDescriptionLore.toCurrentLocalizedItem(player, descriptionLore)) {
                player.openChatEditor<TextList>(itemEditDescriptionLore.toLocalizedItem(player).getDisplayName()) {
                    list(descriptionLore)
                    
                    onSubmit { 
                        descriptionLore = it
                    }

                    onFinal {
                        openEditor(player)
                    }
                }
            }

            onClick(lock = true)
        }
    }
    
    protected fun ItemStack.addCurrentLore(player: Player, currentLore: String?): ItemStack {
        return addLore("", player.getLangText("menu-shining_guide-editor-state-current_lore"), currentLore ?: "null")
    }
    
    protected fun ItemStack.addCurrentLore(player: Player, currentLore: List<String>): ItemStack {
        addLore("", player.getLangText("menu-shining_guide-editor-state-current_lore"))
        return addLore(currentLore)
    }
    
    protected fun NamespacedIdItem.toCurrentLocalizedItem(player: Player, currentLore: String?): ItemStack {
        return toLocalizedItem(player).clone().addCurrentLore(player, currentLore)
    }
    
    protected fun NamespacedIdItem.toCurrentLocalizedItem(player: Player, currentLore: List<String>): ItemStack {
        return toLocalizedItem(player).clone().addCurrentLore(player, currentLore)
    }
    
    
    companion object {
        private val itemEditId = NamespacedIdItem(Material.NAME_TAG, NamespacedId(Shining, "shining_guide-editor-state-element-id"))
        private val itemEditDescriptionName = NamespacedIdItem(Material.APPLE, NamespacedId(Shining, "shining_guide-editor-state-element-description_name"))
        private val itemEditDescriptionLore = NamespacedIdItem(Material.BREAD, NamespacedId(Shining, "shining_guide-editor-state-element-description_lore"))
        
    }
    
}