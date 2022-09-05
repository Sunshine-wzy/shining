package io.github.sunshinewzy.sunstcore.core.data

class SerialDataWrapper<T>(val data: T) {
    
    constructor(data: T, action: SerialDataWrapper<T>.() -> Unit) : this(data) {
        action(this)
    }
    
}