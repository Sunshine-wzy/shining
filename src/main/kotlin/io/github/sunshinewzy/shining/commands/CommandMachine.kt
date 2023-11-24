package io.github.sunshinewzy.shining.commands

import io.github.sunshinewzy.shining.core.machine.creator.MachineCreator
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand

object CommandMachine {
    
    val machine = subCommand { 
        literal("project") {
            literal("last") {
                execute<Player> { sender, _, _ -> 
                    val structure = MachineCreator.getLastStructure(sender.uniqueId) ?: return@execute
                    structure.project(sender, sender.location, null)
                }
            }
        }
    }
    
}