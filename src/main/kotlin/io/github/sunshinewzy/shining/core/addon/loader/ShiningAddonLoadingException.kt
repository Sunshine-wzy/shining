package io.github.sunshinewzy.shining.core.addon.loader

class ShiningAddonLoadingException : RuntimeException {
    
    val name: String
    
    
    constructor(name: String, reason: String) : super("Failed to load addon '$name': $reason") {
        this.name = name
    }
    
    constructor(name: String, cause: Throwable) : super("Failed to load addon '$name'", cause) {
        this.name = name
    }
    
}