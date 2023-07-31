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
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GuideDraftFolderNode) return false

        if (type != other.type) return false
        return index == other.index
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + index.hashCode()
        return result
    }

}