package io.github.sunshinewzy.sunstcore.commands

import io.github.sunshinewzy.sunstcore.SunSTCore.colorName
import io.github.sunshinewzy.sunstcore.interfaces.Initable
import io.github.sunshinewzy.sunstcore.modules.data.DataManager
import io.github.sunshinewzy.sunstcore.modules.machine.SMachineWrench
import io.github.sunshinewzy.sunstcore.objects.SItem
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.isItemSimilar
import io.github.sunshinewzy.sunstcore.objects.sunst.SunSTMenu
import io.github.sunshinewzy.sunstcore.utils.giveItem
import io.github.sunshinewzy.sunstcore.utils.sendMsg
import org.bukkit.Material
import org.bukkit.entity.Player

object SunSTCommand : Initable {
    
    override fun init() {
        SCommand("SunST", "sun")
            .addCommand("machine", "多方块机器") {
                "book" {
                    empty {
                        val player = getPlayer() ?: return@empty
                        val handItem = player.inventory.itemInMainHand

                        fun sendTip() {
                            sender.sendMsg("&c请拿着&a扳手&c输入此命令以获得该扳手可构建的机器图鉴！")
                        }
                        
                        if(handItem.type == Material.AIR) {
                            sendTip()
                            return@empty
                        }

                        for(wrench in SMachineWrench.wrenches) {
                            if(handItem.isItemSimilar(wrench)) {
                                player.giveItem(wrench.illustratedBook)
                                player.sendMsg(colorName, "&a您已获得 [${wrench.illustratedBook.itemMeta?.displayName}&a]")
                                
                                return@empty
                            }
                        }
                        
                        sendTip()
                    }
                }

                empty {
                    sender.sendMsg(colorName, "&a拿着扳手输入 /sun machine book 即可获得该扳手可构建的机器图鉴！")
                }
            }
                
            .addCommand("give", "获得一个SunST物品", isOp = true) {
                SItem.items.keys {
                    empty {
                        if(sender !is Player){
                            sender.sendMessage("只有玩家才能使用此命令")
                            return@empty
                        }

                        val items = SItem.items
                        if(items.containsKey(preArg)){
                            val item = items[preArg] ?: return@empty
                            sender.giveItem(item)
                            sender.sendMsg(colorName, "&a您已获得 $preArg")
                            return@empty
                        }
                    }
                }
                
                empty {
                    sender.sendMsg(colorName,"&agive 后加SunST物品名称 (按 TAB 可以自动补全~)")
                }
            }
                
            .addCommand("reload", "重载配置文件", isOp = true) {
                empty {
                    DataManager.reloadData()
                    sender.sendMsg(colorName, "&a配置文件重载成功！")
                }
            }
        
            .addCommand("group", "组", isOp = true) {
                
                
                empty {  
                    
                }
            }
            
            .addCommand("item", "手持物品编辑", isOp = true) {
                empty { 
                    val player = getPlayer() ?: return@empty
                    SunSTMenu.itemEdit.openInventory(player)
                }
            }
            
    }
    
}