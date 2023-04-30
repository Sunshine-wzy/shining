package io.github.sunshinewzy.shining.commands

import io.github.sunshinewzy.shining.core.editor.chat.ChatEditor
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand

internal object CommandEditor {

    val editor = subCommand {
        literal("chat") {
            literal("submit") {
                execute<Player> { sender, _, _ ->
                    ChatEditor.submit(sender)
                }
            }

            literal("cancel") {
                execute<Player> { sender, _, _ ->
                    ChatEditor.cancel(sender)
                }
            }

            literal("mode") {
                dynamic {
                    execute<Player> { sender, _, argument ->
                        ChatEditor.getSession(sender)?.mode(sender, argument)
                    }
                }
            }
            
            literal("update") {
                execute<Player> { sender, _, _ -> 
                    ChatEditor.update(sender)
                }
            }
        }
    }

}