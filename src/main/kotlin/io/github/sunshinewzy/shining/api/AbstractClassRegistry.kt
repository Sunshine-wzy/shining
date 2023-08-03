package io.github.sunshinewzy.shining.api

import io.github.sunshinewzy.shining.core.lang.item.LanguageItem

abstract class AbstractClassRegistry<T> : IClassRegistry<T> {

    protected val classMap: MutableMap<Class<out T>, LanguageItem> = HashMap()
    protected val classPairListCache: MutableList<Pair<Class<out T>, LanguageItem>> = ArrayList()
    
    
    override fun register(clazz: Class<out T>, icon: LanguageItem) {
        classMap[clazz] = icon
        updateClassPairListCache()
    }

    override fun register(classes: Map<Class<out T>, LanguageItem>) {
        classMap += classes
        updateClassPairListCache()
    }

    override fun getRegisteredClassMap(): Map<Class<out T>, LanguageItem> = classMap

    override fun getRegisteredClassPairList(): List<Pair<Class<out T>, LanguageItem>> = classPairListCache
    
    
    protected open fun updateClassPairListCache() {
        classPairListCache.clear()
        getRegisteredClassMap().mapTo(classPairListCache) { it.key to it.value }
    }
    
}