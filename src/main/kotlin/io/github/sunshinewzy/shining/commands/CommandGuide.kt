package io.github.sunshinewzy.shining.commands

import io.github.sunshinewzy.shining.core.guide.GuideTeam.Companion.letGuideTeamOrWarn
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.submit

internal object CommandGuide {
    
    val guide = subCommand {
        literal("open") {
            literal("main") {
                execute<Player> { sender, context, argument ->
                    ShiningGuide.openMainMenu(sender)
                }
            }

            literal("team") {
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
    }
    
}