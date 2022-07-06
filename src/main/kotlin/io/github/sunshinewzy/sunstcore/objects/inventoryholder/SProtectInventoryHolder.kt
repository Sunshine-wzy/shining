package io.github.sunshinewzy.sunstcore.objects.inventoryholder

/**
 * 使用此类作为InventoryHolder的Inventory会自动带有点击保护(取消玩家点击物品的事件)
 */
open class SProtectInventoryHolder<T>(data: T) : SInventoryHolder<T>(data)