package io.github.sunshinewzy.sunstcore.api

interface SPlugin {
    
    fun getName(): String
    
    
    fun getNamespace(): Namespace {
        return Namespace.get(getName())
    }
    
}