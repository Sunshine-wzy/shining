package io.github.sunshinewzy.sunstcore.exceptions

import org.bukkit.inventory.ItemStack

class NoRecipeException(item: ItemStack) : RuntimeException(
    "$item doesn't have a recipe"
)