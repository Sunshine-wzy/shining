package io.github.sunshinewzy.shining.api

import io.github.sunshinewzy.shining.api.lang.item.ILanguageItem
import io.github.sunshinewzy.shining.api.objects.SPair

abstract class AbstractClassRegistry<T> : IClassRegistry<T> {

    protected val classMap: MutableMap<Class<out T>, ILanguageItem> = HashMap()
    protected val classPairListCache: MutableList<SPair<Class<out T>, ILanguageItem>> = ArrayList()
    
    
    override fun register(clazz: Class<out T>, icon: ILanguageItem) {
        classMap[clazz] = icon
        updateClassPairListCache()
    }

    override fun register(classes: Map<Class<out T>, ILanguageItem>) {
        classMap += classes
        updateClassPairListCache()
    }

    override fun getRegisteredClassMap(): Map<Class<out T>, ILanguageItem> = classMap

    override fun getRegisteredClassPairList(): List<SPair<Class<out T>, ILanguageItem>> = classPairListCache
    
    
    protected open fun updateClassPairListCache() {
        classPairListCache.clear()
        getRegisteredClassMap().mapTo(classPairListCache) { SPair(it.key, it.value) }
    }
    
}