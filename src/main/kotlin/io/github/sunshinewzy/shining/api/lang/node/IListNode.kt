package io.github.sunshinewzy.shining.api.lang.node

interface IListNode : LanguageNode {

    val list: List<LanguageNode>

    fun format(vararg args: Any?): List<String>

    fun getStringList(): List<String>

    fun getColoredStringList(): List<String>
    
}