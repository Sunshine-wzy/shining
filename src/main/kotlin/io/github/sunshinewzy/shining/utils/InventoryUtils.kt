package io.github.sunshinewzy.shining.utils

import io.github.sunshinewzy.shining.interfaces.Materialsable
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.function.BiPredicate

/**
 * 判断物品栏中是否含有 [amount] 数量的物品 [item]
 */
fun Inventory.containsVanillaItem(
    item: ItemStack,
    amount: Int = 1,
    predicate: BiPredicate<ItemStack, ItemStack> = BiPredicate { contentItem, targetItem ->
        contentItem.isItemSimilar(targetItem)
    }
): Boolean {
    if (amount <= 0) return true

    val theItem = item.clone()
    var cnt = theItem.amount * amount
    theItem.amount = 1

    storageContents.forEach {
        if (it == null) return@forEach

        if (predicate.test(it, theItem)) {
            cnt -= it.amount
            if (cnt <= 0) return true
        }
    }

    return false
}

fun Inventory.containsVanillaItem(
    items: Array<ItemStack>,
    predicate: BiPredicate<ItemStack, ItemStack> = BiPredicate { contentItem, targetItem ->
        contentItem.isItemSimilar(targetItem)
    }
): Boolean {
    items.forEach {
        if (!containsVanillaItem(it, predicate = predicate)) return false
    }
    return true
}

fun Inventory.containsVanillaItem(types: List<Material>, amount: Int = 1): Boolean {
    if (amount <= 0) return true
    var cnt = amount

    storageContents.forEach {
        if (it == null) return@forEach

        if (it.type in types) {
            cnt -= it.amount
            if (cnt <= 0) return true
        }
    }

    return false
}

fun Inventory.containsVanillaItem(types: Materialsable, amount: Int = 1): Boolean = containsVanillaItem(types.types(), amount)

/**
 * 移除物品栏中 [amount] 数量的物品 [item]
 */
fun Inventory.removeVanillaItem(
    item: ItemStack,
    amount: Int = 1,
    predicate: BiPredicate<ItemStack, ItemStack> = BiPredicate { contentItem, targetItem ->
        contentItem.isItemSimilar(targetItem)
    }
): Boolean {
    if (amount <= 0) return true

    val theItem = item.clone()
    var cnt = theItem.amount * amount
    theItem.amount = 1

    storageContents.forEach {
        if (it == null) return@forEach

        if (predicate.test(it, theItem)) {
            val theCnt = cnt
            cnt -= it.amount

            if (it.amount > theCnt) it.amount -= theCnt
            else it.amount = 0

            if (cnt <= 0) return true
        }
    }

    return false
}

fun Inventory.removeVanillaItem(
    items: Array<ItemStack>,
    predicate: BiPredicate<ItemStack, ItemStack> = BiPredicate { contentItem, targetItem ->
        contentItem.isItemSimilar(targetItem)
    }
): Boolean {
    items.forEach {
        if (!removeVanillaItem(it, predicate = predicate)) return false
    }
    return true
}

fun Inventory.removeVanillaItem(
    items: List<ItemStack>,
    predicate: BiPredicate<ItemStack, ItemStack> = BiPredicate { contentItem, targetItem ->
        contentItem.isItemSimilar(targetItem)
    }
): Boolean {
    items.forEach {
        if (!removeVanillaItem(it, predicate = predicate)) return false
    }
    return true
}

fun Inventory.removeVanillaItem(type: Material, amount: Int = 1): Boolean {
    if (amount <= 0) return true
    var cnt = amount

    storageContents.forEach {
        if (it == null) return@forEach

        if (it.type == type) {
            val theCnt = cnt
            cnt -= it.amount

            if (it.amount > theCnt) it.amount -= theCnt
            else it.amount = 0

            if (cnt <= 0) return true
        }
    }

    return false
}

fun Inventory.removeVanillaItem(types: List<Material>, amount: Int = 1): Boolean {
    if (amount <= 0) return true
    var cnt = amount

    storageContents.forEach {
        if (it == null) return@forEach

        if (it.type in types) {
            val theCnt = cnt
            cnt -= it.amount

            if (it.amount > theCnt) it.amount -= theCnt
            else it.amount = 0

            if (cnt <= 0) return true
        }
    }

    return false
}

fun Inventory.removeVanillaItem(types: Materialsable, amount: Int = 1): Boolean = removeVanillaItem(types.types(), amount)

fun Inventory.isFull(): Boolean = firstEmpty() == -1 || firstEmpty() > size
