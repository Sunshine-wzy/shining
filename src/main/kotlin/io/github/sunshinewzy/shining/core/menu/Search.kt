package io.github.sunshinewzy.shining.core.menu

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.type.Linked
import taboolib.platform.util.isNotAir
import java.util.*

open class Search<T>(title: String) : Linked<T>(title) {
    private var searchMap: () -> Map<String, T> = { hashMapOf() }
    private var searchMapCache: Map<String, T> = emptyMap()
    private var searchText: String = ""
    
    
    fun searchMap(searchMap: () -> Map<String, T>) {
        this.searchMap = searchMap
    }
    
    fun search(searchText: String) {
        this.searchText = searchText
    }
    
    fun open(player: Player) {
        page(0)
        player.openInventory(build())
        player.updateInventory()
    }


    override fun build(): Inventory {
        searchMapCache = searchMap()
        
        if(searchText != "") {
            val list = LinkedList<T>()
            searchMapCache.forEach { (key, value) ->
                if(key.contains(searchText, true)) {
                    list += value
                }
            }
            
            elements { list }
        } else {
            elements { searchMapCache.values.toList() }
        }
        
        return super.build()
    }
    
    
    companion object {
        val allItemMap: Map<String, ItemStack> = Material.values().filter { it.isNotAir() && it.isItem }.associate { 
            it.name to ItemStack(it)
        }
    }
}