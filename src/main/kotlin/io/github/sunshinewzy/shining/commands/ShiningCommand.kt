package io.github.sunshinewzy.shining.commands

import io.github.sunshinewzy.shining.Shining.COLOR_NAME
import io.github.sunshinewzy.shining.core.data.DataManager
import io.github.sunshinewzy.shining.core.machine.legacy.SMachineWrench
import io.github.sunshinewzy.shining.objects.SItem
import io.github.sunshinewzy.shining.objects.menu.SunSTMenu
import io.github.sunshinewzy.shining.utils.giveItem
import io.github.sunshinewzy.shining.utils.isItemSimilar
import io.github.sunshinewzy.shining.utils.sendMsg
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.mainCommand
import taboolib.expansion.createHelper

@CommandHeader("shining", aliases = ["shi"], permission = "shining.command", permissionDefault = PermissionDefault.TRUE)
object ShiningCommand {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody(permission = "shining.command.item", permissionDefault = PermissionDefault.OP)
    val item = CommandItem.item

    @CommandBody(permission = "shining.command.guide", permissionDefault = PermissionDefault.TRUE)
    val guide = CommandGuide.guide
    
    @CommandBody(permission = "shining.command.dictionary", permissionDefault = PermissionDefault.OP)
    val dictionary = CommandDictionary.dictionary

    @CommandBody(permission = "shining.command.machine", permissionDefault = PermissionDefault.OP)
    val machine = CommandMachine.machine
    

    fun registerSCommands() {
        SCommand("SunST", "sun")
            .addCommand("machine", "多方块机器") {
                "book" {
                    empty {
                        val player = getPlayer() ?: return@empty
                        val handItem = player.inventory.itemInMainHand

                        fun sendTip() {
                            sender.sendMsg("&c请拿着&a扳手&c输入此命令以获得该扳手可构建的机器图鉴！")
                        }

                        if (handItem.type == Material.AIR) {
                            sendTip()
                            return@empty
                        }

                        for (wrench in SMachineWrench.wrenches) {
                            if (handItem.isItemSimilar(wrench)) {
                                player.giveItem(wrench.illustratedBook)
                                player.sendMsg(
                                    COLOR_NAME,
                                    "&a您已获得 [${wrench.illustratedBook.itemMeta?.displayName}&a]"
                                )

                                return@empty
                            }
                        }

                        sendTip()
                    }
                }

                empty {
                    sender.sendMsg(COLOR_NAME, "&a拿着扳手输入 /sun machine book 即可获得该扳手可构建的机器图鉴！")
                }
            }

            .addCommand("give", "获得一个SunST物品", isOp = true) {
                SItem.items.keys {
                    empty {
                        if (sender !is Player) {
                            sender.sendMessage("只有玩家才能使用此命令")
                            return@empty
                        }

                        val items = SItem.items
                        if (items.containsKey(preArg)) {
                            val item = items[preArg] ?: return@empty
                            sender.giveItem(item)
                            sender.sendMsg(COLOR_NAME, "&a您已获得 $preArg")
                            return@empty
                        }
                    }
                }

                empty {
                    sender.sendMsg(COLOR_NAME, "&agive 后加SunST物品名称 (按 TAB 可以自动补全~)")
                }
            }

            .addCommand("reload", "重载配置文件", isOp = true) {
                empty {
                    DataManager.reloadData()
                    sender.sendMsg(COLOR_NAME, "&a配置文件重载成功！")
                }
            }

            .addCommand("item", "手持物品编辑", isOp = true) {
                empty {
                    val player = getPlayer() ?: return@empty
                    SunSTMenu.itemEditor.openInventory(player)
                }
            }

    }

}