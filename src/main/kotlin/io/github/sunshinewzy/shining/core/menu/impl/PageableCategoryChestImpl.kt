package io.github.sunshinewzy.shining.core.menu.impl

import io.github.sunshinewzy.shining.core.menu.PageableCategoryChest
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.common.util.subList
import taboolib.module.ui.ClickEvent
import taboolib.module.ui.type.impl.ChestImpl
import taboolib.module.ui.virtual.VirtualInventory
import taboolib.module.ui.virtual.inject
import taboolib.module.ui.virtual.openVirtualInventory
import taboolib.platform.util.buildItem
import taboolib.platform.util.isNotAir
import java.util.concurrent.CopyOnWriteArrayList

open class PageableCategoryChestImpl<C, T>(
    title: String
) : ChestImpl(title), PageableCategoryChest<C, T> {

    /** 页数 **/
    override var page = 0

    override var currentCategory: C? = null

    /** 页面玩家 **/
    lateinit var viewer: Player

    /** 锁定所有位置 **/
    var menuLocked = true

    /** 页面可用位置 **/
    val menuSlots = CopyOnWriteArrayList<Int>()

    /** 页面可用元素回调 **/
    var elementsCallback: ((category: C) -> List<T>) = { CopyOnWriteArrayList() }

    /** 页面可用元素缓存 **/
    var elementsCache = emptyList<T>()

    /** 点击事件回调 **/
    var elementClickCallback: ((event: ClickEvent, element: T) -> Unit) = { _, _ -> }

    /** 元素生成回调 **/
    var generateCallback: ((player: Player, element: T, index: Int, slot: Int) -> ItemStack) = { _, _, _, _ -> ItemStack(Material.AIR) }

    /** 异步元素生成回调 **/
    var asyncGenerateCallback: ((player: Player, element: T, index: Int, slot: Int) -> ItemStack) = { _, _, _, _ -> ItemStack(Material.AIR) }

    /** 页面切换回调 */
    var pageChangeCallback: ((player: Player) -> Unit) = { _ -> }
    
    override var categoryPage: Int = 0
    
    val categorySlots = CopyOnWriteArrayList<Int>()
    var categoryElementsCallback: (() -> List<C>) = { CopyOnWriteArrayList() }
    var categoryElementsCache = emptyList<C>()
    var categoryElementClickCallback: ((event: ClickEvent, element: C) -> Unit) = { _, _ -> }
    var categoryGenerateCallback: ((player: Player, element: C, index: Int, slot: Int) -> ItemStack) = { _, _, _, _ -> ItemStack(Material.AIR) }
    var categoryAsyncGenerateCallback: ((player: Player, element: C, index: Int, slot: Int) -> ItemStack) = { _, _, _, _ -> ItemStack(Material.AIR) }
    var categoryPageChangeCallback: ((player: Player) -> Unit) = { _ -> }


    /**
     * 是否锁定所有位置
     * 默认为 true
     */
    override fun menuLocked(lockAll: Boolean) {
        this.menuLocked = lockAll
    }

    /**
     * 设置页数
     */
    override fun page(page: Int) {
        this.page = page
    }

    /**
     * 设置可用位置
     */
    override fun slots(slots: List<Int>) {
        this.menuSlots.clear()
        this.menuSlots += slots
    }

    /**
     * 通过抽象字符选择由 map 函数铺设的页面位置
     */
    override fun slotsBy(char: Char) {
        slots(getSlots(char))
    }

    /**
     * 可用元素列表回调
     */
    override fun elements(elements: (category: C) -> List<T>) {
        elementsCallback = elements
    }

    /**
     * 元素对应物品生成回调
     */
    override fun onGenerate(async: Boolean, callback: (player: Player, element: T, index: Int, slot: Int) -> ItemStack) {
        if (async) {
            asyncGenerateCallback = callback
        } else {
            generateCallback = callback
        }
    }

    /**
     * 页面构建回调
     */
    override fun onBuild(async: Boolean, callback: (inventory: Inventory) -> Unit) {
        onBuild(async = async) { _, inventory -> callback(inventory) }
    }

    /**
     * 元素点击回调
     */
    override fun onClick(callback: (event: ClickEvent, element: T) -> Unit) {
        elementClickCallback = callback
    }

    /**
     * 设置下一页按钮
     */
    override fun setNextPage(slot: Int, callback: (page: Int, hasNextPage: Boolean) -> ItemStack) {
        // 设置物品
        set(slot) { callback(page, hasNextPage()) }
        // 点击事件
        onClick(slot) {
            if (hasNextPage()) {
                page++
                // 刷新页面
                if (virtualized) {
                    viewer.openVirtualInventory(build() as VirtualInventory).inject(this)
                } else {
                    viewer.openInventory(build())
                }
                pageChangeCallback(viewer)
            }
        }
    }

    /**
     * 设置上一页按钮
     */
    override fun setPreviousPage(slot: Int, callback: (page: Int, hasPreviousPage: Boolean) -> ItemStack) {
        // 设置物品
        set(slot) { callback(page, hasPreviousPage()) }
        // 点击事件
        onClick(slot) {
            if (hasPreviousPage()) {
                page--
                // 刷新页面
                if (virtualized) {
                    viewer.openVirtualInventory(build() as VirtualInventory).inject(this)
                } else {
                    viewer.openInventory(build())
                }
                pageChangeCallback(viewer)
            }
        }
    }

    /**
     * 切换页面回调
     */
    override fun onPageChange(callback: (player: Player) -> Unit) {
        pageChangeCallback = callback
    }

    /**
     * 是否可以返回上一页
     */
    override fun hasPreviousPage(): Boolean {
        return page > 0
    }

    /**
     * 是否可以前往下一页
     */
    override fun hasNextPage(): Boolean {
        return isNext(page, elementsCache.size, menuSlots.size)
    }

    override fun createTitle(): String {
        return title.replace("%p", (page + 1).toString())
    }

    override fun resetElementsCache(category: C) {
        currentCategory = category
        page = 0
        elementsCache = elementsCallback(category)
    }

    override fun currentCategory(category: C) {
        currentCategory = category
    }

    override fun categoryPage(page: Int) {
        categoryPage = page
    }

    override fun categorySlots(slots: List<Int>) {
        categorySlots.clear()
        categorySlots += slots
    }

    override fun categorySlotsBy(char: Char) {
        categorySlots(getSlots(char))
    }

    override fun categoryElements(elements: () -> List<C>) {
        categoryElementsCallback = elements
    }

    override fun onCategoryGenerate(async: Boolean, callback: (player: Player, element: C, index: Int, slot: Int) -> ItemStack) {
        if (async) categoryAsyncGenerateCallback = callback
        else categoryGenerateCallback = callback
    }

    override fun onCategoryClick(callback: (event: ClickEvent, element: C) -> Unit) {
        categoryElementClickCallback = callback
    }

    override fun setCategoryNextPage(slot: Int, callback: (page: Int, hasNextPage: Boolean) -> ItemStack) {
        set(slot) { callback(categoryPage, hasCategoryNextPage()) }
        onClick(slot) {
            if (hasCategoryNextPage()) {
                categoryPage++
                if (virtualized) {
                    viewer.openVirtualInventory(build() as VirtualInventory).inject(this)
                } else {
                    viewer.openInventory(build())
                }
                categoryPageChangeCallback(viewer)
            }
        }
    }

    override fun setCategoryPreviousPage(slot: Int, callback: (page: Int, hasPreviousPage: Boolean) -> ItemStack) {
        set(slot) { callback(categoryPage, hasCategoryPreviousPage()) }
        onClick(slot) {
            if (hasCategoryPreviousPage()) {
                categoryPage--
                if (virtualized) {
                    viewer.openVirtualInventory(build() as VirtualInventory).inject(this)
                } else {
                    viewer.openInventory(build())
                }
                categoryPageChangeCallback(viewer)
            }
        }
    }

    override fun onCategoryPageChange(callback: (player: Player) -> Unit) {
        categoryPageChangeCallback = callback
    }

    override fun hasCategoryPreviousPage(): Boolean {
        return categoryPage > 0
    }

    override fun hasCategoryNextPage(): Boolean {
        return isNext(categoryPage, categoryElementsCache.size, categorySlots.size)
    }

    override fun resetCategoryElementsCache() {
        categoryElementsCache = categoryElementsCallback()
    }

    override fun build(): Inventory {
        // 更新元素列表缓存
        categoryElementsCache = categoryElementsCallback()
        val currentCategory = this.currentCategory
        if (currentCategory == null) {
            if (categoryElementsCache.isNotEmpty()) {
                val firstCategory = categoryElementsCache.first()
                this.currentCategory = firstCategory
                elementsCache = elementsCallback(firstCategory)
            }
        } else {
            elementsCache = elementsCallback(currentCategory)
        }

        // 本次页面所使用的元素缓存
        val elementMap = hashMapOf<Int, T>()
        val elementItems = subList(elementsCache, page * menuSlots.size, (page + 1) * menuSlots.size)
        val categoryElementMap = hashMapOf<Int, C>()
        val categoryElementItems = subList(categoryElementsCache, categoryPage * categorySlots.size, (categoryPage + 1) * categorySlots.size)
        
        /**
         * 构建事件处理函数
         */
        fun processBuild(p: Player, inventory: Inventory, async: Boolean) {
            viewer = p
            elementItems.forEachIndexed { index, item ->
                val slot = menuSlots.getOrNull(index) ?: 0
                elementMap[slot] = item
                // 生成元素对应物品
                val callback = if (async) asyncGenerateCallback else generateCallback
                val itemStack = callback(viewer, item, index, slot)
                if (itemStack.isNotAir()) {
                    inventory.setItem(slot, itemStack)
                }
            }
            categoryElementItems.forEachIndexed { index, item -> 
                val slot = categorySlots.getOrNull(index) ?: 0
                categoryElementMap[slot] = item
                // 生成元素对应物品
                val callback = if (async) categoryAsyncGenerateCallback else categoryGenerateCallback
                val itemStack = callback(viewer, item, index, slot)
                if (itemStack.isNotAir()) {
                    inventory.setItem(
                        slot,
                        if (item == currentCategory) buildItem(itemStack) { shiny() }
                        else itemStack
                    )
                }
            }
        }

        // 生成回调
        selfBuild { p, it -> processBuild(p, it, false) }
        // 生成异步回调
        selfBuild(async = true) { p, it -> processBuild(p, it, true) }
        // 生成点击回调
        selfClick {
            if (menuLocked) {
                it.isCancelled = true
            }
            elementMap[it.rawSlot]?.let { element ->
                elementClickCallback(it, element)
            }
            categoryElementMap[it.rawSlot]?.let { category ->
                resetElementsCache(category)
                categoryElementClickCallback(it, category)
                if (virtualized) {
                    viewer.openVirtualInventory(build() as VirtualInventory).inject(this)
                } else {
                    viewer.openInventory(build())
                }
            }
        }
        // 构建页面
        return super.build()
    }

    /**
     * 是否存在下一页
     */
    protected fun isNext(page: Int, size: Int, entry: Int): Boolean {
        return size / entry.toFloat() > page + 1
    }
    
}