package io.github.sunshinewzy.sunstcore.interfaces

import io.github.sunshinewzy.sunstcore.modules.category.SCategory
import java.util.*

interface SPlugin {
    
    companion object Registry {
        val categories = TreeSet<SCategory> { category1, category2 ->
            if (category1.tier >= category2.tier) 1
            else -1
        }
    }
    
}