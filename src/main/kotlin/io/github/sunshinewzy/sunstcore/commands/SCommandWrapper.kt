package io.github.sunshinewzy.sunstcore.commands

import io.github.sunshinewzy.sunstcore.commands.SCommandWrapper.Type.EMPTY
import io.github.sunshinewzy.sunstcore.commands.SCommandWrapper.Type.NORMAL
import io.github.sunshinewzy.sunstcore.utils.copy
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

typealias SCWrapper = SCommandWrapper.() -> Unit

class SCommandWrapper(
    val sender: CommandSender,
    val cmd: Command,
    val label: String,
    val args: LinkedList<String>,
    val preArg: String,
    val isTabCompleter: Boolean = false,
    val complements: ArrayList<String> = ArrayList()
) {
    
    private fun wrap(name: String, wrapper: SCWrapper, type: Type = NORMAL): Boolean {
        if(isTabCompleter) {
            if(args.size == 1) {
                val first = args.first()
                if(first == "") {
                    complements += name
                } else if(name.indexOf(first, ignoreCase = true) == 0) {
                    complements += name
                }
            }
            
            if(type == EMPTY)
                return false
        }
        
        
        when(type) {
            NORMAL -> {
                if(args.isEmpty() || !args.first().equals(name, true))
                    return false
            }

            EMPTY -> {
                if(args.isNotEmpty() && args.first() != "")
                    return false
            }

        }
        
        var pre = preArg
        val list = args.copy()
        if(type != EMPTY) pre = list.removeFirst()
        
        val scWrapper = SCommandWrapper(sender, cmd, label, list, pre, isTabCompleter, complements)
        wrapper(scWrapper)
        return true
    }
    
    
    operator fun String.invoke(wrapper: SCWrapper) {
        wrap(this, wrapper)
    }
    
    operator fun Collection<String>.invoke(wrapper: SCWrapper) {
        forEach { 
            if(wrap(it, wrapper))
                return
        }
    }
    
    fun empty(wrapper: SCWrapper) {
        wrap("", wrapper, EMPTY)
    }
    
    
    fun getPlayer(): Player? = sender as? Player


    enum class Type {
        NORMAL,
        EMPTY
    }
    
}