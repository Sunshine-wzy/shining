package io.github.sunshinewzy.shining.objects

import io.github.sunshinewzy.shining.interfaces.Itemable
import io.github.sunshinewzy.shining.objects.SItem.Companion.cloneRandomAmount
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class SRandomItems(val items: List<SRandomItem>) {
    
    constructor(vararg items: SRandomItem) : this(items.toList())
    
    
    
    fun takeAll(): List<ItemStack> {
        val list = ArrayList<ItemStack>()
        
        items.forEach {
            val randInt = Random.nextInt(100) + 1
            
            if(randInt in 1..it.percent) {
                list += it.getItemsByRandom()
            }
        }
        
        return list
    }
    
    fun takeOne(): List<ItemStack> {
        val randInt = Random.nextInt(100) + 1
        
        var cnt = 0
        items.forEach { 
            if(randInt in cnt..(cnt + it.percent)) {
                return it.getItemsByRandom()
            }
            cnt += it.percent
        }
        
        return emptyList()
    }
    
    
    fun takeAllFirst(): List<ItemStack> {
        val list = ArrayList<ItemStack>()

        items.forEach {
            val randInt = Random.nextInt(100) + 1

            if(randInt in 1..it.percent) {
                list += it.getItemByRandom()
            }
        }

        return list
    }
    
    fun takeOneFirst(): ItemStack {
        val randInt = Random.nextInt(100) + 1

        var cnt = 0
        items.forEach {
            if(randInt in cnt..(cnt + it.percent)) {
                return it.getItemByRandom()
            }
            cnt += it.percent
        }

        return SItem(Material.AIR)
    }
    
    
    
    companion object {

        fun Array<ItemStack>.randItem(): ItemStack = random()
        
    }
    
}

/**
 * @param percent 表示概率(0% - 100%)
 * @param items 表示有 [percent] 的概率抽取的物品(组)
 */
class SRandomItem(val percent: Int, val items: List<ItemStack>) {
    private var randomAmount = DEFAULT_RANDOM_AMOUNT
    
    
    constructor(percent: Int, item: ItemStack) : this(percent, listOf(item))
    
    constructor(percent: Int, item: Itemable) : this(percent, item.getSItem())
    
    
    fun getItemByRandom(): ItemStack {
        if(hasRandomAmount())
            return items.first().cloneRandomAmount(randomAmount.first, randomAmount.second)
        return items.first()
    }
    
    fun getItemsByRandom(): List<ItemStack> {
        if(hasRandomAmount()) {
            val list = arrayListOf<ItemStack>()
            items.forEach {
                list += it.cloneRandomAmount(randomAmount.first, randomAmount.second)
            }
            return list
        }
        
        return items
    }
    
    fun setRandomAmount(range: Pair<Int, Int>): SRandomItem {
        randomAmount = range
        return this
    }
    
    fun setRandomAmount(start: Int, end: Int): SRandomItem = setRandomAmount(start to end)
    
    fun setRandomAmount(end: Int): SRandomItem = setRandomAmount(1 to end)
    
    fun hasRandomAmount(): Boolean = randomAmount != DEFAULT_RANDOM_AMOUNT
    
    
    init {
        require(percent in 0..100) {
            "The percent should be between 0 and 100."
        }
        
        require(items.isNotEmpty()) {
            "Items must have one item in it."
        }
    }
    
    companion object {
        val DEFAULT_RANDOM_AMOUNT = 1 to 1
    }
    
}