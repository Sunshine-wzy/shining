package io.github.sunshinewzy.shining.api.lang.node

interface ITextNode : LanguageNode {
    
    val text: String

    fun format(vararg args: Any?): String
    
}