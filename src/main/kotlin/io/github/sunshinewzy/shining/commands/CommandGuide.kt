package io.github.sunshinewzy.shining.commands

import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.element.GuideElementRegistry
import io.github.sunshinewzy.shining.core.guide.settings.ShiningGuideSettings
import io.github.sunshinewzy.shining.core.guide.team.GuideTeam.Companion.letGuideTeamOrWarn
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.submit
import taboolib.expansion.createHelper

internal object CommandGuide {
    
    const val PERMISSION_RELOAD = "shining.command.guide.reload"
    
    
    val guide = subCommand {
        literal("open") {
            literal("main") {
                execute<Player> { sender, context, argument ->
                    ShiningGuide.openMainMenu(sender)
                }
            }

            literal("team", permission = ShiningGuideSettings.PERMISSION_TEAM) {
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
            execute<CommandSender> { _, _, _ ->
                ShiningDispatchers.launchDB { 
                    GuideElementRegistry.reload()
                }
            }
        }
        
        createHelper()
    }

}