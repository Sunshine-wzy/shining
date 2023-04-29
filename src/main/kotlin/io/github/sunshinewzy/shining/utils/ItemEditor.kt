package io.github.sunshinewzy.shining.utils

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.editor.chat.openChatEditor
import io.github.sunshinewzy.shining.core.editor.chat.type.Text
import io.github.sunshinewzy.shining.core.editor.chat.type.TextList
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.lang.sendPrefixedLangText
import io.github.sunshinewzy.shining.core.menu.buildMultiPage
import io.github.sunshinewzy.shining.objects.SItem
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.chat.colored
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked

object ItemEditor {
    private val itemEditLoreWithChatEditor = NamespacedIdItem(Material.EMERALD, NamespacedId(Shining, "editor-item-lore-chat_editor"))
    private val itemEditLoreWithGUI = NamespacedIdItem(Material.PAINTING, NamespacedId(Shining, "editor-item-lore-gui"))
    
    val wools = arrayListOf(
        Material.LIME_WOOL,
        Material.YELLOW_WOOL,
        Material.LIGHT_BLUE_WOOL,
        Material.PINK_WOOL,
        Material.ORANGE_WOOL,
        Material.WHITE_WOOL,
        Material.MAGENTA_WOOL,
        Material.CYAN_WOOL,
        Material.LIGHT_GRAY_WOOL,
        Material.PURPLE_WOOL,
        Material.BROWN_WOOL,
        Material.BLUE_WOOL,
        Material.GREEN_WOOL,
        Material.GRAY_WOOL,
        Material.RED_WOOL,
        Material.BLACK_WOOL
    )

    val editItemOrder = 2 orderWith 2


    fun editItem(item: ItemStack, player: Player) {
        player.openMenu<Basic>(player.getLangText("menu-editor-item-title")) {
            rows(3)

            map(
                "oooxxxxxx",
                "o oxaxbcx",
                "oooxxxxxx"
            )

            set('o', ShiningIcon.EDGE.item)
            set('x', ShiningIcon.EDGE_GLASS_PANE.item)
            set('a', ShiningIcon.RENAME.toLocalizedItem(player))
            set('b', itemEditLoreWithChatEditor.toLocalizedItem(player))
            set('c', itemEditLoreWithGUI.toLocalizedItem(player))

            onBuild { _, inventory ->
                inventory.setItem(editItemOrder, item)
            }

            onClick('a') { event ->
                player.openChatEditor<Text>(player.getLangText("menu-editor-item-name-title")) { 
                    text(item.getDisplayNameOrNull())
                    
                    onSubmit { 
                        item.setName(it)
                    }
                    
                    onFinal { 
                        editItem(item, player)
                    }
                }
            }
            
            onClick('b') {
                editLoreWithChatEditor(item, player)
            }

            onClick('c') {
                editLoreWithGUI(item, player)
            }

            onClick(lock = true)
        }
    }
    
    fun editLoreWithChatEditor(item: ItemStack, player: Player) {
        player.openChatEditor<TextList>(player.getLangText("menu-editor-item-lore-title")) { 
            list(item.getLore())
            
            onSubmit { 
                item.setLore(it)
            }
            
            onFinal { 
                editItem(item, player)
            }
        }
    }

    fun editLoreWithGUI(item: ItemStack, player: Player) {
        player.openMenu<Linked<String>>(player.getLangText("menu-editor-item-lore-title")) {
            buildMultiPage()

            elements { item.getLore() }

            var iterator = wools.iterator()
            onGenerate { _, element, index, _ ->
                if (!iterator.hasNext()) iterator = wools.iterator()
                SItem(iterator.next(), "&f${page * 36 + index}", element)
            }

            var status = Status.EDIT
            onClick onClickLore@{ event, element ->
                when (status) {
                    Status.EDIT -> {
                        event.currentItem?.itemMeta?.displayName?.let { displayName ->
                            item.itemMeta?.let { meta ->
                                meta.lore?.let { lore ->
                                    val index = displayName.substring(2).toInt()

                                    player.openChatEditor<Text>(player.getLangText("menu-editor-item-lore-title")) {
                                        text(lore[index])
                                        
                                        onSubmit { 
                                            lore[index] = it.colored()
                                            meta.lore = lore
                                            item.itemMeta = meta
                                        }
                                        
                                        onFinal { 
                                            editLoreWithGUI(item, player)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Status.ADD -> {
                        event.currentItem?.itemMeta?.displayName?.let { displayName ->
                            item.itemMeta?.let { meta ->
                                meta.lore?.let { lore ->
                                    val index = displayName.substring(2).toInt()
                                    
                                    player.openChatEditor<Text>(player.getLangText("menu-editor-item-lore-add")) { 
                                        onSubmit {
                                            lore.add(index, it.colored())
                                            meta.lore = lore
                                            item.itemMeta = meta
                                        }
                                        
                                        onFinal { 
                                            editLoreWithGUI(item, player)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Status.REMOVE -> {
                        event.currentItem?.itemMeta?.displayName?.let { displayName ->
                            item.itemMeta?.let { meta ->
                                meta.lore?.let { lore ->
                                    val index = displayName.substring(2).toInt()
                                    lore.removeAt(index)
                                    meta.lore = lore
                                    item.itemMeta = meta
                                    player.sendPrefixedLangText("menu-editor-item-lore-remove")
                                    player.closeInventory()

                                    editLoreWithGUI(item, player)
                                }
                            }
                        }
                    }
                }
            }

            onClick { event ->
                if (status == Status.ADD) {
                    val currentItem = event.currentItem
                    if (currentItem == null || currentItem.type == Material.AIR) {
                        val meta = item.getMeta()
                        val lore = meta.lore ?: mutableListOf()
                        
                        player.openChatEditor<Text>(player.getLangText("menu-editor-item-lore-add")) { 
                            onSubmit {
                                lore += it.colored()
                                meta.lore = lore
                                item.itemMeta = meta
                            }
                            
                            onFinal { 
                                editLoreWithGUI(item, player)
                            }
                        }
                    }
                }
            }

            set(2 orderWith 1, ShiningIcon.BACK.item) {
                editItem(item, player)
            }

            set(5 orderWith 1, ShiningIcon.REMOVE_MODE.item) {
                currentItem?.let {
                    if (status == Status.REMOVE) {
                        status = Status.EDIT
                        currentItem = ShiningIcon.REMOVE_MODE.item
                    } else if (status == Status.EDIT) {
                        status = Status.REMOVE
                        currentItem = ShiningIcon.REMOVE_MODE_SHINY.item
                    }

                    player.updateInventory()
                }
            }

            set(8 orderWith 1, ShiningIcon.ADD_MODE.item) {
                currentItem?.let {
                    if (status == Status.ADD) {
                        status = Status.EDIT
                        currentItem = ShiningIcon.ADD_MODE.item
                    } else if (status == Status.EDIT) {
                        status = Status.ADD
                        currentItem = ShiningIcon.ADD_MODE_SHINY.item
                    }

                    player.updateInventory()
                }
            }
        }
    }


    enum class Status {
        EDIT,
        ADD,
        REMOVE
    }

}