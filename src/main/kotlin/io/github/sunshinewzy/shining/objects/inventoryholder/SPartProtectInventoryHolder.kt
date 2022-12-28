package io.github.sunshinewzy.shining.objects.inventoryholder

open class SPartProtectInventoryHolder<T>(val allowClickSlots: MutableList<Int>, data: T) : SInventoryHolder<T>(data)