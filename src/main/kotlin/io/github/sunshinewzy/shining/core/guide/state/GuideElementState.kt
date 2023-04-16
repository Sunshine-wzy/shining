package io.github.sunshinewzy.shining.core.guide.state

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.ElementCondition
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.lock.ElementLock
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.namespace.Namespace
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.editor.chat.openChatEditor
import io.github.sunshinewzy.shining.core.editor.chat.type.Text
import io.github.sunshinewzy.shining.core.editor.chat.type.TextList
import io.github.sunshinewzy.shining.core.editor.chat.type.TextMap
import io.github.sunshinewzy.shining.core.guide.GuideTeam
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.context.GuideSelectElementsContext
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.menu.onBackMenu
import io.github.sunshinewzy.shining.core.menu.openMultiPageMenu
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.addLore
import io.github.sunshinewzy.shining.utils.getDisplayName
import io.github.sunshinewzy.shining.utils.insertLore
import io.github.sunshinewzy.shining.utils.orderWith
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.isAir
import java.util.*

abstract class GuideElementState(private var element: IGuideElement? = null) : IGuideElementState {

    var id: NamespacedId? = null
    var descriptionName: String? = null
    var descriptionLore: MutableList<String> = LinkedList()

    val dependencyMap: MutableMap<NamespacedId, IGuideElement> = HashMap()
    val locks: MutableList<ElementLock> = LinkedList()


    abstract fun openAdvancedEditor(player: Player)


    override fun update(): Boolean =
        element?.update(this) ?: false

    override fun openEditor(player: Player, team: GuideTeam) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-editor-state-title")) {
            rows(3)

            map(
                "-B-------",
                "-  a b  -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)

            onBackMenu(player, team)

            set('a', itemBasicEditor.toLocalizedItem(player)) {
                openBasicEditor(player, team)
            }

            set('b', itemAdvancedEditor.toLocalizedItem(player)) {
                openAdvancedEditor(player)
            }

            onClick(lock = true)
        }
    }

    open fun openBasicEditor(player: Player, team: GuideTeam) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-editor-state-basic-title")) {
            rows(3)

            map(
                "-B-------",
                "- abcde -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)

            set('B', ShiningIcon.BACK_MENU.getLanguageItem().toLocalizedItem(player)) {
                openEditor(player, team)
            }

            set('a', itemEditId.toCurrentLocalizedItem(player, "&f$id")) {
                player.openChatEditor<TextMap>(itemEditId.toLocalizedItem(player).getDisplayName()) {
                    id?.also {
                        map(mapOf("namespace" to it.namespace.toString(), "id" to it.id))
                    } ?: map("namespace", "id")

                    predicate {
                        when (index) {
                            "namespace" -> Namespace.VALID_NAMESPACE.matcher(it).matches()
                            "id" -> NamespacedId.VALID_ID.matcher(it).matches()
                            else -> false
                        }
                    }

                    onSubmit { content ->
                        val theNamespace = content["namespace"] ?: return@onSubmit
                        val theId = content["id"] ?: return@onSubmit

                        NamespacedId.fromString("$theNamespace:$theId")?.let {
                            id = it
                        }
                    }

                    onFinal {
                        openBasicEditor(player, team)
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
                        openBasicEditor(player, team)
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
                        openBasicEditor(player, team)
                    }
                }
            }

            set('d', itemEditDependencies.toLocalizedItem(player)) {
                openDependenciesEditor(player, team)
            }

            set('e', itemEditLocks.toLocalizedItem(player)) {
                openLocksEditor(player, team)
            }

            onClick(lock = true)
        }
    }


    fun openDependenciesEditor(player: Player, team: GuideTeam) {
        player.openMultiPageMenu<IGuideElement>(player.getLangText("menu-shining_guide-editor-state-basic-dependencies-title")) {
            elements { dependencyMap.values.toList() }

            onGenerate(async = true) { player, element, index, slot ->
                element.getSymbolByCondition(player, GuideTeam.CompletedTeam, ElementCondition.UNLOCKED)
                    .insertLore(0, "&7${element.getId()}", "")
            }

            onClick { event, element ->
                openDependencyEditor(player, team, element)
            }

            set(2 orderWith 1, ShiningIcon.BACK_MENU.getLanguageItem().toLocalizedItem(player)) {
                openBasicEditor(player, team)
            }
            
            onClick(lock = true) {
                if (it.rawSlot in ShiningGuide.slotOrders && it.currentItem.isAir()) {
                    ShiningGuide.openCompletedMainMenu(player, GuideSelectElementsContext())
                }
            }
        }
    }

    fun openDependencyEditor(player: Player, team: GuideTeam, element: IGuideElement) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-editor-state-basic-dependencies-title")) {
            rows(3)

            map(
                "-B-------",
                "- a     -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)

            set('B', ShiningIcon.BACK_MENU.getLanguageItem().toLocalizedItem(player)) {
                openDependenciesEditor(player, team)
            }

            set('a', itemEditDependencyRemove.toLocalizedItem(player)) {
                dependencyMap -= element.getId()
                openDependenciesEditor(player, team)
            }

            onClick(lock = true)
        }
    }

    fun openLocksEditor(player: Player, team: GuideTeam) {
        player.openMultiPageMenu<ElementLock>(player.getLangText("menu-shining_guide-editor-state-basic-locks-title")) {
            elements { locks }


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
        private val itemBasicEditor = NamespacedIdItem(Material.NAME_TAG, NamespacedId(Shining, "shining_guide-editor-state-element-basic_editor"))
        private val itemAdvancedEditor = NamespacedIdItem(Material.DIAMOND, NamespacedId(Shining, "shining_guide-editor-state-element-advanced_editor"))

        private val itemEditId = NamespacedIdItem(Material.NAME_TAG, NamespacedId(Shining, "shining_guide-editor-state-element-id"))
        private val itemEditDescriptionName = NamespacedIdItem(Material.APPLE, NamespacedId(Shining, "shining_guide-editor-state-element-description_name"))
        private val itemEditDescriptionLore = NamespacedIdItem(Material.BREAD, NamespacedId(Shining, "shining_guide-editor-state-element-description_lore"))
        private val itemEditDependencies = NamespacedIdItem(Material.CHEST, NamespacedId(Shining, "shining_guide-editor-state-element-dependencies"))
        private val itemEditLocks = NamespacedIdItem(Material.TRIPWIRE_HOOK, NamespacedId(Shining, "shining_guide-editor-state-element-locks"))

        private val itemEditDependencyRemove = NamespacedIdItem(Material.BARRIER, NamespacedId(Shining, "shining_guide-editor-state-element-dependency-remove"))

    }

}