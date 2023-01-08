package io.github.sunshinewzy.shining.core.lang

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class LocalizedItem(item: ItemStack, val localizedName: String) : ItemStack(item) {

    init {
        
    }
    
    constructor(item: ItemStack, amount: Int, localizedName: String) : this(item, localizedName) {
        this.amount = amount
    }
    constructor(type: Material, localizedName: String) : this(ItemStack(type), localizedName)
    constructor(type: Material, amount: Int, localizedName: String) : this(ItemStack(type, amount), localizedName)
    constructor(type: Material, damage: Short, amount: Int, localizedName: String) : this(ItemStack(type, amount, damage), localizedName)
    
    
}