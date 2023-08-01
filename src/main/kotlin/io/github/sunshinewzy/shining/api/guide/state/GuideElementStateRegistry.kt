package io.github.sunshinewzy.shining.api.guide.state

import io.github.sunshinewzy.shining.utils.setName
import org.bukkit.inventory.ItemStack

object GuideElementStateRegistry {
    
    private val stateClassMap: MutableMap<Class<out IGuideElementState>, ItemStack> = HashMap()
    private val stateClassPairListCache: MutableList<Pair<Class<out IGuideElementState>, ItemStack>> = ArrayList()
    
    
    fun <T: IGuideElementState> register(clazz: Class<T>, symbol: ItemStack) {
        symbol.setName("&f${clazz.simpleName}")
        stateClassMap[clazz] = symbol
        updateStateClassPairListCache()
    }
    
    fun register(classes: Map<Class<out IGuideElementState>, ItemStack>) {
        classes.forEach { (clazz, symbol) -> 
            symbol.setName("&f${clazz.simpleName}")
        }
        stateClassMap += classes
        updateStateClassPairListCache()
    }
    
    fun getRegisteredClassMap(): Map<Class<out IGuideElementState>, ItemStack> = stateClassMap
    
    fun getRegisteredClassPairList(): List<Pair<Class<out IGuideElementState>, ItemStack>> =stateClassPairListCache
    
    
    private fun updateStateClassPairListCache() {
        stateClassPairListCache.clear()
        getRegisteredClassMap().mapTo(stateClassPairListCache) { it.key to it.value }
    }
    
}