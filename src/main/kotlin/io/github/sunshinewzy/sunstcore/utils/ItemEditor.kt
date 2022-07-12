package io.github.sunshinewzy.sunstcore.utils

import io.github.sunshinewzy.sunstcore.SunSTCore
import io.github.sunshinewzy.sunstcore.listeners.ChatListener
import io.github.sunshinewzy.sunstcore.modules.guide.SGuide
import io.github.sunshinewzy.sunstcore.objects.SItem
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.getLore
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.setName
import io.github.sunshinewzy.sunstcore.objects.item.SunSTIcon
import io.github.sunshinewzy.sunstcore.objects.orderWith
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.util.sync
import taboolib.module.chat.colored
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked

object ItemEditor {
    val wools = arrayListOf(Material.LIME_WOOL, Material.YELLOW_WOOL, Material.LIGHT_BLUE_WOOL, Material.PINK_WOOL, Material.ORANGE_WOOL, Material.WHITE_WOOL, Material.MAGENTA_WOOL, Material.CYAN_WOOL, Material.LIGHT_GRAY_WOOL, Material.PURPLE_WOOL, Material.BROWN_WOOL, Material.BLUE_WOOL, Material.GREEN_WOOL, Material.GRAY_WOOL, Material.RED_WOOL, Material.BLACK_WOOL)
    

    fun editItem(item: ItemStack, player: Player) {
        player.openMenu<Basic>("物品编辑器") {
            rows(3)

            map(
                "oooxxxxxx",
                "o oxaxxbx",
                "oooxxxxxx"
            )

            set('o', SunSTIcon.EDGE.item)
            set('x', SunSTIcon.EDGE_GLASS_PANE.item)
            set('a', SunSTIcon.RENAME.item)
            set('b', SunSTIcon.EDIT_LORE.item)

            val editItemOrder = 2 orderWith 2

            onBuild { _, inventory ->
                inventory.setItem(editItemOrder, item)
            }

            onClick('a') { event ->
                ChatListener.registerPlayerChatSubscriber(PlayerChatSubscriber(player) {
                    if(message == ".") {
                        player.sendMsg(SunSTCore.prefixName, "&6物品名称编辑已取消")
                    } else {
                        item.setName(message)
                        player.sendMsg(SunSTCore.prefixName, "&a物品名称编辑成功")
                    }

                    true
                })

                player.sendMessage("§f[${SunSTCore.prefixName}§f] 请输入新的物品名称 (可用'§a&§f'表示颜色, 输入'§c.§f'以取消)")
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
            rows(6)
            slots(SGuide.slotOrders)

            elements { item.getLore() }

            var iterator = wools.iterator()
            onGenerate { _, element, index, _ ->
                if(!iterator.hasNext()) iterator = wools.iterator()
                SItem(iterator.next(), "&f${page * 36 + index}", element)
            }

            onBuild(onBuild = SGuide.onBuildEdge)

            setPreviousPage(2 orderWith 6) { page, hasPreviousPage ->
                if(hasPreviousPage) {
                    SunSTIcon.PAGE_PRE_GLASS_PANE.item
                } else SunSTIcon.EDGE.item
            }

            setNextPage(8 orderWith 6) { page, hasNextPage ->
                if(hasNextPage) {
                    SunSTIcon.PAGE_NEXT_GLASS_PANE.item
                } else SunSTIcon.EDGE.item
            }
            
            
            var status = Status.EDIT
            onClick onClickLore@{ event, element ->
                when(status) {
                    Status.EDIT -> {
                        ChatListener.registerPlayerChatSubscriber(PlayerChatSubscriber(player) {
                            val message = message
                            if(message == ".") {
                                player.sendMsg(SunSTCore.prefixName, "&6物品Lore编辑已取消")
                            } else {
                                event.currentItem?.itemMeta?.displayName?.let { displayName ->
                                    item.itemMeta?.let { meta ->
                                        meta.lore?.let { lore ->
                                            val index = displayName.substring(2).toInt()
                                            lore[index] = message.colored()
                                            meta.lore = lore
                                            item.itemMeta = meta
                                            player.sendMsg(SunSTCore.prefixName, "&a物品Lore编辑成功")

                                            sync {
                                                editLore(item, player)
                                            }
                                        }
                                    }
                                }
                            }

                            true
                        })

                        player.sendMessage("§f[${SunSTCore.prefixName}§f] 请输入新的Lore (可用'§a&§f'表示颜色, 输入'§c.§f'以取消)")
                        player.closeInventory()
                    }
                    
                    Status.ADD -> {
                        ChatListener.registerPlayerChatSubscriber(PlayerChatSubscriber(player) {
                            val message = message
                            if(message == ".") {
                                player.sendMsg(SunSTCore.prefixName, "&6物品Lore编辑已取消")
                            } else {
                                event.currentItem?.itemMeta?.displayName?.let { displayName ->
                                    item.itemMeta?.let { meta ->
                                        meta.lore?.let { lore ->
                                            val index = displayName.substring(2).toInt()
                                            lore[index] = message.colored()
                                            meta.lore = lore
                                            item.itemMeta = meta
                                            player.sendMsg(SunSTCore.prefixName, "&a物品Lore编辑成功")

                                            sync {
                                                editLore(item, player)
                                            }
                                        }
                                    }
                                }
                            }

                            true
                        })

                        player.playSound(player.location, Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 0.8f)
                    }
                    
                    Status.REMOVE -> {
                        
                    }
                }
            }
            
            set(2 orderWith 1, SunSTIcon.BACK_LAST_PAGE.item) {
                editItem(item, player)
            }
            
            set(5 orderWith 1, SunSTIcon.REMOVE_MODE.item) {
                currentItem?.let {
                    if(status == Status.REMOVE) {
                        status = Status.EDIT
                        currentItem = SunSTIcon.REMOVE_MODE.item
                    } else {
                        status = Status.REMOVE
                        currentItem = SunSTIcon.REMOVE_MODE_SHINY.item
                    }
                    
                    player.updateInventory()
                }
            }
            
            set(8 orderWith 1, SunSTIcon.ADD_MODE.item) {
                currentItem?.let {
                    if(status == Status.ADD) {
                        status = Status.EDIT
                        currentItem = SunSTIcon.REMOVE_MODE.item
                    } else {
                        status = Status.ADD
                        currentItem = SunSTIcon.REMOVE_MODE_SHINY.item
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