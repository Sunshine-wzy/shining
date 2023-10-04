package io.github.sunshinewzy.shining.api.lang.item

import io.github.sunshinewzy.shining.api.namespace.NamespacedId

interface INamespacedIdItem : ILanguageItem {
    
    val id: NamespacedId
    
}