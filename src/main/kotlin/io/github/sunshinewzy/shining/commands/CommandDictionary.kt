package io.github.sunshinewzy.shining.commands

import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.dictionary.DictionaryRegistry
import io.github.sunshinewzy.shining.utils.giveItem
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.command.player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggest
import taboolib.expansion.createHelper

object CommandDictionary {
    
    val dictionary = subCommand { 
        literal("give") {
            dynamic("name") {
                suggest { 
                    DictionaryRegistry.getItems().map { it.key.toString() }
                }
                
                player(optional = true) {
                    execute<Player> { _, context, argument ->
                        val thePlayer = Bukkit.getPlayerExact(argument) ?: return@execute
                        val theName = NamespacedId.fromString(context["name"]) ?: return@execute
                        val theItem = DictionaryRegistry.get(theName) ?: return@execute
                        thePlayer.giveItem(theItem.getItemStack())
                    }
                }

                execute<Player> { sender, _, argument ->
                    val theName = NamespacedId.fromString(argument) ?: return@execute
                    val theItem = DictionaryRegistry.get(theName) ?: return@execute
                    sender.giveItem(theItem.getItemStack())
                }
            }
        }
        
        createHelper()
    }
    
}