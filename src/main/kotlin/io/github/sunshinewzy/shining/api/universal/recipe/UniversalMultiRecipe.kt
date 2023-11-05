package io.github.sunshinewzy.shining.api.universal.recipe

import io.github.sunshinewzy.shining.api.universal.item.UniversalItem

interface UniversalMultiRecipe : UniversalRecipe {

    override fun getResult(): UniversalItem = getResults()[0]

}