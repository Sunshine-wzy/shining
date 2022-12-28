package io.github.sunshinewzy.shining.objects.inventoryholder

import io.github.sunshinewzy.shining.objects.SCraftRecipe

class SCraftInventoryHolder<T>(
    allowClickSlots: MutableList<Int>,
    val outputSlot: Int,
    data: T,
    var recipe: SCraftRecipe? = null
) : SPartProtectInventoryHolder<T>(allowClickSlots, data)