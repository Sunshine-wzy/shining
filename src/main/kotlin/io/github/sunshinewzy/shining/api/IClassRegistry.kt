package io.github.sunshinewzy.shining.api

import io.github.sunshinewzy.shining.core.lang.item.LanguageItem

interface IClassRegistry<T> {
    
    fun register(clazz: Class<out T>, icon: LanguageItem)
    
    fun register(classes: Map<Class<out T>, LanguageItem>)
    
    fun getRegisteredClassMap(): Map<Class<out T>, LanguageItem>
    
    fun getRegisteredClassPairList(): List<Pair<Class<out T>, LanguageItem>>
    
}