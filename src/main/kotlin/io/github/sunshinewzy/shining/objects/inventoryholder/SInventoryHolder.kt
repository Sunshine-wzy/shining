package io.github.sunshinewzy.shining.objects.inventoryholder

import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

open class SInventoryHolder<T>(var data: T) : InventoryHolder, Cloneable {
    private val inventory: Inventory = Bukkit.createInventory(this, 9)

    val extra: HashMap<String, Any> by lazy { hashMapOf() }

    var page: Int = 0
    var maxPage: Int = 0


    override fun getInventory(): Inventory {
        return inventory
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(CloneNotSupportedException::class)
    public override fun clone(): SInventoryHolder<T> {
        return super.clone() as SInventoryHolder<T>
    }


    inline fun <reified T> isData(target: T): Boolean {
        if (data is T) {
            if (data == target) {
                return true
            }
        }

        return false
    }

    fun getTruePage(): Int {
        if (page > 0) {
            if (maxPage in 1 until page) {
                page = 1
            }
        } else page = 1

        return page
    }

    fun nextPage(isLoop: Boolean = false): Int {
        getTruePage()

        if (page < maxPage) page++
        else if (isLoop) page = 1

        return page
    }

    fun prePage(isLoop: Boolean = false): Int {
        getTruePage()

        if (page > 1) page--
        else if (isLoop) page = maxPage

        return page
    }

    fun toPage(index: Int): Int {
        getTruePage()

        if (index in 1..maxPage) page = index

        return page
    }


    companion object {
        fun Inventory.getSHolder(): SInventoryHolder<*>? {
            if (holder !is SInventoryHolder<*>)
                return null

            return holder as SInventoryHolder<*>
        }
    }


    override fun hashCode(): Int {
        return data?.hashCode() ?: 0
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is SInventoryHolder<*>) return false

        if (data != other.data) return false

        return true
    }

}