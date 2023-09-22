package io.github.sunshinewzy.shining.commands

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.ElementCondition
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.element.GuideElementRegistry
import io.github.sunshinewzy.shining.core.guide.team.GuideTeam.Companion.getGuideTeam
import io.github.sunshinewzy.shining.core.guide.team.GuideTeam.Companion.letGuideTeamOrWarn
import io.github.sunshinewzy.shining.core.lang.sendPrefixedLangText
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import io.github.sunshinewzy.shining.utils.sendMsg
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggest
import taboolib.common.platform.function.submit
import taboolib.expansion.createHelper

object CommandGuide {

    const val PERMISSION_EDIT = "shining.guide.edit"
    const val PERMISSION_TEAM = "shining.guide.team"
    const val PERMISSION_OPEN_TEAM = "shining.guide.open.team"
    const val PERMISSION_RELOAD = "shining.command.guide.reload"
    
    
    val guide = subCommand {
        literal("open") {
            literal("main") {
                execute<Player> { sender, context, argument ->
                    ShiningGuide.openMainMenu(sender)
                }
            }

            literal("team", permission = PERMISSION_OPEN_TEAM) {
                literal("manage") {
                    execute<Player> { sender, context, argument ->
                        sender.letGuideTeamOrWarn { team ->
                            submit {
                                team.openManageMenu(sender)
                            }
                        }
                    }
                }

                execute<Player> { sender, context, argument ->
                    sender.letGuideTeamOrWarn { team ->
                        submit {
                            team.openInfoMenu(sender)
                        }
                    }
                }
            }

            execute<Player> { sender, context, argument ->
                ShiningGuide.openLastElement(sender)
            }
        }
        
        literal("reload", permission = PERMISSION_RELOAD) {
            execute<CommandSender> { sender, _, _ ->
                ShiningDispatchers.launchDB { 
                    GuideElementRegistry.reload()
                    sender.sendPrefixedLangText("text-shining_guide-reload-success")
                }
            }
        }
        
        literal("team", permission = PERMISSION_TEAM) {
            player { 
                literal("data") {
                    literal("element") {
                        dynamic("element") { 
                            suggest { GuideElementRegistry.getElementCache().map { it.key.toString() } }
                            
                            literal("condition") {
                                literal("set") {
                                    dynamic("condition") { 
                                        suggest { ElementCondition.values().map { it.name } }
                                        
                                        execute<Player> { sender, context, argument -> 
                                            ShiningDispatchers.launchDB { 
                                                val theId = NamespacedId.fromString(context["element"]) ?: return@launchDB
                                                val theElement = GuideElementRegistry.getElement(theId) ?: return@launchDB
                                                val thePlayer = Bukkit.getPlayer(context["player"]) ?: return@launchDB
                                                val theTeam = thePlayer.getGuideTeam() ?: return@launchDB
                                                val theCondition = ElementCondition.valueOf(context["condition"])
                                                
                                                theTeam.getTeamData().setElementCondition(theElement, theCondition)
                                                theTeam.updateTeamData()
                                            }
                                        }
                                    }
                                }
                                
                                literal("remove") {
                                    execute<Player> { sender, context, argument ->
                                        ShiningDispatchers.launchDB {
                                            val theId = NamespacedId.fromString(context["element"]) ?: return@launchDB
                                            val thePlayer = Bukkit.getPlayer(context["player"]) ?: return@launchDB
                                            val theTeam = thePlayer.getGuideTeam() ?: return@launchDB

                                            theTeam.getTeamData().elementConditionMap.remove(theId)
                                            theTeam.updateTeamData()
                                        }
                                    }
                                }
                                
                                literal("info") {
                                    execute<Player> { sender, context, argument ->
                                        ShiningDispatchers.launchDB {
                                            val theId = NamespacedId.fromString(context["element"]) ?: return@launchDB
                                            val thePlayer = Bukkit.getPlayer(context["player"]) ?: return@launchDB
                                            val theTeam = thePlayer.getGuideTeam() ?: return@launchDB

                                            val elementCondition = theTeam.getTeamData().elementConditionMap[theId] ?: return@launchDB
                                            sender.sendMsg(Shining.prefix, "$theId -> $elementCondition")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        createHelper()
    }
    
}