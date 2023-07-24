package io.github.sunshinewzy.shining.core.guide.draft

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

class GuideDraftFolderNode {
    val type: Char
    val index: Long
    
    
    constructor(type: Char, index: Long) {
        this.type = type
        this.index = index
    }
    
    constructor() : this(' ', 0)

    @JsonCreator
    constructor(source: String) {
        val list = source.split(':')
        this.type = list[0].toCharArray()[0]
        this.index = list[1].toLong()
    }

    @JsonValue
    override fun toString(): String = "$type:$index"
    
}