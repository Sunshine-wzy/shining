package io.github.sunshinewzy.shining.core.lang.node

interface LanguageNode {
    
    companion object {
        val argRegex = Regex("\\{[0-9]+}")
    }
    
}