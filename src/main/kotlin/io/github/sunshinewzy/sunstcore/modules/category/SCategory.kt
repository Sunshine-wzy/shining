package io.github.sunshinewzy.sunstcore.modules.category

import io.github.sunshinewzy.sunstcore.interfaces.SPlugin
import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

/**
 * Represent a category, which is showed in the guide.
 * 
 * @param key to identify this [SCategory]
 * @param displayItem to display this [SCategory] in guide
 * @param tier higher tier will make this [SCategory] appear further down in the guide
 */
class SCategory(
    private val key: NamespacedKey,
    val displayItem: ItemStack,
        val tier: Int
) : Keyed {
    private lateinit var plugin: SPlugin
    
    
    constructor(key: NamespacedKey, displayItem: ItemStack) : this(key, displayItem, 5)
    
    override fun getKey(): NamespacedKey = key
    
    
    fun register(plugin: SPlugin) {
        if(isRegistered()) throw UnsupportedOperationException("This Category has already been registered!")
        
        this.plugin = plugin
        SPlugin.categories += this
    }
    
    fun isRegistered(): Boolean = SPlugin.categories.contains(this)
    
}