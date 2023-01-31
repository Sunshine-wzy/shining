package io.github.sunshinewzy.shining.commands

import io.github.sunshinewzy.shining.core.editor.chat.ChatEditor
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand

internal object CommandEditor {
    
    val editor = subCommand {
        literal("chat") {
            literal("submit") {
                execute<Player> { sender, context, argument ->
                    ChatEditor.submit(sender)
                }
            }

            literal("cancel") {
                execute<Player> { sender, context, argument ->
                    ChatEditor.cancel(sender)
                }
            }
        }
    }
    
}