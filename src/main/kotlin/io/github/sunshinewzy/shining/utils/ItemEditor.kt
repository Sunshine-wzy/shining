package io.github.sunshinewzy.shining.utils

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.core.menu.MenuBuilder.buildMultiPage
import io.github.sunshinewzy.shining.objects.SItem
import io.github.sunshinewzy.shining.objects.SItem.Companion.getLore
import io.github.sunshinewzy.shining.objects.SItem.Companion.getMeta
import io.github.sunshinewzy.shining.objects.SItem.Companion.setName
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit
import taboolib.module.chat.colored
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked

object ItemEditor {
    val wools = arrayListOf(Material.LIME_WOOL, Material.YELLOW_WOOL, Material.LIGHT_BLUE_WOOL, Material.PINK_WOOL, Material.ORANGE_WOOL, Material.WHITE_WOOL, Material.MAGENTA_WOOL, Material.CYAN_WOOL, Material.LIGHT_GRAY_WOOL, Material.PURPLE_WOOL, Material.BROWN_WOOL, Material.BLUE_WOOL, Material.GREEN_WOOL, Material.GRAY_WOOL, Material.RED_WOOL, Material.BLACK_WOOL)

    val editItemOrder = 2 orderWith 2
    

    fun editItem(item: ItemStack, player: Player) {
        player.openMenu<Basic>("物品编辑器") {
            rows(3)

            map(
                "oooxxxxxx",
                "o oxaxxbx",
                "oooxxxxxx"
            )

            set('o', ShiningIcon.EDGE.item)
            set('x', ShiningIcon.EDGE_GLASS_PANE.item)
            set('a', ShiningIcon.RENAME.item)
            set('b', ShiningIcon.EDIT_LORE.item)

            onBuild { _, inventory ->
                inventory.setItem(editItemOrder, item)
            }

            onClick('a') { event ->
                PlayerChatSubscriber(player, "物品名称编辑") {
                    item.setName(message)
                    player.sendMsg(Shining.prefixName, "&a物品名称编辑成功")

                    submit {
                        editItem(item, player)
                    }
                    true
                }.register()

                player.sendMessage("§f[${Shining.prefixName}§f] 请输入新的物品名称 (可用'§a&§f'表示颜色, 输入'§c.§f'以取消)")
                player.closeInventory()
            }

            onClick('b') { event ->
                editLore(item, event.clicker)
            }
            
            onClick(lock = true)
        }
    }
    
    fun editLore(item: ItemStack, player: Player) {
        player.openMenu<Linked<String>>("编辑 Lore") {
            buildMultiPage()
            
            elements { item.getLore() }

            var iterator = wools.iterator()
            onGenerate { _, element, index, _ ->
                if(!iterator.hasNext()) iterator = wools.iterator()
                SItem(iterator.next(), "&f${page * 36 + index}", element)
            }
            
            var status = Status.EDIT
            onClick onClickLore@{ event, element ->
                when(status) {
                    Status.EDIT -> {
                        event.currentItem?.itemMeta?.displayName?.let { displayName ->
                            PlayerChatSubscriber(player, "物品Lore编辑") {
                                item.itemMeta?.let { meta ->
                                    meta.lore?.let { lore ->
                                        val index = displayName.substring(2).toInt()
                                        lore[index] = message.colored()
                                        meta.lore = lore
                                        item.itemMeta = meta
                                        player.sendMsg(Shining.prefixName, "&a物品Lore编辑成功")

                                        submit {
                                            editLore(item, player)
                                        }
                                    }
                                }

                                true
                            }.register()

                            player.sendMessage("§f[${Shining.prefixName}§f] 请输入新的Lore (可用'§a&§f'表示颜色, 输入'§c.§f'以取消)")
                            player.closeInventory()
                        }
                    }
                    
                    Status.ADD -> {
                        event.currentItem?.itemMeta?.displayName?.let { displayName ->
                            PlayerChatSubscriber(player, "添加物品Lore") {
                                item.itemMeta?.let { meta ->
                                    meta.lore?.let { lore ->
                                        val index = displayName.substring(2).toInt()
                                        lore.add(index, message.colored())
                                        meta.lore = lore
                                        item.itemMeta = meta
                                        player.sendMsg(Shining.prefixName, "&a物品Lore添加成功")

                                        submit {
                                            editLore(item, player)
                                        }
                                    }
                                }

                                true
                            }.register()

                            player.sendMessage("§f[${Shining.prefixName}§f] 请输入要添加的Lore (可用'§a&§f'表示颜色, 输入'§c.§f'以取消)")
                            player.closeInventory()
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
                                    player.sendMsg(Shining.prefixName, "&a物品Lore删除成功")
                                    player.closeInventory()

                                    editLore(item, player)
                                }
                            }
                        }
                    } 
                }
            }
            
            onClick { event ->
                if(status == Status.ADD) {
                    val currentItem = event.currentItem
                    if(currentItem == null || currentItem.type == Material.AIR) {
                        PlayerChatSubscriber(player, "添加物品Lore") {
                            val meta = item.getMeta()
                            val lore = meta.lore ?: mutableListOf()
                            lore += message.colored()
                            meta.lore = lore
                            item.itemMeta = meta
                            player.sendMsg(Shining.prefixName, "&a物品Lore添加成功")

                            submit {
                                editLore(item, player)
                            }
                            
                            true
                        }.register()

                        player.sendMessage("§f[${Shining.prefixName}§f] 请输入要添加的Lore (可用'§a&§f'表示颜色, 输入'§c.§f'以取消)")
                        player.closeInventory()
                    }
                }
            }
            
            set(2 orderWith 1, ShiningIcon.BACK_LAST_PAGE.item) {
                editItem(item, player)
            }
            
            set(5 orderWith 1, ShiningIcon.REMOVE_MODE.item) {
                currentItem?.let {
                    if(status == Status.REMOVE) {
                        status = Status.EDIT
                        currentItem = ShiningIcon.REMOVE_MODE.item
                    } else if(status == Status.EDIT) {
                        status = Status.REMOVE
                        currentItem = ShiningIcon.REMOVE_MODE_SHINY.item
                    }
                    
                    player.updateInventory()
                }
            }
            
            set(8 orderWith 1, ShiningIcon.ADD_MODE.item) {
                currentItem?.let {
                    if(status == Status.ADD) {
                        status = Status.EDIT
                        currentItem = ShiningIcon.ADD_MODE.item
                    } else if(status == Status.EDIT) {
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