package io.github.sunshinewzy.sunstcore.objects.inventoryholder

import io.github.sunshinewzy.sunstcore.objects.SCraftRecipe

class SCraftInventoryHolder<T>(
    allowClickSlots: MutableList<Int>,
    val outputSlot: Int,
    data: T,
    var recipe: SCraftRecipe? = null
) : SPartProtectInventoryHolder<T>(allowClickSlots, data)