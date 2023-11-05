package io.github.sunshinewzy.shining.api.universal.recipe

import io.github.sunshinewzy.shining.api.universal.item.UniversalItem
import java.util.*

interface UniversalSingleRecipe : UniversalRecipe {

    override fun getResults(): List<UniversalItem> =
        Collections.singletonList(getResult())
    
}