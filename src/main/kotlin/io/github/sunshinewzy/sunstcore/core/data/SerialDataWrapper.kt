package io.github.sunshinewzy.sunstcore.core.data

import kotlinx.serialization.KSerializer

class SerialDataWrapper<T>(val data: T) {
    
    var kSerializer: KSerializer<T>? = null
    
    
    constructor(data: T, action: SerialDataWrapper<T>.() -> Unit) : this(data) {
        action(this)
    }
    
}