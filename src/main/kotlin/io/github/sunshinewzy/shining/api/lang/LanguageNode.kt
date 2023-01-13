package io.github.sunshinewzy.shining.api.lang

interface LanguageNode {
    
    companion object {
        val argRegex = Regex("\\{[0-9]+}")
    }
    
}