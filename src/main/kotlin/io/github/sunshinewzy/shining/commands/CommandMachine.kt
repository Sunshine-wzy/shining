package io.github.sunshinewzy.shining.commands

import com.fasterxml.jackson.module.kotlin.readValue
import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.machine.structure.IMachineStructure
import io.github.sunshinewzy.shining.core.machine.creator.MachineCreator
import io.github.sunshinewzy.shining.utils.giveItem
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.info

object CommandMachine {
    
    val machine = subCommand { 
        literal("creator") {
            execute<Player> { sender, _, _ -> 
                sender.giveItem(MachineCreator.creatorItem.getItemStack())
            }
        }
        
        literal("project") {
            literal("last") {
                execute<Player> { sender, _, _ -> 
                    val structure = MachineCreator.getLastStructure(sender.uniqueId) ?: return@execute
                    val str = Shining.objectMapper.writeValueAsString(structure)
                    info(str)
                    Shining.objectMapper.readValue<IMachineStructure>(str).project(sender, sender.location, null)
                }
            }
        }
    }
    
}