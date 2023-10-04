package io.github.sunshinewzy.shining.api

import io.github.sunshinewzy.shining.api.lang.item.ILanguageItem
import io.github.sunshinewzy.shining.api.objects.SPair

interface IClassRegistry<T> {
    
    fun register(clazz: Class<out T>, icon: ILanguageItem)
    
    fun register(classes: Map<Class<out T>, ILanguageItem>)
    
    fun getRegisteredClassMap(): Map<Class<out T>, ILanguageItem>
    
    fun getRegisteredClassPairList(): List<SPair<Class<out T>, ILanguageItem>>

}