package io.github.sunshinewzy.shining.api.guide

import java.util.*

class ElementDescription(val name: String, val lore: List<String>) {

    constructor(name: String) : this(name, Collections.emptyList())

    constructor(name: String, vararg lore: String) : this(name, Arrays.asList(*lore))

    
    companion object {
        val NULL: ElementDescription = ElementDescription("", Collections.emptyList())
    }
    
}