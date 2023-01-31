package io.github.sunshinewzy.shining.core.guide.state

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.lock.ElementLock
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.editor.chat.ChatEditor
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
    val descriptionLore: MutableList<String> = LinkedList()
    
    val dependencyMap: MutableMap<NamespacedId, IGuideElement> = HashMap()
    val locks: MutableList<ElementLock> = LinkedList()
    
    
    override fun update(): Boolean =
        element?.update(this) ?: false

    override fun openEditor(player: Player) {
        player.openMenu<Basic> {
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

            set('a', itemEditId.toCurrentLocalizedItem(player, id.toString())) {
                ChatEditor.open(
                    player,
                    itemEditId.toLocalizedItem(player).getDisplayName(),
                    predicate = { NamespacedId.fromString(it) != null }
                ) { content -> 
                    NamespacedId.fromString(content)?.let { 
                        id = it
                    }
                    openEditor(player)
                }
                player.closeInventory()
            }
            
            set('b', itemEditDescriptionName.toCurrentLocalizedItem(player, descriptionName)) {
                ChatEditor.open(player, itemEditDescriptionName.getDisplayName()) { content ->  
                    descriptionName = content
                    openEditor(player)
                }
                player.closeInventory()
            }
            
            set('c', itemEditDescriptionLore.toCurrentLocalizedItem(player, descriptionLore)) {
                // TODO: Lore editor
                
                player.closeInventory()
            }

            onClick(lock = true)
        }
    }
    
    protected fun ItemStack.addCurrentLore(player: Player, currentLore: String?) {
        addLore("", player.getLangText("menu-shining_guide-editor-state-current_lore"), currentLore ?: "null")
    }
    
    protected fun ItemStack.addCurrentLore(player: Player, currentLore: List<String>) {
        addLore("", player.getLangText("menu-shining_guide-editor-state-current_lore"))
        addLore(currentLore)
    }
    
    protected fun NamespacedIdItem.toCurrentLocalizedItem(player: Player, currentLore: String?): ItemStack {
        return toLocalizedItem(player).clone().also { addCurrentLore(player, currentLore) }
    }
    
    protected fun NamespacedIdItem.toCurrentLocalizedItem(player: Player, currentLore: List<String>): ItemStack {
        return toLocalizedItem(player).clone().also { addCurrentLore(player, currentLore) }
    }
    
    
    companion object {
        private val itemEditId = NamespacedIdItem(Material.NAME_TAG, NamespacedId(Shining, "shining_guide-editor-state-element-id"))
        private val itemEditDescriptionName = NamespacedIdItem(Material.APPLE, NamespacedId(Shining, "shining_guide-editor-state-element-description_name"))
        private val itemEditDescriptionLore = NamespacedIdItem(Material.BREAD, NamespacedId(Shining, "shining_guide-editor-state-element-description_lore"))
        
    }
    
}